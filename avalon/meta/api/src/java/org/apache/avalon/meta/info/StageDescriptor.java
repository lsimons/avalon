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

import java.util.Properties;

/**
 * A descriptor that describes a name and inteface of a lifecycle stage.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:15:10 $
 */
public class StageDescriptor extends Descriptor
{

    /**
     * The stage identifier.
     */
    private final String m_urn;

    /**
     * Constructor a stage descriptor without attributes.
     * @param urn the stage identifier
     * @exception NullPointerException if the classname argument is null
     */
    public StageDescriptor( final String urn )
            throws NullPointerException
    {
        this( urn, null );
    }

    /**
     * Constructor a stage descriptor with attributes.
     * @param urn the stage identifier
     * @param attributes a set of attribute values to associated with the stage
     * @exception NullPointerException if the reference argument is null
     */
    public StageDescriptor( final String urn,
                            final Properties attributes )
            throws NullPointerException
    {
        super( attributes );

        if ( null == urn )
        {
            throw new NullPointerException( "urn" );
        }
        m_urn = urn;
    }

    /**
     * Return the stage identifier.
     *
     * @return the urn identifier
     */
    public String getKey()
    {
        return m_urn;
    }

    /**
     * Return the hashcode for the instance
     * @return the instance hashcode
     */
    public int hashCode()
    {
        int hash = super.hashCode();
        hash >>>= 17;
        hash ^= m_urn.hashCode();
        return hash;
    }

   /**
    * Test is the supplied object is equal to this object.
    * @return true if the object are equivalent
    */
    public boolean equals(Object other)
    {
        if( other instanceof StageDescriptor )
        {
            if( super.equals( other ) )
            {
                return m_urn.equals( ((StageDescriptor)other).m_urn );
            }
        }
        return false;
    }

   /**
    * Return a stringified representation of the instance.
    * @return the string representation
    */
    public String toString()
    {
        return "[stage " + getKey() + "]";
    }
}
