/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.format.test;

import org.apache.log.ContextStack;
import org.apache.log.Formatter;
import org.apache.log.LogEvent;
import org.apache.log.Priority;
import org.apache.log.format.RawFormatter;
import org.apache.log.format.XMLFormatter;
import org.apache.log.format.SyslogFormatter;
import org.apache.log.format.PatternFormatter;
import org.apache.testlet.AbstractTestlet;

/**
 * Test suite for the formatters.
 * TODO: Incorporate testing for ContextStack and ContextMap
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class FormatterTestlet
    extends AbstractTestlet
{
    private static String EOL  = System.getProperty( "line.separator", "\n" );

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

    private static LogEvent E1 = createEvent( C1, M1, null, T1, P1, null );
    private static LogEvent E2 = createEvent( C2, M2, null, T2, P2, null );
    private static LogEvent E3 = createEvent( C3, M3, null, T3, P3, null );

    private static String E1_XML =  "<log-entry>" + EOL + 
        "  <time>" + T1 + "</time>" + EOL + 
        "  <priority>" + P1.getName() + "</priority>" + EOL + 
        "  <category>" + C1 + "</category>" + EOL +
        "  <message><![CDATA[" + M1 + "]]></message>" + EOL + 
        "</log-entry>" + EOL;

    private static String E2_XML =  "<log-entry>" + EOL + 
        "  <time>" + T2 + "</time>" + EOL + 
        "  <priority>" + P2.getName() + "</priority>" + EOL + 
        "  <category>" + C2 + "</category>" + EOL +
        "  <message><![CDATA[" + M2 + "]]></message>" + EOL + 
        "</log-entry>" + EOL;

    private static String E3_XML =  "<log-entry>" + EOL + 
        "  <time>" + T3 + "</time>" + EOL + 
        "  <priority>" + P3.getName() + "</priority>" + EOL + 
        "  <category>" + C3 + "</category>" + EOL +
        "  <message><![CDATA[" + M3 + "]]></message>" + EOL + 
        "</log-entry>" + EOL;

    private static int FACILITY_ID = 9<<3; //cron
    private static String FACILITY_NAME = "cron"; //cron

    private static String E1_SYSLOG = "<" + (2|FACILITY_ID) + "> " + M1;
    private static String E2_SYSLOG = "<" + (3|FACILITY_ID) + "> " + M2;
    private static String E3_SYSLOG = "<" + (4|FACILITY_ID) + "> " + M3;

    private static String E1_SYSLOG_WB = "<" + (2|FACILITY_ID) + "> " + FACILITY_NAME + ": " + M1;
    private static String E2_SYSLOG_WB = "<" + (3|FACILITY_ID) + "> " + FACILITY_NAME + ": " + M2;
    private static String E3_SYSLOG_WB = "<" + (4|FACILITY_ID) + "> " + FACILITY_NAME + ": " + M3;

    private static String PATTERN1 = "[%8.8{category}]: %{message}\\n";
    private static String E1_PATTERN1 = "[Category]: " + M1 + "\n";
    private static String E2_PATTERN1 = "[Category]: " + M2 + "\n";
    private static String E3_PATTERN1 = "[Category]: " + M3 + "\n";

    private static String PATTERN2 = "[%10.{category}]: %{message}\\n";
    private static String E1_PATTERN2 = "[" + C1 + " ]: " + M1 + "\n";
    private static String E2_PATTERN2 = "[" + C2 + "]: " + M2 + "\n";
    private static String E3_PATTERN2 = "[" + C3 + "]: " + M3 + "\n";

    private static String PATTERN3 = "[%.10{category}]: %{message}\\n";
    private static String E1_PATTERN3 = "[" + C1 + "]: " + M1 + "\n";
    private static String E2_PATTERN3 = "[Category2C]: " + M2 + "\n";
    private static String E3_PATTERN3 = "[Category3C]: " + M3 + "\n";

    private static String PATTERN4 = "[%+10.{category}]: %{message}\\n";
    private static String E1_PATTERN4 = "[" + C1 + " ]: " + M1 + "\n";
    private static String E2_PATTERN4 = "[" + C2 + "]: " + M2 + "\n";
    private static String E3_PATTERN4 = "[" + C3 + "]: " + M3 + "\n";

    private static String PATTERN5 = "[%-10.{category}]: %{message}\\n";
    private static String E1_PATTERN5 = "[ " + C1 + "]: " + M1 + "\n";
    private static String E2_PATTERN5 = "[" + C2 + "]: " + M2 + "\n";
    private static String E3_PATTERN5 = "[" + C3 + "]: " + M3 + "\n";

    private static LogEvent createEvent( final String category,
                                         final String message,
                                         final Throwable throwable,
                                         final long time,
                                         final Priority priority,
                                         final ContextStack contextStack )
    {
        final LogEvent event = new LogEvent();
        event.setCategory( category );
        event.setMessage( message );
        event.setThrowable( throwable );
        event.setTime( time );
        event.setPriority( priority );
        event.setContextStack( contextStack );
        return event;
    }

    public void testRawFormatter()
    {
        final Formatter formatter = new RawFormatter();

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquality( "Raw formatting of E1", E1.getMessage(), result1 );
        assertEquality( "Raw formatting of E2", E2.getMessage(), result2 );
        assertEquality( "Raw formatting of E3", E3.getMessage(), result3 );
    }

    public void testXMLFormatter()
    {
        final Formatter formatter = new XMLFormatter();

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquality( "XML formatting of E1", E1_XML, result1 );
        assertEquality( "XML formatting of E2", E2_XML, result2 );
        assertEquality( "XML formatting of E3", E3_XML, result3 );
    }

    public void testSyslogFormatter()
    {
        final Formatter formatter = new SyslogFormatter( FACILITY_ID, false );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquality( "SYSLOG formatting of E1", E1_SYSLOG, result1 );
        assertEquality( "SYSLOG formatting of E2", E2_SYSLOG, result2 );
        assertEquality( "SYSLOG formatting of E3", E3_SYSLOG, result3 );
    }

    public void testSyslogWithBannerFormatter()
    {
        final Formatter formatter = new SyslogFormatter( FACILITY_ID, true );

        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquality( "SYSLOG with banner formatting of E1", E1_SYSLOG_WB, result1 );
        assertEquality( "SYSLOG with banner formatting of E2", E2_SYSLOG_WB, result2 );
        assertEquality( "SYSLOG with banner formatting of E3", E3_SYSLOG_WB, result3 );
    }

    public void testPattern1Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN1 );
        
        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );
        
        assertEquality( "Pattern1 formatting of E1", E1_PATTERN1, result1 );
        assertEquality( "Pattern1 formatting of E2", E2_PATTERN1, result2 );
        assertEquality( "Pattern1 formatting of E3", E3_PATTERN1, result3 );
    }

    public void testPattern2Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN2 );
        
        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquality( "Pattern2 formatting of E1", E1_PATTERN2, result1 );
        assertEquality( "Pattern2 formatting of E2", E2_PATTERN2, result2 );
        assertEquality( "Pattern2 formatting of E3", E3_PATTERN2, result3 );
    }

    public void testPattern3Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN3 );
        
        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );
        
        assertEquality( "Pattern3 formatting of E1", E1_PATTERN3, result1 );
        assertEquality( "Pattern3 formatting of E2", E2_PATTERN3, result2 );
        assertEquality( "Pattern3 formatting of E3", E3_PATTERN3, result3 );
    }

    public void testPattern4Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN4 );
        
        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquality( "Pattern4 formatting of E1", E1_PATTERN4, result1 );
        assertEquality( "Pattern4 formatting of E2", E2_PATTERN4, result2 );
        assertEquality( "Pattern4 formatting of E3", E3_PATTERN4, result3 );
    }

    public void testPattern5Formatter()
    {
        final Formatter formatter = new PatternFormatter( PATTERN5 );
        
        final String result1 = formatter.format( E1 );
        final String result2 = formatter.format( E2 );
        final String result3 = formatter.format( E3 );

        assertEquality( "Pattern5 formatting of E1", E1_PATTERN5, result1 );
        assertEquality( "Pattern5 formatting of E2", E2_PATTERN5, result2 );
        assertEquality( "Pattern5 formatting of E3", E3_PATTERN5, result3 );
    }
}
