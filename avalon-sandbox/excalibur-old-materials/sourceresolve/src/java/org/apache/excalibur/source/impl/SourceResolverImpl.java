/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.source.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.apache.avalon.excalibur.pool.Recyclable;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.*;

/**
 * Base interface for resolving a source by system identifiers.
 * Instead of using the java.net.URL classes which prevent you
 * to add your own custom protocols in a server environment,
 * you should use this resolver for all URLs.
 *
 * The resolver creates for each source a <code>Source</code>
 * object, which could then be asked for an <code>InputStream</code>
 * etc.
 *
 * When the <code>Source</code> object is no longer needed
 * it must be released using the resolver. This is very similar like
 * looking up components from a <code>ComponentManager</code>
 * and releasing them.
 *
 * It looks for the base URL in the <code>Context</code> object with
 * the "container.rootDir" entry.  If the entry does not exist, it is
 * populated with the system property "user.dir".
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: SourceResolverImpl.java,v 1.4 2002/05/01 09:11:47 donaldp Exp $
 */
public class SourceResolverImpl
    extends AbstractLogEnabled
    implements Composable,
    Contextualizable,
    Disposable,
    Parameterizable,
    SourceResolver,
    ThreadSafe
{

    /** The component manager */
    protected ComponentManager m_manager;

    /** The special Source factories */
    protected ComponentSelector m_factorySelector;

    /** The context */
    protected Context m_context;

    /**
     * The base URL
     */
    protected URL m_baseURL;

    /** The URLSource class used */
    protected Class m_urlSourceClass;

    /**
     * Get the context
     */
    public void contextualize( Context context )
        throws ContextException
    {
        m_context = context;

        try
        {
            m_baseURL = ( (File)m_context.get( "context-root" ) ).toURL();
        }
        catch( ContextException ce )
        {
            // set the base URL to the current directory
            try
            {
                m_baseURL = new File( System.getProperty( "user.dir" ) ).toURL();
                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "SourceResolver: Using base URL: " + m_baseURL );
                }
            }
            catch( MalformedURLException mue )
            {
                getLogger().warn( "Malformed URL for user.dir, and no container.rootDir exists", mue );
                throw new ContextException( "Malformed URL for user.dir, and no container.rootDir exists", mue );
            }
        }
        catch( MalformedURLException mue )
        {
            getLogger().warn( "Malformed URL for container.rootDir", mue );
            throw new ContextException( "Malformed URL for container.rootDir", mue );
        }
    }

    /**
     * Set the current <code>ComponentManager</code> instance used by this
     * <code>Composable</code>.
     */
    public void compose( ComponentManager manager )
        throws ComponentException
    {
        m_manager = manager;
        m_factorySelector = (ComponentSelector)m_manager.lookup( SourceFactory.ROLE + "Selector" );
    }

    /**
     * Dispose
     */
    public void dispose()
    {
        if( m_manager != null )
        {
            m_manager.release( m_factorySelector );
            m_factorySelector = null;
        }
    }

    /**
     * Configure
     */
    public void parameterize(Parameters pars)
    throws ParameterException
    {
        final String urlSourceClassName = pars.getParameter("url-source",
                         "org.apache.excalibur.source.impl.URLSource");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if( loader == null )
        {
            loader = this.getClass().getClassLoader();
        }
        try
        {
            this.m_urlSourceClass = loader.loadClass( urlSourceClassName );
        }
        catch (ClassNotFoundException cnfe)
        {
            this.getLogger().error("Class not found: " + urlSourceClassName, cnfe);
            throw new ParameterException("Class not found: " + urlSourceClassName, cnfe);
        }
    }

    /**
     * Get a <code>Source</code> object.
     */
    public Source resolveURI( String location )
        throws MalformedURLException, IOException, SourceException
    {
        return this.resolveURI( location, null, null );
    }

    /**
     * Get a <code>Source</code> object.
     */
    public Source resolveURI( String location,
                              String baseURI,
                              Map parameters )
        throws MalformedURLException, IOException, SourceException
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Resolving '" + location + "' with base '" + baseURI + "' in context '" + m_baseURL + "'" );
        }
        if( location == null ) throw new MalformedURLException( "Invalid System ID" );
        if( null != baseURI && baseURI.indexOf( ':' ) == -1 )
        {
            throw new MalformedURLException( "BaseURI is not valid, it must contain a protocol: " + baseURI );
        }

        // first step: create systemID
        String systemID;

        if( baseURI == null ) baseURI = m_baseURL.toExternalForm();

        if( location.length() == 0 )
        {
            systemID = baseURI;
        }
        else if( location.charAt( 0 ) == '/' )
        {
            // windows: absolute paths can start with drive letter
            if( location.length() > 2 && location.charAt( 2 ) == ':' )
            {
                systemID = new StringBuffer( "file:" ).append( location ).toString();
            }
            else
            {
                final int protocolEnd = baseURI.indexOf( ':' );
                systemID = new StringBuffer( baseURI.substring( protocolEnd + 1 ) )
                    .append( location ).toString();
            }
        }
        else if( location.indexOf( ":" ) > 1 )
        {
            systemID = location;
        }
        // windows: absolute paths can start with drive letter
        else if( location.length() > 1 && location.charAt( 1 ) == ':' )
        {
            systemID = new StringBuffer( "file:/" ).append( location ).toString();
        }
        else
        {
            if( baseURI.startsWith( "file:" ) == true )
            {
                File temp = new File( baseURI.substring( "file:".length() ), location );
                String path = temp.getAbsolutePath();
                // windows paths starts with drive letter
                if( path.charAt( 0 ) != File.separator.charAt( 0 ) )
                {
                    systemID = "file:/" + path;
                }
                else
                {
                    systemID = "file:" + path;
                }
            }
            else
            {
                final StringBuffer buffer = new StringBuffer( baseURI );
                if( !baseURI.endsWith( "/" ) ) buffer.append( '/' );
                buffer.append( location );
                systemID = buffer.toString();
            }
        }
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Resolved to systemID '" + systemID + "'" );
        }

        Source source = null;
        // search for a SourceFactory implementing the protocol
        final int protocolPos = systemID.indexOf( ':' );
        if( protocolPos != -1 )
        {
            final String protocol = systemID.substring( 0, protocolPos );
            if( m_factorySelector.hasComponent( protocol ) )
            {
                SourceFactory factory = null;
                try
                {
                    factory = (SourceFactory)m_factorySelector.select( protocol );
                    source = factory.getSource( systemID, parameters );
                }
                catch (ComponentException ce)
                {
                    throw new SourceException("ComponentException.", ce);
                }
                finally
                {
                    m_factorySelector.release( factory );
                }
            }
        }

        if( null == source )
        {
            // no factory found, so usual url handling stuff...
            try
            {
                if( getLogger().isDebugEnabled() == true )
                {
                    this.getLogger().debug( "Making URL from " + systemID );
                }
                try
                {
                    final URLSource urlSource =
                             (URLSource)this.m_urlSourceClass.newInstance();
                    urlSource.init( new URL( systemID ), parameters );
                    source = urlSource;
                }
                catch( MalformedURLException mue )
                {
                    throw mue;
                }
                catch (Exception ie)
                {
                    throw new SourceException("Unable to create new instance of " +
                                                 this.m_urlSourceClass, ie);
                }
            }
            catch( MalformedURLException mue )
            {
                if( getLogger().isDebugEnabled() )
                {
                    this.getLogger().debug( "Making URL - MalformedURLException in getURL:", mue );
                    this.getLogger().debug( "Making URL a File (assuming that it is full path):" + systemID );
                }
                try
                {
                    final URLSource urlSource =
                             (URLSource)this.m_urlSourceClass.newInstance();
                    urlSource.init( ( new File( systemID ) ).toURL(), parameters );
                    source = urlSource;
                }
                catch (Exception ie)
                {
                    throw new SourceException("Unable to create new instance of " +
                                                 this.m_urlSourceClass, ie);
                }
            }
        }
        if( source instanceof LogEnabled )
        {
            ( (LogEnabled)source ).enableLogging( getLogger() );
        }
        if( source instanceof Contextualizable )
        {
            try
            {
                ( (Contextualizable)source ).contextualize( m_context );
            }
            catch( ContextException ce )
            {
                throw new SourceException( "ContextException occured during source resolving.", ce );
            }
        }

        if( source instanceof Composable )
        {
            try
            {
                ( (Composable)source ).compose( m_manager );
            }
            catch( ComponentException ce )
            {
                throw new SourceException( "ComponentException occured during source resolving.", ce );
            }
        }
        return source;
    }

    /**
     * Releases a resolved resource
     */
    public void release( Source source )
    {
        if( source == null ) return;
        if( source instanceof Recyclable )
        {
            ( (Recyclable)source ).recycle();
        }
        if( source instanceof Disposable )
        {
            ( (Disposable)source ).dispose();
        }
    }

}
