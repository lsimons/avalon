/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager.interfaces;

/**
 * Describes an InstrumentSample and acts as a Proxy to protect the original
 *  InstrumentSample object.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/03 15:00:38 $
 * @since 4.1
 */
public interface InstrumentSampleDescriptor
{
    /**
     * Returns true if the InstrumentSample was configured in the instrumentables
     *  section of the configuration.
     *
     * @return True if configured.
     */
    boolean isConfigured();
    
    /**
     * Returns the name of the sample.
     *
     * @return The name of the sample.
     */
    String getName();
    
    /**
     * Returns the sample interval.  The period of each sample in millisends.
     *
     * @return The sample interval.
     */
    long getInterval();
    
    /**
     * Returns the number of samples in the sample history.
     *
     * @return The size of the sample history.
     */
    int getSize();
    
    /**
     * Returns the description of the sample.
     *
     * @return The description of the sample.
     */
    String getDescription();
    
    /**
     * Returns the type of the Instrument Sample.  Possible values include
     *  InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER,
     *  InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM,
     *  InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN, or
     *  InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM.
     *
     * @return The type of the Instrument Sample.
     */
    int getType();
    
    /**
     * Obtain the value of the sample.  All samples are integers, so the profiled
     * objects must measure quantity (numbers of items), rate (items/period), time in
     * milliseconds, etc.
     *
     * @return The sample value.
     */
    int getValue();
    
    /**
     * Obtain the UNIX time of the beginning of the sample.
     *
     * @return The UNIX time of the beginning of the sample.
     */
    long getTime();
    
    /**
     * Returns the Type of the Instrument which can use the sample.  This
     *  should be the same for all instances of a class.
     * <p>
     * Should be one of the following: InstrumentManager.PROFILE_POINT_TYPE_COUNTER
     *  or InstrumentManager.PROFILE_POINT_TYPE_VALUE
     *
     * @return The Type of the Instrument which can use the sample.
     */
    int getInstrumentType();
    
    /**
     * Returns the time that the current lease expires.  Permanent samples will
     *  return a value of 0.
     *
     * @return The time that the current lease expires.
     */
    long getLeaseExpirationTime();
    
    /**
     * Extends the lease to be lease milliseconds from the current time.
     *
     * @param lease The length of the lease in milliseconds.
     *
     * @return The new lease expiration time.  Returns 0 if the sample is
     *         permanent.
     */
    long extendLease( long lease );
    
    /**
     * Obtains a static snapshot of the InstrumentSample.
     *
     * @return A static snapshot of the InstrumentSample.
     */
    InstrumentSampleSnapshot getSnapshot();
}

