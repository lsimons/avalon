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

import java.io.Serializable;
import java.util.Arrays;

/**
 * This is the Abstract class for all feature descriptors.
 * Every descriptor has the capability of adding Attributes
 * of some kind. These Attributes can then be interpreted by
 * the container. The meaning of the specific Attributes will
 * be defined by future specification documents.
 *
 * @author Peter Donald
 * @version $Revision: 1.4 $ $Date: 2003/12/05 15:14:37 $
 */
public abstract class FeatureDescriptor
    implements Serializable
{
    /**
     * The arbitrary set of Attributes associated with Component.
     */
    private final Attribute[] m_attributes;

    /**
     * Create a FeatureDescriptor with specific set of attributes.
     *
     * @param attributes the attributes
     */
    protected FeatureDescriptor( final Attribute[] attributes )
    {
        if( null == attributes )
        {
            throw new NullPointerException( "attributes" );
        }

        m_attributes = attributes;
    }

    /**
     * Return the attributes associated with descriptor.
     *
     * @return the attributes associated with descriptor.
     */
    public Attribute[] getAttributes()
    {
        return m_attributes;
    }

    /**
     * Return the attribute with specified name.
     *
     * @return the attribute with specified name.
     */
    public Attribute getAttribute( final String name )
    {
        for( int i = 0; i < m_attributes.length; i++ )
        {
            final Attribute attribute = m_attributes[ i ];
            if( attribute.getName().equals( name ) )
            {
                return attribute;
            }
        }
        return null;
    }

    /**
     * Helper method to convert attributes into a
     * string representation.
     *
     * @return attributes converted into a string representation
     */
    protected final String attributesToString()
    {
        if( 0 == m_attributes.length )
        {
            return "";
        }
        else
        {
            return String.valueOf( Arrays.asList( m_attributes ) );
        }
    }
}
