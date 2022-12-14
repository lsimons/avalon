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
package org.apache.avalon.excalibur.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.instrument.Instrument;
import org.apache.excalibur.instrument.Instrumentable;

/**
 * The ResourceLimiting implementation for DataSources in Avalon.
 * This uses the normal <code>java.sql.Connection</code> object and
 * <code>java.sql.DriverManager</code>.
 * <p>
 * This datasource pool implementation is designed to make as many
 * database connections as are needed available without placing
 * undo load on the database server.
 * <p>
 * If an application under normal load needs 3 database connections
 * for example, then the <code>max</code> pool size should be set
 * to a value like 10.  This will allow the pool to grow to accomodate
 * a sudden spike in load without allowing the pool to grow to such
 * a large size as to place undo load on the database server.  The
 * pool's trimming features will keep track of how many connections
 * are actually needed and close those connections which are no longer
 * required.
 * <p>
 * Configuration Example:
 * <pre>
 *   &lt;rl-jdbc&gt;
 *     &lt;pool-controller max="<i>10</i>" max-strict="<i>true</i>"
 *       blocking="<i>true</i>" timeout="<i>-1</i>"
 *       trim-interval="<i>60000</i>" auto-commit="true"
 *       connection-class="<i>my.overrided.ConnectionClass</i>"&gt;
 *       &lt;keep-alive disable="false" age="5000"&gt;select 1&lt;/keep-alive&gt;
 *     &lt;/pool-controller&gt;
 *     &lt;driver&gt;<i>com.database.jdbc.JdbcDriver</i>&lt;/driver&gt;
 *     &lt;dburl&gt;<i>jdbc:driver://host/mydb</i>&lt;/dburl&gt;
 *     &lt;user&gt;<i>username</i>&lt;/user&gt;
 *     &lt;password&gt;<i>password</i>&lt;/password&gt;
 *   &lt;/rl-jdbc&gt;
 * </pre>
 * <p>
 * Roles Example:
 * <pre>
 *   &lt;role name="org.apache.avalon.excalibur.datasource.DataSourceComponentSelector"
 *     shorthand="datasources"
 *     default-class="org.apache.avalon.excalibur.component.ExcaliburComponentSelector"&gt;
 *     &lt;hint shorthand="rl-jdbc"
 *       class="org.apache.avalon.excalibur.datasource.ResourceLimitingJdbcDataSource"/&gt;
 *   &lt;/role&gt;
 * </pre>
 * <p>
 * Configuration Attributes:
 * <ul>
 * <li>The <code>max</code> attribute is used to set the maximum
 * number of connections which will be opened.  See the
 * <code>blocking</code> attribute.  (Defaults to "3")</li>
 *
 * <li>The <code>max-strict</code> attribute is used to determine whether
 * or not the maximum number of connections can be exceeded.  If true,
 * then an exception will be thrown if more than max connections are
 * requested and blocking is false.  (Defaults to "true")<br>
 * <i>WARNING: In most cases, this value
 * should always be set to true.  Setting it to false means that under
 * heavy load, your application may open a very large number of
 * connections to the database.  Some database servers behave very poorly
 * under large connection loads and can even crash.</i></li>
 *
 * <li>The <code>blocking</code> attributes is used to specify the
 * behavior of the DataSource pool when an attempt is made to allocate
 * more than <code>max</code> concurrent connections.  If true, the
 * request will block until a connection is released, otherwise, a
 * NoAvailableConnectionException will be thrown.  Ignored if
 * <code>max-strict</code> is false.  (Defaults to "true")</li>
 *
 * <li>The <code>timeout</code> attribute is used to specify the
 * maximum amount of time in milliseconds that a request for a
 * connection will be allowed to block before a
 * NoAvailableConnectionException is thrown.  A value of "0" specifies
 * that the block will never timeout.  (Defaults to "0")</li>
 *
 * <li>The <code>trim-interval</code> attribute is used to specify how
 * long idle connections will be maintained in the pool before being
 * closed.  For a complete explanation on how this works, see {@link
 * org.apache.avalon.excalibur.pool.ResourceLimitingPool#trim()}
 * (Defaults to "60000", 1 minute)</li>
 *
 * <li>The <code>auto-commit</code> attribute is used to determine the
 * default auto-commit mode for the <code>Connection</code>s returned
 * by this <code>DataSource</code>.
 *
 * <li>The <code>connection-class</code> attribute is used to override
 * the Connection class returned by the DataSource from calls to
 * getConnection().  Set this to
 * "org.apache.avalon.excalibur.datasource.Jdbc3Connection" to gain
 * access to JDBC3 features.  Jdbc3Connection does not exist if your
 * JVM does not support JDBC3.  (Defaults to
 * "org.apache.avalon.excalibur.datasource.JdbcConnection")</li>
 *
 * <li>The <code>keep-alive</code> element is used to override the
 * query used to monitor the health of connections.  If a connection
 * has not been used for 5 seconds then before returning the
 * connection from a call to getConnection(), the connection is first
 * used to ping the database to make sure that it is still alive.
 * Setting the <code>disable</code> attribute to true will disable
 * this feature.  Setting the <code>age</code> allows the 5 second age to
 * be overridden.  (Defaults to a query of "SELECT 1" and being enabled)</li>
 *
 * <li>The <code>driver</code> element is used to specify the driver
 * to use when connecting to the database.  The specified class must
 * be in the classpath.  (Required)</li>
 *
 * <li>The <code>dburl</code> element is the JDBC connection string
 * which will be used to connect to the database.  (Required)</li>
 *
 * <li>The <code>user</code> and <code>password</code> attributes are
 * used to specify the user and password for connections to the
 * database. (Required)</li>
 * </ul>
 * 
 * @avalon.component
 * @avalon.service type=DataSourceComponent
 * @x-avalon.info name=rl-jdbc
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.10 $ $Date: 2003/10/01 03:14:32 $
 * @since 4.1
 */
