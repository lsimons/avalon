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

import org.apache.avalon.activation.lifecycle.Factory;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $ $Date: 2004/01/24 23:25:22 $
 */
public class SingletonLifestyleHandler extends AbstractLifestyleHandler
  implements Disposable
{

    private Reference m_reference;

    public SingletonLifestyleHandler( Logger logger, Factory factory )
    {
        super( logger, factory );
        if( factory == null ) throw new IllegalStateException( "factory" );
    }

    /**
     * Resolve a object to a value relative to a supplied set of 
     * interface classes.
     *
     * @return the resolved object
     * @throws Exception if an error occurs
     */
    public synchronized Object resolve() throws Exception
    {
        Object instance = null;

        if( m_reference == null )
        {
            return refreshReference();
        }
        else
        {
            instance = m_reference.get();
            if( instance == null )
            {
                return refreshReference();
            }
            else
            {
                return instance;
            }
        }
    }

    /**
     * Release an object
     *
     * @param instance the object to be released
     * @param finalized if TRUE the lifestyle handler cannot reuse the instance
     */
    public synchronized void release( Object instance, boolean finalized )
    {
        if( finalized )
        {
            m_reference = null;
        }
        else
        {
            // continue with the current reference
        }
    }

   /**
    * Dispose of the component.
    */
    public void dispose()
    {
        if( m_reference != null )
        {
            disposeInstance( m_reference.get() );
            m_reference = null;
        }
    }

    private Object refreshReference() throws Exception
    {
        synchronized( getFactory() )
        {
            Object instance = getFactory().newInstance();
            m_reference = getReference( instance );
            return instance;
        }
    }
}
