/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application.test;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.containerkit.registry.PartitionProfile;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.excalibur.threadcontext.ThreadContext;
import org.apache.excalibur.threadcontext.impl.DefaultThreadContextPolicy;

/**
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.5 $ $Date: 2003/02/28 23:47:46 $
 */
class MockApplicationContext
    implements ApplicationContext
{
    private final ThreadContext m_threadContext = new ThreadContext( new DefaultThreadContextPolicy(), new HashMap() );
    private final PartitionProfile m_sarMetaData;
    private final Logger m_logger;

    public MockApplicationContext( final PartitionProfile sarMetaData,
                                   final Logger logger )
    {
        m_sarMetaData = sarMetaData;
        m_logger = logger;
    }

    public PartitionProfile getPartitionProfile()
    {
        return m_sarMetaData;
    }

    public ThreadContext getThreadContext()
    {
        return m_threadContext;
    }

    public void requestShutdown()
    {
        //ignore
    }

    public void exportObject( String name, Class[] interfaceClasses, Object object )
        throws Exception
    {
        //ignore
    }

    public void unexportObject( String name )
        throws Exception
    {
        //ignore
    }

    public ClassLoader getClassLoader()
    {
        return getClass().getClassLoader();
    }

    public InputStream getResourceAsStream( final String name )
    {
        return getClassLoader().getResourceAsStream( name );
    }

    public Configuration getConfiguration( String component )
        throws ConfigurationException
    {
        throw new ConfigurationException( "I can't do that dave!" );
    }

    public File getHomeDirectory()
    {
        return new File( "." );
    }

    public ClassLoader getClassLoader( String name )
        throws Exception
    {
        throw new Exception( "I can't do that dave!" );
    }

    public Logger getLogger( String name )
    {
        return m_logger;
    }
}
