/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Enumeration;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.log.Logger;

/**
 * Classloader that uses a specified <code>Policy</code> object
 * rather than system <code>Policy</code> object.
 *
 * <p>Note that parts of this were cloned from other projects</p>
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
class PolicyClassLoader
    extends URLClassLoader
    implements Loggable
{
    ///Policy to use to define permissions for classes loaded in classloader
    private final Policy  m_policy;

    ///Factory that used to create URLStreamHandlers
    private final URLStreamHandlerFactory m_factory;

    ///Logger to use when reporting information
    private Logger        m_logger;

    /**
     * Construct a ClassLoader using specified URLs, parent
     * ClassLoader and Policy object.
     *
     * @param urls the URLs to load resources from
     * @param classLoader the parent ClassLoader
     * @param policy the Policy object
     */
    PolicyClassLoader( final URL[] urls,
                       final ClassLoader parent,
                       final URLStreamHandlerFactory factory,
                       final Policy policy )
        throws MalformedURLException
    {
        super( urls, parent, factory );

        if( null == policy )
        {
            throw new NullPointerException( "policy" );
        }
        m_policy = policy;
        m_factory = factory;
    }

    public void setLogger( final Logger logger )
    {
        m_logger = logger;
    }

    protected final Logger getLogger()
    {
        return m_logger;
    }

    /**
     * Overide so we can have a per-application security policy with
     * no side-effects to other applications.
     *
     * @param codeSource the codeSource to get permissions for
     * @return the PermissionCollection
     */
    protected PermissionCollection getPermissions( final CodeSource codeSource )
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "getPermissions(" + codeSource + ")" );
            //getLogger().debug( "= " + m_policy.getPermissions( codeSource ) );
        }
        return m_policy.getPermissions( codeSource );
    }

    /**
     * Return an enumeration of <code>URLs</code> representing all of the
     * resources with the given name. If no resources with this name are found,
     * return an empty enumeration.
     *
     * <p>Note that this method is overidden to provide debugging
     * information.</p>
     *
     * @param name the name of resource to look for
     * @return the Enumeration of resources
     * @exception IOException if an input/output error occurs
     */
    public Enumeration findResources( final String name )
        throws IOException
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "findResources(" + name + ")" );
        }

        return super.findResources( name );
    }

    /**
     * Find the resource in the ClassLoader. Return a <code>URL</code>
     * object if found, otherwise return null if this resource cannot
     * be found.
     *
     * <p>Note that this method is overidden to provide debugging
     * information.</p>
     *
     * @param name the name of resource to look for
     * @return the URL if found, else null
     */
    public URL findResource( final String name )
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "findResource(" + name + ")" );
        }

        final URL url = super.findResource( name );

        if( getLogger().isDebugEnabled() )
        {
            if( null != url )
            {
                getLogger().debug( "Resource " + name + " located (" + url + ")" );
            }
            else
            {
                getLogger().debug( "Resource " + name + " not located" );
            }
        }

        return url;
    }

    /**
     * Create an array of URL objects from strings, using specified URLHandlerFactory.
     *
     * @param classPath the string representation of urls
     * @return the URL array
     * @exception MalformedURLException if an error occurs
     */
    private URL[] createURLs( final String[] classPath )
        throws MalformedURLException
    {
        final ArrayList urls = new ArrayList();

        for( int i = 0; i < classPath.length; i++ )
        {
            final URL url = createURL( classPath[ i ] );
            urls.add( url );
        }

        //System.out.println( "Created urls: " + urls );
        return (URL[])urls.toArray( new URL[ 0 ] );
    }

    /**
     * Utility method to create a URL from string representation
     * using our <code>URLStreamHandlerFactory</code> object.
     *
     * @param url the string representation of URL
     * @exception MalformedURLException if URL is badly formed or
     *            protocol can not be found
     */
    private URL createURL( final String url )
        throws MalformedURLException
    {
        if( null == url )
        {
            throw new NullPointerException( "url" );
        }

        final String scheme = parseScheme( url );
        final URLStreamHandler handler = createHandler( scheme );

        return new URL( null, url, handler );
    }

    /**
     * Create a URLStreamHandler for protocol if it needs one.
     *
     * @param scheme the scheme/protocol to create handler for
     * @return the created URLStreamHandler or null
     * @exception MalformedURLException if an error occurs
     */
    private URLStreamHandler createHandler( final String scheme )
        throws MalformedURLException
    {
        if( null != m_factory )
        {
            return m_factory.createURLStreamHandler( scheme );
        }
        else
        {
            return null;
        }
    }

    /**
     * Utility method to parse out the scheme of a URL.
     *
     * @param url the full string representation of url
     * @return the scheme part of URL
     * @exception MalformedURLException if an error occurs
     */
    private String parseScheme( final String url )
        throws MalformedURLException
    {
        final int index = url.indexOf( ':' );
        if( -1 == index )
        {
            throw new MalformedURLException( "No scheme specified for url " + url );
        }

        return url.substring( 0, index );
    }
}
