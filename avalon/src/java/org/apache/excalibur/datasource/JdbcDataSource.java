/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.avalon.activity.Disposable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.logger.AbstractLoggable;

/**
 * The Default implementation for DataSources in Avalon.  This uses the
 * normal <code>java.sql.Connection</code> object and
 * <code>java.sql.DriverManager</code>.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.3 $ $Date: 2001/04/25 14:24:43 $
 */
public class JdbcDataSource
    extends AbstractLoggable
    implements DataSourceComponent
{
    protected JdbcConnectionPool        m_pool;

    /**
     *  Configure and set up DB connection.  Here we set the connection
     *  information needed to create the Connection objects.  It must
     *  be called only once.
     *
     * @param conf The Configuration object needed to describe the
     *             connection.
     *
     * @throws ConfigurationException
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        if( null == m_pool )
        {
            final String dburl = configuration.getChild( "dburl" ).getValue();
            final String user = configuration.getChild( "user" ).getValue( null );
            final String passwd = configuration.getChild( "password" ).getValue( null );
            final Configuration controler = configuration.getChild( "pool-controller" );

            final int max = controler.getAttributeAsInt( "max", 3 );
            final boolean autoCommit = configuration.getChild("auto-commit").getValueAsBoolean(true);

            m_pool = new JdbcConnectionPool( dburl, user, passwd, max, autoCommit );
            m_pool.setLogger(getLogger());
            m_pool.initialize();
        }
    }

    /** Get the database connection */
    public Connection getConnection()
        throws SQLException
    {
        try { return (Connection) m_pool.get(); }
        catch( final Exception e )
        {
            getLogger().error( "Could not return Connection", e );
            throw new SQLException( e.getMessage() );
        }
    }

    /** Dispose properly of the pool */
    public void dispose()
    {
        m_pool.dispose();
        m_pool = null;
    }
}
