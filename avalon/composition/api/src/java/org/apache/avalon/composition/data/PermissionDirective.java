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
import java.lang.reflect.InvocationTargetException;
import java.security.Permission;

/**
 * Description of classpath.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/01/19 01:26:19 $
 */
public final class PermissionDirective implements Serializable
{
    /** The Permission object  **/
    private Permission m_permission;
    
    /**
     * Create a PermissionDirective instance.
     *
     * @param classname Permission class
     * @param name The name associated with the permission.
     * @param action The action associated with the permission. Note that some
     *        Permissions doesn't support actions.
     * @throws InstantiationException if the class could not be instantiated.
     * @throws IllegalAccessException, if the class does not have a 
     *         public constructor
     * @throws ClassNotFoundException, if the class could not be reached by the
     *         classloader.
     * @throws ClassCastException, if the class is not a subclass of 
     *         java.security.Permission
     * @throws InvocationTargetException, if the constructor in the Permission
     *         class throws an exception.
     */
    public PermissionDirective( 
        final String classname,  
        final String name,
        final String action
    )
        throws 
          InstantiationException, 
          IllegalAccessException, 
          ClassNotFoundException,
          ClassCastException,
          InvocationTargetException
    {
        if( classname == null )
        {
            throw new NullPointerException( "classname" );
        }
        ClassLoader trustedClassloader = this.getClass().getClassLoader();
        
        Class clazz = trustedClassloader.loadClass( classname );
        Constructor[] constructors = clazz.getConstructors();
        if( name == null )
        {
            m_permission = (Permission) clazz.newInstance();   
        }
        else if( action == null )
        {
            Constructor cons = getConstructor( constructors, 1 );
            Object[] arg = new Object[] { name };
            m_permission = (Permission) cons.newInstance( arg );
        }
        else
        {
            Constructor cons = getConstructor( constructors, 2 );
            Object[] args = new Object[] { name, action };
            m_permission = (Permission) cons.newInstance( args );
        }
    }

    /**
     * Return the Permission.
     *
     * @return the fileset directives
     */
    public Permission getPermission()
    {
        return m_permission;
    }
    
    private Constructor getConstructor( Constructor[] constructors, int noOfParameters )
    {
        for ( int i=0 ; i < constructors.length ; i++ )
        {
            Class[] params = constructors[i].getParameterTypes();
            if( params.length == noOfParameters )
                return constructors[i];
        }
        return null;
    }
}
