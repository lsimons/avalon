/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.activity;

/**
 * This interface should be implemented by those classes that
 * need to provide a service that requires some resources to be
 * initialized before being able to operate.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Initializable
{
    /**
     * Initialize the service.
     */
    void initialize()
        throws Exception;
}
