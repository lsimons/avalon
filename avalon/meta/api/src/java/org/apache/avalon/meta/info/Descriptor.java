/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.meta.info;

import java.io.Serializable;
import java.util.Properties;

/**
 * This is the Abstract class for all feature feature descriptors.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:15:06 $
 */
public abstract class Descriptor
        implements Serializable
{
    private static final String[] EMPTY_SET = new String[0];

    /**
     * The arbitrary set of attributes associated with Component.
     */
    private final Properties m_attributes;

    /**
     * Creation of an abstract descriptor.
     * @param attributes the set of attributes to assign to the descriptor
     */
    protected Descriptor( final Properties attributes )
    {
        m_attributes = attributes;
    }

    /**
     * Return the attribute for specified key.
     *
     * @param key the attribute key to resolve
     * @return the attribute for specified key.
     */
    public String getAttribute( final String key )
    {
        if ( null == m_attributes )
        {
            return null;
        }
        else
        {
            return m_attributes.getProperty( key );
        }
    }

    /**
     * Return the attribute for specified key.
     *
     * @param key the attribute key to resolve
     * @param defaultValue the default value to use if the value is not defined
     * @return the attribute for specified key.
     */
    public String getAttribute( final String key,
                                final String defaultValue )
    {
        if ( null == m_attributes )
        {
            return defaultValue;
        }
        else
        {
            return m_attributes.getProperty( key, defaultValue );
        }
    }

    /**
     * Returns the set of attribute names available under this descriptor.
     *
     * @return an array of the properties names held by the descriptor.
     */
    public String[] getAttributeNames()
    {
        if ( null == m_attributes )
        {
            return EMPTY_SET;
        }
        else
        {
            return (String[]) m_attributes.keySet().toArray( EMPTY_SET );
        }
    }

    /**
     * Compare this object with another for equality.
     * @param other the object to compare this object with
     * @return TRUE if the supplied object equivalent
     */
    public boolean equals( Object other )
    {
        if ( other instanceof Descriptor )
        {
            Descriptor descriptor = (Descriptor) other;
            if ( null == m_attributes ) return null == descriptor.m_attributes;

            return m_attributes.equals( descriptor.m_attributes );
        }
        return false;
    }

   /**
    * Return the hashcode for the object.
    * @return the hashcode value
    */
    public int hashCode()
    {
        if( m_attributes != null )
        {
            return m_attributes.hashCode();
        }
        else
        {
            return 1;
        }
    }

    /**
     * Returns the property set.
     * TODO: check necessity for this operationi and if really needed return 
     * a cloned equivalent (i.e. disable modification)
     * 
     * @return the property set.
     */
    protected Properties getProperties()
    {
        return m_attributes;
    }
}
