/* 
 * Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.avalon.excalibur.concurrent;

/**
 * Also called counting semaphores, Djikstra semaphores are used to control
 * access to a set of resources. A Djikstra semaphore has a count associated
 * with it and each acquire() call reduces the count. A thread that tries to
 * acquire() a Djikstra semaphore with a zero count blocks until someone else
 * calls release() thus increasing the count.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/04/26 10:23:06 $
 * @since 4.0
 * @deprecated Replaced by {@link DijkstraSemaphore}.
 */
public class DjikstraSemaphore
        extends DijkstraSemaphore
{
    /**
     * Creates a Djikstra semaphore with the specified max count and initial
     * count set to the max count (all resources released)
     * @param maxCount is the max semaphores that can be acquired
     */
    public DjikstraSemaphore( int maxCount )
    {
        super( maxCount, maxCount );
    }

    /**
     * Creates a Djikstra semaphore with the specified max count and an initial
     * count of acquire() operations that are assumed to have already been
     * performed.
     * @param maxCount is the max semaphores that can be acquired
     * @param initialCount is the current count (setting it to zero means all
     * semaphores have already been acquired). 0 <= initialCount <= maxCount
     */
    public DjikstraSemaphore( int maxCount, int initialCount )
    {
        super( maxCount, initialCount );
    }
}

