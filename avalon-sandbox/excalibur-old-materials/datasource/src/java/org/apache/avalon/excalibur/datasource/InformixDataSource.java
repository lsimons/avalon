/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource;

import com.informix.jdbcx.IfxConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.PooledConnection;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Loggable;

/**
 * The Informix implementation for DataSources in Excalibur.  This uses the
 * <code>com.informix.jdbcx.IfxConnectionPoolDataSource</code> object.  It uses
 * the following format for configuration (italics mark information you change):
 *
 * <pre>
 *   &lt;informix&gt;
 *     &lt;pool-controller init="<i>5</i>" min="<i>5</i>" max="<i>10</i>"/&gt;
 *     &lt;dbname&gt;<i>dbname</i>&lt;/dbname&gt;
 *     &lt;servername&gt;<i>servername</i>&lt;/servername&gt;
 *     &lt;host port="<i>2000</i>"&gt;<i>host</i>&lt;/host&gt;
 *     &lt;user&gt;<i>user</i>&lt;/user&gt;
 *     &lt;password&gt;<i>password</i>&lt;/password&gt;
 *     &lt;tracing&gt;
 *       &lt;jdbc&gt; file="<i>filename</i>" level="<i>level</i>"&lt;/jdbc&gt;
 *       &lt;sqli&gt; file="<i>filename</i>" level="<i>level</i>"&lt;/sqli&gt;
 *     &lt;/tracing&gt;
 *   &lt;informix&gt;
 * </pre>
 *
 * <p>
 * Informix doesn't like the JdbcDataSource Component, so we gave it it's own.
 * Do not use this datasource if you are planning on using your J2EE server's
 * connection pooling.
 * <p>
 *
 * <p>
 * You must have Informix's JDBC 2.2 or higher jar file, as well as the
 * extensions jar file (<code>ifxjdbc.jar</code> and <code>ifxjdbcx.jar</code>).
 * You will also need the JDBC 2.0 Optional Pacakge
 * (<code>jdbc2_0-stdext.jar</code>) available from 
 * http://java.sun.com/products/jdbc/download.html.
 * </p>
 *
 * <p>
 * The <i>tracing</i> settings optionally enable Informix's tracing support
 * within the jdbc driver. <strong>Note</strong>, for this to work, the
 * <code>ifxjdbc-g.jar</code> and <code>ifxjdbcx-g.jar</code> jar files are
 * required (the options have no effect when using the non -g jar files).
 * </p>
 *
 * <p>
 * <i>jdbc tracing</i> enables general logging information about the driver
 * itself. <i>sqli tracing</i> enables logging of native sqli messages sent
 * between the jdbc driver and the database server.
 * </p>
 *
 * <p>
 * The attribute <code>file</code> specifies where to write tracing information
 * to, and <code>level</code> specifies the tracing level to be used, as
 * documented in the Informix JDBC programmers guide.
 * </p>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Revision: 1.10 $ $Date: 2002/06/10 10:34:08 $
 * @since 4.0
 */
public class InformixDataSource
    extends AbstractLogEnabled
    implements DataSourceComponent, Loggable
{
    private IfxConnectionPoolDataSource m_dataSource;
    private PooledConnection m_pooledConnection;
    private boolean m_autocommit;

    public void setLogger( final org.apache.log.Logger logger )
    {
        enableLogging( new LogKitLogger( logger ) );
    }

    /**
     * Return an Informix Connection object
     */
    public Connection getConnection() throws SQLException
    {
        synchronized (this)
        {
            if (m_pooledConnection == null)
            {
                m_pooledConnection = m_dataSource.getPooledConnection();
            }
        }

        Connection conn = m_pooledConnection.getConnection();

        if( conn.getAutoCommit() != m_autocommit )
        {
            conn.setAutoCommit( m_autocommit );
        }

        return conn;
    }

    /**
     * Set up the Informix driver for direct use.
     */
    public void configure( Configuration conf ) throws ConfigurationException
    {
        Configuration poolController = conf.getChild( "pool-controller" );
        String dbname = conf.getChild( "dbname" ).getValue( "ifx" );
        m_dataSource = new IfxConnectionPoolDataSource();
        m_autocommit = conf.getChild( "autocommit" ).getValueAsBoolean( true );

        m_dataSource.setIfxCPMInitPoolSize( poolController.getAttributeAsInteger( "init", 5 ) );
        m_dataSource.setIfxCPMMinPoolSize( poolController.getAttributeAsInteger( "min", 5 ) );
        m_dataSource.setIfxCPMMaxPoolSize( poolController.getAttributeAsInteger( "max", 10 ) );
        m_dataSource.setIfxCPMServiceInterval( 100 );
        m_dataSource.setServerName( conf.getChild( "servername" ).getValue() );
        m_dataSource.setDatabaseName( conf.getChild( "dbname" ).getValue() );
        m_dataSource.setIfxIFXHOST( conf.getChild( "host" ).getValue() );
        m_dataSource.setPortNumber( conf.getChild( "host" ).getAttributeAsInteger( "port" ) );
        m_dataSource.setUser( conf.getChild( "user" ).getValue() );
        m_dataSource.setPassword( conf.getChild( "password" ).getValue() );

        configureTracing( conf.getChild( "tracing", false ) );
    }

    /**
     * Helper method to enable tracing support in the Informix driver.
     *
     * @param config a <code>Configuration</code> value
     * @exception ConfigurationException if an error occurs
     */
    private void configureTracing( final Configuration config )
        throws ConfigurationException
    {
        if ( config != null )
        {
            Configuration child = config.getChild( "jdbc", false );

            if (child != null)
            {
                // enables tracing on the jdbc driver itself
                m_dataSource.setIfxTRACE( child.getAttributeAsInteger( "level" ) );
                m_dataSource.setIfxTRACEFILE( child.getAttribute( "file" ) );
            }

            child = config.getChild( "sqli", false );

            if (child != null)
            {
                // enables sqli message tracing
                m_dataSource.setIfxPROTOCOLTRACE( child.getAttributeAsInteger( "level" ) );
                m_dataSource.setIfxPROTOCOLTRACEFILE( child.getAttribute( "file" ) );
            }
        }
    }
}
