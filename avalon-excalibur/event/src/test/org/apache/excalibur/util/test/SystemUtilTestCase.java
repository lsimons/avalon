/*
 * Copyright  The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.util.test;

import junit.framework.TestCase;
import org.apache.excalibur.util.SystemUtil;

/**
 * This is used to test SystemUtil for correctness.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class SystemUtilTestCase
    extends TestCase
{
    public SystemUtilTestCase( String name )
    {
        super( name );
    }

    public void testSystemUtil()
    {
        System.out.println( "Number of Processors: " + SystemUtil.numProcessors() );
        System.out.println( "CPU Info:             " + SystemUtil.cpuInfo() );
        System.out.println( "Architecture:         " + SystemUtil.architecture() );
        System.out.println( "Operating System:     " + SystemUtil.operatingSystem() );
        System.out.println( "OS Version:           " + SystemUtil.osVersion() );

        assertEquals( SystemUtil.architecture(), System.getProperty( "os.arch" ) );
        assertEquals( SystemUtil.operatingSystem(), System.getProperty( "os.name" ) );
        assertEquals( SystemUtil.osVersion(), System.getProperty( "os.version" ) );
    }
}
