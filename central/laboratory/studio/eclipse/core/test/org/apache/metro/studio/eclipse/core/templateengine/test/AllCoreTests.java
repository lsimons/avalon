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
package org.apache.metro.studio.eclipse.core.templateengine.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Metro Development Team</a>
 * 12.08.2004
 * last change:
 * 
 */
public class AllCoreTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite(
                "Test for org.apache.metro.studio.eclipse.core.templateengine");
        //$JUnit-BEGIN$
        suite.addTestSuite(ProjectManagerBasicTest.class);
        suite.addTestSuite(DirectoryTemplateManagerTest.class);
        suite.addTestSuite(ResourceTemplateManagerTest.class);
        //$JUnit-END$
        return suite;
    }
}
