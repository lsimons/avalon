/* 
 * Copyright 2004 Apache Software Foundation
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
 * @version $Revision: 1.8.2.1 $ $Date: 2004/02/22 15:50:07 $
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
        final int policy = getFactory().getComponentModel().getCollectionPolicy();
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
                try
                {
                    m_factory.destroy( instance );
                } catch( Exception e )
                {
                    // TODO:  ????
                    // Perhaps report to an Error facility.
                }
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
