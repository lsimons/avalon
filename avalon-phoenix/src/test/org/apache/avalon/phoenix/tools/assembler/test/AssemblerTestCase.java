/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.assembler.test;

import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.metadata.InterceptorMetaData;
import org.apache.avalon.phoenix.test.AbstractContainerTestCase;
import org.apache.avalon.phoenix.test.data.Component1;
import org.apache.avalon.phoenix.test.data.Component2;
import org.apache.avalon.phoenix.test.data.Component3;
import org.apache.avalon.phoenix.test.data.Interceptor1;
import org.apache.avalon.phoenix.test.data.Interceptor2;
import org.apache.avalon.phoenix.test.data.Service2;
import org.apache.avalon.phoenix.tools.assembler.AssemblyException;

/**
 *  An basic test case for the LogManager.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.6.4.2 $ $Date: 2002/10/20 01:00:15 $
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

        assertEquals( "Block2 getImplementationKey",
                      Component2.class.getName(),
                      block2.getImplementationKey() );
        assertEquals( "Block2 getName", "c2", block2.getName() );
        assertEquals( "Block2 getDependencies count",
                      0, dependencies2.length );
        assertTrue( "Block2 getBlockInfo non null",
                       null != block2.getBlockInfo() );
        assertEquals( "Block2 isDisableProxy", true, block2.isDisableProxy() );
    }

    public void testComplex()
        throws Exception
    {
        final SarMetaData sarMetaData = assembleSar( "assembly2.xml" );
        final BlockMetaData[] blocks = sarMetaData.getBlocks();
        assertEquals( "Block Count", 4, blocks.length );

        final BlockMetaData block1 = blocks[ 0 ];
        final BlockMetaData block2 = blocks[ 1 ];
        final BlockMetaData block3 = blocks[ 2 ];
        final BlockMetaData block4 = blocks[ 3 ];
        final DependencyMetaData[] dependencies1 = block1.getDependencies();
        final DependencyMetaData[] dependencies2 = block2.getDependencies();
        final DependencyMetaData[] dependencies3 = block3.getDependencies();
        final DependencyMetaData[] dependencies4 = block4.getDependencies();

        assertEquals( "Block1 getImplementationKey",
                      Component2.class.getName(),
                      block1.getImplementationKey() );
        assertEquals( "Block1 getName", "c2a", block1.getName() );
        assertEquals( "Block1 getDependencies count",
                      0, dependencies1.length );
        assertTrue( "Block2 getBlockInfo non null",
                       null != block1.getBlockInfo() );
        assertEquals( "Block1 isDisableProxy", false, block1.isDisableProxy() );

        assertEquals( "Block2 getImplementationKey",
                      Component2.class.getName(),
                      block2.getImplementationKey() );
        assertEquals( "Block2 getName", "c2b", block2.getName() );
        assertEquals( "Block2 getDependencies count",
                      0, dependencies2.length );
        assertTrue( "Block2 getBlockInfo non null",
                       null != block2.getBlockInfo() );
        assertEquals( "Block2 isDisableProxy", false, block2.isDisableProxy() );

        assertEquals( "Block3 getImplementationKey",
                      Component2.class.getName(),
                      block3.getImplementationKey() );
        assertEquals( "Block3 getName", "c2c", block3.getName() );
        assertEquals( "Block3 getDependencies count",
                      0, dependencies3.length );
        assertTrue( "Block3 getBlockInfo non null",
                       null != block3.getBlockInfo() );
        assertEquals( "Block3 isDisableProxy", false, block3.isDisableProxy() );

        assertEquals( "Block4 getImplementationKey",
                      Component3.class.getName(),
                      block4.getImplementationKey() );
        assertEquals( "Block4 getName", "c3", block4.getName() );
        assertEquals( "Block4 getDependencies count",
                      3, dependencies4.length );
        final DependencyMetaData dependency1 = dependencies4[ 0 ];
        final DependencyMetaData dependency2 = dependencies4[ 1 ];
        final DependencyMetaData dependency3 = dependencies4[ 2 ];
        assertEquals( "Block4 dep1 name", "c2a", dependency1.getName() );
        assertEquals( "Block4 dep1 role",
                      Service2.class.getName(), dependency1.getRole() );
        assertEquals( "Block4 dep1 name", "c2b", dependency2.getName() );
        assertEquals( "Block4 dep1 role",
                      Service2.class.getName(), dependency2.getRole() );
        assertEquals( "Block4 dep1 name", "c2c", dependency3.getName() );
        assertEquals( "Block4 dep1 role",
                      Service2.class.getName(), dependency3.getRole() );
        assertTrue( "Block4 getBlockInfo non null",
                       null != block4.getBlockInfo() );
        assertEquals( "Block4 isDisableProxy", false, block4.isDisableProxy() );
    }

    public void testInterceptorBasic()
        throws Exception
    {
        final SarMetaData sarMetaData = assembleSar( "assembly3.xml" );
        final BlockMetaData[] blocks = sarMetaData.getBlocks();
        assertEquals( "Block Count", 2, blocks.length );

        final BlockMetaData block1 = blocks[ 0 ];
        final BlockMetaData block2 = blocks[ 1 ];
        final InterceptorMetaData[] interceptor1 = block1.getInterceptors();
        final InterceptorMetaData[] interceptor2 = block2.getInterceptors();
        
        assertEquals( "Block1 Interceptor count", 1, interceptor1.length );
        assertEquals( "Block1 Interceptor class",
                      Interceptor1.class.getName(),
                      interceptor1[ 0 ].getClassname() );
       
        assertEquals( "Block2 Interceptor count", 2, interceptor2.length );
        assertEquals( "Block2 Interceptor1 class",
                      Interceptor1.class.getName(),
                      interceptor2[ 0 ].getClassname() );
        assertEquals( "Block2 Interceptor2 class",
                      Interceptor2.class.getName(),
                      interceptor2[ 1 ].getClassname() );
    }

    public void testInterceptorInvalid()
        throws Exception
    {
        try 
        {
            /*final SarMetaData sarMetaData =*/ assembleSar( "assembly4.xml" );
            fail( "Invalid assemble.xml did not fail" );
        }
        catch ( AssemblyException expected )
        {
        }
    }
}
