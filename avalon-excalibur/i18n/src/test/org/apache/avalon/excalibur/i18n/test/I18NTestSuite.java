/*
 * Copyright  The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.i18n.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A basic test suite that tests all the i18n package.
 */
public class I18NTestSuite
{
    public static Test suite()
    {
        final TestSuite suite = new TestSuite( "i18n Utilities" );
        suite.addTest( new TestSuite( ResourceManagerTestCase.class ) );
        return suite;
    }
}
