/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.phoenix.tools.installer.Installation;

/**
 * Recorder for application deployment specific information.
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 */
public interface DeploymentRecorder
{
    String ROLE = DeploymentRecorder.class.getName();

    void recordInstallation( String name, Installation installation )
        throws DeploymentException;

    Installation fetchInstallation( String name )
        throws DeploymentException;

}

