/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.log.test;

import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.format.PatternFormatter;
import org.apache.log.output.io.StreamTarget;

/**
 * Test suite for inheritance features of Logger.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class InheritanceTestCase
    extends TestCase
{
    private static final String PATTERN = "%{priority}-%{message}";
    private static final PatternFormatter FORMATTER = new PatternFormatter( PATTERN );

    private static final String PATTERN2 = "Simon saids %{priority}-%{message}";
    private static final PatternFormatter FORMATTER2 = new PatternFormatter( PATTERN2 );

    private static final String MSG = "No soup for you!";
    private static final String RMSG = "DEBUG-" + MSG;
    private static final String R2MSG = "Simon saids DEBUG-" + MSG;

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

        final LogTarget[] targets1 = new LogTarget[]{target1};
        final LogTarget[] targets2 = new LogTarget[]{target2};

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
        final LogTarget[] targets = new LogTarget[]{target};

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

        final LogTarget[] targets1 = new LogTarget[]{target1};
        final LogTarget[] targets2 = new LogTarget[]{target2};

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
