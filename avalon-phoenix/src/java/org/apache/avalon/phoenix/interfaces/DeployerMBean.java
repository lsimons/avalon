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
 * MBean Interface for the Deployer to use the deploy
 * feature in the HmtlAdaptor
 *
 * @author <a href="mailto:bauer@denic.de">Joerg Bauer</a>
 * @see Deployer
 */
public interface DeployerMBean
{
    String ROLE = Deployer.class.getName();

    /**
     * Deploy an installation.
     *
     * @param name the name of deployment
     * @param sarURL the installation to deploy
     * @throws DeploymentException if an error occurs
     * @see #deploy(String,String)
     * @see #undeploy(String)
     */
    void deploy( String name, String sarURL )
        throws DeploymentException;

    /**
     * Deploy an installation.
     *
     * @param name the name of deployment
     * @param location the installation to deploy
     * @throws DeploymentException if an error occurs
     * @see #deploy(String,String)
     * @see #undeploy(String)
     */
    void deploy( String name, URL location )
        throws DeploymentException;

    /**
     * undeploy a resource from a location.
     *
     * @param name the name of deployment
     * @throws DeploymentException if an error occurs
     * @see #deploy(String,String)
     * @see #deploy(String,URL)
     */
    void undeploy( String name )
        throws DeploymentException;
}
