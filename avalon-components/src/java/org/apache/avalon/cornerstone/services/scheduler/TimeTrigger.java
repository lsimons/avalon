/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.scheduler;

/**
 * A kind of trigger that makes the determination to go off based
 * on time.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:ram.chidambaram@telus.com">Ram Chidambaram</a>
 */
public interface TimeTrigger
    extends Trigger
{
    /**
     * Returns the next time after the given <tt>moment</tt> when
     * this trigger goes off.
     *
     * @param moment base point in milliseconds
     * @return the time in milliseconds when this trigger goes off
     */
    long getTimeAfter( long moment );
}
