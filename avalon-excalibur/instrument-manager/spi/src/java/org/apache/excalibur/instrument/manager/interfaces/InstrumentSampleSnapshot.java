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
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
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
package org.apache.excalibur.instrument.manager.interfaces;

import java.io.Serializable;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 16:36:33 $
 * @since 4.1
 */
public class InstrumentSampleSnapshot
    implements Serializable
{
    static final long serialVersionUID = -3284372358291073513L;
    
    /** The name used to reference the InstrumentSample. */
    private String m_InstrumentSampleName;
    
    /** The interval between each sample. */
    private long m_interval;
    
    /** The number of samples in the InstrumentSample. */
    private int m_size;
    
    /** The time that the last sample starts. */
    private long m_time;
    
    /** The samples as an array of integers. */
    private int[] m_samples;
    
    /** State Version. */
    private int m_stateVersion;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * @param InstrumentSampleName The name used to reference the InstrumentSample.
     * @param interval The interval between each sample.
     * @param size The number of samples in the InstrumentSample.
     * @param time The time that the last sample starts.
     * @param samples The samples as an array of integers.
     * @param stateVersion The current state version of the sample. 
     */
    public InstrumentSampleSnapshot( String InstrumentSampleName,
                           long interval,
                           int size,
                           long time,
                           int[] samples,
                           int stateVersion )
    {
        m_InstrumentSampleName = InstrumentSampleName;
        m_interval = interval;
        m_size = size;
        m_time = time;
        m_samples = samples;
        m_stateVersion = stateVersion;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the name used to reference the InstrumentSample.
     *
     * @return The name used to reference the InstrumentSample.
     */
    public String getInstrumentSampleName()
    {
        return m_InstrumentSampleName;
    }
    
    /**
     * Returns the interval, in milliseconds, between each sample.
     *
     * @return The interval between each sample.
     */
    public long getInterval()
    {
        return m_interval;
    }
    
    /**
     * Returns the number of samples in the InstrumentSample.
     *
     * @return The number of samples in the InstrumentSample.
     */
    public int getSize()
    {
        return m_size;
    }
    
    /**
     * Returns the time that the last sample starts.
     *
     * @return The time that the last sample starts.
     */
    public long getTime()
    {
        return m_time;
    }
    
    /**
     * Returns the samples as an array of integers.  The sample at index 0
     *  will be the oldest.  The end of the array is the newest.
     *
     * @return The samples as an array of integers.
     */
    public int[] getSamples()
    {
        return m_samples;
    }
    
    /**
     * Returns the stateVersion of the sample.  The state version will be
     *  incremented each time any of the configuration of the sample is
     *  modified.
     * Clients can use this value to tell whether or not anything has
     *  changed without having to do an exhaustive comparison.
     *
     * @return The state version of the sample.
     */
    public int getStateVersion()
    {
        return m_stateVersion;
    }
}

