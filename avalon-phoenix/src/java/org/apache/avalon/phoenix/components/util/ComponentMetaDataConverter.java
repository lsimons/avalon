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

package org.apache.avalon.phoenix.components.util;

import java.io.File;
import java.util.ArrayList;
import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;
import org.apache.avalon.phoenix.interfaces.ContainerConstants;
import org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData;
import org.apache.avalon.phoenix.containerkit.profile.ComponentProfile;
import org.apache.avalon.phoenix.containerkit.profile.PartitionProfile;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;

/**
 * Convert a {@link ComponentMetaData} into a {@link BlockMetaData}.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.9 $ $Date: 2003/03/23 00:19:49 $
 */
public class ComponentMetaDataConverter
{
    private ComponentMetaDataConverter()
    {
    }

    public static SarMetaData toSarMetaData( final PartitionProfile partition,
                                             final File homeDirectory )
    {
        final PartitionProfile blockPartition =
            partition.getPartition( ContainerConstants.BLOCK_PARTITION );
        final PartitionProfile listenerPartition =
            partition.getPartition( ContainerConstants.LISTENER_PARTITION );
        final BlockListenerMetaData[] listeners =
            toBlockListeners( listenerPartition.getMetaData().getComponents() );
        final BlockMetaData[] blocks =
            toBlocks( blockPartition.getComponents() );
        return new SarMetaData( partition.getMetaData().getName(),
                                homeDirectory,
                                blocks,
                                listeners );
    }

    /**
     * Convert a set of {@link ComponentProfile} object
     * into a set of {@link BlockMetaData} objects.
     *
     * @param components the {@link ComponentProfile} objects
     * @return the {@link BlockMetaData} objects
     */
    private static BlockMetaData[] toBlocks( final ComponentProfile[] components )
    {
        final ArrayList listenerSet = new ArrayList();
        for( int i = 0; i < components.length; i++ )
        {
            final ComponentProfile component = components[ i ];
            final BlockMetaData block =
                toBlockMetaData( component.getMetaData(), component.getInfo() );
            listenerSet.add( block );
        }
        return (BlockMetaData[])listenerSet.toArray( new BlockMetaData[ listenerSet.size() ] );
    }

    /**
     * Convert a {@link ComponentMetaData} object into a {@link BlockListenerMetaData} object.
     *
     * @param listeners the {@link ComponentMetaData} object
     * @return the {@link BlockListenerMetaData} object
     */
    public static BlockListenerMetaData[] toBlockListeners( final ComponentMetaData[] listeners )
    {
        final ArrayList listenerSet = new ArrayList();
        for( int i = 0; i < listeners.length; i++ )
        {
            final BlockListenerMetaData listener =
                new BlockListenerMetaData( listeners[ i ].getName(),
                                           listeners[ i ].getImplementationKey() );
            listenerSet.add( listener );
        }
        return (BlockListenerMetaData[])listenerSet.toArray( new BlockListenerMetaData[ listenerSet.size() ] );
    }

    /**
     * Convert a {@link ComponentMetaData} object into a {@link BlockMetaData} object.
     *
     * @param component the {@link ComponentMetaData} object
     * @return the {@link BlockMetaData} object
     */
    public static BlockMetaData toBlockMetaData( final ComponentMetaData component,
                                                 final ComponentInfo info )
    {
        final boolean disableProxy = isDisableProxy( component );
        final DependencyMetaData[] dependencies =
            toPhoenixDependencys( component.getDependencies() );

        final BlockInfo blockInfo = ComponentInfoConverter.toBlockInfo( info );
        return new BlockMetaData( component.getName(),
                                  dependencies,
                                  disableProxy,
                                  blockInfo );
    }

    /**
     * Determine whether the proxy disabled flag should be
     * true for specified component.
     *
     * @param component the component
     * @return true if proxy should be disabled, false otherwise
     */
    private static boolean isDisableProxy( final ComponentMetaData component )
    {
        final Attribute[] attributes = component.getAttributes();
        for( int i = 0; i < attributes.length; i++ )
        {
            final Attribute attribute = attributes[ i ];
            if( attribute.getName().equals( ContainerConstants.DISABLE_PROXY_ATTR ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert ContainerKit dependencys to Phoenix dependencys.
     *
     * @param dependencies the ContainerKit dependencys
     * @return the Phoenix dependencys
     */
    private static DependencyMetaData[] toPhoenixDependencys(
        final org.apache.avalon.phoenix.containerkit.metadata.DependencyMetaData[] dependencies )
    {
        final ArrayList depends = new ArrayList();
        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyMetaData dependency =
                new DependencyMetaData( dependencies[ i ].getProviderName(),
                                        dependencies[ i ].getKey(),
                                        dependencies[ i ].getAlias() );
            depends.add( dependency );
        }
        return (DependencyMetaData[])depends.toArray( new DependencyMetaData[ depends.size() ] );
    }
}




