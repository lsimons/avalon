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

package org.apache.avalon.logging.logkit;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.log.LogTarget;

/**
 * LogTargetFactory Interface.  New instances of log target factories
 * are created by the logging manager factory.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2004/02/04 20:48:56 $
 * @since 4.0
 */
public interface LogTargetFactory
{
    /**
     * Create a LogTarget based on a Configuration
     */
    LogTarget createTarget( Configuration configuration ) throws LogTargetException;
}
