/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.assembler.test;

import org.apache.avalon.phoenix.test.AbstractContainerTestCase;
import org.apache.avalon.phoenix.test.data.Component1;
import org.apache.avalon.phoenix.test.data.Component2;
import org.apache.avalon.phoenix.test.data.Component3;
import org.apache.avalon.phoenix.test.data.Service2;
import org.apache.avalon.phoenix.containerkit.registry.PartitionProfile;
import org.apache.avalon.phoenix.containerkit.registry.ComponentProfile;
import org.apache.avalon.phoenix.containerkit.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.components.ContainerConstants;

/**
 *  An basic test case for the LogManager.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.8 $ $Date: 2003/03/01 01:47:10 $
 */
public class AssemblerTestCase
    extends AbstractContainerTestCase
{
    public AssemblerTestCase( final String name )
    {
        super( name );
    }

    public void testBasic()
        throws Exception
    {
        final PartitionProfile partitionProfile = assembleSar( "assembly1.xml" );
        final ComponentProfile[] blocks =
            partitionProfile.getPartition( ContainerConstants.BLOCK_PARTITION ).
            getComponents();
        assertEquals( "Block Count", 2, blocks.length );

        final ComponentProfile block1 = blocks[ 0 ];
        final ComponentProfile block2 = blocks[ 1 ];
        final DependencyMetaData[] dependencies1 = block1.getMetaData().getDependencies();
        final DependencyMetaData[] dependencies2 = block2.getMetaData().getDependencies();

        assertEquals( "Block1 getImplementationKey",
                      Component1.class.getName(),
                      block1.getMetaData().getImplementationKey() );
        assertEquals( "Block1 getName", "c1", block1.getMetaData().getName() );
        assertEquals( "Block1 getDependencies count",
                      1, dependencies1.length );
        assertEquals( "Block1 dep1 name", "c2", dependencies1[ 0 ].getProviderName() );
        assertEquals( "Block1 dep1 role",
                      Service2.class.getName(), dependencies1[ 0 ].getKey() );
        assertTrue( "Block1 getBlockInfo non null",
                    null != block1.getInfo() );
        assertEquals( "Block1 isDisableProxy", false, isProxyDisabled( block1 ) );

        assertEquals( "Block2 getImplementationKey",
                      Component2.class.getName(),
                      block2.getMetaData().getImplementationKey() );
        assertEquals( "Block2 getName", "c2", block2.getMetaData().getName() );
        assertEquals( "Block2 getDependencies count",
                      0, dependencies2.length );
        assertTrue( "Block2 getBlockInfo non null",
                    null != block2.getInfo() );
        assertEquals( "Block2 isDisableProxy", true, isProxyDisabled( block2 ) );
    }

    private boolean isProxyDisabled( final ComponentProfile block2 )
    {
        return block2.getMetaData().getAttribute( ContainerConstants.DISABLE_PROXY_ATTR ) != null;
    }

    public void testComplex()
        throws Exception
    {
        final PartitionProfile partitionProfile = assembleSar( "assembly2.xml" );
        final ComponentProfile[] blocks =
            partitionProfile.getPartition( ContainerConstants.BLOCK_PARTITION ).getComponents();
        assertEquals( "Block Count", 4, blocks.length );

        final ComponentProfile block1 = blocks[ 0 ];
        final ComponentProfile block2 = blocks[ 1 ];
        final ComponentProfile block3 = blocks[ 2 ];
        final ComponentProfile block4 = blocks[ 3 ];
        final DependencyMetaData[] dependencies1 = block1.getMetaData().getDependencies();
        final DependencyMetaData[] dependencies2 = block2.getMetaData().getDependencies();
        final DependencyMetaData[] dependencies3 = block3.getMetaData().getDependencies();
        final DependencyMetaData[] dependencies4 = block4.getMetaData().getDependencies();

        assertEquals( "Block1 getImplementationKey",
                      Component2.class.getName(),
                      block1.getMetaData().getImplementationKey() );
        assertEquals( "Block1 getName", "c2a", block1.getMetaData().getName() );
        assertEquals( "Block1 getDependencies count",
                      0, dependencies1.length );
        assertTrue( "Block2 getBlockInfo non null",
                    null != block1.getInfo() );
        assertEquals( "Block1 isDisableProxy", false, isProxyDisabled( block1 ) );

        assertEquals( "Block2 getImplementationKey",
                      Component2.class.getName(),
                      block2.getMetaData().getImplementationKey() );
        assertEquals( "Block2 getName", "c2b", block2.getMetaData().getName() );
        assertEquals( "Block2 getDependencies count",
                      0, dependencies2.length );
        assertTrue( "Block2 getBlockInfo non null",
                    null != block2.getInfo() );
        assertEquals( "Block2 isDisableProxy", false, isProxyDisabled( block2 ) );

        assertEquals( "Block3 getImplementationKey",
                      Component2.class.getName(),
                      block3.getMetaData().getImplementationKey() );
        assertEquals( "Block3 getName", "c2c", block3.getMetaData().getName() );
        assertEquals( "Block3 getDependencies count",
                      0, dependencies3.length );
        assertTrue( "Block3 getBlockInfo non null",
                    null != block3.getInfo() );
        assertEquals( "Block3 isDisableProxy", false, isProxyDisabled( block3 ) );

        assertEquals( "Block4 getImplementationKey",
                      Component3.class.getName(),
                      block4.getMetaData().getImplementationKey() );
        assertEquals( "Block4 getName", "c3", block4.getMetaData().getName() );
        assertEquals( "Block4 getDependencies count",
                      3, dependencies4.length );
        final DependencyMetaData dependency1 = dependencies4[ 0 ];
        final DependencyMetaData dependency2 = dependencies4[ 1 ];
        final DependencyMetaData dependency3 = dependencies4[ 2 ];
        assertEquals( "Block4 dep1 name", "c2a", dependency1.getProviderName() );
        assertEquals( "Block4 dep1 role",
                      Service2.class.getName(), dependency1.getKey() );
        assertEquals( "Block4 dep1 name", "c2b", dependency2.getProviderName() );
        assertEquals( "Block4 dep1 role",
                      Service2.class.getName(), dependency2.getKey() );
        assertEquals( "Block4 dep1 name", "c2c", dependency3.getProviderName() );
        assertEquals( "Block4 dep1 role",
                      Service2.class.getName(), dependency3.getKey() );
        assertTrue( "Block4 getBlockInfo non null",
                    null != block4.getInfo() );
        assertEquals( "Block4 isDisableProxy", false, isProxyDisabled( block4 ) );
    }
}
