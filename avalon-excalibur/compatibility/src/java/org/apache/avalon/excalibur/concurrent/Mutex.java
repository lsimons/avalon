/* 
 * Copyright 2002-2004 Apache Software Foundation
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
 * A mutual exclusion {@link Semaphore}.
 *
 * @deprecated use EDU.oswego.cs.dl.util.concurrent.Mutex instead
 *
 * @version CVS $Revision: 1.2 $ $Date: 2004/02/24 09:06:45 $
 * @since 4.0
 */

public class Mutex
    extends Semaphore
{
    /** Initialize the Mutex */
    public Mutex()
    {
        super( 1 );
    }
}
