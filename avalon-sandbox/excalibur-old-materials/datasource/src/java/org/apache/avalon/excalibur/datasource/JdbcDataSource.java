/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.excalibur.pool.DefaultPoolController;

/**
 * The Default implementation for DataSources in Avalon.  This uses the
 * normal <code>java.sql.Connection</code> object and
 * <code>java.sql.DriverManager</code>.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/07/19 07:33:01 $
 */
public class JdbcDataSource
    extends AbstractLoggable
    implements DataSourceComponent, Disposable
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
            final Configuration controller = configuration.getChild( "pool-controller" );

            final int min = controller.getAttributeAsInteger( "min", 1 );
            final int max = controller.getAttributeAsInteger( "max", 3 );
            final boolean autoCommit = configuration.getChild("auto-commit").getValueAsBoolean(true);
            final boolean oradb = controller.getAttributeAsBoolean( "oradb", false );

            final int l_max;
            final int l_min;

            if ( min < 1 )
            {
                getLogger().warn( "Minumum number of connections specified must be at least 1." );
                l_min = 1;
            }
            else
            {
                l_min = min;
            }

            if( max < 1 )
            {
                getLogger().warn( "Maximum number of connections specified must be at least 1." );
                l_max = 1;
            }
            else
            {
                if ( max < min )
                {
                    getLogger().warn( "Maximum number of connections specified must be " +
                                      "more than the minimum number of connections." );
                    l_max = min + 1;
                }
                else
                {
                    l_max = max;
                }
            }

            final JdbcConnectionFactory factory = new JdbcConnectionFactory(dburl, user, passwd, autoCommit, oradb);
            final DefaultPoolController poolController = new DefaultPoolController(l_max / 4);

            factory.setLogger(getLogger());

            try
            {
                m_pool = new JdbcConnectionPool( factory, poolController, l_min, l_max, autoCommit );
                m_pool.setLogger(getLogger());
                m_pool.initialize();
            }
            catch (Exception e)
            {
                getLogger().debug("Error on configuration", e);
                throw new ConfigurationException("Error on configuration", e);
            }
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
