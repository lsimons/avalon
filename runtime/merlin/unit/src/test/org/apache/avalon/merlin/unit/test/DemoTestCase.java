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

package org.apache.avalon.merlin.unit.test;

import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.avalon.merlin.unit.MerlinTestSuite;

public class DemoTestCase extends TestCase
{

    public static Test suite()
    {
        return new MerlinTestSuite();
    }

    public void setUp()
    {
        System.out.println( "setup" );
    }

    public void tearDown()
        throws Exception
    {
        System.out.println( "teardown" );
    }
        
    public void testSomething()
        throws Exception
    {
        System.out.println( "test" );
    }

}
