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

package org.apache.avalon.finder.ecm.test;

import junit.framework.TestCase;

import org.apache.avalon.finder.ecm.ECM;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * DefaultFinder testcase.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/04/04 15:00:55 $
 */
public class ECMTestCase extends TestCase
{
    //--------------------------------------------------------
    // constructors
    //--------------------------------------------------------

   /**
    * @param name the name of the test case
    */
    public ECMTestCase( String name )
    {
        super( name );
    }

    //--------------------------------------------------------
    // testcase
    //--------------------------------------------------------

    public void testECMConstructor() throws Exception
    {
        try
        {
            ServiceManager ecm = new ECM( null, null, null );
            fail( "did not throw an NPE" );
        }
        catch( NullPointerException npe )
        {
            // good
        }
        catch( Throwable bad )
        {
            fail( "unexpected constructor error: " + bad.toString() );
        }
    }
}
