/* 
 * Copyright (C) The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */
package org.apache.avalon.camelot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.avalon.component.Component;
import org.apache.avalon.component.ComponentException;
import org.apache.excalibur.io.FileUtil;
import org.apache.log.Logger;

/**
 * A Deployer is responsible for taking a URL (ie a jar/war/ear) and deploying
 * it to a particular "location". "location" means different things for
 * different containers. For a servlet container it may mean the place to
 * mount servlet (ie /myapp --> /myapp/Cocoon.xml is mapping cocoon servlet to
 * /myapp context).
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractDeployer
    extends AbstractLoggable
    implements Deployer
{
    protected final HashMap                m_deployments  = new HashMap();
    protected boolean                      m_autoUndeploy;
    protected String                       m_type;

    public void deploy( final String location, final URL url )
        throws DeploymentException
    {
        checkDeployment( location, url );
        final File file = getFileFor( url );

        getLogger().info( "Deploying " + m_type + " file (" + file + ") as " + location );
        deployFromFile( location, file );
    }

    protected void checkDeployment( final String location, final URL url )
        throws DeploymentException
    {
        if( null != m_deployments.get( location ) )
        {
            throw new DeploymentException( m_type + " already exists at " + location );
        }
        
        if( !isValidLocation( location ) )
        {
            throw new DeploymentException( "Invalid location (" + location + 
                                           ") for " + m_type );
        }
    }

    public void undeploy( final String location )
        throws DeploymentException
    {
        final Component component = (Component)m_deployments.get( location );

        if( null == component )
        {
            throw new DeploymentException( m_type + " does not exist at " + location );
        }

        final boolean canUndeploy = canUndeploy( component );

        if( !canUndeploy )
        {
            if( !m_autoUndeploy )
            {
                //we are midstream but not allowed to automagically undeploy .. therefore
                throw new DeploymentException( m_type + " not ready to undeploy at " + 
                                               location );
            }
            else
            {
                shutdownDeployment( component );
            }
        }

        //if everything has gone successful then remove application
        m_deployments.remove( location );
    }

    protected File getCacheLocationFor( final URL url )
        throws DeploymentException
    {
        throw new DeploymentException( "Unable to deploy non-local resources" );
    }

    protected File getFileFor( final URL url )
        throws DeploymentException
    {
        File file = null;

        if( url.getProtocol().equals( "file" ) )
        {
            file = new File( url.getFile() );
        }
        else
        {
            file = getCacheLocationFor( url );
            try { FileUtil.copyURLToFile( url, file ); }
            catch( final IOException ioe )
            {
                throw new DeploymentException( "Failed attempting to copy from  " + url +
                                               " to local file cache " + file, ioe );
            }
        }

        file = file.getAbsoluteFile();

        if( !file.exists() )
        {
            throw new DeploymentException( "Could not find application archive at " + 
                                           file );
        }

        if( file.isDirectory() )
        {
            throw new DeploymentException( "Could not find application archive at " + 
                                           file + " as it is a directory." );
        }

        return file;
    }

    protected boolean isValidLocation( String location )
    {
        return true;
    }

    protected boolean canUndeploy( Component component )
        throws DeploymentException
    {
        return true;
    }

    protected void shutdownDeployment( Component component )
        throws DeploymentException
    {
    }

    protected abstract void deployFromFile( String location, File file )
        throws DeploymentException;
}
