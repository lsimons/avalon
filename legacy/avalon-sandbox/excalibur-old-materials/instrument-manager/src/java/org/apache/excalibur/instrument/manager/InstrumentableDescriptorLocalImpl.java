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
import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;

/**
 * Describes a Instrumentable and acts as a Proxy to protect the original
 *  Instrumentable.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.5 $ $Date: 2003/09/08 09:00:44 $
 * @since 4.1
 */
public class InstrumentableDescriptorLocalImpl
    implements InstrumentableDescriptorLocal
{
    /** InstrumentableProxy being described. */
    private InstrumentableProxy m_instrumentableProxy;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentableDescriptorLocalImpl.
     *
     * @param instrumentableProxy InstrumentableProxy being described.
     */
    InstrumentableDescriptorLocalImpl( InstrumentableProxy instrumentableProxy )
    {
        m_instrumentableProxy = instrumentableProxy;
    }

    /*---------------------------------------------------------------
     * InstrumentableDescriptor Methods
     *-------------------------------------------------------------*/
    /**
     * Returns true if the Instrumentable was configured in the instrumentables
     *  section of the configuration.
     *
     * @return True if configured.
     */
    public boolean isConfigured()
    {
        return m_instrumentableProxy.isConfigured();
    }

    /**
     * Returns true if the Instrumentable was registered with the Instrument
     *  Manager.
     *
     * @return True if registered.
     */
    public boolean isRegistered()
    {
        return m_instrumentableProxy.isRegistered();
    }
    
    /**
     * Gets the name for the Instrumentable.  The Instrumentable Name is used to
     *  uniquely identify the Instrumentable during the configuration of the
     *  Profiler and to gain access to a InstrumentableDescriptor through a
     *  ProfilerManager.
     *
     * @return The name used to identify a Instrumentable.
     */
    public String getName()
    {
        return m_instrumentableProxy.getName();
    }

    /**
     * Gets the description of the Instrumentable.
     *
     * @return The description of the Instrumentable.
     */
    public String getDescription()
    {
        return m_instrumentableProxy.getDescription();
    }
    
    /**
     * Returns the parent InstrumentableDescriptor or null if this is a top
     *  level instrumentable.
     *
     * @return The parent InstrumentableDescriptor or null.
     */
    public InstrumentableDescriptor getParentInstrumentableDescriptor()
    {
        return getParentInstrumentableDescriptorLocal();
    }

    /**
     * Returns a child InstrumentableDescriptor based on its name or the name
     *  of any of its children.
     *
     * @param childInstrumentableName Name of the child Instrumentable being
     *                                requested.
     *
     * @return A descriptor of the requested child Instrumentable.
     *
     * @throws NoSuchInstrumentableException If the specified Instrumentable
     *                                       does not exist.
     */
    public InstrumentableDescriptor getChildInstrumentableDescriptor(
                                                String childInstrumentableName )
        throws NoSuchInstrumentableException
    {
        return getChildInstrumentableDescriptorLocal( childInstrumentableName );
    }

    /**
     * Returns an array of Descriptors for the child Instrumentables registered
     *  by this Instrumentable.
     *
     * @return An array of Descriptors for the child Instrumentables registered
     *  by this Instrumentable.
     */
    public InstrumentableDescriptor[] getChildInstrumentableDescriptors()
    {
        return getChildInstrumentableDescriptorLocals();
    }
        
    /**
     * Returns a InstrumentDescriptor based on its name.
     *
     * @param instrumentName Name of the Instrument being requested.
     *
     * @return A Descriptor of the requested Instrument.
     *
     * @throws NoSuchInstrumentException If the specified Instrument does
     *                                     not exist.
     */
    public InstrumentDescriptor getInstrumentDescriptor( String instrumentName )
        throws NoSuchInstrumentException
    {
        return getInstrumentDescriptorLocal( instrumentName );
    }

    /**
     * Returns an array of Descriptors for the Instruments registered by this
     *  Instrumentable.
     *
     * @return An array of Descriptors for the Instruments registered by this
     *  Instrumentable.
     */
    public InstrumentDescriptor[] getInstrumentDescriptors()
    {
        return getInstrumentDescriptorLocals();
    }
    
    /**
     * Returns the stateVersion of the instrumentable.  The state version
     *  will be incremented each time any of the configuration of the
     *  instrumentable or any of its children is modified.
     * Clients can use this value to tell whether or not anything has
     *  changed without having to do an exhaustive comparison.
     *
     * @return The state version of the instrumentable.
     */
    public int getStateVersion()
    {
        return m_instrumentableProxy.getStateVersion();
    }
    
    /*---------------------------------------------------------------
     * InstrumentableDescriptorLocal Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the parent InstrumentableDescriptorLocal or null if this is a
     *  top level instrumentable.
     *
     * @return The parent InstrumentableDescriptorLocal or null.
     */
    public InstrumentableDescriptorLocal getParentInstrumentableDescriptorLocal()
    {
        InstrumentableProxy parent = m_instrumentableProxy.getParentInstrumentableProxy();
        if ( parent == null )
        {
            return null;
        }
        else
        {
            return parent.getDescriptor();
        }
    }
    
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
    public InstrumentableDescriptorLocal getChildInstrumentableDescriptorLocal(
                                                    String childInstrumentableName )
        throws NoSuchInstrumentableException
    {
        InstrumentableProxy instrumentableProxy =
            m_instrumentableProxy.getChildInstrumentableProxy( childInstrumentableName );
        if( instrumentableProxy == null )
        {
            throw new NoSuchInstrumentableException(
                "No child instrumentable can be found using name: " + childInstrumentableName );
        }

        return instrumentableProxy.getDescriptor();
    }

    /**
     * Returns an array of Descriptors for the child Instrumentables registered
     *  by this Instrumentable.
     *
     * @return An array of Descriptors for the child Instrumentables registered
     *  by this Instrumentable.
     */
    public InstrumentableDescriptorLocal[] getChildInstrumentableDescriptorLocals()
    {
        return m_instrumentableProxy.getChildInstrumentableDescriptors();
    }
    
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
    public InstrumentDescriptorLocal getInstrumentDescriptorLocal( String instrumentName )
        throws NoSuchInstrumentException
    {
        InstrumentProxy instrumentProxy =
            m_instrumentableProxy.getInstrumentProxy( instrumentName );
        if( instrumentProxy == null )
        {
            throw new NoSuchInstrumentException(
                "No instrument can be found using name: " + instrumentName );
        }

        return instrumentProxy.getDescriptor();
    }

    /**
     * Returns an array of Descriptors for the Instruments registered by this
     *  Instrumentable.
     *
     * @return An array of Descriptors for the Instruments registered by this
     *  Instrumentable.
     */
    public InstrumentDescriptorLocal[] getInstrumentDescriptorLocals()
    {
        return m_instrumentableProxy.getInstrumentDescriptors();
    }
}
