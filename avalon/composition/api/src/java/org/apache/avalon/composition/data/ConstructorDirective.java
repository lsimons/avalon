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


/**
 * A entry descriptor declares the context entry import or creation criteria for
 * a single context entry instance.
 *
 * <p><b>XML</b></p>
 * <p>A entry may contain either (a) a single nested import directive, or (b) a single param constructor directives.</p>
 * <pre>
 *  <font color="gray">&lt;context&gt;</font>
 *
 *    &lt!-- option (a) nested import -->
 *    &lt;entry key="<font color="darkred">my-home-dir</font>"&gt;
 *       &lt;include key="<font color="darkred">urn:avalon:home</font>"/&gt;
 *    &lt;/entry&gt;
 *
 *    &lt!-- option (b) param constructors -->
 *    &lt;entry key="<font color="darkred">title</font>"&gt;
 *       &lt;param&gt;<font color="darkred">Lord of the Rings</font>&lt;/&gt;
 *    &lt;/entry&gt;
 *    &lt;entry key="<font color="darkred">home</font>"&gt;
 *      &lt;param class="<font color="darkred">java.io.File</font>"&gt;<font color="darkred">../home</font>&lt;/param&gt;
 *    &lt;/entry&gt;
 *
 *  <font color="gray">&lt;/context&gt;</font>
 * </pre>
 *
 * @see ImportDirective
 * @see Parameter
 * @see ContextDirective
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:03 $
 */
public class ConstructorDirective extends EntryDirective
{
    /**
     * The constructor classname.
     */
    private final String m_classname;

    /**
     * The constructor param.
     */
    private final Parameter[] m_params;

    /**
     * The alternative argument.
     */
    private final String m_argument;

    /**
     * Creation of a new entry directive using a constructor
     * classname and single argument value.
     * @param key the entry key
     * @param value the single argument value
     */
    public ConstructorDirective( 
      final String key, final String value )
    {
        this( key, "java.lang.String", value );
    }

    /**
     * Creation of a new entry directive using a constructor
     * classname and single argument value.
     * @param key the entry key
     * @param classname the classname of the entry implementation
     * @param value the single argument value
     */
    public ConstructorDirective( 
      final String key, final String classname, final String value )
    {
        super( key );

        if( null == classname )
        {
            throw new NullPointerException( "classname" );
        }

        m_params = new Parameter[0];
        m_classname = classname;
        m_argument = value;
    }

    /**
     * Creation of a new entry directive using a parameter.
     * @param key the entry key
     * @param parameters implementation class constructor parameter directives
     */
    public ConstructorDirective( final String key, final Parameter[] parameters )
    {
        this( key, "java.lang.String", parameters );
    }

    /**
     * Creation of a new entry directive using a parameter.
     * @param key the entry key
     * @param classname the classname of the entry implementation
     * @param params implementation class constructor parameter directives
     */
    public ConstructorDirective( 
      final String key, final String classname, final Parameter[] params )
    {
        super( key );

        if( null == params )
        {
            throw new NullPointerException( "parameters" );
        }
        if( null == classname )
        {
            throw new NullPointerException( "classname" );
        }

        m_classname = classname;
        m_params = params;
        m_argument = null;
    }

    /**
     * Return the constructor classname
     * @return the classname
     */
    public String getClassname()
    {
        return m_classname;
    }

    /**
     * Return the parameter directive if the mode is PARAM else null.
     * @return the directive
     */
    public Parameter[] getParameters()
    {
        return m_params;
    }

    /**
     * Return the constructor single argument
     * @return the costructor argument
     */
    public String getArgument()
    {
        return m_argument;
    }
}
