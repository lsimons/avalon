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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.sql.ConnectionPoolDataSource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

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
 * @avalon.component
 * @avalon.service type=DataSourceComponent
 * @x-avalon.info name=informix
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Revision: 1.17 $ $Date: 2003/08/01 10:11:30 $
 * @since 4.0
 */
public class InformixDataSource
    extends AbstractLogEnabled
    implements DataSourceComponent
{
    private DataSource m_dataSource;
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
        ConnectionPoolDataSource pooledDataSource = (ConnectionPoolDataSource) getInstance( "com.informix.jdbcx.IfxConnectionPoolDataSource" );
        m_autocommit = conf.getChild( "autocommit" ).getValueAsBoolean( true );

        setProperty(pooledDataSource, "IfxCPMInitPoolSize", new Integer(poolController.getAttributeAsInteger( "init", 5 ) ) );
        setProperty(pooledDataSource, "IfxCPMMinPoolSize", new Integer(poolController.getAttributeAsInteger( "min", 5 ) ) );
        setProperty(pooledDataSource, "IfxCPMMaxPoolSize", new Integer(poolController.getAttributeAsInteger( "max", 10 ) ) );
        setProperty(pooledDataSource, "IfxCPMServiceInterval", new Long( 100 ) );
        setProperty(pooledDataSource, "ServerName",  conf.getChild( "servername" ).getValue() );
        setProperty(pooledDataSource, "DatabaseName", conf.getChild( "dbname" ).getValue() );
        setProperty(pooledDataSource, "IfxIFXHOST", conf.getChild( "host" ).getValue() );
        setProperty(pooledDataSource, "PortNumber", new Integer( conf.getChild( "host" ).getAttributeAsInteger( "port" ) ) );
        setProperty(pooledDataSource, "User", conf.getChild( "user" ).getValue() );
        setProperty(pooledDataSource, "Password", conf.getChild( "password" ).getValue() );

        try
        {
            Context context = new InitialContext();

            context.bind( dbname + "pool", pooledDataSource );

            m_dataSource = (DataSource) getInstance( "com.informix.jdbcx.IfxDataSource" );
            setProperty(m_dataSource, "DataSourceName", dbname + "pool" );
            setProperty(m_dataSource, "ServerName", conf.getChild( "servername" ).getValue() );
            setProperty(m_dataSource, "DatabaseName", conf.getChild( "dbname" ).getValue() );
            setProperty(m_dataSource, "IfxIFXHOST", conf.getChild( "host" ).getValue() );
            setProperty(m_dataSource, "PortNumber", new Integer( conf.getChild( "host" ).getAttributeAsInteger( "port" ) ) );
            setProperty(m_dataSource, "User", conf.getChild( "user" ).getValue() );
            setProperty(m_dataSource, "Password", conf.getChild( "password" ).getValue() );
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
                setProperty(m_dataSource, "IfxTRACE", new Integer( child.getAttributeAsInteger( "level" ) ) );
                setProperty(m_dataSource, "IfxTRACEFILE", child.getAttribute( "file" ) );
            }

            child = config.getChild( "sqli", false );

            if (child != null)
            {
                // enables sqli message tracing
                setProperty(m_dataSource, "IfxPROTOCOLTRACE", new Integer( child.getAttributeAsInteger( "level" ) ) );
                setProperty(m_dataSource, "IfxPROTOCOLTRACEFILE", child.getAttribute( "file" ) );
            }
        }
    }
    
    private Object getInstance(String className)
        throws ConfigurationException
    {
        Object instance = null;
        
        try
        {
            instance = Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
        }
        catch (Exception e)
        {
            throw new ConfigurationException("Could not load class", e);
        }
        
        return instance;
    }
    
    private void setProperty(Object obj, String propertyName, Object value)
        throws ConfigurationException
    {
        Class valueClass = value.getClass();
        
        if ( value instanceof Integer )
        {
            valueClass = Integer.TYPE;
        }
        else if ( value instanceof Long )
        {
            valueClass = Long.TYPE;
        }
        
        try
        {
            Method meth = obj.getClass().getMethod("set" + propertyName, new Class[]{valueClass});
            meth.invoke(obj, new Object[]{value});
        }
        catch (Exception e)
        {
            throw new ConfigurationException("Could not set property", e);
        }
    }
}

