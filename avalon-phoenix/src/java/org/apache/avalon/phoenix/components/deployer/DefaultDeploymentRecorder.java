/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.deployer;

import java.util.HashMap;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.interfaces.DeploymentRecorder;
import org.apache.avalon.phoenix.interfaces.DeploymentException;
import org.apache.avalon.phoenix.tools.installer.Installation;

/**
 * Recorder for application deployment specific information (To avoid 
 * installation of applications every time Phoenix starts this class should 
 * persist the information in order to be reconstructed). 
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 */
public class DefaultDeploymentRecorder 
    extends AbstractLoggable 
    implements DeploymentRecorder {
           
    private final HashMap m_installations = new HashMap();

    public synchronized void recordInstallation( final String name, final Installation installation ) 
       throws DeploymentException
    {
        if( null == installation )
        {
            m_installations.remove( name );
        }
        else
        {        
            m_installations.put( name, installation );           
        }
    }           

    public synchronized Installation fetchInstallation( final String name ) 
       throws DeploymentException
    {
       return (Installation)m_installations.get( name );
    }        
}
