/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
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
package org.apache.avalon.fortress;

/**
 * Keeps track of the relationship of all the associated meta data for a
 * component type.  It records the role, short name, component class, and
 * the handler class used to manage it.  The short name is included strictly
 * to enable "self-healing" configuration files.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.7 $ $Date: 2003/03/22 12:46:32 $
 */
public class RoleEntry
{
    private final String m_shortName;
    private final String m_role;
    private final Class m_componentClass;
    private final Class m_handlerClass;

    /**
     * Create a <code>RoleEntry</code> with all the associated information.
     * All arguments must be supplied.
     *
     * @param  role            Role name for this component type
     * @param  shortName       Short name for this component type
     * @param  componentClass  <code>Class</code> to instantiate the
     *                         component type
     * @param  handlerClass    <code>Class</code> to instantiate the
     *                         component handler
     *
     * @exception <code>IllegalArgumentException</code> if any argument is
     *         <code>null</code>.
     */
    public RoleEntry( final String role,
                      final String shortName,
                      final Class componentClass,
                      final Class handlerClass ) throws IllegalArgumentException
    {
        if( null == role )
        {
            throw new IllegalArgumentException( "\"role\" cannot be null." );
        }
        if( null == shortName )
        {
            throw new IllegalArgumentException( "\"shortname\" cannot be null." );
        }
        if( null == componentClass )
        {
            throw new IllegalArgumentException( "\"componentClass\" cannot be null." );
        }
        if( null == handlerClass )
        {
            throw new IllegalArgumentException( "\"handlerClass\" cannot be null." );
        }

        m_role = role;
        m_shortName = shortName;
        m_componentClass = componentClass;
        m_handlerClass = handlerClass;
    }

    /**
     * Get the role name for the component type.
     *
     * @return the role name
     */
    public String getRole()
    {
        return m_role;
    }

    /**
     * Get the short name for the component type.  This is used in
     * "self-healing" configuration files.
     *
     * @return the short name
     */
    public String getShortname()
    {
        return m_shortName;
    }

    /**
     * Get the <code>Class</code> for the component type.
     *
     * @return the <code>Class</code>
     */
    public Class getComponentClass()
    {
        return m_componentClass;
    }

    /**
     * Get the <code>Class</code> for the component type's {@link org.apache.avalon.fortress.impl.handler.ComponentHandler}.
     *
     * @return the <code>Class</code>
     */
    public Class getHandlerClass()
    {
        return m_handlerClass;
    }
}
