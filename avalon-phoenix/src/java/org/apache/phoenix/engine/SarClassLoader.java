/* 
 * Copyright (C) The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */
package org.apache.phoenix.engine;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Policy;
import org.apache.avalon.Component;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.Initializable;
import org.apache.avalon.util.io.ExtensionFileFilter;
import org.apache.avalon.util.security.PolicyClassLoader;

/**
 * This component creates blocks and blockInfos.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class SarClassLoader
    extends PolicyClassLoader
    implements Component, Contextualizable, Composer, Initializable
{
    protected File              m_baseDirectory;
    protected String            m_blocksDirectory;
    protected String            m_libDirectory;

    public SarClassLoader()
    {
        super( new URL[ 0 ], Thread.currentThread().getContextClassLoader(), null );
    }

    public void contextualize( final Context context )
    {
        m_baseDirectory = (File)context.get( SarContextResources.APP_HOME_DIR );
        m_blocksDirectory = (String)context.get( SarContextResources.APP_BLOCKS_DIR );
        m_libDirectory = (String)context.get( SarContextResources.APP_LIB_DIR );
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
    {
        m_policy = (Policy)componentManager.lookup( "java.security.Policy" );
    }

    public void init()
        throws Exception
    {
        final File blockDir = 
            (new File( m_baseDirectory, m_blocksDirectory )).getAbsoluteFile();
        final File libDir = 
            (new File( m_baseDirectory, m_libDirectory )).getAbsoluteFile();
        
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
}
