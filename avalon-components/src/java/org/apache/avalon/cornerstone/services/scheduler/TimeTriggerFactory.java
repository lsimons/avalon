/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.services.scheduler;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.i18n.ResourceManager;

/**
 * Factory for <code>TimeTrigger</code>s.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class TimeTriggerFactory
{
    /**
     * Create <code>TimeTrigger</code> with configuration.
     *
     * @param conf configuration for time trigger
     */
    public TimeTrigger createTimeTrigger( final Configuration conf )
        throws ConfigurationException
    {
        final String type = conf.getAttribute( "type" );

        TimeTrigger trigger;
        if ( "periodic".equals( type ) )
        {
            final int offset =
                conf.getChild( "offset", true ).getValueAsInteger( 0 );
            final int period =
                conf.getChild( "period", true ).getValueAsInteger( -1 );

            trigger = new PeriodicTimeTrigger( offset, period );
        }
        else if ( "cron".equals( type ) )
        {
            final int minute =
                conf.getChild( "minute" ).getValueAsInteger( -1 );
            final int hour =
                conf.getChild( "hour" ).getValueAsInteger( -1 );
            final int day =
                conf.getChild( "day" ).getValueAsInteger( -1 );
            final int month =
                conf.getChild( "month" ).getValueAsInteger( -1 );
            final int year =
                conf.getChild( "year" ).getValueAsInteger( -1 );
            final boolean dayOfWeek =
                conf.getChild( "day" ).getAttributeAsBoolean( "week", false );

            trigger = new CronTimeTrigger( minute, hour, day, month, year,
                                           dayOfWeek );
        }
        else
        {
            throw new ConfigurationException( "Unknown trigger type" );
        }

        return trigger;
    }
}
