/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Cocoon" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.excalibur.xml.xslt;

import java.io.File;
import java.io.IOException;
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
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.xmlizer.XMLizer;

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
 * Currently the Store code is non-operational
 *
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @version CVS $Id: XSLTProcessorImpl.java,v 1.1 2002/04/24 18:23:27 proyal Exp $
 * @version 1.0
 * @since   July 11, 2001
 */
public class XSLTProcessorImpl extends AbstractLogEnabled
  implements XSLTProcessor, Composable, Disposable, Parameterizable, URIResolver
{

    protected ComponentManager manager;

    /** The store service instance */
//    protected Store store;

    /** The trax TransformerFactory */
    protected SAXTransformerFactory tfactory;

    /** The factory class used to create tfactory */
    protected Class tfactoryClass;

    /** Is the store turned on? (default is off) */
    protected boolean useStore = false;

    /** Is incremental processing turned on? (default for Xalan: no) */
    protected boolean incrementalProcessing = false;

    protected SourceResolver resolver;

    /**
     * Compose. Try to get the store
     */
    public void compose( ComponentManager manager )
      throws ComponentException
    {
        this.manager = manager;
        this.getLogger().debug( "XSLTProcessorImpl component initialized." );
//        this.store = (Store)manager.lookup(Store.TRANSIENT_CACHE);
    }

    /**
     * Dispose
     */
    public void dispose()
    {
//        if (this.manager != null) {
//            this.manager.release(this.store);
//            this.store = null;
//        }

        this.manager = null;
        this.tfactoryClass = null;
        this.tfactory = null;
    }

    /**
     * Configure the component
     */
    public void parameterize( Parameters params )
      throws ParameterException
    {
        this.useStore = params.getParameterAsBoolean( "use-store", true );
        this.incrementalProcessing = params.getParameterAsBoolean( "incremental-processing", false );

        String factoryName = params.getParameter( "transformer-factory", null );
        if( factoryName == null )
        {
            // Will use default TRAX mechanism
            this.tfactoryClass = null;
        }
        else
        {
            // Will use specific class
            getLogger().debug( "Using factory " + factoryName );
            try
            {
                this.tfactoryClass = Thread.currentThread().getContextClassLoader().loadClass( factoryName );
            }
            catch( ClassNotFoundException cnfe )
            {
                throw new ParameterException( "Cannot load TransformerFactory class", cnfe );
            }

            if( !TransformerFactory.class.isAssignableFrom( tfactoryClass ) )
            {
                throw new ParameterException( "Class " + factoryName + " isn't a TransformerFactory" );
            }
        }
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
            Templates templates = null; //TODO: getTemplates(stylesheet, id);
            if( templates == null )
            {
                if( this.getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Creating new Templates for " + id );
                }

                // Create a Templates ContentHandler to handle parsing of the
                // stylesheet.
                TemplatesHandler templatesHandler
                  = getTransformerFactory().newTemplatesHandler();

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
                //TODO: putTemplates (templates, stylesheet, id);
            }
            else
            {
                if( this.getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Reusing Templates for " + id );
                }
            }

            TransformerHandler handler = getTransformerFactory().newTransformerHandler( templates );

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

            handler.getTransformer().setErrorListener( new TraxErrorHandler( getLogger() ) );

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
     * Helper for TransformerFactory.
     */
    private SAXTransformerFactory getTransformerFactory() throws Exception
    {
        if( tfactory == null )
        {
            if( tfactoryClass == null )
            {
                tfactory = ( SAXTransformerFactory ) TransformerFactory.newInstance();
            }
            else
            {
                tfactory = ( SAXTransformerFactory ) tfactoryClass.newInstance();
            }
            tfactory.setErrorListener( new TraxErrorHandler( getLogger() ) );
            tfactory.setURIResolver( this );
            // TODO: If we will support this feature with a different
            // transformer than Xalan we'll have to set that corresponding
            // feature
            if( tfactory.getClass().getName().equals( "org.apache.xalan.processor.TransformerFactoryImpl" ) )
            {
                tfactory.setAttribute( "http://xml.apache.org/xalan/features/incremental",
                                       new Boolean( incrementalProcessing ) );
            }
        }
        return tfactory;
    }

// TODO: Can't implement until the we have a store. Change to use SourceValidity instead
//
//    private Templates getTemplates(Source stylesheet, String id)
//    throws IOException, XSLTProcessorException {
//        if (!useStore)
//            return null;
//
//        if (getLogger().isDebugEnabled()) getLogger().debug("XSLTProcessorImpl getTemplates: stylesheet " + id);
//
//        Templates templates = null;
//        // only stylesheets with a last modification date are stored
//
//        if (stylesheet.getLastModified() != 0) {
//            // Stored is an array of the template and the caching time
//            if (store.containsKey(id)) {
//                Object[] templateAndTime = (Object[])store.get(id);
//
//                if(templateAndTime != null && templateAndTime[1] != null) {
//                    long storedTime = ((Long)templateAndTime[1]).longValue();
//
//                    if (storedTime < stylesheet.getLastModified()) {
//                        store.remove(id);
//                    } else {
//                        templates = (Templates)templateAndTime[0];
//                    }
//                }
//            }
//        } else if (store.containsKey(id)) {
//            // remove an old template if it exists
//            store.remove(id);
//        }
//        return templates;
//    }
//
//    private void putTemplates (Templates templates, Source stylesheet, String id)
//    throws IOException, XSLTProcessorException {
//        if (!useStore)
//            return;
//
//        // only stylesheets with a last modification date are stored
//        if (stylesheet.getLastModified() != 0) {
//
//            // Stored is an array of the template and the current time
//            Object[] templateAndTime = new Object[2];
//            templateAndTime[0] = templates;
//            templateAndTime[1] = new Long(stylesheet.getLastModified());
//            store.hold(id, templateAndTime);
//        }
//    }

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

    public void setSourceResolver( SourceResolver resolver )
    {
        this.resolver = resolver;
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
