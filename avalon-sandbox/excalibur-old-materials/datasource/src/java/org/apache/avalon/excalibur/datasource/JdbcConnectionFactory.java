/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * The Factory implementation for JdbcConnections.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.14 $ $Date: 2002/04/04 07:09:53 $
 * @since 4.0
 */
public class JdbcConnectionFactory extends AbstractLogEnabled implements ObjectFactory
{
    private final String m_dburl;
    private final String m_username;
    private final String m_password;
    private final boolean m_autoCommit;
    private final String m_keepAlive;
    private final Class m_class;
    private static final String DEFAULT_KEEPALIVE = "SELECT 1";
    private static final String ORACLE_KEEPALIVE = JdbcConnectionFactory.DEFAULT_KEEPALIVE + " FROM DUAL";
    private Connection m_firstConnection;

    /**
     * @deprecated  Use the new constructor with the keepalive and connectionClass
     *              specified.
     */
    public JdbcConnectionFactory( final String url,
                                  final String username,
                                  final String password,
                                  final boolean autoCommit,
                                  final boolean oradb )
    {
        this( url, username, password, autoCommit, oradb, null );
    }

    /**
     * @deprecated Use the new constructor with the keepalive and connectionClass
     *             specified.
     */
    public JdbcConnectionFactory( final String url,
                                  final String username,
                                  final String password,
                                  final boolean autoCommit,
                                  final boolean oradb,
                                  final String connectionClass )
    {
        this( url, username, password, autoCommit, ( oradb ) ? JdbcConnectionFactory.ORACLE_KEEPALIVE : JdbcConnectionFactory.DEFAULT_KEEPALIVE, connectionClass );
    }

    /**
     * Creates and configures a new JdbcConnectionFactory.
     *
     * @param url full JDBC database url.
     * @param username username to use when connecting to the database.
     * @param password password to use when connecting to the database.
     * @param autoCommit true if connections to the database should operate with auto commit
     *                   enabled.
     * @param keepAlive a query which will be used to check the statis of a connection after it
     *                  has been idle.  A null value will cause the keep alive feature to
     *                  be disabled.
     * @param connectionClass class of connections created by the factory.
     */
    public JdbcConnectionFactory( final String url,
                                  final String username,
                                  final String password,
                                  final boolean autoCommit,
                                  final String keepAlive,
                                  final String connectionClass )
    {
        this.m_dburl = url;
        this.m_username = username;
        this.m_password = password;
        this.m_autoCommit = autoCommit;
        this.m_keepAlive = keepAlive;

        Class clazz = null;

        try
        {
            if( null == m_username )
            {
                m_firstConnection = DriverManager.getConnection( m_dburl );
            }
            else
            {
                m_firstConnection = DriverManager.getConnection( m_dburl, m_username, m_password );
            }

            String className = connectionClass;
            if( null == className )
            {
                try
                {
                    java.lang.reflect.Method meth = m_firstConnection.getClass().getMethod( "getHoldability", new Class[]{} );
                    className = "org.apache.avalon.excalibur.datasource.Jdbc3Connection";
                }
                catch( Exception e )
                {
                    className = "org.apache.avalon.excalibur.datasource.JdbcConnection";
                }
            }

            clazz = Thread.currentThread().getContextClassLoader().loadClass( className );
        }
        catch( Exception e )
        {
            // ignore for now
            // No logger here, so we can't log this.  Really should output something here though
            //  as it can be a real pain to track down the cause when this happens.
            //System.out.println( "Unable to get specified connection class: " + e );
        }

        this.m_class = clazz;
    }

    public Object newInstance() throws Exception
    {
        AbstractJdbcConnection jdbcConnection = null;
        Connection connection = m_firstConnection;

        if( null == connection )
        {
            if( null == m_username )
            {
                connection = DriverManager.getConnection( m_dburl );
            }
            else
            {
                connection = DriverManager.getConnection( m_dburl, m_username, m_password );
            }
        }
        else
        {
            m_firstConnection = null;
        }

        if( null != this.m_class )
        {
            try
            {
                Class[] paramTypes = new Class[]{Connection.class, String.class};
                Object[] params = new Object[]{connection, this.m_keepAlive};

                Constructor constructor = m_class.getConstructor( paramTypes );
                jdbcConnection = (AbstractJdbcConnection)constructor.newInstance( params );
            }
            catch( Exception e )
            {
                try
                {
                    // Support the deprecated connection constructor as well.
                    boolean oracleKeepAlive = ( m_keepAlive != null ) && m_keepAlive.equalsIgnoreCase( JdbcConnectionFactory.ORACLE_KEEPALIVE );

                    Class[] paramTypes = new Class[]{Connection.class, boolean.class};
                    Object[] params = new Object[]{connection, new Boolean( oracleKeepAlive )};

                    Constructor constructor = m_class.getConstructor( paramTypes );
                    jdbcConnection = (AbstractJdbcConnection)constructor.newInstance( params );
                }
                catch( Exception ie )
                {
                    if( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( "Exception in JdbcConnectionFactory.newInstance:", ie );
                    }

                    throw new NoValidConnectionException( ie.getMessage() );
                }
            }
        }
        else
        {
            throw new NoValidConnectionException( "No valid JdbcConnection class available" );
        }

        jdbcConnection.enableLogging( getLogger() );

        // Not all drivers are friendly to explicitly setting autocommit
        if( jdbcConnection.getAutoCommit() != m_autoCommit )
        {
            jdbcConnection.setAutoCommit( m_autoCommit );
        }

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "JdbcConnection object created" );
        }

        return jdbcConnection;
    }

    public Class getCreatedClass()
    {
        return m_class;
    }

    public void decommission( Object object ) throws Exception
    {
        if( object instanceof AbstractJdbcConnection )
        {
            ( (AbstractJdbcConnection)object ).dispose();
        }
    }
}
