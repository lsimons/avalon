/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.avalon.excalibur.datasource.cluster;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.excalibur.datasource.NoValidConnectionException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.8 $ $Date: 2003/02/27 15:20:56 $
 * @since 4.1
 */
public abstract class AbstractDataSourceCluster
    extends AbstractLogEnabled
    implements Serviceable, Configurable, Initializable, Disposable, ThreadSafe
{
    protected int m_size;
    private String[] m_dataSourceNames;
    private ServiceSelector m_dbSelector;
    private DataSourceComponent[] m_dataSources;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public AbstractDataSourceCluster()
    {
    }

    /*---------------------------------------------------------------
     * AbstractDataSourceCluster Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the number of DataSources in the cluster.
     *
     * @return size of the cluster.
     */
    public int getClusterSize()
    {
        return m_size;
    }

    /**
     * Gets a Connection to a database given an index.
     *
     * @param index Index of the DataSource for which a connection is to be returned.
     *
     * @throws NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader or when the index is not valid.
     *
     * @throws NoValidConnectionException when there are no more available
     *         Connections in the pool.
     */
    public Connection getConnectionForIndex( int index ) throws SQLException
    {
        if( ( index < 0 ) || ( index >= m_size ) )
        {
            throw new NoValidConnectionException(
                "index (" + index + ") must be in the range 0 to " + ( m_size - 1 ) );
        }
        return m_dataSources[ index ].getConnection();
    }

    /*---------------------------------------------------------------
     * Composable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to tell the component which ComponentLocator
     *  is controlling it.
     *
     * @param manager which curently owns the component.
     * @avalon.dependency type="org.apache.avalon.excalibur.datasource.DataSourceComponentClusterSelector"
     */
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        m_dbSelector =
            (ServiceSelector)manager.lookup( DataSourceComponent.ROLE + "ClusterSelector" );
    }

    /*---------------------------------------------------------------
     * Configurable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to configure the component.
     *
     * @param configuration configuration info used to setup the component.
     *
     * @throws ConfigurationException if there are any problems with the configuration.
     */
    public void configure( Configuration configuration ) throws ConfigurationException
    {
        // Get the size
        m_size = configuration.getAttributeAsInteger( "size" );
        if( m_size < 1 )
        {
            throw new ConfigurationException(
                "Invalid value (" + m_size + ") for size attribute." );
        }

        // Read in the data source names.
        m_dataSourceNames = new String[ m_size ];
        Configuration[] dataSourceConfigs = configuration.getChildren( "dbpool" );
        for( int i = 0; i < dataSourceConfigs.length; i++ )
        {
            int index = dataSourceConfigs[ i ].getAttributeAsInteger( "index" );
            if( ( index < 0 ) || ( index >= m_size ) )
            {
                throw new ConfigurationException( "The dbpool with index=\"" + index +
                                                  "\" is invalid.  Index must be in the range 0 to " + ( m_size - 1 ) );
            }
            if( m_dataSourceNames[ index ] != null )
            {
                throw new ConfigurationException( "Only one dbpool with index=\"" + index +
                                                  "\" can be defined." );
            }
            m_dataSourceNames[ index ] = dataSourceConfigs[ i ].getValue();
        }

        // Make sure that all of the dbpools were defined
        for( int i = 0; i < m_dataSourceNames.length; i++ )
        {
            if( m_dataSourceNames[ i ] == null )
            {
                throw new ConfigurationException( "Expected a dbpool with index=\"" + i + "\"" );
            }
        }
    }

    /*---------------------------------------------------------------
     * Initializable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to initialize the component.
     *
     * @throws Exception if there were any problems durring initialization.
     */
    public void initialize() throws Exception
    {
        // Get references to a data sources
        m_dataSources = new DataSourceComponent[ m_size ];
        for( int i = 0; i < m_dataSourceNames.length; i++ )
        {
            m_dataSources[ i ] = (DataSourceComponent)m_dbSelector.select( m_dataSourceNames[ i ] );
        }
    }

    /*---------------------------------------------------------------
     * Disposable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to dispose the component.
     */
    public void dispose()
    {
        // Free up the data source
        if( m_dbSelector != null )
        {
            if( m_dataSources != null )
            {
                for( int i = 0; i < m_dataSources.length; i++ )
                {
                    if( m_dataSources[ i ] != null )
                    {
                        m_dbSelector.release( m_dataSources[ i ] );
                    }
                }

                m_dataSources = null;
            }

            m_dbSelector = null;
        }
    }
}
