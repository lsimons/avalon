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

 4. The names "Jakarta", "Apache Avalon", "Avalon Components", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.cornerstone.services.scheduler;

/**
 * Goes off every <tt>period</tt> milliseconds after waiting for
 * <tt>offset</tt> milliseconds from the moment the trigger was
 * <tt>reset</tt>.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:ram.chidambaram@telus.com">Ram Chidambaram</a>
 */
public class PeriodicTimeTrigger
    implements TimeTrigger
{
    protected final long m_offset;
    protected final long m_period;
    private long m_triggerTime;

    /**
     * Creates a periodic trigger. It goes off the first time after
     * <tt>offset</tt> milliseconds from the time it was
     * <tt>reset</tt> and then every <tt>period</tt>
     * milliseconds. The trigger is <tt>reset</tt> as
     * part of its construction.
     *
     * @param offset initial delay in milliseconds, -1 means fire immediately
     * @param period after initial delay in milliseconds, -1 means fire only once after initial delay
     */
    public PeriodicTimeTrigger( final int offset, final int period )
    {
        m_offset = offset;
        m_period = period;

        reset();
    }

    /**
     * Returns the next time after the given <tt>moment</tt> when
     * this trigger goes off.
     *
     * @param moment base point in milliseconds
     * @return the time in milliseconds when this trigger goes off
     */
    public long getTimeAfter( final long moment )
    {
        if( moment <= m_triggerTime )
        {
            return m_triggerTime;
        }
        else
        {
            if( -1 == m_period )
            {
                return -1;
            }

            final long over = moment - m_triggerTime;
            final long remainder = over % m_period;

            return moment + ( m_period - remainder );
        }
    }

    public long getOffset()
    {
        return m_offset;
    }

    public long getPeriod()
    {
        return m_period;
    }

    /**
     * Reset the original TimeTrigger.
     * This will recalculate the activation time for this trigger.
     */
    public void reset()
    {
        final long current = System.currentTimeMillis();

        if( -1 == m_offset )
        {
            m_triggerTime = current;
        }
        else
        {
            m_triggerTime = current + m_offset;
        }
    }

    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( "PeriodicTimeTrigger[ " );

        sb.append( "trigger time=" );
        sb.append( m_triggerTime );
        sb.append( " " );

        sb.append( "offset=" );
        sb.append( m_offset );
        sb.append( " " );

        if( -1 != m_period )
        {
            sb.append( "period=" );
            sb.append( m_period );
            sb.append( " " );
        }

        sb.append( "]" );

        return sb.toString();
    }
}



