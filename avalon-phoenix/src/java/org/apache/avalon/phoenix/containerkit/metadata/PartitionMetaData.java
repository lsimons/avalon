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

package org.apache.avalon.phoenix.containerkit.metadata;

import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.phoenix.framework.info.FeatureDescriptor;

/**
 * In each Assembly there may be groups of components that
 * are activated together and treated as a group. These
 * components are all "visible" to each other as peers.
 * The group will have a name and may use resources from
 * other partitions. Partitions can also be nested one inside
 * each other.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003/03/22 12:07:12 $
 */
public class PartitionMetaData
    extends FeatureDescriptor
{
    /**
     * Constant for an empty set of partitions.
     */
    public static final PartitionMetaData[] EMPTY_SET = new PartitionMetaData[ 0 ];

    /**
     * The name of the partition. This is an
     * abstract name used during assembly.
     */
    private final String m_name;

    /**
     * An array listing the set of other partitions required by
     * this partition. The required partitions must be initialized
     * and in ready state prior to this partition starting and this
     * partition must be shutdown prior
     */
    private final String[] m_depends;

    /**
     * AN array of partitions that are contained by this
     * object.
     */
    private final PartitionMetaData[] m_partitions;

    /**
     * AN array of components that are contained by this
     * object.
     */
    private final ComponentMetaData[] m_components;

    /**
     * Create a PartitionMetaData.
     *
     * @param name the abstract name of component meta data instance
     * @param depends the partitions depended upon by this parition
     * @param partitions the partitions contained by this partition
     * @param components the components contained by this partition
     * @param attributes the extra attributes that are used to describe component
     */
    public PartitionMetaData( final String name,
                              final String[] depends,
                              final PartitionMetaData[] partitions,
                              final ComponentMetaData[] components,
                              final Attribute[] attributes )
    {
        super( attributes );
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == depends )
        {
            throw new NullPointerException( "depends" );
        }
        if( null == partitions )
        {
            throw new NullPointerException( "partitions" );
        }
        if( null == components )
        {
            throw new NullPointerException( "components" );
        }

        m_name = name;
        m_depends = depends;
        m_partitions = partitions;
        m_components = components;
    }

    /**
     * Return the name of component profile.
     *
     * @return the name of the component profile.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the set of prereqs for this partition.
     *
     * @return the set of prereqs for this partition.
     */
    public String[] getDepends()
    {
        return m_depends;
    }

    /**
     * Return the set of partitions contained in this partition.
     *
     * @return the set of partitions contained in this partition.
     */
    public PartitionMetaData[] getPartitions()
    {
        return m_partitions;
    }

    /**
     * Return the set of components contained in this partition.
     *
     * @return the set of components contained in this partition.
     */
    public ComponentMetaData[] getComponents()
    {
        return m_components;
    }

    /**
     * Return the partition with specified name.
     *
     * @return the partition with specified name.
     */
    public PartitionMetaData getPartition( final String name )
    {
        for( int i = 0; i < m_partitions.length; i++ )
        {
            final PartitionMetaData partition = m_partitions[ i ];
            if( partition.getName().equals( name ) )
            {
                return partition;
            }
        }
        return null;
    }

    /**
     * Return the component with specified name.
     *
     * @return the component with specified name.
     */
    public ComponentMetaData getComponent( final String name )
    {
        for( int i = 0; i < m_components.length; i++ )
        {
            final ComponentMetaData component = m_components[ i ];
            if( component.getName().equals( name ) )
            {
                return component;
            }
        }
        return null;
    }
}

