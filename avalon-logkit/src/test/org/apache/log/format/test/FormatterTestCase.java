/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
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
package org.apache.log.format.test;

import junit.framework.TestCase;
import org.apache.log.ContextMap;
import org.apache.log.ContextStack;
import org.apache.log.LogEvent;
import org.apache.log.Priority;
import org.apache.log.format.Formatter;
import org.apache.log.format.PatternFormatter;
import org.apache.log.format.RawFormatter;
import org.apache.log.format.SyslogFormatter;
import org.apache.log.format.XMLFormatter;

/**
 * Test suite for the formatters.
 * TODO: Incorporate testing for ContextStack and ContextMap
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class FormatterTestCase
    extends TestCase
{
    private static String EOL = System.getProperty( "line.separator", "\n" );

    private static String M1 = "Message1";
    private static String M2 = "Message2Message2";
    private static String M3 = "Message3Message3Message3";

    private static String C1 = "Category1";
    private static String C2 = "Category2Category2";
    private static String C3 = "Category3Category3Category3";

    private static long T1 = 0;
    private static long T2 = 1;
    private static long T3 = 2;

    private static Priority P1 = Priority.FATAL_ERROR;
    private static Priority P2 = Priority.ERROR;
    private static Priority P3 = Priority.WARN;

    private static boolean mapsConfigured;
    private static ContextMap CM1 = new ContextMap();
    private static ContextMap CM2 = new ContextMap();

    private static LogEvent E1 = createEvent( C1, M1, null, T1, P1, null, CM1 );
    private static LogEvent E2 = createEvent( C2, M2, null, T2, P2, null, CM2 );
    private static LogEvent E3 = createEvent( C3, M3, null, T3, P3, null, null );

    private static String E1_XML = "<log-entry>" + EOL +
        "  <time>" + T1 + "</time>" + EOL +
        "  <priority>" + P1.getName() + "</priority>" + EOL +
        "  <category>" + C1 + "</category>" + EOL +
        "  <message><![CDATA[" + M1 + "]]></message>" + EOL +
        "</log-entry>" + EOL;

    private static String E2_XML = "<log-entry>" + EOL +
        "  <time>" + T2 + "</time>" + EOL +
        "  <priority>" + P2.getName() + "</priority>" + EOL +
        "  <category>" + C2 + "</category>" + EOL +
        "  <message><![CDATA[" + M2 + "]]></message>" + EOL +
        "</log-entry>" + EOL;

    private static String E3_XML = "<log-entry>" + EOL +
        "  <time>" + T3 + "</time>" + EOL +
        "  <priority>" + P3.getName() + "</priority>" + EOL +
        "  <category>" + C3 + "</category>" + EOL +
        "  <message><![CDATA[" + M3 + "]]></message>" + EOL +
        "</log-entry>" + EOL;

    private static int FACILITY_ID = 9 << 3; //cron
    private static String FACILITY_NAME = "cron"; //cron

    private static String E1_SYSLOG = "<" + ( 2 | FACILITY_ID ) + "> " + M1;
    private static String E2_SYSLOG = "<" + ( 3 | FACILITY_ID ) + "> " + M2;
    private static String E3_SYSLOG = "<" + ( 4 | FACILITY_ID ) + "> " + M3;

    private static String E1_SYSLOG_WB = "<" + ( 2 | FACILITY_ID ) + "> " + FACILITY_NAME + ": " + M1;
    private static String E2_SYSLOG_WB = "<" + ( 3 | FACILITY_ID ) + "> " + FACILITY_NAME + ": " + M2;
    private static String E3_SYSLOG_WB = "<" + ( 4 | FACILITY_ID ) + "> " + FACILITY_NAME + ": " + M3;

    private static String PATTERN1 = "[%8.8{category}]: %{message}" + EOL;
    private static String E1_PATTERN1 = "[Category]: " + M1 + EOL;
    private static String E2_PATTERN1 = "[Category]: " + M2 + EOL;
    private static String E3_PATTERN1 = "[Category]: " + M3 + EOL;

    private static String PATTERN2 = "[%10.{category}]: %{message}" + EOL;
    private static String E1_PATTERN2 = "[" + C1 + " ]: " + M1 + EOL;
    private static String E2_PATTERN2 = "[" + C2 + "]: " + M2 + EOL;
    private static String E3_PATTERN2 = "[" + C3 + "]: " + M3 + EOL;

    private static String PATTERN3 = "[%.10{category}]: %{message}" + EOL;
    private static String E1_PATTERN3 = "[" + C1 + "]: " + M1 + EOL;
    private static String E2_PATTERN3 = "[Category2C]: " + M2 + EOL;
    private static String E3_PATTERN3 = "[Category3C]: " + M3 + EOL;

    private static String PATTERN4 = "[%+10.{category}]: %{message}" + EOL;
    private static String E1_PATTERN4 = "[" + C1 + " ]: " + M1 + EOL;
    private static String E2_PATTERN4 = "[" + C2 + "]: " + M2 + EOL;
    private static String E3_PATTERN4 = "[" + C3 + "]: " + M3 + EOL;

    private static String PATTERN5 = "[%-10.{category}]: %{message}" + EOL;
    private static String E1_PATTERN5 = "[ " + C1 + "]: " + M1 + EOL;
    private static String E2_PATTERN5 = "[" + C2 + "]: " + M2 + EOL;
    private static String E3_PATTERN5 = "[" + C3 + "]: " + M3 + EOL;

    private static String PATTERN6 = "[%{context}]: %{message}" + EOL;
    private static String E1_PATTERN6 = "[]: " + M1 + EOL;
    private static String E2_PATTERN6 = "[]: " + M2 + EOL;
    private static String E3_PATTERN6 = "[]: " + M3 + EOL;

    private static String PATTERN7 = "[%{context:stack}]: %{message}" + EOL;
    private static String E1_PATTERN7 = "[]: " + M1 + EOL;
    private static String E2_PATTERN7 = "[]: " + M2 + EOL;
    private static String E3_PATTERN7 = "[]: " + M3 + EOL;

    private static String PATTERN8 = "[%{context:method}]: %{message}" + EOL;
    private static String E1_PATTERN8 = "[com.biz.MyObject.myMethod(MyObject:53)]: " + M1 + EOL;
    private static String E2_PATTERN8 = "[]: " + M2 + EOL;
    private static String E3_PATTERN8 = "[]: " + M3 + EOL;

    private static String CLASS_PREFIX = FormatterTestCase.class.getName() + ".";

    private static String PATTERN9 = "[%{method}]: %{message}" + EOL;
    private static String E1_PATTERN9 = "[com.biz.MyObject.myMethod(MyObject:53)]: " + M1 + EOL;
    private static String E2_PATTERN9_START = "[" + CLASS_PREFIX + "testPattern9Formatter(";
    private static String E2_PATTERN9_END = ")]: " + M2 + EOL;
    private static String E3_PATTERN9_START = "[" + CLASS_PREFIX + "testPattern9Formatter(";
    private static String E3_PATTERN9_END = ")]: " + M3 + EOL;

    private static String PATTERN10 = "[%{context:method}]: %{message}" + EOL;
    private static String E1_PATTERN10 = "[com.biz.MyObject.myMethod(MyObject:53)]: " + M1 + EOL;
    private static String E2_PATTERN10 = "[]: " + M2 + EOL;
    private static String E3_PATTERN10 = "[]: " + M3 + EOL;

    private static String PATTERN11 = "[%{context:method}]: %{message}" + EOL;
    private static String E1_PATTERN11 = "[com.biz.MyObject.myMethod(MyObject:53)]: " + M1 + EOL;
    private static String E2_PATTERN11 = "[]: " + M2 + EOL;
    private static String E3_PATTERN11 = "[]: " + M3 + EOL;

    private static LogEvent createEvent( final String category,
                                         final String message,
                                         final Throwable throwable,
                                         final long time,
                                         final Priority priority,
                                         final ContextStack contextStack,
                                         final ContextMap contextMap )
    {
        setupContext();

        final LogEvent event = new LogEvent();
        event.setCategory( category );
        event.setMessage( message );
        event.setThrowable( throwable );
        event.setTime( time );
        event.setPriority( priority );
        event.setContextStack( contextStack );
        event.setContextMap( contextMap );
        return event;
    }

    private static void setupContext()
    {
        if( !mapsConfigured )
        {
            mapsConfigured = true;
            CM1.set( "method", "com.biz.MyObject.myMethod(MyObject:53)" );
            CM1.set( "hostname", "helm.realityforge.org" );
            CM1.set( "interface", "127.0.0.1" );
            CM1.set( "user", "barney" );
            CM1.makeReadOnly();

            CM2.set( "hostname", "helm.realityforge.org" );
            CM2.set( "interface", "127.0.0.1" );
            CM2.set( "user", "barney" );
            CM2.makeReadOnly();
        }
    }

    public FormatterTestCase( final String name )
    {
        super( name );
    }

    public void testRawFormatter()
    {
        final Formatter formatter = new RawFormatter();

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "Raw formatting of E1", E1.getMessage(), result1 );
        assertEquals( "Raw formatting of E2", E2.getMessage(), result2 );
        assertEquals( "Raw formatting of E3", E3.getMessage(), result3 );
    }

    public void testXMLFormatter()
    {
        final Formatter formatter = new XMLFormatter();

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "XML formatting of E1", E1_XML, result1 );
        assertEquals( "XML formatting of E2", E2_XML, result2 );
        assertEquals( "XML formatting of E3", E3_XML, result3 );
    }

    public void testSyslogFormatter()
    {
        final Formatter formatter = new SyslogFormatter( FACILITY_ID, false );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "SYSLOG formatting of E1", E1_SYSLOG, result1 );
        assertEquals( "SYSLOG formatting of E2", E2_SYSLOG, result2 );
        assertEquals( "SYSLOG formatting of E3", E3_SYSLOG, result3 );
    }

    public void testSyslogWithBannerFormatter()
    {
        final Formatter formatter = new SyslogFormatter( FACILITY_ID, true );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "SYSLOG with banner formatting of E1", E1_SYSLOG_WB, result1 );
        assertEquals( "SYSLOG with banner formatting of E2", E2_SYSLOG_WB, result2 );
        assertEquals( "SYSLOG with banner formatting of E3", E3_SYSLOG_WB, result3 );
    }

    public void testPattern1Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN1 );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "Pattern1 formatting of E1", E1_PATTERN1, result1 );
        assertEquals( "Pattern1 formatting of E2", E2_PATTERN1, result2 );
        assertEquals( "Pattern1 formatting of E3", E3_PATTERN1, result3 );
    }

    public void testPattern2Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN2 );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "Pattern2 formatting of E1", E1_PATTERN2, result1 );
        assertEquals( "Pattern2 formatting of E2", E2_PATTERN2, result2 );
        assertEquals( "Pattern2 formatting of E3", E3_PATTERN2, result3 );
    }

    public void testPattern3Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN3 );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "Pattern3 formatting of E1", E1_PATTERN3, result1 );
        assertEquals( "Pattern3 formatting of E2", E2_PATTERN3, result2 );
        assertEquals( "Pattern3 formatting of E3", E3_PATTERN3, result3 );
    }

    public void testPattern4Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN4 );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "Pattern4 formatting of E1", E1_PATTERN4, result1 );
        assertEquals( "Pattern4 formatting of E2", E2_PATTERN4, result2 );
        assertEquals( "Pattern4 formatting of E3", E3_PATTERN4, result3 );
    }

    public void testPattern5Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN5 );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "Pattern5 formatting of E1", E1_PATTERN5, result1 );
        assertEquals( "Pattern5 formatting of E2", E2_PATTERN5, result2 );
        assertEquals( "Pattern5 formatting of E3", E3_PATTERN5, result3 );
    }

    public void testPattern6Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN6 );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "Pattern6 formatting of E1", E1_PATTERN6, result1 );
        assertEquals( "Pattern6 formatting of E2", E2_PATTERN6, result2 );
        assertEquals( "Pattern6 formatting of E3", E3_PATTERN6, result3 );
    }

    public void testPattern7Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN7 );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "Pattern7 formatting of E1", E1_PATTERN7, result1 );
        assertEquals( "Pattern7 formatting of E2", E2_PATTERN7, result2 );
        assertEquals( "Pattern7 formatting of E3", E3_PATTERN7, result3 );
    }

    public void testPattern8Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN8 );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "Pattern8 formatting of E1", E1_PATTERN8, result1 );
        assertEquals( "Pattern8 formatting of E2", E2_PATTERN8, result2 );
        assertEquals( "Pattern8 formatting of E3", E3_PATTERN8, result3 );
    }
    /*
        public void testPattern9Formatter()
        {
            final Formatter formatter = new PatternFormatter( PATTERN9 );

            final String result1 = formatter.format( E1 );
            final String result2 = formatter.format( E2 );
            final String result3 = formatter.format( E3 );

            System.out.println( "results1: " + result1 );
            System.out.println( "results2: " + result2 );
            System.out.println( "results3: " + result3 );

            assertEquals( "Pattern9 formatting of E1", E1_PATTERN9, result1 );
            assertTrue( "Pattern9 formatting of E2", result2.startsWith( E2_PATTERN9_START ) );
            assertTrue( "Pattern9 end formatting of E2", result2.endsWith( E2_PATTERN9_END ) );
            assertTrue( "Pattern9 formatting of E3", result3.startsWith( E3_PATTERN9_START ) );
            assertTrue( "Pattern9 end formatting of E3", result3.endsWith( E3_PATTERN9_END ) );
        }
    */
    public void testPattern10Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN10 );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "Pattern10 formatting of E1", E1_PATTERN10, result1 );
        assertEquals( "Pattern10 formatting of E2", E2_PATTERN10, result2 );
        assertEquals( "Pattern10 formatting of E3", E3_PATTERN10, result3 );
    }

    public void testPattern11Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN11 );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquals( "Pattern11 formatting of E1", E1_PATTERN11, result1 );
        assertEquals( "Pattern11 formatting of E2", E2_PATTERN11, result2 );
        assertEquals( "Pattern11 formatting of E3", E3_PATTERN11, result3 );
    }
}
