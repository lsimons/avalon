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

import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;

/**
 * Describes a Instrumentable and acts as a Proxy to protect the original
 *  Instrumentable.  Methods defined by the Local interface should only
 *  be accessed from within the same JVM for performance reasons.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:25 $
 * @since 4.1
 */
public interface InstrumentableDescriptorLocal
    extends InstrumentableDescriptor
{
    /**
     * Returns the parent InstrumentableDescriptorLocal or null if this is a
     *  top level instrumentable.
     *
     * @return The parent InstrumentableDescriptorLocal or null.
     */
    InstrumentableDescriptorLocal getParentInstrumentableDescriptorLocal();
    
    /**
     * Returns a child InstrumentableDescriptorLocal based on its name or the
     *  name of any of its children.
     *
     * @param childInstrumentableName Name of the child Instrumentable being
     *                                requested.
     *
     * @return A descriptor of the requested child Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable
     *                                       does not exist.
     */
    InstrumentableDescriptorLocal getChildInstrumentableDescriptorLocal(
        String childInstrumentableName ) throws NoSuchInstrumentableException;

    /**
     * Returns an array of Descriptors for the child Instrumentables registered
     *  by this Instrumentable.
     *
     * @return An array of Descriptors for the child Instrumentables registered
     *  by this Instrumentable.
     */
    InstrumentableDescriptorLocal[] getChildInstrumentableDescriptorLocals();
    
    /**
     * Returns a InstrumentDescriptorLocal based on its name.
     *
     * @param instrumentName Name of the Instrument being requested.
     *
     * @return A Descriptor of the requested Instrument.
     *
     * @throws NoSuchInstrumentException If the specified Instrument does
     *                                     not exist.
     */
    InstrumentDescriptorLocal getInstrumentDescriptorLocal( String instrumentName )
        throws NoSuchInstrumentException;

    /**
     * Returns an array of Descriptors for the Instruments registered by this
     *  Instrumentable.
     *
     * @return An array of Descriptors for the Instruments registered by this
     *  Instrumentable.
     */
    InstrumentDescriptorLocal[] getInstrumentDescriptorLocals();
}
