/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.console;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Provides convenient time utilities for Velocity.
 *
 * @author <a href="mailto:nathan@esha.com">Nathan Bubna</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class TimeTool
{
    private final static long SECOND = 1000;
    private final static long MINUTE = SECOND * 60;
    private final static long HOUR = MINUTE * 60;
    private final static long DAY = HOUR * 24;
    private final static long YEAR = DAY * 365;
    
    /**
     * This is probably going to be the most used method.  
     * As such, here's the some quick examples
     *
     *   Examples: "E, MMMM d" will result in "Tue, July 7"
     *             "EEE, M/dd/yyyy (H:m)" will result in "Tuesday, 7/07/1996 (14:12)"
     *
     * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>
     * @param format SimpleDateFormat (e.g. "MM/dd/yyyy") that you wish to use for the given date
     * @param date that is a Date
     * @return String representation of the given date/time in the given format
     */
    public static String formatDate( final String format, final long date )
    {
        final SimpleDateFormat formatter = new SimpleDateFormat( format );
        return formatter.format( new Date( date ) );
    }

    public static String formatDuration( final long duration )
    {
        return formatDuration( duration, Integer.MAX_VALUE );
    }

    public static String formatDuration( long duration, int accuracy )
    {
        final long years = duration / YEAR;
        duration -= years * YEAR;
        final long days = duration / DAY;
        duration -= days * DAY;
        final long hours = duration / HOUR;
        duration -= hours * HOUR;
        final long minutes = duration / MINUTE;
        duration -= minutes * MINUTE;
        final long seconds = duration / SECOND;
        duration -= seconds * SECOND;

        final StringBuffer sb = new StringBuffer();

        if( 0 < years )
        {
            if( 0 != sb.length() ) sb.append( ", " );
            sb.append( years );
            sb.append( " year" );
            if( 1 != years ) sb.append( "s" );
            accuracy--;
            if( 0 >= accuracy ) return sb.toString();
        }

        if( 0 < days )
        {
            if( 0 != sb.length() ) sb.append( ", " );
            sb.append( days );
            sb.append( " day" );
            if( 1 != days ) sb.append( "s" );
            accuracy--;
            if( 0 >= accuracy ) return sb.toString();
        }

        if( 0 < hours )
        {
            if( 0 != sb.length() ) sb.append( ", " );
            sb.append( hours );
            sb.append( " hour" );
            if( 1 != hours ) sb.append( "s" );
            accuracy--;
            if( 0 >= accuracy ) return sb.toString();
        }

        if( 0 < minutes )
        {
            if( 0 != sb.length() ) sb.append( ", " );
            sb.append( minutes );
            sb.append( " minute" );
            if( 1 != minutes ) sb.append( "s" );
            accuracy--;
            if( 0 >= accuracy ) return sb.toString();
        }

        if( 0 < seconds )
        {
            if( 0 != sb.length() ) sb.append( ", " );
            sb.append( seconds );
            sb.append( " second" );
            if( 1 != seconds ) sb.append( "s" );
            accuracy--;
            if( 0 >= accuracy ) return sb.toString();
        }

        return sb.toString();
    }
}
