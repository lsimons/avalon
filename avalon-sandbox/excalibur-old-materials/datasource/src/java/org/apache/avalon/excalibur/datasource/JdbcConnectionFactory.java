/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.excalibur.pool.ObjectFactory;

import java.lang.reflect.Constructor;
import java.sql.DriverManager;
import java.sql.Connection;

/**
 * The Factory implementation for JdbcConnections.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.9 $ $Date: 2002/01/14 21:49:34 $
 * @since 4.0
 */
public class JdbcConnectionFactory extends AbstractLogEnabled implements ObjectFactory
{
    private final String m_dburl;
    private final String m_username;
    private final String m_password;
    private final boolean m_autoCommit;
    private final String m_keepAlive;
    private final Class  m_class;
    private final static String DEFAULT_KEEPALIVE = "SELECT 1";
    private final static String ORACLE_KEEPALIVE = JdbcConnectionFactory.DEFAULT_KEEPALIVE + " FROM DUAL";
    private       Connection m_firstConnection;

    /**
     * @deprecated  Use the new constructor with the connectionClass
     *              specified.
     */
    public JdbcConnectionFactory( final String url,
                                  final String username,
                                  final String password,
                                  final boolean autoCommit,
                                  final boolean oradb )
    {
        this(url, username, password, autoCommit, oradb, null);
    }

   /**
    * @ deprecated Use the new constructor with the keepalive and connectionClass
    *              specified.
    */
   public JdbcConnectionFactory( final String url,
                                 final String username,
                                 final String password,
                                 final boolean autoCommit,
                                 final boolean oradb,
                                 final String connectionClass)
   {
       this(url, username, password, autoCommit, (oradb) ? JdbcConnectionFactory.ORACLE_KEEPALIVE : JdbcConnectionFactory.DEFAULT_KEEPALIVE, connectionClass);
   }

   public JdbcConnectionFactory( final String url,
                                 final String username,
                                 final String password,
                                 final boolean autoCommit,
                                 final String keepAlive,
                                 final String connectionClass)
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
            if ( null == className )
            {
                try
                {
                    java.lang.reflect.Method meth = m_firstConnection.getClass().getMethod("getHoldability", new Class[] {});
                    className = "org.apache.avalon.excalibur.datasource.Jdbc3Connection";
                }
                catch (Exception e)
                {
                    className = "org.apache.avalon.excalibur.datasource.JdbcConnection";
                }
            }

            clazz = Thread.currentThread().getContextClassLoader().loadClass( className );
        }
        catch (Exception e)
        {
            // ignore for now
        }

        this.m_class = clazz;
    }

    public Object newInstance() throws Exception
    {
        AbstractJdbcConnection jdbcConnection = null;
        Connection connection = m_firstConnection;

        if ( null == connection )
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

        if ( null != this.m_class )
        {
            try
            {
                Class[] paramTypes = new Class[] { Connection.class, String.class };
                Object[] params = new Object[] { connection, this.m_keepAlive };

                Constructor constructor = m_class.getConstructor( paramTypes );
                jdbcConnection = (AbstractJdbcConnection) constructor.newInstance( params );
            }
            catch ( Exception e )
            {
                try
                {
                    Class[] paramTypes = new Class[] { Connection.class, boolean.class };
                    Object[] params = new Object[] { connection, new Boolean( this.m_keepAlive.equalsIgnoreCase(JdbcConnectionFactory.ORACLE_KEEPALIVE) ) };

                    Constructor constructor = m_class.getConstructor( paramTypes );
                    jdbcConnection = (AbstractJdbcConnection) constructor.newInstance( params );
                }
                catch ( Exception ie )
                {
                    if ( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug("Exception in JdbcConnectionFactory.newInstance:", ie);
                    }

                    throw new NoValidConnectionException(ie.getMessage());
                }
            }
        }
        else
        {
            throw new NoValidConnectionException("No valid JdbcConnection class available");
        }

        jdbcConnection.enableLogging(getLogger());

        // Not all drivers are friendly to explicitly setting autocommit
        if (jdbcConnection.getAutoCommit() != m_autoCommit) {
            jdbcConnection.setAutoCommit(m_autoCommit);
        }

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug( "JdbcConnection object created" );
        }

        return jdbcConnection;
    }

    public Class getCreatedClass()
    {
        return m_class;
    }

    public void decommission(Object object) throws Exception
    {
        if (object instanceof AbstractJdbcConnection) {
            ((AbstractJdbcConnection) object).dispose();
        }
    }
}
