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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.AggregatedValidity;
import org.apache.excalibur.store.Store;
import org.apache.excalibur.xmlizer.XMLizer;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;

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
 * @version CVS $Id: XSLTProcessorImpl.java,v 1.10 2002/07/10 08:53:17 donaldp Exp $
 * @version 1.0
 * @since   July 11, 2001
 */
public final class XSLTProcessorImpl
    extends AbstractLogEnabled
    implements XSLTProcessor,
    Composable,
    Disposable,
    Parameterizable,
    URIResolver
{
    private ComponentManager m_manager;

    /** The store service instance */
    private Store m_store;

    /** The trax TransformerFactory this component uses */
    private SAXTransformerFactory m_factory;

    /** Is the store turned on? (default is off) */
    private boolean m_useStore;

    /** Is incremental processing turned on? (default for Xalan: no) */
    private boolean m_incrementalProcessing;

    /** Resolver used to resolve XSLT document() calls, imports and includes */
    private SourceResolver m_resolver;

    /** The error handler for the transformer */
    private TraxErrorHandler m_errorHandler;

    /** Map of pairs of System ID's / validities of the included stylesheets */
    private Map m_includesMap = new HashMap();

    /**
     * Compose. Try to get the store
     */
    public void compose( final ComponentManager manager )
        throws ComponentException
    {
        m_manager = manager;
        m_errorHandler = new TraxErrorHandler( getLogger() );
        m_resolver = (SourceResolver)manager.lookup( SourceResolver.ROLE );
    }

    /**
     * Dispose
     */
    public void dispose()
    {
        if( null != m_manager )
        {
            m_manager.release( m_store );
            m_store = null;
            m_manager.release( m_resolver );
            m_resolver = null;
        }
        m_errorHandler = null;
        m_manager = null;
    }

    /**
     * Configure the component
     */
    public void parameterize( final Parameters params )
        throws ParameterException
    {
        m_useStore = params.getParameterAsBoolean( "use-store", this.m_useStore );
        m_incrementalProcessing = params.getParameterAsBoolean( "incremental-processing", this.m_incrementalProcessing );
        m_factory = getTransformerFactory( params.getParameter( "transformer-factory", null ) );
        if( m_useStore )
        {
            try
            {
                m_store = (Store)m_manager.lookup( Store.TRANSIENT_STORE );
            }
            catch( final ComponentException ce )
            {
                final String message =
                    "XSLTProcessor: use-store is set to true, " +
                    "but the lookup of the Store failed.";
                throw new ParameterException( message, ce );
            }
        }
    }

    /**
     * Set the transformer factory used by this component
     */
    public void setTransformerFactory( final String classname )
    {
        m_factory = getTransformerFactory( classname );
    }

    public TransformerHandler getTransformerHandler( final Source stylesheet )
        throws XSLTProcessorException
    {
        return getTransformerHandler( stylesheet, null );
    }

    public TransformerHandler getTransformerHandler( final Source stylesheet,
                                                     final XMLFilter filter )
        throws XSLTProcessorException
    {
        final XSLTProcessor.TransformerHandlerAndValidity validity = getTransformerHandlerAndValidity( stylesheet, filter );
        return validity.getTransfomerHandler();
    }

    public TransformerHandlerAndValidity getTransformerHandlerAndValidity( final Source stylesheet )
        throws XSLTProcessorException
    {
        return getTransformerHandlerAndValidity( stylesheet, null );
    }

    public TransformerHandlerAndValidity getTransformerHandlerAndValidity( Source stylesheet, XMLFilter filter )
        throws XSLTProcessorException
    {
        try
        {
            final String id = stylesheet.getSystemId();
            TransformerHandlerAndValidity handlerAndValidity = getTemplates( stylesheet, id );
            if( null == handlerAndValidity )
            {
                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Creating new Templates for " + id );
                }

                // Create a Templates ContentHandler to handle parsing of the
                // stylesheet.
                TemplatesHandler templatesHandler = m_factory.newTemplatesHandler();

                // Set the system ID for the template handler since some
                // TrAX implementations (XSLTC) rely on this in order to obtain
                // a meaningful identifier for the Templates instances.
                templatesHandler.setSystemId( id );
                if( filter != null )
                {
                    filter.setContentHandler( templatesHandler );
                }

                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Source = " + stylesheet
                                       + ", templatesHandler = " + templatesHandler );
                }

                // Initialize List for included validities
                final SourceValidity validity = stylesheet.getValidity();
                if( validity != null )
                {
                    m_includesMap.put( id, new ArrayList() );
                }

                try
                {
                    // Process the stylesheet.
                    sourceToSAX( stylesheet,
                                 filter != null ? (ContentHandler)filter : (ContentHandler)templatesHandler );

                    // Get the Templates object (generated during the parsing of
                    // the stylesheet) from the TemplatesHandler.
                    final Templates template = templatesHandler.getTemplates();
                    putTemplates( template, stylesheet, id );

                    // Create transformer handler
                    final TransformerHandler handler = m_factory.newTransformerHandler( template );
                    handler.getTransformer().setErrorListener( m_errorHandler );

                    // Create aggregated validity
                    AggregatedValidity aggregated = null;
                    if( validity != null )
                    {
                        List includes = (List)m_includesMap.get( id );
                        if( includes != null )
                        {
                            aggregated = new AggregatedValidity();
                            aggregated.add( validity );
                            for( int i = includes.size() - 1; i >= 0; i-- )
                            {
                                aggregated.add( (SourceValidity)( (Object[])includes.get( i ) )[ 1 ] );
                            }
                        }
                    }

                    // Create result
                    handlerAndValidity = new TransformerHandlerAndValidity( handler, aggregated );
                }
                finally
                {
                    m_includesMap.remove( id );
                }
            }
            else
            {
                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Reusing Templates for " + id );
                }
            }

            return handlerAndValidity;
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
            ( (XMLizable)source ).toSAX( handler );
        }
        else
        {
            final XMLizer xmlizer = (XMLizer)m_manager.lookup( XMLizer.ROLE );
            try
            {
                final InputStream inputStream = source.getInputStream();
                final String mimeType = source.getMimeType();
                final String systemId = source.getSystemId();
                xmlizer.toSAX( inputStream, mimeType, systemId, handler );
            }
            finally
            {
                m_manager.release( xmlizer );
            }
        }
    }

    public void transform( final Source source,
                           final Source stylesheet,
                           final Parameters params,
                           final Result result )
        throws XSLTProcessorException
    {
        try
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Transform source = " + source +
                                   ", stylesheet = " + stylesheet +
                                   ", parameters = " + params +
                                   ", result = " + result );
            }
            final TransformerHandler handler = getTransformerHandler( stylesheet );
            if( params != null )
            {
                final Transformer transformer = handler.getTransformer();
                transformer.clearParameters();
                String[] names = params.getNames();
                for( int i = names.length - 1; i >= 0; i-- )
                {
                    transformer.setParameter( names[ i ], params.getParameter( names[ i ] ) );
                }
            }

            handler.setResult( result );
            sourceToSAX( source, handler );
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Transform done" );
            }
        }
        catch( SAXException e )
        {
            if( e.getException() == null )
            {
                final String message = "Error in running Transformation";
                throw new XSLTProcessorException( message, e );
            }
            else
            {
                final String message = "Got SAXException. Rethrowing cause exception.";
                getLogger().debug( message, e );
                throw new XSLTProcessorException( "Error in running Transformation", e.getException() );
            }
        }
        catch( Exception e )
        {
            final String message = "Error in running Transformation";
            throw new XSLTProcessorException( message, e );
        }
    }

    /**
     * Get the TransformerFactory associated with the given classname. If
     * the class can't be found or the given class doesn't implement
     * the required interface, the default factory is returned.
     */
    private SAXTransformerFactory getTransformerFactory( String factoryName )
    {
        SAXTransformerFactory _factory;

        if( null == factoryName )
        {
            _factory = (SAXTransformerFactory)TransformerFactory.newInstance();
        }
        else
        {
            try
            {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                if( loader == null )
                {
                    loader = getClass().getClassLoader();
                }
                _factory = (SAXTransformerFactory)loader.loadClass( factoryName ).newInstance();
            }
            catch( ClassNotFoundException cnfe )
            {
                getLogger().error( "Cannot find the requested TrAX factory '" + factoryName
                                   + "'. Using default TrAX Transformer Factory instead." );
                if( m_factory != null )
                    return m_factory;
                _factory = (SAXTransformerFactory)TransformerFactory.newInstance();
            }
            catch( ClassCastException cce )
            {
                getLogger().error( "The indicated class '" + factoryName
                                   + "' is not a TrAX Transformer Factory. Using default TrAX Transformer Factory instead." );
                if( m_factory != null )
                    return m_factory;
                _factory = (SAXTransformerFactory)TransformerFactory.newInstance();
            }
            catch( Exception e )
            {
                getLogger().error( "Error found loading the requested TrAX Transformer Factory '"
                                   + factoryName + "'. Using default TrAX Transformer Factory instead." );
                if( m_factory != null )
                    return m_factory;
                _factory = (SAXTransformerFactory)TransformerFactory.newInstance();
            }
        }

        _factory.setErrorListener( m_errorHandler );
        _factory.setURIResolver( this );

        // FIXME (SM): implementation-specific parameter passing should be
        // made more extensible.
        if( _factory.getClass().getName().equals( "org.apache.xalan.processor.TransformerFactoryImpl" ) )
        {
            _factory.setAttribute( "http://xml.apache.org/xalan/features/incremental",
                                   new Boolean( m_incrementalProcessing ) );
        }

        return _factory;
    }

    private TransformerHandlerAndValidity getTemplates( Source stylesheet, String id )
        throws IOException, SourceException, TransformerException
    {
        if( !m_useStore )
        {
            return null;
        }

        // we must augment the template ID with the factory classname since one
        // transformer implementation cannot handle the instances of a
        // template created by another one.
        String key = id + m_factory.getClass().getName();

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "getTemplates: stylesheet " + id );
        }

        SourceValidity newValidity = stylesheet.getValidity();

        // Only stylesheets with validity are stored
        if( newValidity == null )
        {
            // Remove an old template
            m_store.remove( key );
            return null;
        }

        // Stored is an array of the templates and the caching time and list of includes
        Object[] templateAndValidityAndIncludes = (Object[])m_store.get( key );
        if( templateAndValidityAndIncludes == null )
        {
            // Templates not found in cache
            return null;
        }

        // Check template modification time
        SourceValidity storedValidity = (SourceValidity)templateAndValidityAndIncludes[ 1 ];
        int valid = storedValidity.isValid();
        boolean isValid;
        if( valid == 0 )
        {
            isValid = storedValidity.isValid( newValidity );
        }
        else
        {
            isValid = ( valid == 1 );
        }
        if( !isValid )
        {
            m_store.remove( key );
            return null;
        }

        // Check includes
        AggregatedValidity aggregated = null;
        List includes = (List)templateAndValidityAndIncludes[ 2 ];
        if( includes != null )
        {
            aggregated = new AggregatedValidity();
            aggregated.add( storedValidity );

            for( int i = includes.size() - 1; i >= 0; i-- )
            {
                // Every include stored as pair of source ID and validity
                Object[] pair = (Object[])includes.get( i );
                storedValidity = (SourceValidity)pair[ 1 ];
                aggregated.add( storedValidity );

                valid = storedValidity.isValid();
                isValid = false;
                if( valid == 0 )
                {
                    SourceValidity included = m_resolver.resolveURI( (String)pair[ 0 ] ).getValidity();
                    if( included != null )
                    {
                        isValid = storedValidity.isValid( included );
                    }
                }
                else
                {
                    isValid = ( valid == 1 );
                }
                if( !isValid )
                {
                    m_store.remove( key );
                    return null;
                }
            }
        }

        TransformerHandler handler = m_factory.newTransformerHandler(
            (Templates)templateAndValidityAndIncludes[ 0 ] );
        handler.getTransformer().setErrorListener( m_errorHandler );
        return new TransformerHandlerAndValidity( handler, aggregated );
    }

    private void putTemplates( Templates templates, Source stylesheet, String id )
        throws IOException
    {
        if( !m_useStore )
            return;

        // we must augment the template ID with the factory classname since one
        // transformer implementation cannot handle the instances of a
        // template created by another one.
        String key = id + m_factory.getClass().getName();

        // only stylesheets with a last modification date are stored
        SourceValidity validity = stylesheet.getValidity();
        if( null != validity )
        {
            // Stored is an array of the template and the current time
            Object[] templateAndValidityAndIncludes = new Object[ 3 ];
            templateAndValidityAndIncludes[ 0 ] = templates;
            templateAndValidityAndIncludes[ 1 ] = validity;
            templateAndValidityAndIncludes[ 2 ] = m_includesMap.get( id );
            m_store.store( key, templateAndValidityAndIncludes );
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
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "resolve(href = " + href +
                               ", base = " + base + "); resolver = " + m_resolver );
        }

        Source xslSource = null;
        try
        {
            if( base == null || href.indexOf( ":" ) > 1 )
            {
                // Null base - href must be an absolute URL
                xslSource = m_resolver.resolveURI( href );
            }
            else if( href.length() == 0 )
            {
                // Empty href resolves to base
                xslSource = m_resolver.resolveURI( base );
            }
            else
            {
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
                        xslSource = m_resolver.resolveURI( base.substring( 0, lastPathElementPos )
                                                              + "/" + href );
                    }
                }
                else
                {
                    File parent = new File( base.substring( 5 ) );
                    File parent2 = new File( parent.getParentFile(), href );
                    xslSource = m_resolver.resolveURI( parent2.toURL().toExternalForm() );
                }
            }

            InputSource is = getInputSource( xslSource );

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "xslSource = " + xslSource + ", system id = " + xslSource.getSystemId() );
            }

            // Populate included validities
            List includes = (List)m_includesMap.get( base );
            if( includes != null )
            {
                SourceValidity included = xslSource.getValidity();
                if( included != null )
                {
                    includes.add( new Object[]{xslSource.getSystemId(), xslSource.getValidity()} );
                }
                else
                {
                    // One of the included stylesheets is not cacheable
                    m_includesMap.remove( base );
                }
            }

            return new StreamSource( is.getByteStream(), is.getSystemId() );
        }
        catch( SourceException e )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Failed to resolve " + href
                                   + "(base = " + base + "), return null", e );
            }

            // CZ: To obtain the same behaviour as when the resource is
            // transformed by the XSLT Transformer we should return null here.
            return null;
        }
        catch( java.net.MalformedURLException mue )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Failed to resolve " + href
                                   + "(base = " + base + "), return null", mue );
            }

            return null;
        }
        catch( IOException ioe )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Failed to resolve " + href
                                   + "(base = " + base + "), return null", ioe );
            }

            return null;
        }
        finally
        {
            m_resolver.release( xslSource );
        }
    }

    /**
     * Return a new <code>InputSource</code> object that uses
     * the <code>InputStream</code> and the system ID of the
     * <code>Source</code> object.
     *
     * @throws IOException if I/O error occured.
     */
    private static InputSource getInputSource( final Source source )
        throws IOException, SourceException
    {
        final InputSource newObject = new InputSource( source.getInputStream() );
        newObject.setSystemId( source.getSystemId() );
        return newObject;
    }
}
