/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.services.scheduler.test;

import java.util.Calendar;
import java.util.Date;
import org.apache.avalon.cornerstone.services.scheduler.CronTimeTrigger;
import org.apache.testlet.AbstractTestlet;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class CronTimeTriggerTestlet
    extends AbstractTestlet
{
    protected final static long MINUTE = 60000;
    protected final static long HOUR = 60 * MINUTE;
    protected final static long DAY = 24 * HOUR;
    protected final static long WEEK = 7 * DAY;

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
            assert("Time increments of 1 minute", MINUTE == delta );
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
        assert( "Collect time at start", expected == delta );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            delta = next - time;
            assert( "Time increments of 1 hour", HOUR == delta );
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

        assert( "Collect time at start", expected == delta );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            delta = next - time;
            assert( "Time increments of 1 day", DAY == delta );
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
        assert( "Collect time at start", expected == delta );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            delta = next - time;
            assert( "Time increments of 1 day", DAY == delta );
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
        assert( "Collect time at start", expected == delta );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            delta = next - time;
            assert( "Time increments of 1 hour", WEEK == delta );
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
        assert( "Collect time at start", expected == delta );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            delta = next - time;
            assert( "Time increments of 1 hour", WEEK == delta );
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
        assert( "Collect time at start", expected == delta );

        int month = now.get( Calendar.MONTH );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            month = (month + 1) % 12;

            now.setTime( new Date( next ) );

            assert( "Minute", now.get( Calendar.MINUTE ) == 3 );
            assert( "Hour of Day", now.get( Calendar.HOUR_OF_DAY ) == 5 );
            assert( "Day of month", now.get( Calendar.DAY_OF_MONTH ) == 2 );
            assert( "Month", now.get( Calendar.MONTH ) == month );

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

        assert( "Minute", now.get( Calendar.MINUTE ) == 0 );
        assert( "Hour of Day", now.get( Calendar.HOUR_OF_DAY ) == 0 );
        assert( "Day of month", now.get( Calendar.DAY_OF_MONTH ) == 1 );
        assert( "Month", now.get( Calendar.MONTH ) == 4 );

        time = next;
        next = trigger.getTimeAfter( time );

        for( int i = 0; i < 5; i++ )
        {
            now.setTime( new Date( next ) );

            //System.out.println( "day/Month hr:min " + now.get( Calendar.DAY_OF_MONTH ) +
            //"/" + now.get( Calendar.MONTH ) + " " +
            //now.get( Calendar.HOUR ) + ":" + now.get( Calendar.MINUTE ) );

            assert( "Minute", now.get( Calendar.MINUTE ) == 0 );
            assert( "Hour of Day", now.get( Calendar.HOUR_OF_DAY ) == 0 );
            assert( "Day of month", now.get( Calendar.DAY_OF_MONTH ) == 1 );
            assert( "Month", now.get( Calendar.MONTH ) == 4 );

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

        assert( "Minute", now.get( Calendar.MINUTE ) == 0 );
        assert( "Hour of Day", now.get( Calendar.HOUR_OF_DAY ) == 0 );
        assert( "Day of month", now.get( Calendar.DAY_OF_MONTH ) == 1 );
        assert( "Month", now.get( Calendar.MONTH ) == 0 );
        assert( "year", now.get( Calendar.YEAR ) == 2020 );

        time = next;
        next = trigger.getTimeAfter( time );
        assert( "year", -1 == next );
    }
}
