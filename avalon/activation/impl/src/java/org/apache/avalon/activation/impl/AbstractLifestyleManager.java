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

package org.apache.avalon.activation.impl;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

import org.apache.avalon.activation.LifestyleManager;
import org.apache.avalon.activation.ComponentFactory;

import org.apache.avalon.composition.model.ComponentModel;

import org.apache.avalon.meta.info.InfoDescriptor;

import org.apache.avalon.framework.logger.Logger;

/**
 * Abstract implentation class for a lifestyle handler.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/02/12 05:59:41 $
 */
public abstract class AbstractLifestyleManager implements LifestyleManager
{
    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    private final ComponentModel m_model;

    private final ComponentFactory m_factory;

    private final Logger m_logger;

    private final ReferenceQueue m_liberals = new ReferenceQueue();

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

   /**
    * Creation of a new instance.
    * @param logger the logging channel
    */
    public AbstractLifestyleManager( ComponentModel model, ComponentFactory factory  )
    {
        m_factory = factory;
        m_model = model;
        m_logger = model.getLogger();
    }

    //-------------------------------------------------------------------
    // Commissionable
    //-------------------------------------------------------------------

   /**
    * Commission the runtime handler. 
    *
    * @exception Exception if a hanfdler commissioning error occurs
    */
    public abstract void commission() throws Exception;

   /**
    * Invokes the decommissioning phase.  Once a handler is 
    * decommissioned it may be re-commissioned.
    */
    public abstract void decommission();

    //-------------------------------------------------------------------
    // Resolver
    //-------------------------------------------------------------------

    /**
     * Resolve a object to a value.
     *
     * @return the resolved object
     * @throws Exception if an error occurs
     */
    public abstract Object resolve() throws Exception;

    /**
     * Release an object
     *
     * @param instance the object to be released
     */
    public abstract void release( Object instance );

    //-------------------------------------------------------------------
    // LifecycleManager
    //-------------------------------------------------------------------

    public void finalize( Object instance )
    {
        synchronized( m_factory )
        {
            if( instance != null )
            {
                m_factory.etherialize( instance );
            }
        }
    }

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

    protected Logger getLogger()
    {
        return m_logger;
    }

    protected ComponentModel getComponentModel()
    { 
        return m_model;
    }

    protected ComponentFactory getComponentFactory()
    { 
        return m_factory;
    }

   /**
    * Return the liberal queue.
    */
    ReferenceQueue getLiberalQueue()
    {
        return m_liberals;
    }

    protected Reference getReference( Object instance )
    {
        final int policy = getComponentModel().getCollectionPolicy();
        if( policy == InfoDescriptor.WEAK )
        {
             return new WeakReference( instance, m_liberals );
        }
        else if( policy == InfoDescriptor.SOFT )
        {
             return new SoftReference( instance );
        }
        else
        {
             return new StrongReference( instance );
        }
    }

    private class StrongReference extends WeakReference
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

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

    public void finalize()
    {
        decommission();
    }

}
