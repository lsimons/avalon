/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

/**
 * Describes an object which can be monitored.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 */
public interface Monitorable
{

    /**
     *  Get the corresponding Resource object for monitoring.
     */
    Resource getResource()
        throws Exception;
}
