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
package org.apache.excalibur.event.impl;

import org.apache.excalibur.event.EnqueuePredicate;
import org.apache.excalibur.event.Sink;

/**
 * This enqueue predicate implements input rate policing.
 *
 * @version $Revision: 1.2 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class RateLimitingPredicate implements EnqueuePredicate
{
    /** The rate to which the enqueuing should be limited */
    private double m_targetRate;

    /** The depth of the token bucket */
    private int m_depth;

    private int m_tokenCount;
    private long m_lastTime;
    private double m_regenTimeMs;

    /** Number of milliseconds between regenerations */
    private static final long MIN_REGENERATION_TIME = 0;

    //------------------------- RateLimitingPredicate constructors
    /**
     * Create a new RateLimitingPredicate for the given sink,
     * bucket depth and no rate limit.
     * @since May 15, 2002
     *
     * @param depth
     *  The token bucket depth.
     */
    public RateLimitingPredicate(int depth)
    {
        this(-1.0, depth);
    }

    /**
     * Create a new RateLimitingPredicate for the given sink,
     * targetRate, and token bucket depth.
     * A rate of <m_code>-1.0</m_code> indicates no rate limit.
     * @since May 15, 2002
     *
     * @param targetRate
     *  The rate that is the target for this predicate
     * @param depth
     *  The token bucket depth.
     */
    public RateLimitingPredicate(double targetRate, int depth)
    {
        m_targetRate = targetRate;
        m_depth = depth;

        m_regenTimeMs = (1.0 / targetRate) * 1.0e3;
        if (m_regenTimeMs < 1)
        {
            m_regenTimeMs = 1;
        }

        m_tokenCount = depth;
        m_lastTime = System.currentTimeMillis();
    }

    //------------------------- EnqueuePredicate implementation
    /**
     * @see EnqueuePredicate#accept(Object, Sink)
     */
    public boolean accept(Object element, Sink sink)
    {
        if (m_targetRate == -1.0)
        {
            return true;
        }

        // First regenerate tokens
        long currentTime = System.currentTimeMillis();
        long delay = currentTime - m_lastTime;

        if (delay >= MIN_REGENERATION_TIME)
        {
            double numTokens = ((double) delay * 1.0) / (m_regenTimeMs * 1.0);
            m_tokenCount += numTokens;

            if (m_tokenCount > m_depth)
            {
                m_tokenCount = m_depth;
            }

            m_lastTime = currentTime;
        }

        if (m_tokenCount >= 1)
        {
            m_tokenCount--;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @see EnqueuePredicate#accept(Object, Sink)
     */
    public boolean accept(Object[] elements, Sink sink)
    {
        if (m_targetRate == -1.0)
        {
            return true;
        }

        // First regenerate tokens
        long currentTime = System.currentTimeMillis();
        long delay = currentTime - m_lastTime;

        if (delay >= MIN_REGENERATION_TIME)
        {
            double numTokens = ((double) delay * 1.0) / (m_regenTimeMs * 1.0);
            m_tokenCount += numTokens;

            if (m_tokenCount > m_depth)
            {
                m_tokenCount = m_depth;
            }
            m_lastTime = currentTime;
        }

        if (m_tokenCount >= elements.length)
        {
            m_tokenCount -= elements.length;
            return true;
        }
        else
        {
            return false;
        }
    }

    //------------------------- RateLimitingPredicate specific implementation
    /**
     * Returns the current rate limit.
     * @since May 15, 2002
     *
     * @return double
     *  the current target rate
     */
    public double getTargetRate()
    {
        return m_targetRate;
    }

    /**
     * Returns the current depth.
     * @since May 15, 2002
     *
     * @return int
     *  The current bucket depth.
     */
    public int getDepth()
    {
        return m_depth;
    }

    /**
     * Returns the number of tokens currently in the bucket.
     * @since May 15, 2002
     *
     * @return int
     *  the number of tokens currently in the bucket.
     */
    public int getBucketSize()
    {
        return (int) m_tokenCount;
    }

    /**
     * Allows to set the rate limit. A limit of <m_code>-1.0</m_code>
     * indicates no rate limit.
     * @since May 15, 2002
     *
     * @param targetRate
     *  the current rate limit.
     */
    public void setTargetRate(double targetRate)
    {
        m_targetRate = targetRate;

        m_regenTimeMs = (1.0 / targetRate) * 1.0e3;
        if (m_regenTimeMs < 1)
        {
            m_regenTimeMs = 1;
        }
    }

    /**
     * Allows to set the bucket depth.
     * @since May 15, 2002
     *
     * @param depth
     *  The bucket depth as an integer.
     */
    public void setDepth(int depth)
    {
        m_depth = depth;
    }

}