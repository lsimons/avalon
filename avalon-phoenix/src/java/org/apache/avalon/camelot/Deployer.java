/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.camelot;

import java.net.URL;
import org.apache.avalon.component.Component;

/**
 * A Deployer is responsible for taking a URL (ie a jar/war/ear) and deploying
 * it to a particular "location". "location" means different things for
 * different containers. For a servlet container it may mean the place to
 * mount servlet (ie /myapp --> /myapp/Cocoon.xml is mapping cocoon servlet to
 * /myapp context).
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Deployer
    extends Component
{
    /**
     * Deploy a resource indicate by url to location.
     *
     * @param location the location to deploy to
     * @param url the url of deployment
     * @exception DeploymentException if an error occurs
     */
    void deploy( String location, URL url )
        throws DeploymentException;

    /**
     * undeploy a resource from a location.
     *
     * @param location the location
     * @exception DeploymentException if an error occurs
     */
    void undeploy( String location )
        throws DeploymentException;
}
