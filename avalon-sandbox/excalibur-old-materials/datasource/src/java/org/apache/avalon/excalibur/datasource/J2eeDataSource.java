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
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Loggable;

/**
 * The J2EE implementation for DataSources in Cocoon.  This uses the
 * <code>javax.sql.DataSource</code> object and assumes that the
 * J2EE container pools the datasources properly.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.8 $ $Date: 2002/06/03 00:23:21 $
 * @since 4.0
 */
public class J2eeDataSource
    extends AbstractLogEnabled
    implements DataSourceComponent, Loggable
{
    public static final String JDBC_NAME = "java:comp/env/jdbc/";
    protected DataSource m_dataSource = null;
    protected String m_user;
    protected String m_password;

    public void setLogger( org.apache.log.Logger logger )
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
        if( null == m_dataSource )
        {
            final String contextFactory =
                configuration.getChild( "initial-context-factory" ).getValue( null );
            final String providerUrl =
                configuration.getChild( "provider-url" ).getValue( null );
            String lookupName =
                configuration.getChild( "lookup-name" ).getValue( null );

            if ( null == lookupName )
            {
                lookupName = JDBC_NAME +
                    configuration.getChild( "dbname" ).getValue();
            }

            try
            {
                Context initialContext;
                if ( null == contextFactory && null == providerUrl )
                {
                    initialContext = new InitialContext();
                }
                else
                {
                    final Hashtable props = new Hashtable();
                    if ( null != contextFactory )
                    {
                        props.put( Context.INITIAL_CONTEXT_FACTORY, contextFactory );
                    }
                    if ( null != providerUrl )
                    {
                        props.put( Context.PROVIDER_URL, providerUrl );
                    }
                    initialContext = new InitialContext( props );
                }

                if ( null == lookupName )
                {
                    m_dataSource =
                        (DataSource)initialContext.lookup( lookupName );
                }
                else
                {
                    m_dataSource = (DataSource)initialContext.lookup( lookupName );
                }
            }
            catch( final NamingException ne )
            {
                if( getLogger().isErrorEnabled() )
                {
                    getLogger().error( "Problem with JNDI lookup of datasource", ne );
                }

                throw new ConfigurationException( "Could not use JNDI to find datasource", ne );
            }
        }

        m_user = configuration.getChild( "user" ).getValue( null );
        m_password = configuration.getChild( "password" ).getValue( null );
    }

    /** Get the database connection */
    public Connection getConnection()
        throws SQLException
    {
        if( null == m_dataSource )
        {
            throw new SQLException( "Can not access DataSource object" );
        }

        if ( null == m_user || null == m_password )
        {
            return m_dataSource.getConnection();
        }
        else
        {
            return m_dataSource.getConnection( m_user, m_password );
        }
    }
}
