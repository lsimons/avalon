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
package org.apache.avalon.excalibur.logger;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;

/**
 * LogKitManager Interface.
 *
 * @deprecated we should use the new LoggerManager interface that directly
 *             supports the new framework Logger interface.
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2004/02/19 09:12:03 $
 */
public interface LogKitManager
{
    /**
     * Find a logger based on a category name.
     */
    Logger getLogger( String categoryName );

    /**
     * Retrieve Hierarchy for Loggers configured by the system.
     *
     * @return the Hierarchy
     */
    Hierarchy getHierarchy();
}
