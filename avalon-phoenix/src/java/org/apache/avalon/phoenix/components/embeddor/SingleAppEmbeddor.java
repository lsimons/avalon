/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.embeddor;

import java.io.File;
import java.util.Iterator;
import org.apache.avalon.phoenix.components.application.Application;
import org.apache.avalon.excalibur.container.ContainerException;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;

/**
 * Embeddor to host only a single application.
 * It is required that the user set parameter
 * <ul>
 *   <li>application-location = location of application directory or file
 * </ul>
 * Other parameters are inherited from PhoenixEmbeddor.
 *
 * @author <a href="colus@isoft.co.kr">Eung-ju Park</a>
 * @author <a href="donaldp@apache.org">Peter Donald</a>
 */
public class SingleAppEmbeddor
    extends DefaultEmbeddor
    implements ComponentManager
{
    ///Sole application hosted in kernel
    private Application    m_application;

    /**
     * Deploy a single application.
     *
     * @exception Exception if an error occurs
     */
    protected void deployDefaultApplications()
        throws Exception
    {
        final String applicationName = getParameters().getParameter( "application-name", "default" );
        final String applicationLocation = getParameters().getParameter( "application-location" );

        final File directory = new File( applicationLocation );
        deployFile( applicationName, directory );

        m_application = (Application)getKernel().getEntry( applicationName ).getInstance();
    }

    /**
     * List all block names in application.
     *
     * @return the list of all block names
     */
    public String[] list()
    {
        return m_application.list();
    }

    /**
     * Get a Block by name.
     *
     * @return the block in application
     */
    public Component lookup( final String role )
        throws ComponentException
    {
        Component component = null;

        try
        {
            component = (Component)m_application.getEntry( role ).getInstance();
        }
        catch( final ContainerException ce )
        {
            throw new ComponentException( ce.getMessage(), ce );
        }

        return component;
    }

    public boolean hasComponent( final String role )
    {
        try
        {
            m_application.getEntry( role );
            return true;
        }
        catch( final ContainerException ce )
        {
            return false;
        }
    }

    /**
     * Release block back to application.
     *
     * @param component the block
     */
    public void release( final Component component )
    {
    }
}
