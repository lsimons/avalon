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
package org.apache.avalon.excalibur.pool;

import java.lang.reflect.Constructor;

/**
 * This is the default for factory that is used to create objects for Pool.
 *
 * It creates objects via reflection and constructor.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author Peter Donald
 * @version CVS $Revision: 1.3 $ $Date: 2003/12/05 15:15:15 $
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
