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
package org.apache.excalibur.instrument.manager;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

/**
 * The InstrumentSample represents a single data sample in a ProfileDataSet.
 * Access to InstrumentSamples are synchronized through the ProfileDataSet.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 16:36:32 $
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
