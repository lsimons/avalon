/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.manager;

import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * The InstrumentSample represents a single data sample in a ProfileDataSet.
 * Access to InstrumentSamples are synchronized through the ProfileDataSet.
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/26 11:56:16 $
 * @since 4.1
 */
class InstrumentSampleFactory
{
    /**
     * A Profile Sample Type loaded in from a Configuration.
     *
     * @param type Type of the InstrumentSample to create.  Accepted values are:
     *              "max", "maximum", "min", "minimum", "avg", "average", 
     *              "ctr", and "counter".
     * @param name The name of the new InstrumentSample.
     * @param interval The sample interval of the new InstrumentSample.
     * @param size The number of samples to store as history.
     * @param description The description of the new InstrumentSample.
     */
    static InstrumentSample getInstrumentSample( String type,
                                           String name,
                                           long interval,
                                           int size,
                                           String description )
        throws ConfigurationException
    {
        if ( type.equalsIgnoreCase( "max" ) || type.equalsIgnoreCase( "maximum" ) )
        {
            return new MaximumValueInstrumentSample( name, interval, size, description );
        }
        else if ( type.equalsIgnoreCase( "min" ) || type.equalsIgnoreCase( "minimum" ) )
        {
            return new MinimumValueInstrumentSample( name, interval, size, description );
        }
        else if ( type.equalsIgnoreCase( "avg" ) || type.equalsIgnoreCase( "average" ) )
        {
            return new AverageValueInstrumentSample( name, interval, size, description );
        }
        else if ( type.equalsIgnoreCase( "ctr" ) || type.equalsIgnoreCase( "counter" ) )
        {
            return new CounterInstrumentSample( name, interval, size, description );
        }
        else
        {
            throw new ConfigurationException( "'" + type + "' is not a valid sample type." );
        }
    }
}
