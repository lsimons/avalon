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
package org.apache.excalibur.instrument.client;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleUtils;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 16:36:49 $
 * @since 4.1
 */
class MaintainedSampleLease
{
    private String m_instrumentName;
    private String m_sampleName;
    private int    m_type;
    private long   m_interval;
    private int    m_size;
    private long   m_leaseDuration;
    private String m_description;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    MaintainedSampleLease( String instrumentName,
                           int    type,
                           long   interval,
                           int    size,
                           long   leaseDuration,
                           String description )
    {
        m_instrumentName = instrumentName;
        m_type           = type;
        m_interval       = interval;
        m_size           = size;
        m_leaseDuration  = leaseDuration;
        m_description    = description;
        
        m_sampleName = InstrumentSampleUtils.generateFullInstrumentSampleName(
            m_instrumentName, m_type, m_interval, m_size );
    }
    
    MaintainedSampleLease( Configuration stateConfig ) throws ConfigurationException
    {
        m_instrumentName = stateConfig.getAttribute         ( "instrument-name" );
        m_type           = stateConfig.getAttributeAsInteger( "type" );
        m_interval       = stateConfig.getAttributeAsLong   ( "interval" );
        m_size           = stateConfig.getAttributeAsInteger( "size" );
        m_leaseDuration  = stateConfig.getAttributeAsLong   ( "lease-duration" );
        m_description    = stateConfig.getAttribute         ( "description" );
        
        m_sampleName = InstrumentSampleUtils.generateFullInstrumentSampleName(
            m_instrumentName, m_type, m_interval, m_size );
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Saves the current state into a Configuration.
     *
     * @return The state as a Configuration.
     */
    public final Configuration saveState()
    {
        DefaultConfiguration stateConfig = new DefaultConfiguration( "maintained-sample", "-" );
        
        stateConfig.setAttribute( "instrument-name", m_instrumentName );
        stateConfig.setAttribute( "type",
            InstrumentSampleUtils.getInstrumentSampleTypeName( m_type ) );
        stateConfig.setAttribute( "interval",        Long.toString( m_interval ) );
        stateConfig.setAttribute( "size",            Integer.toString( m_size ) );
        stateConfig.setAttribute( "lease-duration",  Long.toString( m_leaseDuration ) );
        stateConfig.setAttribute( "description",     m_description );
        
        return stateConfig;
    }
    
    String getInstrumentName()
    {
        return m_instrumentName;
    }
    
    String getSampleName()
    {
        return m_sampleName;
    }
    
    int getType()
    {
        return m_type;
    }
    
    long getInterval()
    {
        return m_interval;
    }
    
    int getSize()
    {
        return m_size;
    }
    
    long getLeaseDuration()
    {
        return m_leaseDuration;
    }
    
    String getDescription()
    {
        return m_description;
    }
}

