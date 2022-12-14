/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.pool;

import java.lang.reflect.Constructor;

/**
 * This is the default for factory that is used to create objects for Pool.
 *
 * It creates objects via reflection and constructor.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2004/03/29 16:50:37 $
 * @since 4.0
 */
public class DefaultObjectFactory
    implements ObjectFactory
{
    protected Constructor m_constructor;
    protected Object[] m_arguements;

    public DefaultObjectFactory( final Constructor constructor, final Object[] arguements )
    {
        m_arguements = arguements;
        m_constructor = constructor;
    }

    public DefaultObjectFactory( final Class clazz,
                                 final Class[] arguementClasses,
                                 final Object[] arguements )
        throws NoSuchMethodException
    {
        this( clazz.getConstructor( arguementClasses ), arguements );
    }

    public DefaultObjectFactory( final Class clazz )
        throws NoSuchMethodException
    {
        this( clazz, null, null );
    }

    public Class getCreatedClass()
    {
        return m_constructor.getDeclaringClass();
    }

    public Object newInstance()
    {
        try
        {
            return (Poolable)m_constructor.newInstance( m_arguements );
        }
        catch( final Exception e )
        {
            throw new Error( "Failed to instantiate the class " +
                             m_constructor.getDeclaringClass().getName() + " due to " + e );
        }
    }

    public void decommission( final Object object )
    {
        // Nothing to do
    }
}
