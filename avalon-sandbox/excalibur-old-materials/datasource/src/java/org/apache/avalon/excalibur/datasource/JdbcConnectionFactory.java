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

import java.sql.DriverManager;

/**
 * The Factory implementation for JdbcConnections.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/07/19 07:33:01 $
 */
public class JdbcConnectionFactory extends AbstractLoggable implements ObjectFactory
{
    private final String m_dburl;
    private final String m_username;
    private final String m_password;
    private final boolean m_autoCommit;
    private final boolean m_oradb;

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
    }

    public Object newInstance() throws Exception
    {
        JdbcConnection connection = null;

        if( null == m_username )
        {
            connection = new JdbcConnection( DriverManager.getConnection( m_dburl ), this.m_oradb );
        }
        else
        {
            connection =
                new JdbcConnection( DriverManager.getConnection( m_dburl,
                                                                 m_username,
                                                                 m_password ),
                                    this.m_oradb);
        }

        connection.setLogger(getLogger());

        // Not all drivers are friendly to explicitly setting autocommit
        if (connection.getAutoCommit() != m_autoCommit) {
            connection.setAutoCommit(m_autoCommit);
        }

        getLogger().debug( "JdbcConnection object created" );
        return connection;
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
