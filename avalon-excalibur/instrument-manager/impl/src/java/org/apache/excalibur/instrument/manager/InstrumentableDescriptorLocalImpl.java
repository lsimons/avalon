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

import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;

/**
 * Describes a Instrumentable and acts as a Proxy to protect the original
 *  Instrumentable.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:25 $
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
