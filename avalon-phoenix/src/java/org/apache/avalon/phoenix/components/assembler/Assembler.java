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

package org.apache.avalon.phoenix.components.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.interfaces.ContainerConstants;
import org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData;
import org.apache.avalon.phoenix.containerkit.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.containerkit.metadata.MetaDataBuilder;
import org.apache.avalon.phoenix.containerkit.metadata.PartitionMetaData;

/**
 * Assemble a {@link PartitionMetaData} object from a Configuration
 * object. The Configuration object represents the assembly descriptor
 * and is in the format specified for <tt>assembly.xml</tt> files.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.8 $ $Date: 2003/03/23 00:19:48 $
 */
public class Assembler
    extends AbstractLogEnabled
    implements MetaDataBuilder
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( Assembler.class );

    /**
     * Create a {@link PartitionMetaData} object based on specified
     * name and assembly configuration. This implementation takes two
     * parameters. {@link org.apache.avalon.phoenix.interfaces.ContainerConstants#ASSEMBLY_NAME} specifies
     * the name of the assembly and
     * {@link org.apache.avalon.phoenix.interfaces.ContainerConstants#ASSEMBLY_CONFIG} specifies the configuration
     * tree to use when assembling Partition.
     *
     * @param parameters the parameters for constructing assembly
     * @return the new PartitionMetaData
     * @throws AssemblyException if an error occurs
     */
    public PartitionMetaData buildAssembly( final Map parameters )
        throws Exception
    {
        final String name =
            (String)parameters.get( ContainerConstants.ASSEMBLY_NAME );
        final Configuration configuration =
            (Configuration)parameters.get( ContainerConstants.ASSEMBLY_CONFIG );
        return assembleSar( name, configuration );
    }

    /**
     * Create a {@link PartitionMetaData} object based on specified
     * name and assembly configuration.
     *
     * @param name the name of Sar
     * @param assembly the assembly configuration object
     * @return the new PartitionMetaData
     * @throws AssemblyException if an error occurs
     */
    private PartitionMetaData assembleSar( final String name,
                                           final Configuration assembly )
        throws AssemblyException
    {
        final Configuration[] blockConfig = assembly.getChildren( "block" );
        final ComponentMetaData[] blocks = buildBlocks( blockConfig );
        final PartitionMetaData blockPartition =
            new PartitionMetaData( ContainerConstants.BLOCK_PARTITION,
                                   new String[]{ContainerConstants.LISTENER_PARTITION},
                                   PartitionMetaData.EMPTY_SET,
                                   blocks, Attribute.EMPTY_SET );

        final Configuration[] listenerConfig = assembly.getChildren( "listener" );
        final ComponentMetaData[] listeners = buildBlockListeners( listenerConfig );
        final PartitionMetaData listenerPartition =
            new PartitionMetaData( ContainerConstants.LISTENER_PARTITION,
                                   new String[ 0 ],
                                   PartitionMetaData.EMPTY_SET,
                                   listeners, Attribute.EMPTY_SET );

        final PartitionMetaData[] partitions =
            new PartitionMetaData[]{blockPartition, listenerPartition};

        return new PartitionMetaData( name, new String[ 0 ], partitions,
                                      new ComponentMetaData[ 0 ], Attribute.EMPTY_SET );
    }

    /**
     * Create an array of {@link ComponentMetaData} objects to represent
     * the &lt;block .../&gt; sections in <tt>assembly.xml</tt>.
     *
     * @param blocks the list of Configuration objects for blocks
     * @return the BlockMetaData array
     * @throws AssemblyException if an error occurs
     */
    private ComponentMetaData[] buildBlocks( final Configuration[] blocks )
        throws AssemblyException
    {
        final ArrayList blockSet = new ArrayList();
        for( int i = 0; i < blocks.length; i++ )
        {
            blockSet.add( buildBlock( blocks[ i ] ) );
        }

        return (ComponentMetaData[])blockSet.toArray( new ComponentMetaData[ blockSet.size() ] );
    }

    /**
     * Create a single {@link ComponentMetaData} object to represent
     * specified &lt;block .../&gt; section.
     *
     * @param block the Configuration object for block
     * @return the BlockMetaData object
     * @throws AssemblyException if an error occurs
     */
    private ComponentMetaData buildBlock( final Configuration block )
        throws AssemblyException
    {
        try
        {
            final String name = block.getAttribute( "name" );
            final String classname = block.getAttribute( "class" );
            final Configuration proxy = block.getChild( "proxy" );

            final ArrayList attributeSet = new ArrayList();
            final boolean disableProxy =
                proxy.getAttributeAsBoolean( "disable", false );
            if( disableProxy )
            {
                final Attribute attribute =
                    new Attribute( ContainerConstants.DISABLE_PROXY_ATTR, null );
                attributeSet.add( attribute );
            }

            final Configuration[] provides = block.getChildren( "provide" );
            final DependencyMetaData[] dependencys = buildDependencies( provides );
            final Attribute[] attributes =
                (Attribute[])attributeSet.toArray( new Attribute[ attributeSet.size() ] );

            return new ComponentMetaData( name, classname,
                                          dependencys,
                                          null, null, attributes );
        }
        catch( final ConfigurationException ce )
        {
            final String message =
                REZ.getString( "block-entry-malformed", block.getLocation(), ce.getMessage() );
            throw new AssemblyException( message, ce );
        }
    }

    /**
     * Create an array of {@link ComponentMetaData} objects to represent
     * the &lt;listener .../&gt; sections in <tt>assembly.xml</tt>.
     *
     * @param config the list of Configuration objects for config
     * @return the array of listeners
     * @throws AssemblyException if an error occurs
     */
    private ComponentMetaData[] buildBlockListeners( final Configuration[] config )
        throws AssemblyException
    {
        final List listeners = new ArrayList();
        for( int i = 0; i < config.length; i++ )
        {
            final ComponentMetaData listener = buildBlockListener( config[ i ] );
            listeners.add( listener );
        }
        return (ComponentMetaData[])listeners.
            toArray( new ComponentMetaData[ listeners.size() ] );
    }

    /**
     * Create a {@link ComponentMetaData} object to represent
     * the specified &lt;listener .../&gt; section.
     *
     * @param listener the Configuration object for listener
     * @return the BlockListenerMetaData object
     * @throws AssemblyException if an error occurs
     */
    private ComponentMetaData buildBlockListener( final Configuration listener )
        throws AssemblyException
    {
        try
        {
            final String name = listener.getAttribute( "name" );
            final String classname = listener.getAttribute( "class" );

            return new ComponentMetaData( name, classname,
                                          new DependencyMetaData[ 0 ],
                                          null, null, Attribute.EMPTY_SET );
        }
        catch( final ConfigurationException ce )
        {
            final String message =
                REZ.getString( "listener-entry-malformed",
                               listener.getLocation(),
                               ce.getMessage() );
            throw new AssemblyException( message, ce );
        }
    }

    /**
     * Helper method to build an array of DependencyMetaDatas from input config data.
     *
     * @param provides the set of provides elements for block
     * @return the created DependencyMetaData array
     * @throws ConfigurationException if config data is malformed
     */
    private DependencyMetaData[] buildDependencies( final Configuration[] provides )
        throws ConfigurationException
    {
        final ArrayList dependencies = new ArrayList();
        for( int j = 0; j < provides.length; j++ )
        {
            final Configuration provide = provides[ j ];
            final String requiredName = provide.getAttribute( "name" );
            final String alias = provide.getAttribute( "alias", requiredName );
            final String key = provide.getAttribute( "role" );

            dependencies.add( new DependencyMetaData( key, requiredName, alias, Attribute.EMPTY_SET ) );
        }

        return (DependencyMetaData[])dependencies.toArray( new DependencyMetaData[ 0 ] );
    }
}
