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

package org.apache.avalon.fortress.util;

import org.apache.avalon.fortress.ContainerManagerConstants;

/**
 * Provides constants used to access the Context object for containers.
 *
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 * @version CVS $Revision: 1.7 $ $Date: 2004/02/21 13:27:00 $
 */
public interface ContextManagerConstants extends ContainerManagerConstants
{
    String LOG_CATEGORY = "impl.logcategory";

    String LOGGER_MANAGER_CONFIGURATION = "impl.logManager.config";
    String LOGGER_MANAGER_CONFIGURATION_URI = "impl.logManager.config.uri";

    String INSTRUMENT_MANAGER_CONFIGURATION = "impl.instrumentManager.config";
    String INSTRUMENT_MANAGER_CONFIGURATION_URI = "impl.instrumentManager.config.uri";

    String ROLE_MANAGER_CONFIGURATION = "impl.roleManager.config";
    String ROLE_MANAGER_CONFIGURATION_URI = "impl.roleManager.config.uri";

    String CONFIGURATION_URI = "impl.configuration.uri";
}
