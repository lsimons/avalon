/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.excalibur.pool.DefaultPoolController;

/**
 * The Default implementation for DataSources in Avalon.  This uses the
 * normal <code>java.sql.Connection</code> object and
 * <code>java.sql.DriverManager</code>.  The Configuration is like this:
 *
 * <pre>
 *   &lt;jdbc&gt;
 *     &lt;pool-controller min="<i>5</i>" max="<i>10</i>" connection-class="<i>my.overrided.ConnectionClass</i>"&gt;
 *       &lt;keep-alive disable="false"&gt;select 1&lt;/keep-alive&gt;
 *     &lt;/pool-controller&gt;
 *     &lt;driver&gt;<i>com.database.jdbc.JdbcDriver</i>&lt;/driver&gt;
 *     &lt;dburl&gt;<i>jdbc:driver://host/mydb</i>&lt;/dburl&gt;
 *     &lt;user&gt;<i>username</i>&lt;/user&gt;
 *     &lt;password&gt;<i>password</i>&lt;/password&gt;
 *   &lt;/jdbc&gt;
 * </pre>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.14 $ $Date: 2002/01/26 16:58:06 $
 * @since 4.0
 */
public class JdbcDataSource
    extends AbstractLogEnabled
    implements DataSourceComponent, Disposable, Loggable
{
    protected JdbcConnectionPool        m_pool;

    public void setLogger( final org.apache.log.Logger logger )
    {
        enableLogging( new LogKitLogger( logger ) );
    }

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
            final String driver = configuration.getChild( "driver" ).getValue("");
            final String dburl = configuration.getChild( "dburl" ).getValue();
            final String user = configuration.getChild( "user" ).getValue( null );
            final String passwd = configuration.getChild( "password" ).getValue( null );
            final Configuration controller = configuration.getChild( "pool-controller" );
            String keepAlive = controller.getChild( "keep-alive" ).getValue( "SELECT 1" );
            final boolean disableKeepAlive = controller.getChild( "keep-alive" ).getAttributeAsBoolean( "disable", false );

            final int min = controller.getAttributeAsInteger( "min", 1 );
            final int max = controller.getAttributeAsInteger( "max", 3 );
            final long timeout = controller.getAttributeAsLong( "timeout", -1 );
            final boolean autoCommit = configuration.getChild("auto-commit").getValueAsBoolean(true);
            final boolean oradb = controller.getAttributeAsBoolean( "oradb", false );
            // Get the JdbcConnection class.  The factory will resolve one if null.
            final String connectionClass = controller.getAttribute( "connection-class", null );

            final int l_max;
            final int l_min;

            // If driver is specified....
            if ( ! "".equals(driver) )
            {
                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug("Loading new driver: " + driver);
                }

                try
                {
                    Class.forName( driver, true, Thread.currentThread().getContextClassLoader() );
                }
                catch (ClassNotFoundException cnfe)
                {
                    if (getLogger().isWarnEnabled())
                    {
                        getLogger().warn( "Could not load driver: " + driver, cnfe );
                    }
                }
            }

            // Validate the min and max pool size values.
            if ( min < 1 )
            {
                if (getLogger().isWarnEnabled())
                {
                    getLogger().warn( "Minumum number of connections specified must be at least 1." );
                }

                l_min = 1;
            }
            else
            {
                l_min = min;
            }

            if( max < 1 )
            {
                if (getLogger().isWarnEnabled())
                {
                    getLogger().warn( "Maximum number of connections specified must be at least 1." );
                }

                l_max = 1;
            }
            else
            {
                if ( max < min )
                {
                    if (getLogger().isWarnEnabled())
                    {
                        getLogger().warn( "Maximum number of connections specified must be " +
                                          "more than the minimum number of connections." );
                    }

                    l_max = min + 1;
                }
                else
                {
                    l_max = max;
                }
            }
            
            // If the keepAlive disable attribute was set, then set the keepAlive query to null, disabling it.
            if (disableKeepAlive)
            {
                keepAlive = null;
            }

            // If the oradb attribute was set, then override the keepAlive query.
            // This will override any specified keepalive value even if disabled.
            //  (Deprecated, but keep this for backwards-compatability)
            if (oradb)
            {
                keepAlive = "SELECT 1 FROM DUAL";

                if (getLogger().isWarnEnabled())
                {
                    getLogger().warn("The oradb attribute is deprecated, please use the" +
                                     "keep-alive element instead.");
                }
            }

            
            final JdbcConnectionFactory factory =
                    new JdbcConnectionFactory( dburl, user, passwd, autoCommit, keepAlive, connectionClass );
            final DefaultPoolController poolController = new DefaultPoolController(l_max / 4);

            factory.enableLogging( getLogger() );

            try
            {
                m_pool = new JdbcConnectionPool( factory, poolController, l_min, l_max, autoCommit );
                m_pool.enableLogging( getLogger() );
                m_pool.setTimeout( timeout );
                m_pool.initialize();
            }
            catch (Exception e)
            {
                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug("Error configuring JdbcDataSource", e);
                }

                throw new ConfigurationException("Error configuring JdbcDataSource", e);
            }
        }
    }

    /** Get the database connection */
    public Connection getConnection()
        throws SQLException
    {
        try
        {
            return (Connection) m_pool.get();
        }
        catch( final SQLException se )
        {
            if (getLogger().isWarnEnabled())
            {
                getLogger().warn( "Could not return Connection", se );
            }

            // Rethrow so that we keep the original stack trace
            throw se;
        }
        catch( final Exception e )
        {
            if (getLogger().isWarnEnabled())
            {
                getLogger().warn( "Could not return Connection", e );
            }

            throw new NoAvailableConnectionException( e.getMessage() );
        }
    }

    /** Dispose properly of the pool */
    public void dispose()
    {
        m_pool.dispose();
        m_pool = null;
    }
}
