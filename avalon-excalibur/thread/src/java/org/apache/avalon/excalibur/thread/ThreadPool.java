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
package org.apache.avalon.excalibur.thread;

import org.apache.avalon.framework.activity.Executable;
import org.apache.excalibur.thread.ThreadControl;

/**
 * This class is the public frontend for the thread pool code.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @deprecated Replaced with org.apache.excalibur.thread.ThreadPool
 */
public interface ThreadPool
    extends org.apache.excalibur.thread.ThreadPool
{
    /**
     * Run work in separate thread.
     * Return a valid ThreadControl to control work thread.
     *
     * @param work the work to be executed.
     * @return the ThreadControl
     */
    ThreadControl execute( Executable work );
}