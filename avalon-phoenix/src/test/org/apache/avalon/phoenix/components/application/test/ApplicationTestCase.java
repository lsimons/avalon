/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import junit.framework.TestCase;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.components.logger.DefaultLogManager;
import org.apache.avalon.phoenix.components.application.DefaultApplication;
import org.apache.avalon.phoenix.interfaces.LogManager;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.test.AbstractContainerTestCase;
import org.apache.excalibur.threadcontext.ThreadContext;
import org.apache.excalibur.threadcontext.impl.DefaultThreadContextPolicy;

/**
 *  An basic test case for the LogManager.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/10/01 06:19:50 $
 */
public class ApplicationTestCase
    extends AbstractContainerTestCase
{
    public ApplicationTestCase( final String name )
    {
        super( name );
    }

    public void testBasic()
        throws Exception
    {
        runApplicationTest( "assembly1.xml" );
    }

    public void testComplex()
        throws Exception
    {
        runApplicationTest( "assembly2.xml" );
    }

    private void runApplicationTest( final String config )
        throws Exception
    {
        final SarMetaData sarMetaData = assembleSar( config );
        runApplicationTest( sarMetaData );
    }

    private void runApplicationTest( final SarMetaData sarMetaData )
        throws Exception
    {
        final DefaultApplication application = new DefaultApplication();
        application.enableLogging( new ConsoleLogger() );
        final MockApplicationContext context =
            new MockApplicationContext( sarMetaData, new ConsoleLogger() );
        application.setApplicationContext( context );
        application.initialize();
        application.start();
        application.stop();
        application.dispose();
    }
}
