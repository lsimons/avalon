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

package org.apache.avalon.phoenix.containerkit.profile;

import org.apache.avalon.phoenix.containerkit.metadata.PartitionMetaData;

/**
 * The PartitionProfile contains the set of data required
 * to construct a specific instance of a Profile. It contains
 * a set of child PartitionProfile and {@link ComponentProfile}
 * objects.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/22 12:07:12 $
 */
public class PartitionProfile
{
    /**
     * Constant for an empty set of partitions.
     */
    public static final PartitionProfile[] EMPTY_SET = new PartitionProfile[ 0 ];

    /**
     * The {@link PartitionMetaData} for this partition.
     */
    private final PartitionMetaData m_metaData;

    /**
     * An array of partitions that are contained by this
     * object.
     */
    private final PartitionProfile[] m_partitions;

    /**
     * An array of partitions that are contained by this
     * object.
     */
    private final ComponentProfile[] m_components;

    /**
     * Create a PartitionProfile.
     *
     * @param metaData the meta data about this profile
     * @param partitions the partitions contained by this partition
     * @param components the components contained by this partition
     */
    public PartitionProfile( final PartitionMetaData metaData,
                             final PartitionProfile[] partitions,
                             final ComponentProfile[] components )
    {
        if( null == metaData )
        {
            throw new NullPointerException( "metaData" );
        }
        if( null == partitions )
        {
            throw new NullPointerException( "partitions" );
        }
        if( null == components )
        {
            throw new NullPointerException( "components" );
        }

        m_metaData = metaData;
        m_partitions = partitions;
        m_components = components;
    }

    /**
     * Return the metaData about this profile.
     *
     * @return the metaData about this profile.
     */
    public PartitionMetaData getMetaData()
    {
        return m_metaData;
    }

    /**
     * Return the set of partitions contained in this partition.
     *
     * @return the set of partitions contained in this partition.
     */
    public PartitionProfile[] getPartitions()
    {
        return m_partitions;
    }

    /**
     * Return the set of components contained in this partition.
     *
     * @return the set of components contained in this partition.
     */
    public ComponentProfile[] getComponents()
    {
        return m_components;
    }

    /**
     * Return the partition with specified name.
     *
     * @return the partition with specified name.
     */
    public PartitionProfile getPartition( final String name )
    {
        for( int i = 0; i < m_partitions.length; i++ )
        {
            final PartitionProfile partition = m_partitions[ i ];
            if( partition.getMetaData().getName().equals( name ) )
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
    public ComponentProfile getComponent( final String name )
    {
        for( int i = 0; i < m_components.length; i++ )
        {
            final ComponentProfile component = m_components[ i ];
            if( component.getMetaData().getName().equals( name ) )
            {
                return component;
            }
        }
        return null;
    }
}
