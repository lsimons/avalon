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
package org.apache.avalon.excalibur.logger.test;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * TestComponent.
 *
 * @author <a href="mailto:giacomo@apache,org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2004/04/01 04:22:07 $
 */
public class TestComponentImpl
    extends AbstractLogEnabled
    implements TestComponent
{
    public void test( Logger defaultLogger, String message )
    {
        final Logger logger = getLogger();
        //final String cat = logger.getCategory();
        //defaultLogger.info( "Category is " + cat );
        logger.debug( message );
        logger.info( message );
        logger.warn( message );
        logger.error( message );
        logger.fatalError( message );
    }
}
