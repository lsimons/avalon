/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.dbcp;

import java.io.File;
import java.sql.Connection;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.Logger;

/**
 * This is a JDBC Connection Manager implementation that uses the
 * Jakarta-Commons database connection pooling and dbcp packages
 * to serve up pooled connections to datasources.
 *
 * @avalon.component name="dbcp-manager" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.sql.ConnectionManager"
 */
public class DbcpConnectionManager
	implements
		Contextualizable,
		Configurable,
		Initializable,
		Startable,
		ConnectionManager
{

	//---------------------------------------------------------
	// state
	//---------------------------------------------------------

	/**
	 * The logging channel assigned by the container.
	 */
	private Logger m_logger;
	/**
	 * The working base directory.
	 */
	private File m_basedir;
	
    //---------------------------------------------------------
    // constructor
    //---------------------------------------------------------

    /**
     * Creates a new instance of the HSQL database server with the
     * logging channel supplied by the container.
     *
     * @param logger the container-supplied logging channel
     */
    public DbcpConnectionManager( final Logger logger )
    {
        m_logger = logger;
    }

    //---------------------------------------------------------
    // Contextualizable
    //---------------------------------------------------------

   /**
    * Contextualization of the server by the container.
    *
    * @param context the supplied server context
    * @exception ContextException if a contextualization error occurs
    * @avalon.entry key="urn:avalon:home" type="java.io.File"
    */
    public void contextualize( final Context context )
      throws ContextException
    {
        // cache the working base directory
        m_basedir = getBaseDirectory( context );
    }

	//---------------------------------------------------------
	// Configurable
	//---------------------------------------------------------

	/**
	 * Configuration of the server by the container.
	 * 
	 * @param config the supplied server configuration
	 * @exception ConfigurationException if a configuration error occurs
	 */
	public void configure(Configuration config) throws ConfigurationException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize() throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Startable#start()
	 */
	public void start() throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Startable#stop()
	 */
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.sql.ConnectionManager#getConnection()
	 */
	public Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.sql.ConnectionManager#getConnection(java.lang.String)
	 */
	public Connection getConnection(String datasource) {
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * Return the working base directory.
     * 
     * @param context the supplied server context
     * @return a <code>File</code> object representing the working
     * base directory
     * @exception ContextException if a contextualization error occurs
     */
    private File getBaseDirectory( final Context context ) 
      throws ContextException 
    {
        File home = (File) context.get( "urn:avalon:home" );
        if ( !home.exists() )
        {
            boolean success = home.mkdirs();
            if ( !success )
            {
                throw new ContextException( "Unable to create component "
                    + "home directory: " + home.getAbsolutePath() );
            }
        }
        if ( getLogger().isInfoEnabled() )
        {
            getLogger().info( "Setting home directory to: " + home );
        }
        return home;
    }
    
   /**
    * Return the assigned logging channel.
    * @return the logging channel
    */
    private Logger getLogger()
    {
        return m_logger;
    }
}
