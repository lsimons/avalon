/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.assembler.test;

import java.io.File;
import java.net.URL;
import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.tools.assembler.Assembler;
import org.apache.avalon.phoenix.tools.assembler.test.data.Component1;
import org.apache.avalon.phoenix.tools.assembler.test.data.Service2;
import org.apache.avalon.phoenix.tools.configuration.ConfigurationBuilder;

/**
 *  An basic test case for the LogManager.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/09/30 23:24:02 $
 */
public class AssemblerTestCase
    extends TestCase
{
    public static final String DEFAULT_LOGFILE = "logs/default.log";
    public static final String BLOCK_LOGFILE = "logs/myBlock.log";

    private File m_baseDirectory;

    public AssemblerTestCase( final String name )
    {
        super( name );
    }

    protected void setUp() throws Exception
    {
        m_baseDirectory = new File( "." );
    }

    public void testBasic()
        throws Exception
    {
        final SarMetaData sarMetaData = assembleSar( "assembly1.xml" );
        final BlockMetaData[] blocks = sarMetaData.getBlocks();
        assertEquals( "Block Count", 2, blocks.length );

        final BlockMetaData block1 = blocks[ 0 ];
        final BlockMetaData block2 = blocks[ 1 ];
        final DependencyMetaData[] dependencies1 = block1.getDependencies();
        final DependencyMetaData[] dependencies2 = block2.getDependencies();

        assertEquals( "Block1 getImplementationKey",
                      Component1.class.getName(),
                      block1.getImplementationKey() );
        assertEquals( "Block1 getName", "c1", block1.getName() );
        assertEquals( "Block1 getDependencies count",
                      1, dependencies1.length );
        assertEquals( "Block1 dep1 name", "c2", dependencies1[ 0 ].getName() );
        assertEquals( "Block1 dep1 role",
                      Service2.class.getName(), dependencies1[ 0 ].getRole() );
        assertTrue( "Block1 getBlockInfo non null",
                       null != block1.getBlockInfo() );
        assertEquals( "Block1 isDisableProxy", false, block1.isDisableProxy() );
    }

    private SarMetaData assembleSar( final String config ) throws Exception
    {
        final Assembler assembler = new Assembler();
        assembler.enableLogging( new ConsoleLogger() );
        final ClassLoader classLoader = getClass().getClassLoader();
        final Configuration assembly = loadConfig( config );
        return assembler.assembleSar( "test", assembly, m_baseDirectory, classLoader );
    }

    private Configuration loadConfig( final String config )
        throws Exception
    {
        final URL resource = getClass().getResource( config );
        return ConfigurationBuilder.build( resource.toExternalForm() );
    }
}
