/*
   
      Copyright 2004. The Apache Software Foundation.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. 
   
 */
package test.org.apache.avalon.ide.eclipse.core.resource;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class AllTests
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(AllTests.class);
    }

    public static Test suite()
    {
        TestSuite suite =
            new TestSuite("Test for test.org.apache.avalon.ide.eclipse.core.resource");
        //$JUnit-BEGIN$
        suite.addTestSuite(ProjectResourceManagerTest.class);
        //$JUnit-END$
        return suite;
    }
}
