/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.test;

import junit.framework.TestCase;
import org.apache.log.Hierarchy;
import org.apache.log.Logger;

/**
 * Test suite for logger listener features of Logger.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class LoggerListenerTestCase
    extends TestCase
{
    public LoggerListenerTestCase( final String name )
    {
        super( name );
    }

    public void testUnicastLoggerListener()
    {
        final Hierarchy hierarchy = new Hierarchy();
        final RecordingLoggerListener listener = new RecordingLoggerListener();

        try
        {
            hierarchy.addLoggerListener( listener );
            hierarchy.addLoggerListener( listener );

            fail( "You should only be able to add one listener." );
        }
        catch (UnsupportedOperationException uoe)
        {
            // It passed, yay!
        }
    }

    public void testPriorityInheritance()
        throws Exception
    {
        final RecordingLoggerListener listener = new RecordingLoggerListener();
        final Hierarchy hierarchy = new Hierarchy();
        hierarchy.addLoggerListener( listener );

        final Logger root = hierarchy.getRootLogger();
        final Logger l1 = root.getChildLogger( "logger1" );
        final Logger l2 = root.getChildLogger( "logger2" );
        final Logger l3 = root.getChildLogger( "logger1.logger3" );
        final Logger l4 = root.getChildLogger( "logger5.logger4" );
        final Logger l5 = root.getChildLogger( "logger5" );

        final Logger[] loggers = listener.getLoggers();
        assertEquals( "Logger Count", 5, loggers.length );
        assertEquals( "Logger[0]", l1, loggers[ 0 ] );
        assertEquals( "Logger[1]", l2, loggers[ 1 ] );
        assertEquals( "Logger[2]", l3, loggers[ 2 ] );
        assertEquals( "Logger[3]", l5, loggers[ 3 ] );
        assertEquals( "Logger[4]", l4, loggers[ 4 ] );
    }
}
