/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.datasource;

import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.excalibur.pool.ObjectFactory;

import java.lang.reflect.Constructor;
import java.sql.DriverManager;
import java.sql.Connection;

/**
 * The Factory implementation for JdbcConnections.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.4 $ $Date: 2001/08/07 10:57:07 $
 * @since 4.0
 */
public class JdbcConnectionFactory extends AbstractLoggable implements ObjectFactory
{
    private final String m_dburl;
    private final String m_username;
    private final String m_password;
    private final boolean m_autoCommit;
    private final boolean m_oradb;
    private final String m_connectionClass;

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
        this.m_dburl = url;
        this.m_username = username;
        this.m_password = password;
        this.m_autoCommit = autoCommit;
        this.m_oradb = oradb;
        this.m_connectionClass = null;
    }

   public JdbcConnectionFactory( final String url,
                                 final String username,
                                 final String password,
                                 final boolean autoCommit,
                                 final boolean oradb,
                                 final String connectionClass)
   {
       this.m_dburl = url;
       this.m_username = username;
       this.m_password = password;
       this.m_autoCommit = autoCommit;
       this.m_oradb = oradb;
       this.m_connectionClass = connectionClass;
   }

    public Object newInstance() throws Exception
    {
        JdbcConnection jdbcConnection = null;
        Connection connection = null;

        if( null == m_username )
        {
            connection = DriverManager.getConnection( m_dburl );
        }
        else
        {
            connection = DriverManager.getConnection( m_dburl, m_username, m_password );
        }

        if ( null == this.m_connectionClass )
        {
            jdbcConnection = new JdbcConnection(connection, this.m_oradb);
        }
        else
        {
            try
            {
                Class clazz = Thread.currentThread().getContextClassLoader().loadClass( this.m_connectionClass );
                Class[] paramTypes = new Class[] { Connection.class, boolean.class };
                Object[] params = new Object[] { connection, new Boolean( this.m_oradb ) };

                Constructor constructor = clazz.getConstructor( paramTypes );
                jdbcConnection = (JdbcConnection) constructor.newInstance( params );
            }
            catch ( Exception e )
            {
                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug("Exception in JdbcConnectionFactory.newInstance:", e);
                }
            }
        }

        jdbcConnection.setLogger(getLogger());

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
        return JdbcConnection.class;
    }

    public void decommission(Object object) throws Exception
    {
        if (object instanceof JdbcConnection) {
            ((JdbcConnection) object).dispose();
        }
    }
}
