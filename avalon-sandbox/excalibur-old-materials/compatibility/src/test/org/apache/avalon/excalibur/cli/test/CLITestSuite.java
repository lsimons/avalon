/*
 * Copyright  The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cli.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A basic test suite that tests all the IO package.
 */
public class CLITestSuite
{
    public static Test suite()
    {
        final TestSuite suite = new TestSuite( "CLI Parsing Utilities" );
        suite.addTest( new TestSuite( ClutilTestCase.class ) );
        return suite;
    }
}