public class ResourceLimitingJdbcDataSource
    extends AbstractLogEnabled
    implements DataSourceComponent, Instrumentable, Disposable
{
    private boolean m_configured;
    private boolean m_disposed;
    protected ResourceLimitingJdbcConnectionPool m_pool;

    /** Instrumentable Name assigned to this Instrumentable */
    private String m_instrumentableName;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public ResourceLimitingJdbcDataSource()
    {
    }

    /*---------------------------------------------------------------
     * DataSourceComponent Methods
     *-------------------------------------------------------------*/
    /**
     * Gets the Connection to the database
     *
     * @throws NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader.
     *
     * @throws NoAvailableConnectionException when there are no more available
     *         Connections in the pool.
     */
    public Connection getConnection()
        throws SQLException
    {
        if( !m_configured ) throw new IllegalStateException( "Not Configured" );
        if( m_disposed ) throw new IllegalStateException( "Already Disposed" );

        Object connection;
        try
        {
            connection = m_pool.get();
            if (null == connection)
            {
                throw new SQLException("Could not return Connection");
            }
        }
        catch( SQLException e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Could not return Connection", e );
            }

            throw e;
        }
        catch( Exception e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Could not return Connection", e );
            }

            throw new NoAvailableConnectionException( e.getMessage() );
        }
        
        return (Connection)connection;
    }

    /*---------------------------------------------------------------
     * DataSourceComponent (Configurable) Methods
     *-------------------------------------------------------------*/
    /**
     * Pass the <code>Configuration</code> to the <code>Configurable</code>
     * class. This method must always be called after the constructor
     * and before any other method.
     *
     * @param configuration the class configurations.
     */
    public void configure( Configuration configuration ) throws ConfigurationException
    {
        if( m_configured ) throw new IllegalStateException( "Already Configured" );

        final String driver = configuration.getChild( "driver" ).getValue( "" );
        final String dburl = configuration.getChild( "dburl" ).getValue( null );
        final String user = configuration.getChild( "user" ).getValue( null );
        final String passwd = configuration.getChild( "password" ).getValue( null );

        final Configuration controller = configuration.getChild( "pool-controller" );
        final int keepAliveAge = controller.getChild( "keep-alive" ).getAttributeAsInteger( "age", 5000 );
        String keepAlive = controller.getChild( "keep-alive" ).getValue( "SELECT 1" );
        final boolean disableKeepAlive =
            controller.getChild( "keep-alive" ).getAttributeAsBoolean( "disable", false );

        final int max = controller.getAttributeAsInteger( "max", 3 );
        final boolean maxStrict = controller.getAttributeAsBoolean( "max-strict", true );
        final boolean blocking = controller.getAttributeAsBoolean( "blocking", true );
        final long timeout = controller.getAttributeAsLong( "timeout", 0 );
        final long trimInterval = controller.getAttributeAsLong( "trim-interval", 60000 );
        final boolean oradb = controller.getAttributeAsBoolean( "oradb", false );

        final boolean autoCommit = configuration.getChild( "auto-commit" ).getValueAsBoolean( true );
        // Get the JdbcConnection class.  The factory will resolve one if null.
        final String connectionClass = controller.getAttribute( "connection-class", null );

        final int l_max;

        // If driver is specified....
        if( !"".equals( driver ) )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Loading new driver: " + driver );
            }

            try
            {
                Class.forName( driver, true, Thread.currentThread().getContextClassLoader() );
            }
            catch( ClassNotFoundException cnfe )
            {
                if( getLogger().isWarnEnabled() )
                {
                    getLogger().warn( "Could not load driver: " + driver, cnfe );
                }
            }
        }

        // Validate the max pool size values.
        if( max < 1 )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Maximum number of connections specified must be at least 1." );
            }

            l_max = 1;
        }
        else
        {
            l_max = max;
        }

        // If the keepAlive disable attribute was set, then set the keepAlive query to null,
        //  disabling it.
        if( disableKeepAlive )
        {
            keepAlive = null;
        }

        // If the oradb attribute was set, then override the keepAlive query.
        // This will override any specified keepalive value even if disabled.
        //  (Deprecated, but keep this for backwards-compatability)
        if( oradb )
        {
            keepAlive = "SELECT 1 FROM DUAL";

            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "The oradb attribute is deprecated, please use the" +
                                  "keep-alive element instead." );
            }
        }

        final JdbcConnectionFactory factory = new JdbcConnectionFactory
            ( dburl, user, passwd, autoCommit, keepAlive, keepAliveAge, connectionClass );

        factory.enableLogging( getLogger() );

        try
        {
            m_pool = new ResourceLimitingJdbcConnectionPool(
                factory, l_max, maxStrict, blocking, timeout, trimInterval, autoCommit );

            m_pool.enableLogging( getLogger() );
        }
        catch( Exception e )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Error configuring ResourceLimitingJdbcDataSource", e );
            }

            throw new ConfigurationException( "Error configuring ResourceLimitingJdbcDataSource", e );
        }

        m_configured = true;
    }

    /*---------------------------------------------------------------
     * Instrumentable Methods
     *-------------------------------------------------------------*/
    /**
     * Sets the name for the Instrumentable.  The Instrumentable Name is used
     *  to uniquely identify the Instrumentable during the configuration of
     *  the InstrumentManager and to gain access to an InstrumentableDescriptor
     *  through the InstrumentManager.  The value should be a string which does
     *  not contain spaces or periods.
     * <p>
     * This value may be set by a parent Instrumentable, or by the
     *  InstrumentManager using the value of the 'instrumentable' attribute in
     *  the configuration of the component.
     *
     * @param name The name used to identify a Instrumentable.
     */
    public void setInstrumentableName( String name )
    {
        m_instrumentableName = name;
    }

    /**
     * Gets the name of the Instrumentable.
     *
     * @return The name used to identify a Instrumentable.
     */
    public String getInstrumentableName()
    {
        return m_instrumentableName;
    }

    /**
     * Obtain a reference to all the Instruments that the Instrumentable object
     *  wishes to expose.  All sampling is done directly through the
     *  Instruments as opposed to the Instrumentable interface.
     *
     * @return An array of the Instruments available for profiling.  Should
     *         never be null.  If there are no Instruments, then
     *         EMPTY_INSTRUMENT_ARRAY can be returned.  This should never be
     *         the case though unless there are child Instrumentables with
     *         Instruments.
     */
    public Instrument[] getInstruments()
    {
        return Instrumentable.EMPTY_INSTRUMENT_ARRAY;
    }

    /**
     * Any Object which implements Instrumentable can also make use of other
     *  Instrumentable child objects.  This method is used to tell the
     *  InstrumentManager about them.
     *
     * @return An array of child Instrumentables.  This method should never
     *         return null.  If there are no child Instrumentables, then
     *         EMPTY_INSTRUMENTABLE_ARRAY can be returned.
     */
    public Instrumentable[] getChildInstrumentables()
    {
        return new Instrumentable[]{ m_pool };
    }

    /*---------------------------------------------------------------
     * Disposable Methods
     *-------------------------------------------------------------*/
    /**
     * The dispose operation is called at the end of a components lifecycle.
     * This method will be called after Startable.stop() method (if implemented
     * by component). Components use this method to release and destroy any
     * resources that the Component owns.
     */
    public void dispose()
    {
        m_disposed = true;
        m_pool.dispose();
        m_pool = null;
    }
}

