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
package org.apache.excalibur.event.impl;

import org.apache.excalibur.event.DequeueInterceptor;
import org.apache.excalibur.event.Source;

/**
 * The dequeue executable interface describes operations that
 * are executed before and after elements are pulled from a
 * queue.
 *
 * @version $Revision: 1.2 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class NullDequeueInterceptor implements DequeueInterceptor
{

    /**
     * An operation executed before dequeing events from
     * the queue. The size of the queue is passed in so the
     * implementation can determine to execute based on the
     * size of the queue.
     * @since Feb 10, 2003
     *
     * @param context
     *  The source from which the dequeue is performed.
     */
    public void before(Source context) {}

    /**
     * An operation executed after dequeing events from
     * the queue. The size of the queue is passed in so the
     * implementation can determine to execute based on the
     * size of the queue.
     * @since Feb 10, 2003
     *
     * @param context
     *  The source from which the dequeue is performed.
     */
    public void after(Source context) {}

}
