/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.excalibur.instrument.manager;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;

/**
 * Describes an InstrumentSample and acts as a Proxy to protect the original
 *  InstrumentSample object.  Methods defined by the Local interface should
 *  only be accessed from within the same JVM for performance reasons.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:25 $
 * @since 4.1
 */
public interface InstrumentSampleDescriptorLocal
    extends InstrumentSampleDescriptor
{
    /**
     * Returns a reference to the descriptor of the Instrument of the sample.
     *
     * @return A reference to the descriptor of the Instrument of the sample.
     */
    InstrumentDescriptorLocal getInstrumentDescriptorLocal();
    
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
}
