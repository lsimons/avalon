/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Apache Avalon", "Avalon Cornerstone", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
*/

package org.apache.avalon.cornerstone.services.scheduler.test;

import java.util.Calendar;
import java.util.Date;
import org.apache.avalon.cornerstone.services.scheduler.CronTimeTrigger;
import junit.framework.TestCase;

/**
 * TestCase for CronTimeTrigger.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class CronTimeTriggerTestCase
    extends TestCase
{
    protected static final long MINUTE = 60000;
    protected static final long HOUR = 60 * MINUTE;
    protected static final long DAY = 24 * HOUR;
    protected static final long WEEK = 7 * DAY;

    public CronTimeTriggerTestCase( final String name )
    {
        super( name );
    }

    public void testMinutes()
    {
        final CronTimeTrigger trigger = new CronTimeTrigger( -1, -1, -1, -1, -1, true );

        final Calendar now = Calendar.getInstance();
        now.set( Calendar.SECOND, 0 );

        long time = now.getTime().getTime();
        long next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            final long delta = next - time;
            assertEquals("Time increments of 1 minute", MINUTE, delta );
            time = next;
            next = trigger.getTimeAfter( time );
        }
    }

    public void testHours()
    {
        final CronTimeTrigger trigger = new CronTimeTrigger( 51, -1, -1, -1, -1, true );

        final Calendar now = Calendar.getInstance();
        now.set( Calendar.SECOND, 0 );
        now.set( Calendar.MINUTE, 2 );

        long time = now.getTime().getTime();
        long next = trigger.getTimeAfter( time );
        long delta = next - time;

        final long expected = (51 - 2) * MINUTE;
        assertEquals( "Collect time at start", expected, delta );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            delta = next - time;
            assertEquals( "Time increments of 1 hour", HOUR, delta );
            time = next;
            next = trigger.getTimeAfter( time );
        }
    }

    public void testDays()
    {
        final CronTimeTrigger trigger = new CronTimeTrigger( 51, 5, -1, -1, -1, true );

        final Calendar now = Calendar.getInstance();
        now.set( Calendar.SECOND, 0 );
        now.set( Calendar.MINUTE, 2 );
        now.set( Calendar.HOUR_OF_DAY, 3 );

        long time = now.getTime().getTime();
        long next = trigger.getTimeAfter( time );
        long delta = next - time;

        final long expected = (51 - 2) * MINUTE + HOUR * (5 - 3);

        assertEquals( "Collect time at start", expected, delta );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            delta = next - time;
            assertEquals( "Time increments of 1 day", DAY, delta );
            time = next;
            next = trigger.getTimeAfter( time );
        }
    }

    public void testMinutelessDays()
    {
        final CronTimeTrigger trigger = new CronTimeTrigger( -1, 5, -1, -1, -1, true );

        final Calendar now = Calendar.getInstance();
        now.set( Calendar.SECOND, 0 );
        now.set( Calendar.MINUTE, 2 );
        now.set( Calendar.HOUR_OF_DAY, 3 );

        long time = now.getTime().getTime();
        long next = trigger.getTimeAfter( time );
        long delta = next - time;

        final long expected = ( -2 ) * MINUTE + HOUR * (5 - 3);
        assertEquals( "Collect time at start", expected, delta );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            delta = next - time;
            assertEquals( "Time increments of 1 day", DAY, delta );
            time = next;
            next = trigger.getTimeAfter( time );
        }
    }

    public void testWeekly()
    {
        final CronTimeTrigger trigger = new CronTimeTrigger( -1, 5, 2, -1, -1, true );

        final Calendar now = Calendar.getInstance();
        now.set( Calendar.SECOND, 0 );
        now.set( Calendar.MINUTE, 2 );
        now.set( Calendar.HOUR_OF_DAY, 3 );
        now.set( Calendar.DAY_OF_WEEK, 3 );
        now.set( Calendar.MONTH, 0 );

        long time = now.getTime().getTime();
        long next = trigger.getTimeAfter( time );
        long delta = next - time;

        final long expected = ( -2 ) * MINUTE + HOUR * (5 - 3) + DAY * 6;
        assertEquals( "Collect time at start", expected, delta );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            delta = next - time;
            assertEquals( "Time increments of 1 hour", WEEK, delta );
            time = next;
            next = trigger.getTimeAfter( time );
        }
    }

    public void testWeekly2()
    {
        final CronTimeTrigger trigger = new CronTimeTrigger( 3, 5, 2, -1, -1, true );

        final Calendar now = Calendar.getInstance();
        now.set( Calendar.SECOND, 0 );
        now.set( Calendar.MINUTE, 2 );
        now.set( Calendar.HOUR_OF_DAY, 3 );
        now.set( Calendar.DAY_OF_WEEK, 3 );
        now.set( Calendar.MONTH, 0 );

        long time = now.getTime().getTime();
        long next = trigger.getTimeAfter( time );
        long delta = next - time;

        final long expected = ( 1 ) * MINUTE + HOUR * (5 - 3) + DAY * 6;
        assertEquals( "Collect time at start", expected, delta );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            delta = next - time;
            assertEquals( "Time increments of 1 hour", WEEK, delta );
            time = next;
            next = trigger.getTimeAfter( time );
        }
    }

    public void testDayAMonth()
    {
        final CronTimeTrigger trigger = new CronTimeTrigger( 3, 5, 2, -1, -1, false );

        final Calendar now = Calendar.getInstance();
        now.set( Calendar.SECOND, 0 );
        now.set( Calendar.MINUTE, 2 );
        now.set( Calendar.HOUR_OF_DAY, 3 );
        now.set( Calendar.DAY_OF_MONTH, 1 );

        long time = now.getTime().getTime();
        long next = trigger.getTimeAfter( time );
        long delta = next - time;

        long expected = ( 1 ) * MINUTE + HOUR * (5 - 3) + DAY * 1;
        assertEquals( "Collect time at start", expected, delta );

        int month = now.get( Calendar.MONTH );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            month = (month + 1) % 12;

            now.setTime( new Date( next ) );

            assertEquals( "Minute", now.get( Calendar.MINUTE ), 3 );
            assertEquals( "Hour of Day", now.get( Calendar.HOUR_OF_DAY ), 5 );
            assertEquals( "Day of month", now.get( Calendar.DAY_OF_MONTH ), 2 );
            assertEquals( "Month", now.get( Calendar.MONTH ), month );

            time = next;
            next = trigger.getTimeAfter( time );
        }
    }

    public void testYearly()
    {
        final CronTimeTrigger trigger = new CronTimeTrigger( -1, -1, -1, 4, -1, true );
        //System.out.println( "CronTimeTrigger: " + trigger );

        final Calendar now = Calendar.getInstance();
        now.set( Calendar.SECOND, 0 );
        now.set( Calendar.MINUTE, 2 );
        now.set( Calendar.HOUR_OF_DAY, 3 );
        now.set( Calendar.DAY_OF_MONTH, 1 );
        now.set( Calendar.MONTH, 3 );

        long time = now.getTime().getTime();
        long next = trigger.getTimeAfter( time );
        long delta = next - time;

        now.setTime( new Date( next ) );

        assertEquals( "Minute", now.get( Calendar.MINUTE ), 0 );
        assertEquals( "Hour of Day", now.get( Calendar.HOUR_OF_DAY ), 0 );
        assertEquals( "Day of month", now.get( Calendar.DAY_OF_MONTH ), 1 );
        assertEquals( "Month", now.get( Calendar.MONTH ), 4 );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            now.setTime( new Date( next ) );

            //System.out.println( "day/Month hr:min " + now.get( Calendar.DAY_OF_MONTH ) +
            //"/" + now.get( Calendar.MONTH ) + " " +
            //now.get( Calendar.HOUR ) + ":" + now.get( Calendar.MINUTE ) );

            assertEquals( "Minute", now.get( Calendar.MINUTE ), 0 );
            assertEquals( "Hour of Day", now.get( Calendar.HOUR_OF_DAY ), 0 );
            assertEquals( "Day of month", now.get( Calendar.DAY_OF_MONTH ), 1 );
            assertEquals( "Month", now.get( Calendar.MONTH ), 4 );

            time = next;
            next = trigger.getTimeAfter( time );
        }
    }

    public void testOneYear()
    {
        final CronTimeTrigger trigger = new CronTimeTrigger( -1, -1, -1, -1, 2020, true );
        //System.out.println( "CronTimeTrigger: " + trigger );

        final Calendar now = Calendar.getInstance();
        now.set( Calendar.SECOND, 0 );
        now.set( Calendar.MINUTE, 2 );
        now.set( Calendar.HOUR_OF_DAY, 3 );
        now.set( Calendar.DAY_OF_MONTH, 1 );
        now.set( Calendar.MONTH, 3 );
        now.set( Calendar.YEAR, 2000 );

        long time = now.getTime().getTime();
        long next = trigger.getTimeAfter( time );
        long delta = next - time;

        now.setTime( new Date( next ) );

        assertEquals( "Minute", now.get( Calendar.MINUTE ), 0 );
        assertEquals( "Hour of Day", now.get( Calendar.HOUR_OF_DAY ), 0 );
        assertEquals( "Day of month", now.get( Calendar.DAY_OF_MONTH ), 1 );
        assertEquals( "Month", now.get( Calendar.MONTH ), 0 );
        assertEquals( "year", now.get( Calendar.YEAR ), 2020 );

        time = next;
        next = trigger.getTimeAfter( time );
        assertEquals( "year", -1, next );
    }

    public void testRolledMonthDay()
    {
        final CronTimeTrigger trigger = new CronTimeTrigger( -1, -1, 5, 2, -1, false );

        final Calendar now = Calendar.getInstance();
        now.set( Calendar.SECOND, 0 );
        now.set( Calendar.MINUTE, 0 );
        now.set( Calendar.HOUR_OF_DAY, 0 );
        now.set( Calendar.DAY_OF_MONTH, 29 );
        now.set( Calendar.MONTH, 7 );

        long time = now.getTime().getTime();
        long next = trigger.getTimeAfter( time );
        long delta = next - time;

        now.setTime( new Date( next ) );

        assertEquals( "Second", now.get( Calendar.SECOND ), 0 );
        assertEquals( "Minute", now.get( Calendar.MINUTE ), 0 );
        assertEquals( "Hour of Day", now.get( Calendar.HOUR_OF_DAY ), 0 );
        assertEquals( "Day of month", now.get( Calendar.DAY_OF_MONTH ), 5 );
        assertEquals( "Month", now.get( Calendar.MONTH ), 2 );
    }

    public void testMaxDayBoundaries()
    {
        final CronTimeTrigger trigger = new CronTimeTrigger( -1, -1, 30, 1, -1, false );

        final Calendar now = Calendar.getInstance();
        now.set( Calendar.SECOND, 0 );
        now.set( Calendar.MINUTE, 0 );
        now.set( Calendar.HOUR_OF_DAY, 0 );
        now.set( Calendar.DAY_OF_MONTH, 0 );
        now.set( Calendar.MONTH, 7 );
        now.set( Calendar.YEAR, 2001 );

        long time = now.getTime().getTime();
        long next = trigger.getTimeAfter( time );
        long delta = next - time;

        now.setTime( new Date( next ) );

        assertEquals( "Second", now.get( Calendar.SECOND ), 0 );
        assertEquals( "Minute", now.get( Calendar.MINUTE ), 0 );
        assertEquals( "Hour of Day", now.get( Calendar.HOUR_OF_DAY ), 0 );
        assertEquals( "Month", now.get( Calendar.MONTH ), 1 );
        assertEquals( "Day of month", now.get( Calendar.DAY_OF_MONTH ), 28 );
    }
    
    public void testDaysEndOfYear()
    {
        final CronTimeTrigger trigger = new CronTimeTrigger( 51, 5, -1, -1, -1, true );

        final Calendar now = Calendar.getInstance();
        now.set( Calendar.SECOND, 0 );
        now.set( Calendar.MINUTE, 2 );
        now.set( Calendar.HOUR_OF_DAY, 11 );
        now.set( Calendar.DAY_OF_MONTH, 31 );
        now.set( Calendar.MONTH, Calendar.DECEMBER );
        
        long time = now.getTime().getTime();
        long next = trigger.getTimeAfter( time );
        int year =  now.get( Calendar.YEAR ) + 1;

        now.setTime( new Date( next ) );

        assertEquals( "Second", now.get( Calendar.SECOND ), 0 );
        assertEquals( "Minute", now.get( Calendar.MINUTE ), 51 );
        assertEquals( "Hour of Day", now.get( Calendar.HOUR_OF_DAY ), 5 );
        assertEquals( "Month", now.get( Calendar.MONTH ), Calendar.JANUARY );
        assertEquals( "Year", now.get( Calendar.YEAR ), year );
    }
}
