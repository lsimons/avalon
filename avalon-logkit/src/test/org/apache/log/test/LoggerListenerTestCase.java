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

    public void testRemoveLoggerListener()
    {
        final Hierarchy hierarchy = new Hierarchy();
        final RecordingLoggerListener listener = new RecordingLoggerListener();

        hierarchy.addLoggerListener( listener );
        hierarchy.removeLoggerListener( listener );
        hierarchy.addLoggerListener( listener );

        // If no exceptions have been thrown, we are in business!
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
