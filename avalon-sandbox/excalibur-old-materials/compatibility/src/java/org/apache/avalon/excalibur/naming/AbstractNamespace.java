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
package org.apache.avalon.excalibur.naming;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.StateFactory;

/**
 * This is the class to extend that provides
 * basic facilities for Namespace management.
 *
 * @author Peter Donald
 * @version $Revision: 1.2 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public abstract class AbstractNamespace
    implements Namespace
{
    protected ObjectFactory[] m_objectFactorySet;
    protected StateFactory[] m_stateFactorySet;

    public Object getStateToBind( final Object object,
                                  final Name name,
                                  final Context parent,
                                  final Hashtable environment )
        throws NamingException
    {
        //for thread safety so that member variable can be updated
        //at any time
        final StateFactory[] stateFactorySet = m_stateFactorySet;

        for( int i = 0; i < stateFactorySet.length; i++ )
        {
            final Object result =
                stateFactorySet[ i ].getStateToBind( object, name, parent, environment );

            if( null != result )
            {
                return result;
            }
        }

        return object;
    }

    public Object getObjectInstance( final Object object,
                                     final Name name,
                                     final Context parent,
                                     final Hashtable environment )
        throws Exception
    {
        //for thread safety so that member variable can be updated
        //at any time
        final ObjectFactory[] objectFactorySet = m_objectFactorySet;

        for( int i = 0; i < objectFactorySet.length; i++ )
        {
            final Object result =
                objectFactorySet[ i ].getObjectInstance( object, name, parent, environment );

            if( null != result )
            {
                return result;
            }
        }

        return object;
    }

    /**
     * Utility method for subclasses to add factorys.
     *
     * @param stateFactory the StateFactory to add
     */
    protected synchronized void addStateFactory( final StateFactory stateFactory )
    {
        //create new array of factory objects
        final StateFactory[] stateFactorySet =
            new StateFactory[ m_stateFactorySet.length + 1 ];

        //copy old factory objects to new array
        System.arraycopy( m_stateFactorySet, 0, stateFactorySet, 0, m_stateFactorySet.length );

        //add in new factory at end
        stateFactorySet[ m_stateFactorySet.length ] = stateFactory;

        //update factory set
        m_stateFactorySet = stateFactorySet;
    }

    /**
     * Utility method for subclasses to add factorys.
     *
     * @param objectFactory the ObjectFactory to add
     */
    protected synchronized void addObjectFactory( final ObjectFactory objectFactory )
    {
        //create new array of factory objects
        final ObjectFactory[] objectFactorySet =
            new ObjectFactory[ m_objectFactorySet.length + 1 ];

        //copy old factory objects to new array
        System.arraycopy( m_objectFactorySet, 0, objectFactorySet, 0, m_objectFactorySet.length );

        //add in new factory at end
        objectFactorySet[ m_objectFactorySet.length ] = objectFactory;

        //update factory set
        m_objectFactorySet = objectFactorySet;
    }
}
