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

import org.apache.avalon.ide.repository.InvalidSchemeException;
import org.apache.avalon.ide.repository.RepositorySchemeDescriptor;

/** A RepositorySchemeDescriptor container for generic purpose.
 * 
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public class GenericSchemeDescriptor implements RepositorySchemeDescriptor
{
    private String m_Scheme;
    private String m_Name;
    private String m_Description;
    
    public GenericSchemeDescriptor( String prefix, String name, String desc )
        throws InvalidSchemeException
    {
        m_Scheme = normalize( prefix );
        m_Name = name;
        m_Description = desc;
    }
    
    /* (non-Javadoc)
     * @see org.apache.avalon.repository.tools.RepositorySchemeDescriptor#getPrefix()
     */
    public String getScheme()
    {
        return m_Scheme;
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.repository.tools.RepositorySchemeDescriptor#getName()
     */
    public String getName()
    {
        return m_Name;
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.repository.tools.ReopsitorySchemeDescriptor#getDescription()
     */
    public String getDescription()
    {
        return m_Description;
    }

    /** Validates a Scheme name and then calls the parse() method. 
     * 
     * @param urn to be normalized.
     * @return A valid type.
     * @throws InvalidSchemeException if the Scheme contains invalid characters.
     */
    private String normalize(String urn) throws InvalidSchemeException
    {
        for (int i = 0; i < urn.length(); i++)
        {
            char ch = urn.charAt(i);
            if( ch == ':')
                break; // Anything beyond the colon could be legal.
                
            if (!(Character.isLetterOrDigit(ch) || ch == '-' || ch == '_' || ch == ':' ))
            {
                throw new InvalidSchemeException("Illegal characters in Scheme. Only Letter, Digit, dash and underscored allowed.");
            }
        }
        return parse( urn );
    }

    /** Drops any trailing [location].
     * 
     * @param urn to be parsed for a [type].
     * @return The type in the URN.
     */
    private String parse(String urn)
    {
        urn = urn.trim();
        int pos = urn.indexOf(':');
        if (pos < 0)
            return urn;
        return urn.substring( 0, pos );
    }
    
    public String toString()
    {
        return m_Scheme + ":";
    }
    
    public int hashCode()
    {
        return m_Scheme.hashCode();
    }
    
    public boolean equals( Object o )
    {
        return m_Scheme.equals( o );
    }
}
