/* 
 * Copyright 1999-2004 Apache Software Foundation
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

package org.apache.avalon.cornerstone.services.threads;

import org.apache.avalon.excalibur.thread.ThreadPool;

/**
 * Manage a set of ThreadPools.
 *
 * @author Peter Donald
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.5 $ $Date: 2004/02/18 03:27:16 $
 */
public interface ThreadManager
{
    String ROLE = ThreadManager.class.getName();

    /**
     * Retrieve a thread pool by name.
     *
     * @param name the name of thread pool
     * @return the threadpool
     * @exception IllegalArgumentException if the name of thread pool is
     *            invalid or named pool does not exist
     */
    ThreadPool getThreadPool( String name )
        throws IllegalArgumentException;

    /**
     * Retrieve the default thread pool.
     *
     * @return the thread pool
     */
    ThreadPool getDefaultThreadPool();
}
