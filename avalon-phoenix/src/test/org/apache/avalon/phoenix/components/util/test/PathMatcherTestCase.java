/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.util.test;

import junit.framework.TestCase;
import org.apache.avalon.phoenix.components.util.PathMatcher;

/**
 *  An basic test case for the PathMatcher.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2002/10/30 07:42:21 $
 */
public class PathMatcherTestCase
    extends TestCase
{
    private static final String PATH1 = "SAR-INF/lib/foo.jar";
    private static final String PATH2 = "SAR-INF/lib/bar.jar";
    private static final String PATH3 = "SAR-INF/classes";

    public PathMatcherTestCase( final String name )
    {
        super( name );
    }

    public void testMatch1()
    {
        final String[] includes = new String[]{"**/*"};
        final String[] excludes = new String[]{};
        final PathMatcher matcher = new PathMatcher( includes, excludes );

        assertTrue( PATH1 + " matches", matcher.match( PATH1 ) );
        assertTrue( PATH2 + " matches", matcher.match( PATH2 ) );
        assertTrue( PATH3 + " matches", matcher.match( PATH3 ) );
    }

    public void testMatch2()
    {
        final String[] includes = new String[]{"**/*.jar"};
        final String[] excludes = new String[]{};
        final PathMatcher matcher = new PathMatcher( includes, excludes );

        assertTrue( PATH1 + " matches", matcher.match( PATH1 ) );
        assertTrue( PATH2 + " matches", matcher.match( PATH2 ) );
        assertTrue( PATH3 + " not matches", !matcher.match( PATH3 ) );
    }

    public void testMatch3()
    {
        final String[] includes = new String[]{"**/*.jar"};
        final String[] excludes = new String[]{"**/bar*"};
        final PathMatcher matcher = new PathMatcher( includes, excludes );

        assertTrue( PATH1 + " matches", matcher.match( PATH1 ) );
        assertTrue( PATH2 + " not matches", !matcher.match( PATH2 ) );
        assertTrue( PATH3 + " not matches", !matcher.match( PATH3 ) );
    }

}
