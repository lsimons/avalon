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

package org.apache.avalon.excalibur.datasource;

import java.sql.SQLException;

/**
 * Exception that is thrown when there is no valid Connection wrapper available
 * in the ClassLoader.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.3 $ $Date: 2004/02/25 10:33:11 $
 * @since 4.1
 */
public class NoValidConnectionException extends SQLException
{

    public NoValidConnectionException()
    {
        super();
    }

    public NoValidConnectionException( String message )
    {
        super( message );
    }

    public NoValidConnectionException( String message, String SQLState )
    {
        super( message, SQLState );
    }

    public NoValidConnectionException( String message, String SQLState, int vendorCode )
    {
        super( message, SQLState, vendorCode );
    }
}
