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
import java.util.Properties;

/**
 * Attributes are the mechanism via which the Component model
 * is extended. Each Attribute is made up of
 * <ul>
 *   <li>name: the name of the Attribute</li>
 *   <li>parameters: a set of key-value pairs specifying parameters for Attribute</li>
 * </ul>
 *
 * @author Peter Donald
 * @version $Revision: 1.4 $ $Date: 2003/12/05 15:14:37 $
 */
public final class Attribute
    implements Serializable
{
    /**
     * An empty array of attributes.
     */
    public static final Attribute[] EMPTY_SET = new Attribute[ 0 ];

    /**
     * To save memory always return same emtpy array of names
     */
    private static final String[] EMPTY_NAME_SET = new String[ 0 ];

    /**
     * The name of the Attribute.
     */
    private final String m_name;

    /**
     * The arbitrary set of parameters associated with the Attribute.
     */
    private final Properties m_parameters;

    /**
     * Create a Attribute with specified name and parameters.
     *
     * @param name the Attribute name
     * @param parameters the Attribute parameters
     */
    public Attribute( final String name,
                      final Properties parameters )
    {
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }

        m_name = name;
        m_parameters = parameters;
    }

    /**
     * Return the name of the Attribute.
     *
     * @return the name of the Attribute.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the parameter for specified key.
     *
     * @return the parameter for specified key.
     */
    public String getParameter( final String key )
    {
        if( null == m_parameters )
        {
            return null;
        }
        else
        {
            return m_parameters.getProperty( key );
        }
    }

    /**
     * Return the parameter for specified key, or defaultValue if unspecified.
     *
     * @return the parameter for specified key, or defaultValue if unspecified.
     */
    public String getParameter( final String key,
                                final String defaultValue )
    {
        if( null == m_parameters )
        {
            return defaultValue;
        }
        else
        {
            return m_parameters.getProperty( key, defaultValue );
        }
    }

    /**
     * Returns an array of parameter names available under this Attribute.
     *
     * @return an array of parameter names available under this Attribute.
     */
    public String[] getParameterNames()
    {
        if( null == m_parameters )
        {
            return EMPTY_NAME_SET;
        }
        else
        {
            return (String[])m_parameters.keySet().toArray( EMPTY_NAME_SET );
        }
    }

    public String toString()
    {
        if( null != m_parameters )
        {
            return getName() + m_parameters;
        }
        else
        {
            return getName();
        }
    }
}
