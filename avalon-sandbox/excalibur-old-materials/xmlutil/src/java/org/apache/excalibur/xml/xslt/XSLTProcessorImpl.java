/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.excalibur.xml.xslt;

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

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.AggregatedValidity;
import org.apache.excalibur.store.Store;
import org.apache.excalibur.xml.sax.XMLizable;
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
 * @version CVS $Id: XSLTProcessorImpl.java,v 1.28 2003/03/29 18:53:26 bloritsch Exp $
 * @version 1.0
 * @since   July 11, 2001
 */
public final class XSLTProcessorImpl
    extends AbstractLogEnabled
    implements XSLTProcessor,
    Serviceable,
    Initializable,
    Disposable,
    Parameterizable,
    Recyclable,
    URIResolver
{
    /** The store service instance */
    private Store m_store;

    /** The trax TransformerFactory this component uses */
    private String m_transformerFactory;
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

    private XMLizer m_xmlizer;

    /** The ServiceManager */
    private ServiceManager m_manager;
    
    /**
     * Compose. Try to get the store
     *
     * @avalon.service interface="XMLizer"
     * @avalon.service interface="SourceResolver"
     * @avalon.service interface="Store/TransientStore" optional="true"
     */
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        m_manager = manager;
        m_xmlizer = (XMLizer)m_manager.lookup( XMLizer.ROLE );
        m_resolver = (SourceResolver)m_manager.lookup( SourceResolver.ROLE );

        if( m_manager.hasService( Store.TRANSIENT_STORE ) )
        {
            m_store = (Store)m_manager.lookup( Store.TRANSIENT_STORE );
        }
    }

    public void initialize()
        throws Exception
    {
        m_errorHandler = new TraxErrorHandler( getLogger() );
        m_factory = getTransformerFactory( m_transformerFactory );
    }

    public void dispose()
    {
        if ( null != m_manager) 
        {
            m_manager.release( m_store );
            m_manager.release( m_resolver );
            m_manager.release( m_xmlizer );
            m_manager = null;
        }
        m_xmlizer = null;
        m_store = null;
        m_resolver = null;
        m_errorHandler = null;
    }

    /**
     * Configure the component
     */
    public void parameterize( final Parameters params )
        throws ParameterException
    {
        m_useStore = params.getParameterAsBoolean( "use-store", this.m_useStore );
        m_incrementalProcessing = params.getParameterAsBoolean( "incremental-processing", this.m_incrementalProcessing );
        m_transformerFactory = params.getParameter( "transformer-factory", null );
        if( !m_useStore )
        {
            // release the store, if we don't need it anymore
            m_manager.release( m_store );
            m_store = null;
        }
        else if( null == m_store )
        {
            final String message =
                "XSLTProcessor: use-store is set to true, " +
                "but unable to aquire the Store.";
            throw new ParameterException( message );
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
            final String id = stylesheet.getURI();
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

                    if( null == template )
                    {
                        throw new XSLTProcessorException(
                            "Unable to create templates for stylesheet: "
                            + stylesheet.getURI() );
                    }

                    putTemplates( template, stylesheet, id );

                    // Create transformer handler
                    final TransformerHandler handler = m_factory.newTransformerHandler( template );
                    handler.getTransformer().setErrorListener( m_errorHandler );
		    handler.getTransformer().setURIResolver( this );

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
            final InputStream inputStream = source.getInputStream();
            final String mimeType = source.getMimeType();
            final String systemId = source.getURI();
            m_xmlizer.toSAX( inputStream, mimeType, systemId, handler );
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
            valid = storedValidity.isValid( newValidity );
            isValid = ( valid == 1 );
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
                        valid = storedValidity.isValid( included );
                        isValid = ( valid == 1 );
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
                // is the base a file or a real m_url
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
                getLogger().debug( "xslSource = " + xslSource + ", system id = " + xslSource.getURI() );
            }

            // Populate included validities
            List includes = (List)m_includesMap.get( base );
            if( includes != null )
            {
                SourceValidity included = xslSource.getValidity();
                if( included != null )
                {
                    includes.add( new Object[]{xslSource.getURI(), xslSource.getValidity()} );
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
        newObject.setSystemId( source.getURI() );
        return newObject;
    }
    
    /**
     * Recycle the component
     */
    public void recycle() 
    {
        m_includesMap.clear();
        m_factory = getTransformerFactory( m_transformerFactory );
    }

}
