/* 
 * Copyright 2003-2004 The Apache Software Foundation
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

package org.apache.avalon.fortress.impl.handler;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * The ThreadSafeComponentHandler to make sure components are initialized
 * and destroyed correctly.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.9 $ $Date: 2004/02/28 15:16:25 $
 * @since 4.0
 */
public final class PerThreadComponentHandler
    extends AbstractComponentHandler
{
    private ThreadLocalComponent m_instance;
    private List m_instances;

    public void initialize()
        throws Exception
    {
        super.initialize();
        m_instance = new ThreadLocalComponent( this );
        m_instances = Collections.synchronizedList(new LinkedList());
    }

    /**
     * Get a reference of the desired Component
     */
    protected Object doGet()
        throws Exception
    {
        final Object instance = m_instance.get();
        if ( null == instance )
        {
            throw new IllegalStateException( "Instance is unavailable" );
        }

        return instance;
    }

    protected void doDispose()
    {
        Iterator it = m_instances.iterator();
        while (it.hasNext())
        {
            disposeComponent( it.next() );
            it.remove();
        }
        m_instance = null;
        m_instances = null;
    }

    private static final class ThreadLocalComponent
        extends ThreadLocal
    {
        private final PerThreadComponentHandler m_handler;

        protected ThreadLocalComponent( final PerThreadComponentHandler handler )
        {
            m_handler = handler;
        }

        protected Object initialValue()
        {
            try
            {
                Object component = m_handler.newComponent();
                m_handler.m_instances.add(component);
                return component;
            }
            catch ( final Exception e )
            {
                return null;
            }
        }
    }
}
