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

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

import org.apache.avalon.activation.lifecycle.Factory;
import org.apache.avalon.activation.lifestyle.LifestyleHandler;
import org.apache.avalon.meta.info.InfoDescriptor;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * Abstract implentation class for a lifestyle handler.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $ $Date: 2003/12/14 14:09:59 $
 */
public abstract class AbstractLifestyleHandler extends AbstractLogEnabled
  implements LifestyleHandler
{
    private final Factory m_factory;

    private final ReferenceQueue m_liberals = new ReferenceQueue();

   /**
    * Creation of a new instance.
    * @param logger the logging channel
    */
    public AbstractLifestyleHandler( Logger logger, Factory factory  )
    {
        enableLogging( logger );
        m_factory = factory;
    }

   /**
    * Return the liberal queue.
    */
    ReferenceQueue getLiberalQueue()
    {
        return m_liberals;
    }

    /**
     * Release an object. 
     *
     * @param instance the object to be reclaimed
     */
    public void release( Object instance )
    {
        release( instance, false );
    }

    Reference getReference( Object instance )
    {
        final int policy = getFactory().getDeploymentModel().getCollectionPolicy();
        if( policy == InfoDescriptor.LIBERAL )
        {
             return new WeakReference( instance, m_liberals );
        }
        else if( policy == InfoDescriptor.DEMOCRAT )
        {
             return new SoftReference( instance );
        }
        else
        {
             return new StrongReference( instance );
        }
    }

    void disposeInstance( Object instance )
    {
        if( instance != null )
        {
            synchronized( getFactory() )
            {
                m_factory.destroy( instance );
            }
        }
    }

    Object newInstance() throws Exception
    {
        return m_factory.newInstance();
    }
    
    Factory getFactory()
    { 
        return m_factory;
    }

    class StrongReference extends WeakReference
    {
        private final Object m_instance;

        public StrongReference( Object instance )
        {
            super( instance );
            m_instance = instance;
        }

        public Object get()
        {
            return m_instance;
        }
    }
}
