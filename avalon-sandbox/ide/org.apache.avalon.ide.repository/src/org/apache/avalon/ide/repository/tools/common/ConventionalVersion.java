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

import org.apache.avalon.ide.repository.Version;

/** This Version implementation handles the classic X.Y.Z versioning scheme.
 * 
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public class ConventionalVersion implements Version
{
    private int m_Major;
    private int m_Minor;
    private int m_Micro;
    private boolean m_ShortForm;

    public ConventionalVersion(int major, int minor, int micro)
    {
        m_Major = major;
        m_Minor = minor;
        m_Micro = micro;
    }

    public ConventionalVersion(String ver)
    {
        int pos1 = ver.indexOf('.');
        int pos2 = ver.indexOf('.', pos1 + 1);
        if (pos2 < 0)
        {
            m_Major = Integer.parseInt(ver.substring(0, pos1));
            m_Minor = Integer.parseInt(ver.substring(pos1 + 1));
            m_ShortForm = true;
        } else
        {
            m_Major = Integer.parseInt(ver.substring(0, pos1));
            m_Minor = Integer.parseInt(ver.substring(pos1 + 1, pos2));
            m_Micro = Integer.parseInt(ver.substring(pos2 + 1));
            m_ShortForm = false;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.eclipse.merlin.model.repository.Version#toExternalName()
     */
    public String toExternalName()
    {
        if (m_ShortForm)
            return "" + m_Major + "." + m_Minor;
        else
            return "" + m_Major + "." + m_Minor + "." + m_Micro;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        if (equals(o))
            return 0;

        ConventionalVersion ver = (ConventionalVersion) o;
        if (ver.m_Major == m_Major)
        {
            if (ver.m_Minor == m_Minor)
            {
                if (ver.m_Micro > m_Micro)
                    return -1;
                if (ver.m_Micro < m_Micro)
                    return 1;
                return 0;
            }
            if (ver.m_Minor > m_Minor)
                return -1;
            return 1;
        }
        if (ver.m_Major > m_Major)
            return -1;
        return 1;
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof ConventionalVersion))
            return false;
        ConventionalVersion v = (ConventionalVersion) o;
        return v.m_Major == m_Major
            && v.m_Minor == m_Minor
            && v.m_Micro == m_Micro
            && !(v.m_ShortForm ^ m_ShortForm);

    }

    public int hashCode()
    {
        return m_Major + m_Minor + m_Micro;
    }
}
