/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.ext;

import org.apache.excalibur.event.Sink;

/**
 * Default predicate that does nothing but accept all
 * elements waiting to be enqueued.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultPredicate implements EnqueuePredicate
{
    //-------------------- EnqueuePredicate implementation
    /**
     * @see EnqueuePredicate#accept(Object, Sink)
     */
    public boolean accept(Object element, Sink context)
    {
        return true;
    }
}
