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

package org.apache.avalon.cornerstone.blocks.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.avalon.cornerstone.services.scheduler.Target;
import org.apache.avalon.cornerstone.services.scheduler.TimeTrigger;

/**
 * Class use internally to package to hold scheduled time entries.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public final class TimeScheduledEntry
    implements Comparable
{
    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat();

    private final String m_name;
    private final TimeTrigger m_trigger;
    private final Target m_target;

    //cached version of time from TimeTrigger class
    private long m_time;
    private boolean m_isValid;

    public TimeScheduledEntry( String name, TimeTrigger trigger, Target target )
    {
        m_name = name;
        m_trigger = trigger;
        m_target = target;
        //m_time = m_trigger.getTimeAfter( System.currentTimeMillis() );
        m_isValid = true;
    }

    /**
     * Return name of trigger.
     *
     * @return the name of trigger
     */
    public String getName()
    {
        return m_name;
    }

    public Target getTarget()
    {
        return m_target;
    }

    public TimeTrigger getTimeTrigger()
    {
        return m_trigger;
    }

    /**
     * Determine if this entry is valid
     *
     * @return true if trigger is valid, false otherwise
     */
    public boolean isValid()
    {
        return m_isValid;
    }

    /**
     * Invalidate trigger
     */
    public void invalidate()
    {
        m_isValid = false;
    }

    /**
     * Retrieve cached time when trigger should run next.
     *
     * @return the time in milliseconds when trigger should run
     */
    public long getNextTime()
    {
        return m_time;
    }

    /**
     * Set cached time in milliseconds when trigger should run
     *
     * @param time the time
     */
    public void setNextTime( long time )
    {
        m_time = time;
    }

    /**
     * Implement comparable interface used to help sort triggers.
     * Triggers are compared based on next time to run
     *
     * @param object the other trigger
     * @return -'ve value if other trigger occurs before this trigger
     */
    public int compareTo( final Object object )
    {
        final TimeScheduledEntry other = (TimeScheduledEntry)object;
        return (int)-( other.m_time - m_time );
    }

    public String toString()
    {
        return "TimeEntry[ name=" + m_name + " valid=" + m_isValid + " time=" + DATEFORMAT.format( new Date( m_time ) ) + " ]";
    }
}

