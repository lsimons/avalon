/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Policy;
import org.apache.avalon.Initializable;
import org.apache.avalon.atlantis.Facility;
import org.apache.avalon.component.ComponentException;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.component.Composable;
import org.apache.avalon.context.Context;
import org.apache.avalon.context.ContextException;
import org.apache.avalon.context.Contextualizable;
import org.apache.excalibur.io.ExtensionFileFilter;
import org.apache.phoenix.engine.SarContextResources;
import org.apache.phoenix.engine.facilities.PolicyManager;

/**
 * This component creates blocks and blockInfos.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class SarClassLoader
    extends PolicyClassLoader
    implements Facility, Contextualizable, Composable, Initializable
{
    protected File              m_baseDirectory;

    public SarClassLoader()
    {
        super( new URL[ 0 ], Thread.currentThread().getContextClassLoader(), null );
    }

    public void contextualize( final Context context )
        throws ContextException
    {
        m_baseDirectory = (File)context.get( SarContextResources.APP_HOME_DIR );
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        final PolicyManager policyManager = (PolicyManager)componentManager.
            lookup( "org.apache.phoenix.engine.facilities.PolicyManager" );

        m_policy = policyManager.getPolicy();
    }

    public void init()
        throws Exception
    {
        final File blockDir =
            (new File( m_baseDirectory, "blocks" )).getAbsoluteFile();
        final File libDir =
            (new File( m_baseDirectory, "lib" )).getAbsoluteFile();

        addURLs( blockDir, new String[] { ".bar" } );
        addURLs( libDir, new String[] { ".jar", ".zip" } );
    }

    protected void addURLs( final File directory, final String[] extentions )
        throws MalformedURLException
    {
        final ExtensionFileFilter filter = new ExtensionFileFilter( extentions );
        final File[] files = directory.listFiles( filter );
        if( null == files ) return;
        addURLs( files );
    }

    protected void addURLs( final File[] files )
        throws MalformedURLException
    {
        for( int i = 0; i < files.length; i++ )
        {
            addURL( files[ i ].toURL() );
        }
    }

    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( "ClassLoader[" );

        final URL[] urls = getURLs();

        for( int i = 0; i < urls.length; i++ )
        {
            sb.append( ' ' );
            sb.append( urls[ i ] );
        }

        sb.append( " ]" );
        return sb.toString();
    }
}
