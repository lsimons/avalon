/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.thread;

/**
 * A interface to mark a component as not ThreadSafe.
 *
 * NB: It was a deliberat e choice not to extend Component. This will have to
 * be reassed once we see it in action.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface SingleThreaded
{
}
