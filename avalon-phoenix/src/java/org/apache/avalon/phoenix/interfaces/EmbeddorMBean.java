/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import java.util.Date;

/**
 * This is the interface via which the Management interface interacts
 * with the Embeddor.
 *
 * @author <a href="peter at apache.org">Peter Donald</a>
 */
public interface EmbeddorMBean
{
    String ROLE = EmbeddorMBean.class.getName();

    /**
     * Get name by which the server is know.
     * Usually this defaults to "Phoenix" but the admin
     * may assign another name. This is useful when you
     * are managing a cluster of Phoenix servers.
     *
     * @return the name of server
     */
    String getName();

    /**
     * Get location of Phoenix installation
     *
     * @return the home directory of phoenix
     */
    String getHomeDirectory();

    /**
     * Get the date at which this server started.
     *
     * @return the date at which this server started
     */
    Date getStartTime();

    /**
     * Retrieve the number of millisecond
     * the server has been up.
     *
     * @return the the number of millisecond the server has been up
     */
    long getUpTimeInMillis();

    /**
     * Retrieve a string identifying version of server.
     * Usually looks like "v4.0.1a".
     *
     * @return version string of server.
     */
    String getVersion();

    /**
     * Get a string defining the build.
     * Possibly the date on which it was built, where it was built,
     * with what features it was built and so forth.
     *
     * @return the string describing build
     */
    String getBuild();

    /**
     * Request the Embeddor shutsdown.
     */
    void shutdown();

    /**
     * Request the embeddor to restart.
     *
     * @throws UnsupportedOperationException if restart not a supported operation
     */
    void restart()
        throws UnsupportedOperationException;
}
