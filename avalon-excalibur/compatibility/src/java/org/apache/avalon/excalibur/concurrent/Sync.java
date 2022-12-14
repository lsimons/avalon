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
 * The interface to synchronization objects.
 *
 * @deprecated use EDU.oswego.cs.dl.util.concurrent.Sync instead
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2004/04/26 10:23:06 $
 * @since 4.0
 */
public interface Sync
{
    /**
     * Aquire access to resource.
     * This method will block until resource aquired.
     *
     * @throws InterruptedException if an error occurs
     */
    void acquire()
            throws InterruptedException;

    /**
     * Aquire access to resource.
     * This method will block for a maximum of msec.
     *
     * @param msec the duration to wait for lock to be released
     * @return true if lock aquired, false on timeout
     * @throws InterruptedException if an error occurs
     */
    boolean attempt( long msec )
            throws InterruptedException;

    /**
     * Release lock.
     */
    void release();
}
