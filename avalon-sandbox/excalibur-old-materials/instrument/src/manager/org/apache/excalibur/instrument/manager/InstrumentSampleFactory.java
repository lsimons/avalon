/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * The InstrumentSample represents a single data sample in a ProfileDataSet.
 * Access to InstrumentSamples are synchronized through the ProfileDataSet.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/03 15:00:38 $
 * @since 4.1
 */
class InstrumentSampleFactory
{
    /**
     * A Profile Sample Type loaded in from a Configuration.
     *
     * @param instrumentProxy The InstrumentProxy which owns the
     *                        InstrumentSample.
     * @param type Type of the InstrumentSample to create.
     * @param name The name of the new InstrumentSample.
     * @param interval The sample interval of the new InstrumentSample.
     * @param size The number of samples to store as history.
     * @param description The description of the new InstrumentSample.
     * @param lease Requested lease time in milliseconds.  A value of 0 implies
     *              that the lease will never expire.
     */
    static InstrumentSample getInstrumentSample( InstrumentProxy instrumentProxy,
                                                 int type,
                                                 String name,
                                                 long interval,
                                                 int size,
                                                 String description,
                                                 long lease )
    {
        switch ( type )
        {
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM:
            return new MaximumValueInstrumentSample(
                instrumentProxy, name, interval, size, description, lease );
            
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM:
            return new MinimumValueInstrumentSample(
                instrumentProxy, name, interval, size, description, lease );
        
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN:
            return new MeanValueInstrumentSample(
                instrumentProxy, name, interval, size, description, lease );
            
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER:
            return new CounterInstrumentSample(
                instrumentProxy, name, interval, size, description, lease );
            
        default:
            throw new IllegalArgumentException( "'" + type + "' is not a valid sample type." );
        }
    }
}
