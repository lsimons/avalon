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

package org.apache.avalon.composition.data;

import java.io.Serializable;

/**
 * A context descriptor declares the context creation criteria for
 * the context instance and context entries.
 *
 * <p><b>XML</b></p>
 * <p>A context directive may contain multiple import statements.  Each import
 * statement corresponds to a request for a context value from the container.</p>
 * <pre>
 *    &lt;context class="<font color="darkred">MyContextClass</font>"&gt;
 *       &lt;entry key="<font color="darkred">special</font>"&gt;
 *         &lt;import key="<font color="darkred">urn:avalon:classloader</font>"/&gt;
 *       &lt;/entry&gt;
 *       &lt;entry key="<font color="darkred">xxx</font>"&gt;
 *         &lt;param class="<font color="darkred">MySpecialClass</font>"&gt;
 *           &lt;param&gt<font color="darkred">hello</font>&lt;/param&gt;
 *           &lt;param class="<font color="darkred">java.io.File</font>"&gt;<font color="darkred">../lib</font>&lt;/param&gt;
 *         &lt;/param&gt;
 *       &lt;/entry&gt;
 *    &lt;/context&gt;
 * </pre>
 *
 * @see EntryDirective
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:04 $
 */
public class ContextDirective implements Serializable
{
    /**
     * The set of entry directives.
     */
    private final EntryDirective[] m_entries;

    /**
     * The constext casting classname.
     */
    private final String m_classname;

    /**
     * The optional provider source path.
     */
    private final String m_source;

    /**
     * Creation of a new file target.
     * @param entries the set of entry descriptors
     */
    public ContextDirective( final EntryDirective[] entries )
    {
        this( null, entries );
    }

    /**
     * Creation of a new file target.
     * @param classname the context implementation class
     * @param entries the set of entry descriptors
     */
    public ContextDirective( final String classname, final EntryDirective[] entries )
    {
        this( classname, entries, null );
    }

    /**
     * Creation of a new file target.
     * @param classname the context implementation class
     * @param entries the set of entry descriptors
     * @param source a path to a source component for contextualization
     *    phase handling
     */
    public ContextDirective( 
      final String classname, final EntryDirective[] entries, String source )
    {
        m_source = source;
        m_classname = classname;
        if( entries != null )
        {
            m_entries = entries;
        }
        else
        {
            m_entries = new EntryDirective[0];
        }
    }

    /**
     * Return the relative path to a source provider component that
     * will handle a custom contextualization phase implementation.
     * @return the source path
     */
    public String getSource()
    {
        return m_source;
    }

    /**
     * Return the classname of the context implementation to use.
     * @return the classname
     */
    public String getClassname()
    {
        return m_classname;
    }

    /**
     * Return the set of entry directives.
     * @return the entries
     */
    public EntryDirective[] getEntryDirectives()
    {
        return m_entries;
    }

    /**
     * Return a named entry.
     * @param key the context entry key
     * @return the entry corresponding to the supplied key or null if the
     *   key is unknown
     */
    public EntryDirective getEntryDirective( String key )
    {
        for( int i = 0; i < m_entries.length; i++ )
        {
            EntryDirective entry = m_entries[ i ];
            if( entry.getKey().equals( key ) )
            {
                return entry;
            }
        }
        return null;
    }
}
