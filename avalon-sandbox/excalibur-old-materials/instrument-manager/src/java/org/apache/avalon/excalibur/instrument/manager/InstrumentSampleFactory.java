/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.manager;

import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * The InstrumentSample represents a single data sample in a ProfileDataSet.
 * Access to InstrumentSamples are synchronized through the ProfileDataSet.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/04/28 17:05:41 $
 * @since 4.1
 */
class InstrumentSampleFactory
{
    /**
     * A Profile Sample Type loaded in from a Configuration.
     *
     * @param type Type of the InstrumentSample to create.
     * @param name The name of the new InstrumentSample.
     * @param interval The sample interval of the new InstrumentSample.
     * @param size The number of samples to store as history.
     * @param description The description of the new InstrumentSample.
     * @param lease Requested lease time in milliseconds.  A value of 0 implies
     *              that the lease will never expire.
     */
    static InstrumentSample getInstrumentSample( int type,
                                                 String name,
                                                 long interval,
                                                 int size,
                                                 String description,
                                                 long lease )
    {
        switch ( type )
        {
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM:
            return new MaximumValueInstrumentSample( name, interval, size, description, lease );
            
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM:
            return new MinimumValueInstrumentSample( name, interval, size, description, lease );
        
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN:
            return new MeanValueInstrumentSample( name, interval, size, description, lease );
            
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER:
            return new CounterInstrumentSample( name, interval, size, description, lease );
            
        default:
            throw new IllegalArgumentException( "'" + type + "' is not a valid sample type." );
        }
    }
    
    /**
     * Resolves an instrument sample type based on a name.
     *
     * @param type Type of the InstrumentSample to resolve.  Accepted values are:
     *              "max", "maximum", "min", "minimum", "mean", 
     *              "ctr", and "counter".
     *
     * @throws ConfigurationException if the specified sample type is unknown.
     */
    static int resolveInstrumentSampleType( String type )
        throws ConfigurationException {
        
        if ( type.equalsIgnoreCase( "max" ) || type.equalsIgnoreCase( "maximum" ) )
        {
            return InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM;
        }
        else if ( type.equalsIgnoreCase( "min" ) || type.equalsIgnoreCase( "minimum" ) )
        {
            return InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM;
        }
        else if ( type.equalsIgnoreCase( "mean" ) )
        {
            return InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN;
        }
        else if ( type.equalsIgnoreCase( "ctr" ) || type.equalsIgnoreCase( "counter" ) )
        {
            return InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER;
        }
        else
        {
            throw new ConfigurationException( "'" + type + "' is not a valid sample type." );
        }
    }
    
    static String getInstrumentSampleTypeName( int type )
    {
        switch ( type )
        {
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM:
            return "maximum";
            
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM:
            return "minimum";
        
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN:
            return "mean";
            
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER:
            return "counter";
            
        default:
            return "unknown-" + type;
        }
    }
}
