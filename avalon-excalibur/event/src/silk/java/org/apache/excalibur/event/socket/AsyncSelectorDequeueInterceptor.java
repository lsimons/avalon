/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.event.ext.DequeueInterceptor;
import org.apache.excalibur.nbio.AsyncSelection;
import org.apache.excalibur.nbio.AsyncSelector;

/**
 * An implementation of the executable command interface that
 * uses the selector to select elements for the specified queue.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public final class AsyncSelectorDequeueInterceptor extends AbstractLogEnabled
    implements DequeueInterceptor
{
    /** The selector to be used */
    private final AsyncSelector m_selector;
    
    /** The timeout for the selection process */
    private final long m_timeout;

    //-------------------------- AsyncSelectorDequeueInterceptor constructors
    /**
     * Constructor for AsyncSelectorDequeueInterceptor that
     * takes the wrapped selector object.
     * @since Sep 23, 2002
     * 
     * @param selector
     *  The selector object wrapped by this executable.
     */
    public AsyncSelectorDequeueInterceptor(AsyncSelector selector)
    {
        this(selector, -1);
    }
    
    /**
     * Constructor for AsyncSelectorDequeueInterceptor that
     * takes the wrapped selector object.
     * @since Sep 23, 2002
     * 
     * @param selector
     *  The selector object wrapped by this executable.
     * @param timeout
     *  The timeout for the selection process 
     */
    public AsyncSelectorDequeueInterceptor(AsyncSelector selector, long timeout)
    {
        super();
        m_selector = selector;
        m_timeout = timeout;
    }

    //-------------------------- DequeueInterceptor implementation
    /**
     * @see DequeueInterceptor#before(Source)
     */
    public void before(Queue context)
    {
        if(context.size() == 0 && !m_selector.isClosed())
        {
            try
            {
                final AsyncSelection[] keys = m_selector.select(m_timeout);
                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug(
                        keys.length + " system events polled for.");
                }
                if (keys.length > 0)
                {
                    context.enqueue(keys);
                }
            }
            catch (Exception e)
            {
                // for now just log. later apply back pressure
                if (getLogger().isErrorEnabled())
                {
                    getLogger().error("Could not enqueue system events", e);
                }
            }
        }
    }

    /**
     * @see DequeueInterceptor#after(Source)
     */
    public void after(Queue context)
    {
        // do nothing
    }

}
