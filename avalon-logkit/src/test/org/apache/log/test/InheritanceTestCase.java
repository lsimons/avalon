/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.test;

import java.io.ByteArrayOutputStream;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.format.PatternFormatter;
import org.apache.log.output.io.StreamTarget;
import junit.framework.TestCase;

/**
 * Test suite for inheritance features of Logger.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class InheritanceTestCase
    extends TestCase
{
    private final static String POSITIVE = "+1 Positive - yay - lets do the chicken dance.";

    private final static String PATTERN = "%{priority}-%{message}";
    private final static PatternFormatter FORMATTER = new PatternFormatter( PATTERN );

    private final static String PATTERN2 = "Simon saids %{priority}-%{message}";
    private final static PatternFormatter FORMATTER2 = new PatternFormatter( PATTERN2 );

    private final static String MSG = "No soup for you!";
    private final static String RMSG = "DEBUG-" + MSG;
    private final static String R2MSG = "Simon saids DEBUG-" + MSG;

    public InheritanceTestCase( final String name )
    {
        super( name );
    }

    private String getResult( final ByteArrayOutputStream output )
    {
        final String result = output.toString();
        output.reset();
        return result;
    }

    public void testPriorityInheritance()
        throws Exception
    {
        final Hierarchy hierarchy = new Hierarchy();
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final StreamTarget target = new StreamTarget( output, FORMATTER );
        hierarchy.setDefaultLogTarget( target );

        final Logger b = hierarchy.getLoggerFor( "b" );
        final Logger bc = hierarchy.getLoggerFor( "b.c" );
        final Logger bcd = hierarchy.getLoggerFor( "b.c.d" );

        b.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );

        b.setPriority( Priority.WARN );
        b.debug( MSG );
        assertEquals( "Priority debug output", "", getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Priority debug output", "", getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Priority debug output", "", getResult( output ) );

        bc.setPriority( Priority.DEBUG );
        b.debug( MSG );
        assertEquals( "Priority debug output", "", getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );

        bcd.setPriority( Priority.WARN );
        b.debug( MSG );
        assertEquals( "Priority debug output", "", getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Priority debug output", "", getResult( output ) );

        bcd.unsetPriority();
        b.debug( MSG );
        assertEquals( "Priority debug output", "", getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );

        bc.unsetPriority();
        b.debug( MSG );
        assertEquals( "Priority debug output", "", getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Priority debug output", "", getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Priority debug output", "", getResult( output ) );

        b.unsetPriority();
        b.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );

        bc.setPriority( Priority.WARN );
        b.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Priority debug output", "", getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Priority debug output", "", getResult( output ) );

        b.unsetPriority( true );
        b.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Priority debug output", RMSG, getResult( output ) );
    }

    public void testLogTargetInheritance()
    {
        final ByteArrayOutputStream output1 = new ByteArrayOutputStream();
        final StreamTarget target1 = new StreamTarget( output1, FORMATTER );
        final ByteArrayOutputStream output2 = new ByteArrayOutputStream();
        final StreamTarget target2 = new StreamTarget( output2, FORMATTER2 );

        final LogTarget[] targets1 = new LogTarget[] { target1 };
        final LogTarget[] targets2 = new LogTarget[] { target2 };

        final Hierarchy hierarchy = new Hierarchy();
        hierarchy.setDefaultLogTarget( target1 );

        final Logger b = hierarchy.getLoggerFor( "b" );
        final Logger bc = hierarchy.getLoggerFor( "b.c" );
        final Logger bcd = hierarchy.getLoggerFor( "b.c.d" );

        b.setLogTargets( targets1 );
        b.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );
        bc.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );
        bcd.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );

        b.setLogTargets( targets2 );
        b.debug( MSG );
        assertEquals( "LogTarget inherit debug output", R2MSG, getResult( output2 ) );
        bc.debug( MSG );
        assertEquals( "LogTarget inherit debug output", R2MSG, getResult( output2 ) );
        bcd.debug( MSG );
        assertEquals( "LogTarget inherit debug output", R2MSG, getResult( output2 ) );

        bc.setLogTargets( targets1 );
        b.debug( MSG );
        assertEquals( "LogTarget inherit debug output", R2MSG, getResult( output2 ) );
        bc.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );
        bcd.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );

        bcd.setLogTargets( targets2 );
        b.debug( MSG );
        assertEquals( "LogTarget inherit debug output", R2MSG, getResult( output2 ) );
        bc.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );
        bcd.debug( MSG );
        assertEquals( "LogTarget inherit debug output", R2MSG, getResult( output2 ) );

        bcd.unsetLogTargets();
        b.debug( MSG );
        assertEquals( "LogTarget inherit debug output", R2MSG, getResult( output2 ) );
        bc.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );
        bcd.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );

        bc.unsetLogTargets();
        b.debug( MSG );
        assertEquals( "LogTarget inherit debug output", R2MSG, getResult( output2 ) );
        bc.debug( MSG );
        assertEquals( "LogTarget inherit debug output", R2MSG, getResult( output2 ) );
        bcd.debug( MSG );
        assertEquals( "LogTarget inherit debug output", R2MSG, getResult( output2 ) );

        b.unsetLogTargets();
        b.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );
        bc.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );
        bcd.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );

        bc.setLogTargets( targets2 );
        b.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );
        bc.debug( MSG );
        assertEquals( "LogTarget inherit debug output", R2MSG, getResult( output2 ) );
        bcd.debug( MSG );
        assertEquals( "LogTarget inherit debug output", R2MSG, getResult( output2 ) );

        b.unsetLogTargets( true );
        b.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );
        bc.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );
        bcd.debug( MSG );
        assertEquals( "LogTarget inherit debug output", RMSG, getResult( output1 ) );
    }

    public void testAdditivity()
        throws Exception
    {
        final Hierarchy hierarchy = new Hierarchy();
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final StreamTarget target = new StreamTarget( output, FORMATTER );
        final LogTarget[] targets = new LogTarget[] { target };

        final Logger b = hierarchy.getLoggerFor( "b" );
        final Logger bc = hierarchy.getLoggerFor( "b.c" );
        final Logger bcd = hierarchy.getLoggerFor( "b.c.d" );
        
        b.setLogTargets( targets );
        bc.setLogTargets( targets );
        bcd.setLogTargets( targets );

        b.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );

        b.setAdditivity( true );
        b.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );

        bc.setAdditivity( true );
        b.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Additivity debug output", RMSG + RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );

        bcd.setAdditivity( true );
        b.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Additivity debug output", RMSG + RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Additivity debug output", RMSG + RMSG + RMSG, getResult( output ) );

        bcd.setAdditivity( false );
        b.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Additivity debug output", RMSG + RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );

        bc.setAdditivity( false );
        b.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );

        b.setAdditivity( false );
        b.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );
        bc.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );
        bcd.debug( MSG );
        assertEquals( "Additivity debug output", RMSG, getResult( output ) );
    }

    public void testChainedAdditivity()
        throws Exception
    {
        final Hierarchy hierarchy = new Hierarchy();
        final ByteArrayOutputStream output1 = new ByteArrayOutputStream();
        final ByteArrayOutputStream output2 = new ByteArrayOutputStream();
        final StreamTarget target1 = new StreamTarget( output1, FORMATTER );
        final StreamTarget target2 = new StreamTarget( output2, FORMATTER );

        final LogTarget[] targets1 = new LogTarget[] { target1 };
        final LogTarget[] targets2 = new LogTarget[] { target2 };

        final Logger b = hierarchy.getLoggerFor( "b" );
        final Logger bc = hierarchy.getLoggerFor( "b.c" );
        final Logger bcd = hierarchy.getLoggerFor( "b.c.d" );
        
        b.setLogTargets( targets1 );
        bc.setLogTargets( targets2 );
        bc.setAdditivity( true );
        bcd.setAdditivity( true );

        b.debug( MSG );
        assertEquals( "Additivity debug output1", RMSG, getResult( output1 ) );
        bc.debug( MSG );
        assertEquals( "Additivity debug output1", RMSG, getResult( output1 ) );
        assertEquals( "Additivity debug output2", RMSG, getResult( output2 ) );
        bcd.debug( MSG );
        assertEquals( "Additivity debug output1", RMSG, getResult( output1 ) );
        assertEquals( "Additivity debug output2", RMSG, getResult( output2 ) );
    }
}
