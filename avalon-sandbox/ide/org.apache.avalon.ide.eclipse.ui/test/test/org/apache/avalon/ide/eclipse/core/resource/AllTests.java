/*
 * Created on 03.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
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
