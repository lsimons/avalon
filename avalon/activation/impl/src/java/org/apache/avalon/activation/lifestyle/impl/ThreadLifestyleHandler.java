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

import org.apache.avalon.activation.lifecycle.DestructionException;
import org.apache.avalon.activation.lifecycle.LifecycleRuntimeException;
import org.apache.avalon.activation.lifecycle.Factory;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.activity.Disposable;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6.2.1 $ $Date: 2004/02/22 15:50:07 $
 */
public class ThreadLifestyleHandler extends AbstractLifestyleHandler 
    implements Disposable
{
    /**
     * Internal utility class to hold the thread local instance.
     */
    private static final class ThreadLocalHolder extends ThreadLocal
    {
        private final Factory m_factory;

        protected ThreadLocalHolder( Factory factory )
        {
            m_factory = factory;
        }

        //
        // TODO: the current implementation is hard coded to HARD
        // collection policy - we need to update the ThreadLocalHolder so 
        // it regenerates the value relative to SOFT or WEAK policies
        // (but I just need to check docs on thread local state access
        // semantics)
        //

        protected Object initialValue()
        {
            try
            {
                return m_factory.newInstance();
            }
            catch( Exception e )
            {
                final String error = 
                  "Unable to establish thread local variable.";
                throw new LifecycleRuntimeException( error );
            }
        }
    }

    private final Factory m_factory;

    private ThreadLocalHolder m_local;

    public ThreadLifestyleHandler( Logger logger, Factory factory )
    {
        super( logger, factory );
        m_factory = factory;
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
        if( m_local == null )
        {
            m_local = new ThreadLocalHolder( m_factory );
        }
        return m_local.get();
    }

    /**
     * Release an object.  The abstract implementation does nothing,
     *
     * @param instance the object to be reclaimed
     */
    public void release( Object instance, boolean finalized )
    {
        if( finalized )
        {
            final String error =
              "Not possible because the thread local varliable is holding a hard reference.";
            throw new IllegalStateException( error );
        }
        else
        {
            // don't release because this is a sharable reference
        }
    }

   /**
    * Dispose of the component.
    */
    public void dispose()
    {
        if( m_local != null )
        {
            disposeInstance( m_local.get() );
        }
        m_local = null;
    }

}
