/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import java.net.URL;

/**
 * A basic service to Install an application.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface Installer
{
    String ROLE = Installer.class.getName();

    /**
     * Install the Sar designated by url.
     *
     * @param url the url of instalation
     * @throws InstallationException if an error occurs
     */
    Installation install( String name, URL url )
        throws InstallationException;

    /**
     * Uninstall the Sar designated installation.
     *
     * @param installation the installation
     * @throws InstallationException if an error occurs
     */
    void uninstall( Installation installation )
        throws InstallationException;
}
