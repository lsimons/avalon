/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.manager;

import org.apache.avalon.excalibur.instrument.manager.client.InstrumentSampleDescriptor;
import org.apache.avalon.excalibur.instrument.manager.client.InstrumentSampleListener;
import org.apache.avalon.excalibur.instrument.manager.client.InstrumentSampleSnapshot;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;

/**
 * InstrumentSamples are used to provide an Instrument with state.  Samples
 *  have a sample interval, which is the period over which data is grouped.
 * <p>
 * InstrmentSamples can be created when the InstrumentManager is created as
 *  part of the configuration process, or as a result of a request from an
 *  InstrumentClient.
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/26 11:56:16 $
 * @since 4.1
 */
public interface InstrumentSample
    extends LogEnabled
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
     * Returns a Descriptor for the InstrumentSample.
     *
     * @return A Descriptor for the InstrumentSample.
     */
    InstrumentSampleDescriptor getDescriptor();
    
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
     * Obtains a static snapshot of the InstrumentSample.
     *
     * @return A static snapshot of the InstrumentSample.
     */
    InstrumentSampleSnapshot getSnapshot();
    
    /**
     * Registers a InstrumentSampleListener with a InstrumentSample given a name.
     *
     * @param listener The listener which should start receiving updates from the
     *                 InstrumentSample.
     */
    void addInstrumentSampleListener( InstrumentSampleListener listener );
    
    /**
     * Unregisters a InstrumentSampleListener from a InstrumentSample given a name.
     *
     * @param listener The listener which should stop receiving updates from the
     *                 InstrumentSample.
     */
    void removeInstrumentSampleListener( InstrumentSampleListener listener );
    
    /**
     * Saves the current state into a Configuration.
     *
     * @param useCompactSamples Flag for whether or not InstrumentSample data
     *                          should be saved in compact format or not.
     *
     * @return The state as a Configuration.
     */
    Configuration saveState( boolean useCompactSamples );
    
    /**
     * Loads the state into the InstrumentSample.
     *
     * @param state Configuration object to load state from.
     *
     * @throws ConfigurationException If there were any problems loading the
     *                                state.
     */
    void loadState( Configuration state ) throws ConfigurationException;
}
