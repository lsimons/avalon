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

package org.apache.excalibur.instrument;

/**
 * An InstrumentManager which doesn't do anything.
 *
 * @author <a href="mail at leosimons dot com">Leo Simons</a>
 * @version $Id: NoopInstrumentManager.java,v 1.2 2004/02/25 09:20:19 niclas Exp $
 */
public class NoopInstrumentManager implements InstrumentManager
{
    public void registerInstrumentable( Instrumentable instrumentable, String instrumentableName )
    {
        // do nothing
    }
}
