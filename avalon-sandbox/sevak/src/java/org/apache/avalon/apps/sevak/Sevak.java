/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak;

import java.io.File;

/**
 *  Web Server Interface (Catalina is the target implementation)
 * @author  Vinay Chandrasekharan<vinayc77@yahoo.com>
 * @version 1.0
 */
public interface Sevak
{
    /** Role of the Sevak Service*/
    String ROLE = Sevak.class.getName();

    /**
     * A constant for localhost
     */
    String LOCALHOST = "localhost";


    /**
     * Deploy the given Web Application
     * @param context Context for the the webapp
     * @param pathToWebAppFolder path can be a war-archive or exploded directory
     * @throws SevakException Thrown when context already exists
     */
    void deploy(String context, File pathToWebAppFolder) throws SevakException;

    /**
     * Undeploy the given WebApp 
     * @param context Context for the the webapp
     * @throws SevakException Thrown if context does NOT exist
     */
    void undeploy(String context) throws SevakException;

}
