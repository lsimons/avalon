/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager.interfaces;

import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * A series of methods which are useful when working with InstrumentSamples.
 *  These methods can be used on the server as well as the client.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/03 15:00:38 $
 * @since 4.1
 */
public class InstrumentSampleUtils
{
    /**
     * Resolves an instrument sample type based on a name.
     *
     * @param type Type of the InstrumentSample to resolve.  Accepted values are:
     *              "max", "maximum", "min", "minimum", "mean", 
     *              "ctr", and "counter".
     *
     * @throws ConfigurationException if the specified sample type is unknown.
     */
    public static int resolveInstrumentSampleType( String type )
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
    
    public static String getInstrumentSampleTypeName( int type )
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
    
    /**
     * Generates a sample name given its parameters.
     *
     * @param sampleType Type of the sample.
     * @param sampleInterval Interval of the sample.
     * @param sampleSize Size of the sample.
     *
     * @return A sample name.
     */
    public static String generateInstrumentSampleName( int sampleType,
                                                       long sampleInterval,
                                                       int sampleSize )
    {
        return getInstrumentSampleTypeName( sampleType ) + "." + 
            sampleInterval + "." + sampleSize;
    }
    
    /**
     * Generates a fully qualified sample name given its parameters.
     *
     * @param instrumentName Name of the instrument which owns the sample.
     * @param sampleType Type of the sample.
     * @param sampleInterval Interval of the sample.
     * @param sampleSize Size of the sample.
     *
     * @return A fully qualified sample name.
     */
    public static String generateFullInstrumentSampleName( String instrumentName,
                                                           int sampleType,
                                                           long sampleInterval,
                                                           int sampleSize )
    {
        return instrumentName + "." +
            generateInstrumentSampleName( sampleType, sampleInterval, sampleSize );
    }
}
