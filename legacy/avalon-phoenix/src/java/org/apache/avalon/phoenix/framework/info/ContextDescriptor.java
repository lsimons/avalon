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

package org.apache.avalon.phoenix.framework.info;

/**
 * A descriptor describing the Context that the component
 * is passed to describe information about Runtime environment
 * of Component. It contains information such as;
 * <ul>
 *   <li>type: the type of the Context if it
 *       differs from base Context class (Such as
 *       <a href="http://jakarta.apache.org/avalon/phoenix">
 *       Phoenixes</a> BlockContext).</li>
 *   <li>entrys: a list of entrys contained in context</li>
 * </ul>
 *
 * <p>Associated with each Context is a set of arbitrary
 * Attributes that can be used to store extra information
 * about Context requirements.</p>
 *
 * @author Peter Donald
 * @version $Revision: 1.4 $ $Date: 2003/12/05 15:14:37 $
 */
public class ContextDescriptor
    extends FeatureDescriptor
{
    /**
     * The default type of the context.
     */
    public static final String DEFAULT_TYPE =
        "org.apache.avalon.framework.context.Context";

    /**
     * A constant for an empty context with standard type.
     */
    public static final ContextDescriptor EMPTY_CONTEXT =
        new ContextDescriptor( DEFAULT_TYPE, EntryDescriptor.EMPTY_SET, Attribute.EMPTY_SET );

    /**
     * The type of the context. (ie a name of the context
     * interface that is required by the component).
     */
    private final String m_type;

    /**
     * The set of entrys to populate the context with
     * for this component.
     */
    private final EntryDescriptor[] m_entrys;

    /**
     * Create a descriptor.
     *
     * @throws NullPointerException if type or entrys argument is null
     * @throws IllegalArgumentException if the classname format is invalid
     */
    public ContextDescriptor( final String type,
                              final EntryDescriptor[] entrys,
                              final Attribute[] attribute )
    {
        super( attribute );

        if( null == type )
        {
            throw new NullPointerException( "type" );
        }
        if( null == entrys )
        {
            throw new NullPointerException( "entrys" );
        }

        m_type = type;
        m_entrys = entrys;
    }

    /**
     * Return the type of Context class.
     *
     * @return the type of Context class.
     */
    public String getType()
    {
        return m_type;
    }

    /**
     * Return the entrys contained in the context.
     *
     * @return the entrys contained in the context.
     */
    public EntryDescriptor[] getEntrys()
    {
        return m_entrys;
    }

    /**
     * Return the entry with specified key.
     *
     * @return the entry with specified key.
     */
    public EntryDescriptor getEntry( final String key )
    {
        for( int i = 0; i < m_entrys.length; i++ )
        {
            final EntryDescriptor entry = m_entrys[ i ];
            if( entry.getKey().equals( key ) )
            {
                return entry;
            }
        }
        return null;
    }
}
