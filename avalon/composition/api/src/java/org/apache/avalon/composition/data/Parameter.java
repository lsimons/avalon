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
import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * A <code>Parameter</code> represents a single constructor typed argument value.  A parameter
 * container a classname (default value of <code>java.lang.String</code>) and possible sub-parameters.
 * A parameter's value is established by creating a new instance using the parameter's classname,
 * together with the values directived from the sub-sidiary parameters as constructor arguments.
 *
 * <p><b>XML</b></p>
 * <p>A param is a nested structure containing a string value or contructor parameter arguments.</p>
 * <pre>
 *    <font color="gray">&lt;-- Simple string param declaration --&gt;</font>
 *
 *    &lt;param&gt;<font color="darkred">London</font>&lt;/param&gt;
 *
 *    <font color="gray">&lt;-- Typed param declaration --&gt;</font>
 *
 *    &lt;param class="<font color="darkred">java.io.File</font>"&gt;<font color="darkred">./home</font>&lt;/param&gt;
 *
 *    <font color="gray">&lt;-- Typed parameter declaration referencing a context value --&gt;</font>
 *
 *    &lt;param class="<font color="darkred">java.lang.ClassLoader</font>"&gt;<font color="darkred">${my-classloader-import-key}</font>&lt;/param&gt;
 *
 *    <font color="gray">&lt;-- Multi-argument parameter declaration --&gt;</font>
 *
 *    &lt;param class="<font color="darkred">MyClass</font>"&gt;
 *       &lt;param class="<font color="darkred">java.io.File</font>"><font color="darkred">./home</font>&lt;/param&gt;
 *       &lt;param&gt;<font color="darkred">London</font>&lt;/param&gt;
 *    &lt;/param&gt;
 * </pre>
 *
 * <p>TODO: Fix usage of basic type (int, float, long, etc.) - how do we return 
 *   basic types - can't use getValue() becuase it returns an Object unless
 *   have some way of packing the basic type into a carrier</p>
 *
 * @see EntryDirective
 * @see ImportDirective
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:09 $
 */
public class Parameter implements Serializable
{

    /**
     * The classname to use as the parameter implementation class (defaults to java.lang.String)
     */
    private final String m_classname;

    /**
     * The supplied argument.
     */
    private String m_argument;

    /**
     * The sub-parameters from which the value for this parameter may be derived.
     */
    private final Parameter[] m_parameters;

    /**
     * The derived value.
     */
    private transient Object m_value;

    /**
     * Creation of a new parameter using the default <code>java.lang.String</code>
     * type and a supplied value.
     *
     * @param value the string value
     */
    public Parameter( final String value )
    {
        m_classname = "java.lang.String";
        m_parameters = new Parameter[ 0 ];
        m_argument = value;
    }

    /**
     * Creation of a new entry directive using a supplied classname and value.
     * @param classname the classname of the parameter
     * @param value the parameter constructor value
     */
    public Parameter( final String classname, final String value )
    {
        if( null == classname )
        {
            throw new NullPointerException( "classname" );
        }

        m_classname = classname;
        m_parameters = new Parameter[ 0 ];
        m_argument = value;
    }

    /**
     * Creation of a new entry directive.
     * @param classname the classname of the entry implementation
     * @param parameters implementation class constructor parameter directives
     */
    public Parameter( final String classname, final Parameter[] parameters )
    {
        if( null == classname )
        {
            throw new NullPointerException( "classname" );
        }
        if( null == parameters )
        {
            throw new NullPointerException( "parameters" );
        }

        m_classname = classname;
        m_parameters = parameters;
    }

    /**
     * Return the classname of the parameter implementation to use.
     * @return the classname
     */
    public String getClassname()
    {
        return m_classname;
    }

    /**
     * Return the argument (may be null).
     */
    public String getArgument()
    {
        return m_argument;
    }

    /**
     * Return the constructor parameters for this parameter.
     */
    public Parameter[] getParameters()
    {
        return m_parameters;
    }
}
