/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;

/**
 * Classloader that uses a specified <code>Policy</code> object
 * rather than system <code>Policy</code> object.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
class PolicyClassLoader
    extends URLClassLoader
{
    ///Policy to use to define permissions for classes loaded in classloader
    private final Policy  m_policy;

    ///Factory to use when creating URLs
    private final URLStreamHandlerFactory m_factory;

    /**
     * Construct a ClassLoader using specified URLs, parent
     * ClassLoader and Policy object.
     *
     * @param urls the URLs to load resources from
     * @param classLoader the parent ClassLoader
     * @param policy the Policy object
     */
    PolicyClassLoader( final String[] urls,
                       final ClassLoader classLoader,
                       final URLStreamHandlerFactory factory,
                       final Policy policy )
        throws MalformedURLException
    {
        super( new URL[ 0 ], classLoader, factory );

        if( null == policy )
        {
            throw new NullPointerException( "policy" );
        }

        if( null == factory )
        {
            throw new NullPointerException( "factory" );
        }

        m_policy = policy;
        m_factory = factory;

        if( 0 != urls.length ) 
        {
            for( int i = 0; i < urls.length; i++ )
            {
                final URL url = createURL( urls[ i ] );
                addURL( url );
            }
        }

        System.out.println( "ClassPath: " + java.util.Arrays.asList( getURLs() ) );
    }
            
    /**
     * Utility method to create a URL from string representation 
     * using our <code>URLStreamHandlerFactory</code> object.
     *
     * @param urlString the string representation of URL
     * @exception MalformedURLException if URL is badly formed or 
     *            protocol can not be found
     */
    private URL createURL( final String urlString )
        throws MalformedURLException
    {
        if( null == urlString )
        {
            throw new NullPointerException( "url" );
        }
        
        final int index = urlString.indexOf( ':' );
        if( -1 == index )
        {
            throw new MalformedURLException( "No scheme specified for url " + urlString );
        }

        final String scheme = urlString.substring( 0, index );
        final URLStreamHandler handler = m_factory.createURLStreamHandler( scheme );
        return new URL( null, urlString, handler );
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
        return m_policy.getPermissions( codeSource );
    }
}
