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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * The J2EE implementation for DataSources in Cocoon.  This uses the
 * <code>javax.sql.DataSource</code> object and assumes that the
 * J2EE container pools the datasources properly.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/07/19 07:33:01 $
 */
public class J2eeDataSource
    extends AbstractLoggable
    implements DataSourceComponent
{
    public static final String  JDBC_NAME     = "java:comp/env/jdbc/";
    protected DataSource        m_dataSource  = null;

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
            final String databaseName = configuration.getChild("dbname").getValue();

            try
            {
                final Context initialContext = new InitialContext();
                m_dataSource = (DataSource)initialContext.lookup( JDBC_NAME + databaseName );
            }
            catch( final NamingException ne )
            {
                getLogger().error( "Problem with JNDI lookup of datasource", ne );
                throw new ConfigurationException( "Could not use JNDI to find datasource", ne );
            }
        }
    }

    /** Get the database connection */
    public Connection getConnection()
        throws SQLException
    {
        if( null == m_dataSource )
        {
            throw new SQLException( "Can not access DataSource object" );
        }

        return m_dataSource.getConnection();
    }
}
