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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;

/**
 * The Factory implementation for JdbcConnections.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.18 $ $Date: 2003/03/05 18:59:01 $
 * @since 4.0
 */
public class JdbcConnectionFactory extends AbstractLogEnabled implements ObjectFactory
{
    private final String m_dburl;
    private final String m_username;
    private final String m_password;
    private final boolean m_autoCommit;
    private final String m_keepAlive;
    private final String m_connectionClass;
    private Class m_class;
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
        this.m_connectionClass = connectionClass;

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

            init( m_firstConnection );
        }
        catch( Exception e )
        {
            // ignore for now
            // No logger here, so we can't log this.  Really should output something here though
            //  as it can be a real pain to track down the cause when this happens.
            //System.out.println( "Unable to get specified connection class: " + e );
        }
    }

    private void init( Connection connection ) throws Exception
    {
        String className = m_connectionClass;
        if( null == className )
        {
            m_class = AbstractJdbcConnection.class;
        }
        else
        {
            m_class = Thread.currentThread().getContextClassLoader().loadClass( className );
        }
    }

    public Object newInstance() throws Exception
    {
        Connection jdbcConnection = null;
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

        if( null == this.m_class )
        {
            try
            {
                init( connection );
            }
            catch( Exception e )
            {
                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Exception in JdbcConnectionFactory.newInstance:", e );
                }
                throw new NoValidConnectionException( "No valid JdbcConnection class available" );
            }
        }

        try
        {
            jdbcConnection = getProxy(connection, this.m_keepAlive);
        }
        catch( Exception e )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Exception in JdbcConnectionFactory.newInstance:", e );
            }

            throw new NoValidConnectionException( e.getMessage() );
        }

        ContainerUtil.enableLogging(jdbcConnection, getLogger());

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
    
    private Connection getProxy(Connection conn, String keepAlive)
    {
        InvocationHandler handler = null;
        
        try
        {
            Constructor builder = m_class.getConstructor(new Class[]{Connection.class, String.class});
            handler = (InvocationHandler)builder.newInstance(new Object[]{conn, keepAlive});
        }
        catch (Exception e)
        {
            getLogger().error("Could not create the proper invocation handler, defaulting to AbstractJdbcConnection", e);
            handler = new AbstractJdbcConnection(conn, keepAlive);
        }
        
        return (Connection) Proxy.newProxyInstance(
                m_class.getClassLoader(),
                new Class[]{Connection.class,
                            LogEnabled.class,
                            PoolSettable.class,
                            Disposable.class},
                handler);
    }
}
