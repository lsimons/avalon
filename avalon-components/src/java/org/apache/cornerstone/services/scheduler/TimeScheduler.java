/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.scheduler;

import java.util.NoSuchElementException;
import org.apache.phoenix.Service;

/**
 * This service provides a way to regularly schedule jobs.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface TimeScheduler
    extends Service
{
    /**
     * Schedule a time based trigger.
     * Note that if a TimeTrigger already has same name then it is removed.
     *
     * @param name the name of the trigger
     * @param trigger the trigger
     * @param target the target
     */
    void addTrigger( String name, TimeTrigger trigger, Target target );

    /**
     * Remove a scheduled trigger by name.
     *
     * @param name the name of the trigger
     * @exception NoSuchElementException if no trigger exists with that name
     */
    void removeTrigger( String name )
        throws NoSuchElementException;

    /**
     * Force a trigger time to be recalculated.
     *
     * @param name the name of the trigger
     * @exception NoSuchElementException if no trigger exists with that name
     */
    void resetTrigger( String name )
        throws NoSuchElementException;
}
