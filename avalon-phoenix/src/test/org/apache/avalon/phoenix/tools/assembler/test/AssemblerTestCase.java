/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

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
