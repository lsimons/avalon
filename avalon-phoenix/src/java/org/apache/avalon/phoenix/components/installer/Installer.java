/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.installer;

import java.net.URL;
import org.apache.avalon.framework.component.Component;

/**
 * An Installer is responsible for taking a URL (ie a jar/war/ear) and 
 * installing it.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Installer
    extends Component
{
    String ROLE = "org.apache.avalon.phoenix.components.installer.Installer";

    /**
     * Install a resource indicate by url to location.
     *
     * @param name the name of instalation
     * @param url the url of instalation
     * @exception InstallationException if an error occurs
     */
    Installation install( String name, URL url )
        throws InstallationException;

    /**
     * undeploy a resource from a location.
     *
     * @param name the name of instalation
     * @exception InstallationException if an error occurs
     */
    void uninstall( Installation installation )
        throws InstallationException;

    /**
     * Determine if a instalation is valid.
     *
     * @param installation the instalation
     * @return true if valid install, false otherwise
     */
    //boolean isInstalled( Installation installation );
}
