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

package datasources;

import org.apache.avalon.cornerstone.services.datasources.DataSourceSelector;

import org.apache.avalon.merlin.unit.AbstractMerlinTestCase;

/**
 * Hello Test Case.
 *
 * @author mcconnell@apache.org
 */
public class StandardTestCase extends AbstractMerlinTestCase
{

    //--------------------------------------------------------
    // constructors
    //--------------------------------------------------------

   /**
    * @param name the name of the test case
    * @param root the merlin system install directory
    */
    public StandardTestCase( String name )
    {
        super( 
          MAVEN_TARGET_CLASSES_DIR, 
          MERLIN_DEFAULT_CONFIG_FILE, 
          MERLIN_INFO_OFF, 
          MERLIN_DEBUG_ON, 
          name );
    }

    //--------------------------------------------------------
    // testcase
    //--------------------------------------------------------

    public void testService() throws Exception
    {
        Object dss = resolve( "/datasources/manager" );
        assertNotNull( dss );
        getLogger().info( "Selector established established." );
    }
}

