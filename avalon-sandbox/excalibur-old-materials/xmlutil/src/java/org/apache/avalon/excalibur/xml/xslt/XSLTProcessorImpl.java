/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml.xslt;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;

import org.apache.avalon.excalibur.xml.XMLizable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.xmlizer.XMLizer;
import org.apache.excalibur.store.Store;

/**
 * This class defines the implementation of the {@link XSLTProcessor}
 * component.
 *
 * The &lt;use-store&gt; configuration forces the transformer to put the
 * <code>Templates</code> generated from the XSLT stylesheet into the
 * <code>Store</code>. This property is false by default.
 * <p>
 * The &lt;transformer-factory&gt; configuration tells the transformer to use a particular
 * implementation of <code>javax.xml.transform.TransformerFactory</code>. This allows to force
 * the use of a given TRAX implementation (e.g. xalan or saxon) if several are available in the
 * classpath. If this property is not set, the transformer uses the standard TRAX mechanism
 * (<code>TransformerFactory.newInstance()</code>).
 *
 *
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @version CVS $Id: XSLTProcessorImpl.java,v 1.4 2002/05/02 10:56:40 cziegeler Exp $
 * @version 1.0
 * @since   July 11, 2001
 */
public class XSLTProcessorImpl
  extends AbstractLogEnabled
  implements XSLTProcessor,
             Composable,
             Disposable,
             Parameterizable,
             URIResolver {

    protected ComponentManager manager;

    /** The store service instance */
    protected Store store;

    /** The trax TransformerFactory lookup table*/
    protected HashMap factories;

    /** The trax TransformerFactory this component uses */
    protected SAXTransformerFactory factory;

    /** Is the store turned on? (default is off) */
    protected boolean useStore = false;

    /** Is incremental processing turned on? (default for Xalan: no) */
    protected boolean incrementalProcessing = false;

    /** The source resolver */
    protected SourceResolver resolver;

    /** The error handler for the transformer */
    protected TraxErrorHandler errorHandler;

    /**
     * Compose. Try to get the store
     */
    public void compose( ComponentManager manager )
      throws ComponentException
    {
        this.manager = manager;
        this.store = (Store)manager.lookup(Store.TRANSIENT_STORE);
        this.errorHandler = new TraxErrorHandler( this.getLogger() );
        this.resolver = (SourceResolver)this.manager.lookup( SourceResolver.ROLE );
    }

    /**
     * Dispose
     */
    public void dispose()
    {
        if ( null != this.manager )
        {
            this.manager.release(this.store);
            this.store = null;
            this.manager.release( this.resolver );
            this.resolver = null;
        }
        this.errorHandler = null;
        this.manager = null;
    }

    /**
     * Configure the component
     */
    public void parameterize( Parameters params )
      throws ParameterException
    {
        this.useStore = params.getParameterAsBoolean( "use-store", true );
        this.incrementalProcessing = params.getParameterAsBoolean( "incremental-processing", false );
        this.factory = getTransformerFactory(params.getParameter("transformer-factory", null));
    }

    /**
     * Set the transformer factory used by this component
     */
    public void setTransformerFactory(String classname) {
        this.factory = getTransformerFactory(classname);
    }

    public TransformerHandler getTransformerHandler( Source stylesheet )
      throws XSLTProcessorException
    {
        return getTransformerHandler( stylesheet, null );
    }

    public TransformerHandler getTransformerHandler( Source stylesheet,
                                                     XMLFilter filter )
      throws XSLTProcessorException
    {
        try
        {
            final String id = stylesheet.getSystemId();
            Templates templates = getTemplates(stylesheet, id);
            if( templates == null )
            {
                if( this.getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Creating new Templates for " + id );
                }

                // Create a Templates ContentHandler to handle parsing of the
                // stylesheet.
                TemplatesHandler templatesHandler = this.factory.newTemplatesHandler();

                // Set the system ID for the template handler since some
                // TrAX implementations (XSLTC) rely on this in order to obtain
                // a meaningful identifier for the Templates instances.
                templatesHandler.setSystemId(id);
                if( filter != null )
                {
                    filter.setContentHandler( templatesHandler );
                }

                if( this.getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Source = " + stylesheet
                                       + ", templatesHandler = " + templatesHandler );
                }

                // Process the stylesheet.
                sourceToSAX( stylesheet,
                             filter != null ? ( ContentHandler ) filter : ( ContentHandler ) templatesHandler );

                // Get the Templates object (generated during the parsing of
                // the stylesheet) from the TemplatesHandler.
                templates = templatesHandler.getTemplates();
                putTemplates (templates, stylesheet, id);
            }
            else
            {
                if( this.getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Reusing Templates for " + id );
                }
            }

            TransformerHandler handler = this.factory.newTransformerHandler(templates);

            /* (VG)
            From http://java.sun.com/j2se/1.4/docs/api/javax/xml/transform/TransformerFactory.html#newTemplates(javax.xml.transform.Source)
            Or http://xml.apache.org/xalan-j/apidocs/javax/xml/transform/TransformerFactory.html#newTemplates(javax.xml.transform.Source)

            "Returns: Templates object capable of being used for transformation
            purposes, never null."
            if (handler == null) {
            if (this.getLogger().isDebugEnabled()) {
            getLogger().debug("Re-creating new Templates for " + id);
            }

            templates = getTransformerFactory().newTemplates(new SAXSource(stylesheet.getInputSource()));
            putTemplates (templates, stylesheet, id);
            handler = getTransformerFactory().newTransformerHandler(templates);
            }
            */

            handler.getTransformer().setErrorListener( this.errorHandler );
            return handler;
        }
        catch( XSLTProcessorException e )
        {
            throw e;
        }
        catch( SAXException e )
        {
            if( e.getException() == null )
            {
                throw new XSLTProcessorException( "Exception in creating Transform Handler", e );
            }
            else
            {
                getLogger().debug( "Got SAXException. Rethrowing cause exception.", e );
                throw new XSLTProcessorException( "Exception in creating Transform Handler", e.getException() );
            }
        }
        catch( Exception e )
        {
            throw new XSLTProcessorException( "Exception in creating Transform Handler", e );
        }
    }

    private void sourceToSAX( Source source, ContentHandler handler )
      throws SAXException, IOException, ComponentException, SourceException
    {
        if( source instanceof XMLizable )
        {
            ( ( XMLizable ) source ).toSAX( handler );

        }
        else
        {
            XMLizer xmlizer = null;

            try
            {
                xmlizer = ( XMLizer ) this.manager.lookup( XMLizer.ROLE );

                xmlizer.toSAX( source.getInputStream(), source.getMimeType(), source.getSystemId(), handler );
            }
            finally
            {
                this.manager.release( xmlizer );
            }
        }
    }

    public void transform( Source source,
                           Source stylesheet,
                           Parameters params,
                           Result result )
      throws XSLTProcessorException
    {
        try
        {
            if( this.getLogger().isDebugEnabled() )
            {
                getLogger().debug( "XSLTProcessorImpl: transform source = " + source
                                   + ", stylesheet = " + stylesheet
                                   + ", parameters = " + params
                                   + ", result = " + result );
            }
            TransformerHandler handler = getTransformerHandler( stylesheet );
            Transformer transformer = handler.getTransformer();

            if( params != null )
            {
                transformer.clearParameters();
                String[] names = params.getNames();
                for( int i = names.length - 1; i >= 0; i-- )
                {
                    transformer.setParameter( names[i], params.getParameter( names[i] ) );
                }
            }
            if( getLogger().isDebugEnabled() ) this.getLogger().debug( "XSLTProcessorImpl: starting transform" );
            // FIXME (VG): Is it possible to use Source's toSAX method?
            handler.setResult( result );

            sourceToSAX( source, handler );

            if( getLogger().isDebugEnabled() ) this.getLogger().debug( "XSLTProcessorImpl: transform done" );
        }
        catch( Exception e )
        {
            throw new XSLTProcessorException( "Error in running Transformation", e );
        }
    }

    /**
     * Get the TransformerFactory associated with the given classname. If
     * the class can't be found or the given class doesn't implement
     * the required interface, the default factory is returned.
     */
    private SAXTransformerFactory getTransformerFactory(String factoryName)
    {
        SAXTransformerFactory _factory;

        if ( null == factoryName )
        {
            _factory = (SAXTransformerFactory) TransformerFactory.newInstance();
        }
        else
        {
            try
            {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                if( loader == null )
                {
                    loader = this.getClass().getClassLoader();
                }
                _factory = (SAXTransformerFactory) loader.loadClass(factoryName).newInstance();
            }
            catch (ClassNotFoundException cnfe)
            {
                getLogger().error("Cannot find the requested TrAX factory '" + factoryName
                        + "'. Using default TrAX Transformer Factory instead.");
                if (this.factory != null) return this.factory;
                _factory = (SAXTransformerFactory) TransformerFactory.newInstance();
            }
            catch (ClassCastException cce)
            {
                getLogger().error("The indicated class '" + factoryName
                        + "' is not a TrAX Transformer Factory. Using default TrAX Transformer Factory instead.");
                if (this.factory != null) return this.factory;
                _factory = (SAXTransformerFactory) TransformerFactory.newInstance();
            }
            catch (Exception e)
            {
                getLogger().error("Error found loading the requested TrAX Transformer Factory '"
                        + factoryName + "'. Using default TrAX Transformer Factory instead.");
                if (this.factory != null) return this.factory;
                _factory = (SAXTransformerFactory) TransformerFactory.newInstance();
            }
        }

        _factory.setErrorListener(this.errorHandler);
        _factory.setURIResolver(this);

        // FIXME (SM): implementation-specific parameter passing should be
        // made more extensible.
        if (_factory.getClass().getName().equals("org.apache.xalan.processor.TransformerFactoryImpl"))
        {
            _factory.setAttribute("http://xml.apache.org/xalan/features/incremental",
                                       new Boolean( incrementalProcessing ) );
        }

        return _factory;
    }

    private Templates getTemplates(Source stylesheet, String id)
    throws IOException, XSLTProcessorException {
        if (!useStore)
            return null;

        // we must augment the template ID with the factory classname since one
        // transformer implementation cannot handle the instances of a
        // template created by another one.
        id += factory.getClass().getName();

        if (getLogger().isDebugEnabled()) getLogger().debug("XSLTProcessorImpl getTemplates: stylesheet " + id);

        Templates templates = null;
        // only stylesheets with a validity are stored

        if (store.containsKey(id)) {
            Object[] templateAndValidity = (Object[])store.get(id);

            SourceValidity storedValidity = (SourceValidity)templateAndValidity[1];
            boolean isValid = storedValidity.isValid();
            if ( !isValid ) {
                SourceValidity validity = stylesheet.getValidity();
                if ( null != validity) {

                    isValid = storedValidity.isValid( validity );

                }
            }

            if ( isValid ) {
                templates = (Templates)templateAndValidity[0];
            } else {
                // remove an old template if it exists
                store.remove(id);
            }
        }
        return templates;
    }

    private void putTemplates (Templates templates, Source stylesheet, String id)
    throws IOException, XSLTProcessorException {
        if (!useStore)
            return;

        // we must augment the template ID with the factory classname since one
        // transformer implementation cannot handle the instances of a
        // template created by another one.
        id += factory.getClass().getName();

        // only stylesheets with a last modification date are stored
        SourceValidity validity = stylesheet.getValidity();
        if ( null != validity ) {

            // Stored is an array of the template and the current time
            Object[] templateAndValidity = new Object[2];
            templateAndValidity[0] = templates;
            templateAndValidity[1] = validity;
            store.hold(id, templateAndValidity);
        }
    }

    /**
     * Called by the processor when it encounters
     * an xsl:include, xsl:import, or document() function.
     *
     * @param href An href attribute, which may be relative or absolute.
     * @param base The base URI in effect when the href attribute
     * was encountered.
     *
     * @return A Source object, or null if the href cannot be resolved,
     * and the processor should try to resolve the URI itself.
     *
     * @throws TransformerException if an error occurs when trying to
     * resolve the URI.
     */
    public javax.xml.transform.Source resolve( String href, String base )
      throws TransformerException
    {
        if( this.getLogger().isDebugEnabled() )
        {
            this.getLogger().debug( "resolve(href = " + href +
                                    ", base = " + base + "); resolver = " + this.resolver );
        }

        Source xslSource = null;
        try
        {
            if( href.indexOf( ":" ) > 1 )
            {
                xslSource = this.resolver.resolveURI( href );
            }
            else
            {
                // patch for a null pointer passed as base
                if( base == null )
                    throw new IllegalArgumentException( "Null pointer passed as base" );

                // is the base a file or a real url
                if( !base.startsWith( "file:" ) )
                {
                    int lastPathElementPos = base.lastIndexOf( '/' );
                    if( lastPathElementPos == -1 )
                    {
                        // this should never occur as the base should
                        // always be protocol:/....
                        return null; // we can't resolve this
                    }
                    else
                    {
                        xslSource = this.resolver.resolveURI( new StringBuffer( base.substring( 0, lastPathElementPos ) )
                                                              .append( "/" ).append( href ).toString() );
                    }
                }
                else
                {
                    File parent = new File( base.substring( 5 ) );
                    File parent2 = new File( parent.getParentFile(), href );
                    xslSource = this.resolver.resolveURI( parent2.toURL().toExternalForm() );
                }
            }

            InputSource is = getInputSource( xslSource );

            if( this.getLogger().isDebugEnabled() )
            {
                getLogger().debug( "xslSource = " + xslSource + ", system id = " + xslSource.getSystemId() );
            }

            return new StreamSource( is.getByteStream(), is.getSystemId() );
        }
        catch( SourceException e )
        {
            // to obtain the same behaviour as when the resource is
            // transformed by the XSLT Transformer we should return null here.
            return null;
        }
        catch( java.net.MalformedURLException mue )
        {
            return null;
        }
        catch( IOException ioe )
        {
            return null;
        }
        finally
        {
            this.resolver.release( xslSource );
        }
    }

    /**
     * Return a new <code>InputSource</code> object that uses
     * the <code>InputStream</code> and the system ID of the
     * <code>Source</code> object.
     *
     * @throws IOException if I/O error occured.
     */
    private static InputSource getInputSource( Source source ) throws IOException, SourceException
    {
        final InputSource newObject = new InputSource( source.getInputStream() );
        newObject.setSystemId( source.getSystemId() );
        return newObject;
    }
}
