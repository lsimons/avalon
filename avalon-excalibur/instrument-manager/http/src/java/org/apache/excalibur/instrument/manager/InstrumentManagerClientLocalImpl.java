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

import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentSampleException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 16:36:32 $
 * @since 4.1
 */
public class InstrumentManagerClientLocalImpl
    implements InstrumentManagerClientLocal
{
    private DefaultInstrumentManager m_manager;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public InstrumentManagerClientLocalImpl( DefaultInstrumentManager manager )
    {
        m_manager = manager;
    }
    
    /*---------------------------------------------------------------
     * InstrumentManagerClient Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the name used to identify this InstrumentManager.
     *
     * @return The name used to identify this InstrumentManager.
     */
    public String getName()
    {
        return m_manager.getName();
    }
    
    /**
     * Returns the description of this InstrumentManager.
     *
     * @return The description of this InstrumentManager.
     */
    public String getDescription()
    {
        return m_manager.getDescription();
    }
    
    /**
     * Returns a InstrumentableDescriptor based on its name or the name of any
     *  of its children.
     *
     * @param instrumentableName Name of the Instrumentable being requested.
     *
     * @return A Descriptor of the requested Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable does
     *                                   not exist.
     */
    public InstrumentableDescriptor getInstrumentableDescriptor( String instrumentableName )
        throws NoSuchInstrumentableException
    {
        return getInstrumentableDescriptorLocal( instrumentableName );
    }

    /**
     * Returns an array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     *
     * @return An array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     */
    public InstrumentableDescriptor[] getInstrumentableDescriptors()
    {
        return getInstrumentableDescriptorLocals();
    }
    
    /**
     * Searches the entire instrument tree an instrumentable with the given
     *  name.
     *
     * @param instrumentableName Name of the Instrumentable being requested.
     *
     * @return A Descriptor of the requested Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable does
     *                                       not exist.
     */
    public InstrumentableDescriptor locateInstrumentableDescriptor( String instrumentableName )
        throws NoSuchInstrumentableException
    {
        return locateInstrumentableDescriptorLocal( instrumentableName );
    }
    
    /**
     * Searches the entire instrument tree an instrument with the given name.
     *
     * @param instrumentName Name of the Instrument being requested.
     *
     * @return A Descriptor of the requested Instrument.
     *
     * @throws NoSuchInstrumentException If the specified Instrument does
     *                                   not exist.
     */
    public InstrumentDescriptor locateInstrumentDescriptor( String instrumentName )
        throws NoSuchInstrumentException
    {
        return locateInstrumentDescriptorLocal( instrumentName );
    }

    /**
     * Searches the entire instrument tree an instrument sample with the given
     *  name.
     *
     * @param sampleName Name of the Instrument Sample being requested.
     *
     * @return A Descriptor of the requested Instrument Sample.
     *
     * @throws NoSuchInstrumentSampleException If the specified Instrument
     *                                         Sample does not exist.
     */
    public InstrumentSampleDescriptor locateInstrumentSampleDescriptor( String sampleName )
        throws NoSuchInstrumentSampleException
    {
        return locateInstrumentSampleDescriptorLocal( sampleName );
    }
    
    /**
     * Returns the stateVersion of the instrument manager.  The state version
     *  will be incremented each time any of the configuration of the
     *  instrument manager or any of its children is modified.
     * Clients can use this value to tell whether or not anything has
     *  changed without having to do an exhaustive comparison.
     *
     * @return The state version of the instrument manager.
     */
    public int getStateVersion()
    {
        return m_manager.getStateVersion();
    }
    
    /**
     * Invokes garbage collection.
     */
    public void invokeGarbageCollection()
    {
        m_manager.invokeGarbageCollection();
    }
    
    /*---------------------------------------------------------------
     * InstrumentManagerClientLocal Methods
     *-------------------------------------------------------------*/
    /**
     * Returns a InstrumentableDescriptorLocal based on its name or the name
     *  of any of its children.
     *
     * @param instrumentableName Name of the Instrumentable being requested.
     *
     * @return A Descriptor of the requested Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable does
     *                                   not exist.
     */
    public InstrumentableDescriptorLocal getInstrumentableDescriptorLocal(
                                                    String instrumentableName )
        throws NoSuchInstrumentableException
    {
        return m_manager.getInstrumentableDescriptor( instrumentableName );
    }

    /**
     * Returns an array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     *
     * @return An array of Descriptors for the Instrumentables managed by this
     *  InstrumentManager.
     */
    public InstrumentableDescriptorLocal[] getInstrumentableDescriptorLocals()
    {
        return m_manager.getInstrumentableDescriptors();
    }
    
    /**
     * Searches the entire instrument tree an instrumentable with the given
     *  name.
     *
     * @param instrumentableName Name of the Instrumentable being requested.
     *
     * @return A Descriptor of the requested Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable does
     *                                       not exist.
     */
    public InstrumentableDescriptorLocal locateInstrumentableDescriptorLocal(
                                                String instrumentableName )
        throws NoSuchInstrumentableException
    {
        return m_manager.locateInstrumentableDescriptor( instrumentableName );
    }
    
    /**
     * Searches the entire instrument tree an instrument with the given name.
     *
     * @param instrumentName Name of the Instrument being requested.
     *
     * @return A Descriptor of the requested Instrument.
     *
     * @throws NoSuchInstrumentException If the specified Instrument does
     *                                   not exist.
     */
    public InstrumentDescriptorLocal locateInstrumentDescriptorLocal( String instrumentName )
        throws NoSuchInstrumentException
    {
        return m_manager.locateInstrumentDescriptor( instrumentName );
    }

    /**
     * Searches the entire instrument tree an instrument sample with the given
     *  name.
     *
     * @param sampleName Name of the Instrument Sample being requested.
     *
     * @return A Descriptor of the requested Instrument Sample.
     *
     * @throws NoSuchInstrumentSampleException If the specified Instrument
     *                                         Sample does not exist.
     */
    public InstrumentSampleDescriptorLocal locateInstrumentSampleDescriptorLocal(
                                                String sampleName )
        throws NoSuchInstrumentSampleException
    {
        return m_manager.locateInstrumentSampleDescriptor( sampleName );
    }
}

