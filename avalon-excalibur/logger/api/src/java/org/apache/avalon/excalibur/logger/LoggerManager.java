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

import org.apache.avalon.framework.logger.Logger;

/**
 * LoggerManager Interface.  This is the interface used to get instances of
 * a Logger for your system.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.3 $ $Date: 2004/02/25 10:33:16 $
 */
public interface LoggerManager
{
    String ROLE = LoggerManager.class.getName();

    /**
     * Return the Logger for the specified category.
     */
    Logger getLoggerForCategory( String categoryName );

    /**
     * Return the default Logger.  This is basically the same
     * as getting the Logger for the "" category.
     */
    Logger getDefaultLogger();
}
