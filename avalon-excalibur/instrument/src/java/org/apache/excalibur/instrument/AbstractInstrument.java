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
package org.apache.excalibur.instrument;

/**
 * The AbstractInstrument class can be used by an class wishing to implement
 *  the Instruement interface.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 15:59:13 $
 * @since 4.1
 */
public abstract class AbstractInstrument
    implements Instrument
{
    /** The name of the Instrument. */
    private String m_name;

    /** Proxy object used to communicate with the InstrumentManager. */
    private InstrumentProxy m_proxy;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractInstrument.
     *
     * @param name The name of the Instrument.  The value should be a string
     *             which does not contain spaces or periods.
     */
    protected AbstractInstrument( String name )
    {
        m_name = name;
    }

    /*---------------------------------------------------------------
     * Instrument Methods
     *-------------------------------------------------------------*/
    /**
     * Gets the name for the Instrument.  When an Instrumentable publishes more
     *  than one Instrument, this name makes it possible to identify each
     *  Instrument.  The value should be a string which does not contain
     *  spaces or periods.
     *
     * @return The name of the Instrument.
     */
    public String getInstrumentName()
    {
        return m_name;
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * When the InstrumentManager is present, an InstrumentProxy will be set
     *  to enable the Instrument to communicate with the InstrumentManager.
     *  Once the InstrumentProxy is set, it should never be changed or set
     *  back to null.  This restriction removes the need for synchronization
     *  within the Instrument classes.  Which in turn makes them more
     *  efficient.
     *
     * @param proxy Proxy object used to communicate with the
     *              InstrumentManager.
     */
    public void setInstrumentProxy( InstrumentProxy proxy )
    {
        if( m_proxy != null )
        {
            throw new IllegalStateException(
                "Once an InstrumentProxy has been set, it can not be changed." );
        }
        m_proxy = proxy;
    }

    /**
     * Used by classes being profiled so that they can avoid unnecessary
     *  code when the data from an Instrument is not being used.
     *
     * @return True if an InstrumentProxy has been set and is active.
     */
    public boolean isActive()
    {
        return ( m_proxy != null ) && ( m_proxy.isActive() );
    }

    /**
     * Returns the InstrumentProxy object assigned to the instrument by the
     *  InstrumentManager.
     *
     * @return Proxy object used to communicate with the InstrumentManager.
     */
    protected InstrumentProxy getInstrumentProxy()
    {
        return m_proxy;
    }
}
