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

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.util.ArrayList;

import org.apache.avalon.activation.lifecycle.Factory;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.meta.info.InfoDescriptor;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.8 $ $Date: 2004/01/24 23:25:22 $
 */
public class TransientLifestyleHandler extends AbstractLifestyleHandler implements Disposable
{
    private ArrayList m_list = new ArrayList();

    public TransientLifestyleHandler( Logger logger, Factory factory )
    {
        super( logger, factory );
    }

    /**
     * Resolve a object to a value relative to a supplied set of 
     * interface classes.
     *
     * @return the resolved object
     * @throws Exception if an error occurs
     */
    public Object resolve() throws Exception
    {
        // TODO: setup a background thread to check queues for 
        // released references and remove them from our list, otherwise we
        // have a memory leak due to accumulation of weak references

        Object instance = newInstance();
        Reference reference = getReference( instance );
        m_list.add( reference );
        return instance;
    }

    /**
     * Release an object
     *
     * @param instance the object to be released
     * @param finalized if TRUE the lifestyle handler cannot reuse the instance
     */
    public void release( Object instance, boolean finalized )
    {
        disposeInstance( instance );
    }

   /**
    * Dispose of the component.
    */
    public synchronized void dispose()
    {
        Reference[] refs = (Reference[]) m_list.toArray( new Reference[0] );
        for( int i=0; i<refs.length; i++ )
        {
            Reference ref = refs[i];
            disposeInstance( refs[i].get() );
        }
        m_list.clear();
    }

   /**
    * Overriding getReference to ensure that we never return a hard 
    * reference for a transient.
    */
    Reference getReference( Object instance )
    {
        final int policy = getFactory().getComponentModel().getCollectionPolicy();
        if( policy == InfoDescriptor.DEMOCRAT )
        {
             return new SoftReference( instance );
        }
        else
        {
             return new WeakReference( instance, getLiberalQueue() );
        }
    }
}
