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
package org.apache.avalon.ide.repository.tools.common;

import org.apache.avalon.ide.repository.InvalidURNException;
import org.apache.avalon.ide.repository.URNDescriptor;

/** A simple URNDescriptor container for generic purposes.
 * 
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public class URNDescriptorImpl implements URNDescriptor
{
    private String m_URN;
    private String m_Name;
    private String m_Description;
    
    public URNDescriptorImpl( String urn, String name, String desc )
        throws InvalidURNException
    {
        m_URN = normalize( urn );
        m_Name = name;
        m_Description = desc;
    }
    
    /* (non-Javadoc)
     * @see org.apache.avalon.repository.tools.URNDescriptor#getURN()
     */
    public String getURN()
    {
        return m_URN;
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.repository.tools.URNDescriptor#getName()
     */
    public String getName()
    {
        return m_Name;
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.repository.tools.URNDescriptor#getDescription()
     */
    public String getDescription()
    {
        return m_Description;
    }

    public String toExternalName()
    {
        return "urn:" + m_URN;
    }

    /** Validates a URN name and then calls the parseURN. 
     * 
     * @param urn to be normalized.
     * @return A valid type.
     * @throws InvalidURNException if the URN contains invalid characters.
     */
    private String normalize(String urn) throws InvalidURNException
    {
        int colonCounter = 0;
        for (int i = 0; i < urn.length(); i++)
        {
            char ch = urn.charAt(i);
            if( ch == ':')
                colonCounter++;
            if( colonCounter == 2 )
                break; // Anything beyond the second colon could be legal.
                
            if (!(Character.isLetterOrDigit(ch) || ch == '-' || ch == '_' || ch == ':' ))
            {
                throw new InvalidURNException("Illegal characters in URN. Only Letter, Digit, dash and underscored allowed.");
            }
        }
        return parse( urn );
    }

    /** Drops any initial "urn:" and any trailing [location].
     * 
     * @param urn to be parsed for a [type].
     * @return The type in the URN.
     */
    private String parse(String urn)
    {
        urn = urn.trim();
        if (urn.startsWith("urn:"))
            urn = urn.substring(4);
        int pos = urn.indexOf(':');
        if (pos < 0)
            return urn;
        return urn.substring(pos + 1);
    }
    
    public String toString()
    {
        return toExternalName();
    }
    
    public int hashCode()
    {
        return m_URN.hashCode();
    }
    
    public boolean equals( Object o )
    {
        return m_URN.equals( o );
    }
}
