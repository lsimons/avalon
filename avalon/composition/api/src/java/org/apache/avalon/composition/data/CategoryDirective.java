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
 * A logging category descriptor hierachy.  The descriptor contains a category name, a
 * optional priority value, and an optional target.  If the priority or target values
 * null, the resulting value will be derived from the parent category desciptor. A
 * category descriptor may 0-n subsidiary categories.  CategoryDirective names are relative.
 * For example, the category "orb" will appear as "my-app.orb" if the parent category
 * name is "my-app".
 *
 * <p><b>XML</b></p>
 * <pre>
 *    &lt;categories priority="<font color="darkred">INFO</font>"&gt;
 *      &lt;category priority="<font color="darkred">DEBUG</font>"  name="<font color="darkred">loader</font>" /&gt;
 *      &lt;category priority="<font color="darkred">WARN</font>"  name="<font color="darkred">types</font>" /&gt;
 *      &lt;category priority="<font color="darkred">ERROR</font>"  name="<font color="darkred">types.builder</font>" target="<font color="darkred">default</font>"/&gt;
 *      &lt;category name="<font color="darkred">profiles</font>" /&gt;
 *      &lt;category name="<font color="darkred">lifecycle</font>" /&gt;
 *      &lt;category name="<font color="darkred">verifier</font>" /&gt;
 *    &lt;/categories&gt;
 * </pre>
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:02 $
 */
public class CategoryDirective implements Serializable
{

    /**
     * Constant category priority value for debug mode.
     */
    public static final String DEBUG = "DEBUG";

    /**
     * Constant category priority value for info mode.
     */
    public static final String INFO = "INFO";

    /**
     * Constant category priority value for warning mode.
     */
    public static final String WARN = "WARN";

    /**
     * Constant category priority value for error mode.
     */
    public static final String ERROR = "ERROR";

    /**
     * The logging category name.
     */
    private String m_name;

    /**
     * The default logging priority.
     */
    private final String m_priority;

    /**
     * The default logging target.
     */
    private final String m_target;

    /**
     * Creation of a new CategoryDirective using a supplied name.
     *
     * @param name the category name
     */
    public CategoryDirective( final String name )
    {
        this( name, null, null );
    }

    /**
     * Creation of a new CategoryDirective using a supplied name and priority.
     *
     * @param name the category name
     * @param priority the category priority - DEBUG, INFO, WARN, or ERROR
     */
    public CategoryDirective( final String name, String priority )
    {
        this( name, priority, null );
    }

    /**
     * Creation of a new CategoryDirective using a supplied name, priority, target and
     * collection of subsidiary categories.
     *
     * @param name the category name
     * @param priority the category priority - DEBUG, INFO, WARN, or ERROR
     * @param target the name of a logging category target
     *
     */
    public CategoryDirective(
        final String name, final String priority, final String target )
    {
        m_name = name;
        m_target = target;
        if( priority != null )
        {
            m_priority = priority.trim().toUpperCase();
        }
        else
        {
            m_priority = null;
        }
    }

    /**
     * Return the category name.
     *
     * @return the category name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the logging priority for the category.
     *
     * @return the logging priority for the category
     */
    public String getPriority()
    {
        return m_priority;
    }

    /**
     * Return the default log target for the category.
     *
     * @return the default target name
     */
    public String getTarget()
    {
        return m_target;
    }

    public boolean equals(Object other)
    {
        if( null == other )
        {
            return false;
        }

        if( ! ( other instanceof CategoryDirective ) )
        {
            return false;
        }

        CategoryDirective test = (CategoryDirective) other;
        return ( equalName( test.getName() ) 
              && equalPriority( test.getPriority() ) 
              && equalTarget( test.getTarget() ) );
    }

    private boolean equalName( String other )
    {
        if( m_name == null )
        {
            return other == null;
        }
        else
        {
            return m_name.equals( other );
        }
    }

    private boolean equalPriority( String other )
    {
        if( m_priority == null )
        {
            return other == null;
        }
        else
        {
            return m_priority.equals( other );
        }
    }

    private boolean equalTarget( String other )
    {
        if( m_target == null )
        {
            return other == null;
        }
        else
        {
            return m_target.equals( other );
        }
    }

    public int hashCode()
    {
        int hash = m_name.hashCode();
        hash >>>= 13;
        if( m_priority != null )
        {
            hash ^= m_priority.hashCode();
        }
        hash >>>= 5;
        if( m_target != null )
        {
            hash ^= m_target.hashCode();
        }
        return hash;
    }
}
