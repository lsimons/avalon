/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.monitor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.monitor.DirectoryResource;
import org.apache.avalon.excalibur.monitor.impl.ActiveMonitor;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.interfaces.Deployer;

/**
 * This class is responsible for monitoring the deployment
 * directory and deploying, undelploying or redeploying an
 * application as necessary.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/09/13 10:26:12 $
 */
public class DefaultDeploymentMonitor
    extends AbstractLogEnabled
    implements LogEnabled, Parameterizable, Serviceable, Startable, PropertyChangeListener
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( DefaultDeploymentMonitor.class );

    private String m_appsDir;
    private ActiveMonitor m_monitor;
    private Deployer m_deployer;

    /**
     * requires parameter "phoenix.apps.dir" to be set to directory
     * that the component is to monitor.
     */
    public void parameterize( final Parameters parameters )
        throws ParameterException
    {
        m_appsDir = parameters.getParameter( "phoenix.apps.dir" );
    }

    /**
     * Aquire Deployer service for dpeloyment
     */
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        m_deployer = (Deployer)manager.lookup( Deployer.ROLE );
    }

    /**
     * Start the monitor.
     */
    public void start()
        throws Exception
    {
        final DirectoryResource resource =
            new DirectoryResource( m_appsDir );
        resource.addPropertyChangeListener( this );
        m_monitor = new ActiveMonitor();
        m_monitor.setFrequency( 1000L );
        m_monitor.addResource( resource );
        m_monitor.start();
    }

    /**
     * Stop the monitor.
     */
    public void stop()
        throws Exception
    {
        m_monitor.stop();
    }

    /**
     * This method is called when the monitor detects that the contents
     * of deployment directory has changed.
     */
    public void propertyChange( final PropertyChangeEvent event )
    {
        final String name = event.getPropertyName();
        final Set newValue = (Set)event.getNewValue();
        final Set deployments = getDeployments( newValue );
        final Iterator iterator = deployments.iterator();

        if( name.equals( DirectoryResource.ADDED ) )
        {
            while( iterator.hasNext() )
            {
                final File file = (File)iterator.next();
                deployApplication( file );
            }
        }
        else if( name.equals( DirectoryResource.REMOVED ) )
        {
            while( iterator.hasNext() )
            {
                final File file = (File)iterator.next();
                undeployApplication( file );
            }
        }
        else
        {
            while( iterator.hasNext() )
            {
                final File file = (File)iterator.next();
                redeployApplication( file );
            }
        }
    }

    /**
     * Deploy application for specified file.
     *
     * @param file the application archive
     */
    private void deployApplication( final File file )
    {
        final String name =
            FileUtil.removeExtension( file.getName() );
        try
        {
            final String message =
                REZ.getString( "monitor.deploy.notice",
                               name,
                               file );
            getLogger().info( message );

            m_deployer.deploy( name, file.toURL() );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "monitor.no-deploy.error", file, e );
            getLogger().warn( message, e );
        }
    }

    /**
     * Undeploy application for specified file.
     *
     * @param file the application archive
     */
    private void undeployApplication( final File file )
    {
        final String name =
            FileUtil.removeExtension( file.getName() );
        try
        {
            final String message =
                REZ.getString( "monitor.undeploy.notice",
                               name );
            getLogger().info( message );
            m_deployer.undeploy( name );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "monitor.no-undeploy.error", file, e );
            getLogger().warn( message, e );
        }
    }

    /**
     * Redeploy application for specified file.
     *
     * @param file the application archive
     */
    private void redeployApplication( final File file )
    {
        final String name =
            FileUtil.removeExtension( file.getName() );
        try
        {
            final String message =
                REZ.getString( "monitor.redeploy.notice",
                               name,
                               file );
            getLogger().info( message );
            m_deployer.undeploy( name );
            m_deployer.deploy( name, file.toURL() );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "monitor.no-redeploy.error", file, e );
            getLogger().warn( message, e );
        }
    }

    /**
     * Retrieve the set of files that are candidate deployments.
     */
    private Set getDeployments( final Set newValue )
    {
        final Set deployments = new HashSet();
        final Iterator iterator = newValue.iterator();
        while( iterator.hasNext() )
        {
            final File file = (File)iterator.next();
            if( isDeployment( file ) )
            {
                deployments.add( file );
            }
            else
            {
                final String message =
                    REZ.getString( "monitor.skipping-file.notice", file );
                getLogger().info( message );
            }
        }
        return deployments;
    }

    /**
     * Return true if file represents a phoenix deployment.
     *
     * @param file the file
     * @return
     */
    private boolean isDeployment( final File file )
    {
        return
            !file.isDirectory() &&
            file.getName().endsWith( ".sar" );
    }
}
