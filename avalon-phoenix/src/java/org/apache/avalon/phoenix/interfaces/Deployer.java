/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import java.net.URL;
import org.apache.avalon.framework.component.Component;

/**
 * A Deployer is responsible for taking a URL (ie a jar/war/ear) and deploying
 * it to a particular "location". "location" means different things for
 * different containers. For a servlet container it may mean the place to
 * mount servlet (ie /myapp --> /myapp/Cocoon.xml is mapping cocoon servlet to
 * /myapp context).
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface Deployer
    extends Component
{
    String ROLE = "org.apache.avalon.phoenix.interfaces.Deployer";

    /**
     * Deploy an installation.
     *
     * @param name the name of deployment
     * @param installation the installation to deploy
     * @exception DeploymentException if an error occurs
     */
    void deploy( String name, URL location )
        throws DeploymentException;

    /**
     * undeploy a resource from a location.
     *
     * @param name the name of deployment
     * @exception DeploymentException if an error occurs
     */
    void undeploy( String name )
        throws DeploymentException;

    /**
     * Determine if a deployment has matching name.
     *
     * @param name the name of deployment
     * @return true if deployed by this deployer, false otherwise
     */
    //boolean isDeployed( String name );
}
