/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource;

import com.informix.jdbcx.IfxConnectionPoolDataSource;
import com.informix.jdbcx.IfxDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
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
 * Also, this DataSource requires the Avalon Cadastre package because it uses
 * the MemoryContext.
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
 * @version CVS $Revision: 1.11 $ $Date: 2002/10/15 13:46:24 $
 * @since 4.0
 */
public class InformixDataSource
    extends AbstractLogEnabled
    implements DataSourceComponent, Loggable
{
    private IfxDataSource m_dataSource;
    private boolean m_autocommit;
    private static boolean INIT_FACTORY = false;

    /**
     * Set up the system property for the context factory if it hasn't been
     * done already.  This is not done in a static initializer due to the
     * existence of the J2eeDataSource.
     */
    public InformixDataSource()
    {
        if( !InformixDataSource.INIT_FACTORY )
        {
            System.setProperty( Context.INITIAL_CONTEXT_FACTORY,
                                "org.apache.avalon.excalibur.naming.memory.MemoryInitialContextFactory" );
        }
    }

    public void setLogger( final org.apache.log.Logger logger )
    {
        enableLogging( new LogKitLogger( logger ) );
    }

    /**
     * Return an Informix Connection object
     */
    public Connection getConnection() throws SQLException
    {
        Connection conn = m_dataSource.getConnection();

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
        IfxConnectionPoolDataSource pooledDataSource = new IfxConnectionPoolDataSource();
        m_autocommit = conf.getChild( "autocommit" ).getValueAsBoolean( true );

        pooledDataSource.setIfxCPMInitPoolSize( poolController.getAttributeAsInteger( "init", 5 ) );
        pooledDataSource.setIfxCPMMinPoolSize( poolController.getAttributeAsInteger( "min", 5 ) );
        pooledDataSource.setIfxCPMMaxPoolSize( poolController.getAttributeAsInteger( "max", 10 ) );
        pooledDataSource.setIfxCPMServiceInterval( 100 );
        pooledDataSource.setServerName( conf.getChild( "servername" ).getValue() );
        pooledDataSource.setDatabaseName( conf.getChild( "dbname" ).getValue() );
        pooledDataSource.setIfxIFXHOST( conf.getChild( "host" ).getValue() );
        pooledDataSource.setPortNumber( conf.getChild( "host" ).getAttributeAsInteger( "port" ) );
        pooledDataSource.setUser( conf.getChild( "user" ).getValue() );
        pooledDataSource.setPassword( conf.getChild( "password" ).getValue() );

        try
        {
            Context context = new InitialContext();

            context.bind( dbname + "pool", pooledDataSource );

            m_dataSource = new IfxDataSource();
            m_dataSource.setDataSourceName( dbname + "pool" );
            m_dataSource.setServerName( conf.getChild( "servername" ).getValue() );
            m_dataSource.setDatabaseName( conf.getChild( "dbname" ).getValue() );
            m_dataSource.setIfxIFXHOST( conf.getChild( "host" ).getValue() );
            m_dataSource.setPortNumber( conf.getChild( "host" ).getAttributeAsInteger( "port" ) );
            m_dataSource.setUser( conf.getChild( "user" ).getValue() );
            m_dataSource.setPassword( conf.getChild( "password" ).getValue() );
            configureTracing( conf.getChild( "tracing", false ) );

            context.bind( dbname, m_dataSource );
        }
        catch( Exception e )
        {
            if( getLogger().isErrorEnabled() )
            {
                getLogger().error( "There was an error trying to bind the connection pool", e );
            }
            throw new ConfigurationException( "There was an error trying to bind the connection pool", e );
        }
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
