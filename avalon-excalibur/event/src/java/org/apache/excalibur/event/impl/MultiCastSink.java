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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.excalibur.event.PreparedEnqueue;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.SinkFullException;

/**
 * This is a {@link org.apache.excalibur.event.seda.event.Sink}
 * implementation that multicasts enqueue operations to the
 * contained and concrete sink objects.  The multi cast sink
 * will try to enqueue and only succeeds if no element was
 * rejected from any sink. The sink can be configured to
 * enqueue into one sink alone or all sinks.
 * If a sink array in the collection of sinks contains more
 * than one sink the multicast sink will try to enqueue the
 * element always to <b>only one</b> of these sinks.
 *
 * @version $Revision: 1.5 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class MultiCastSink implements Sink
{
    /** A collection of sink arrays representing the  sinks to enqueue to. */
    private final Collection m_sinks;

    /** The size of the sink. */
    private final int m_size;

    /** Boolean value describing if one or all operations must succeed. */
    private final boolean m_single;

    //---------------------- LossyMultiCastSink constructors
    /**
     * This constructor creates a failure in-tolerant multicast
     * sink based on the collection of sink arrays. The delivery
     * must succeed for all sinks in the collection or it will
     * fail entirely.
     * @since May 16, 2002
     *
     * @param sinks
     *  A collection of sink arrays for each stage.
     */
    public MultiCastSink(Collection sinks)
    {
        this(sinks, false);
    }

    /**
     * This constructor creates a failure in-tolerant multicast
     * sink based on the collection of sink arrays.
     * @since May 16, 2002
     *
     * @param sinks
     *  A collection of sink arrays for each stage.
     * @param single
     *  <m_code>true</m_code> if just one operation must succeed.
     *  <m_code>false</m_code> if all operations must succeed.
     */
    public MultiCastSink(Collection sinks, boolean single)
    {
        m_sinks = sinks;
        m_size = -1;
        m_single = single;
    }

    //---------------------- Sink implementation
    /**
     * @see Sink#canAccept()
     */
    public int canAccept()
    {
        return 0;
    }

    /**
     * @see Sink#isFull()
     */
    public boolean isFull()
    {
        return false;
    }

    /**
     * @see Sink#maxSize()
     */
    public int maxSize()
    {
        return 0;
    }

    /**
     * @see Sink#enqueue(Object)
     */
    public void enqueue(Object element) throws SinkException
    {
        final PreparedEnqueue prepared;
        prepared = prepareEnqueue(new Object[] { element });
        prepared.commit();
    }

    /**
     * @see Sink#enqueue(Object[])
     */
    public void enqueue(Object[] elements) throws SinkException
    {
        final PreparedEnqueue prepared = prepareEnqueue(elements);
        prepared.commit();
    }

    /**
     * @see Sink#tryEnqueue(Object)
     */
    public boolean tryEnqueue(Object element)
    {
        try
        {
            enqueue(element);
            return true;
        }
        catch (SinkException e)
        {
            return false;
        }
    }

    /**
     * @see Sink#prepareEnqueue(Object[])
     */
    public PreparedEnqueue prepareEnqueue(Object[] elements)
        throws SinkException
    {

        //checkEnqueuePredicate(elements);

        final DefaultPreparedEnqueue prepares = new DefaultPreparedEnqueue();
        int successful = 0;

        final Iterator sinks = m_sinks.iterator();

        // iterate through the sinks and try to enqueue
        while (sinks.hasNext())
        {
            final Sink sink = (Sink) sinks.next();

            try
            {
                prepares.addPreparedEnqueue(sink.prepareEnqueue(elements));
            }
            catch (SinkFullException e)
            {
                continue;
            }

            // if enqueue successful return here or just break and continue
            if (m_single)
            {
                return prepares;
            }
            successful++;
            break;

        }
        if (successful < m_sinks.size())
        {
            // rollback all enqueues.
            prepares.abort();

            throw new SinkFullException("Could not deliver elements.");
        }

        return prepares;
    }

    /**
     * @see Sink#size()
     */
    public int size()
    {
        return m_size;
    }

    //------------------------- LossyMultiCastSink inner classes
    /**
     * A prepared enqueue object that holds other prepared
     * enqueue objects and allows to perform a commit / abort
     * on all of these objects.
     * @since May 16, 2002
     *
     * @author <a href = "mailto:mschier@earthlink.net">schierma</a>
     */
    private static final class DefaultPreparedEnqueue
        implements PreparedEnqueue
    {
        /**
         * A collection of prepared enqueue objects
         */
        private final Collection m_preparedEnqueues = new LinkedList();

        //------------------------ PreparedEnqueue implementation
        /**
         * @see PreparedEnqueue#abort()
         */
        public void abort()
        {
            final Iterator iter = m_preparedEnqueues.iterator();

            while (iter.hasNext())
            {
                ((PreparedEnqueue) iter.next()).abort();
            }
        }

        /**
         * @see PreparedEnqueue#commit()
         */
        public void commit()
        {
            final Iterator iter = m_preparedEnqueues.iterator();

            while (iter.hasNext())
            {
                ((PreparedEnqueue) iter.next()).commit();
            }
        }

        //------------------------ DefaultPreparedEnqueue specific implementation
        /**
         * Adds a prepared enqueue object to the list
         * of prepared enqueues.
         * @since May 16, 2002
         *
         * @param preparedEnqueue
         *  The prepared enqueue object to be added.
         */
        public void addPreparedEnqueue(PreparedEnqueue preparedEnqueue)
        {
            m_preparedEnqueues.add(preparedEnqueue);
        }
    } //-- end DefaultPreparedEnqueue inner class
}
