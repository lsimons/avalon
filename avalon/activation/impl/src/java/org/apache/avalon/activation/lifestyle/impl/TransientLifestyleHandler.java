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

package org.apache.avalon.activation.lifestyle.impl;

import java.util.ArrayList;

import org.apache.avalon.activation.lifecycle.Factory;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.activity.Disposable;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:30:48 $
 */
public class TransientLifestyleHandler extends AbstractLifestyleHandler implements Disposable
{
    private final Factory m_factory;

    // TODO: change this to weak references
    private ArrayList m_list = new ArrayList();

    public TransientLifestyleHandler( Logger logger, Factory factory )
    {
        super( logger );
        m_factory = factory;
    }

    /**
     * Resolve a object to a value relative to a supplied set of 
     * interface classes.
     *
     * @param source the aquiring source
     * @param ref the castable service classes 
     * @return the resolved object
     * @throws Exception if an error occurs
     */
    public Object resolve( Object source, Class[] ref ) throws Exception
    {
        Object instance = m_factory.newInstance();
        m_list.add( instance );
        return instance;
    }

    /**
     * Release an object.  The abstract implementation does nothing,
     *
     * @param source the context with respect the reclaimed object is qualified
     * @param object the object to be reclaimed
     */
    public void release( Object source, Object object )
    {
        m_list.remove( object );
        m_factory.destroy( object );
    }

   /**
    * Dispose of the component.
    */
    public synchronized void dispose()
    {
        Object[] instances = m_list.toArray();
        for( int i=0; i<instances.length; i++ )
        {
            m_factory.destroy( instances[i] );
        }
        m_list.clear();
    }

}
