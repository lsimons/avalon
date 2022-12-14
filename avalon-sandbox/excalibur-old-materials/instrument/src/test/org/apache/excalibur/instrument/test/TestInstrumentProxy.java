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
package org.apache.excalibur.instrument.test;

import org.apache.excalibur.instrument.InstrumentProxy;

/**
 * Dummy InstrumentProxy used to test instruments.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/04/05 19:39:36 $
 */
public class TestInstrumentProxy
    implements InstrumentProxy
{
    private boolean m_active;
    private int m_value;
    
    /*---------------------------------------------------------------
     * InstrumentProxy Methods
     *-------------------------------------------------------------*/
    /**
     * Used by classes being profiles so that they can avoid unnecessary
     *  code when the data from a Instrument is not being used.
     *
     * @return True if listeners are registered with the Instrument.
     */
    public boolean isActive()
    {
        return m_active;
    }
    
    /**
     * Increments the Instrument by a specified count.  This method should be
     *  optimized to be extremely light weight when there are no registered
     *  CounterInstrumentListeners.
     * <p>
     * This method may throw an IllegalStateException if the proxy is not meant
     *  to handle calls to increment.
     *
     * @param count A positive integer to increment the counter by.
     */
    public void increment( int count )
    {
        m_value += count;
    }
    
    /**
     * Sets the current value of the Instrument.  This method is optimized
     *  to be extremely light weight when there are no registered
     *  ValueInstrumentListeners.
     * <p>
     * This method may throw an IllegalStateException if the proxy is not meant
     *  to handle calls to setValue.
     *
     * @param value The new value for the Instrument.
     */
    public void setValue( int value )
    {
        m_value = value;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Sets the activate flag on the proxy so that it will collect information.
     */
    public void activate()
    {
        m_active = true;
    }
    
    /**
     **/
    public int getValue()
    {
        return m_value;
    }
}
