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
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * The J2EE implementation for DataSources in Cocoon.  This uses the
 * <code>javax.sql.DataSource</code> object and assumes that the
 * J2EE container pools the datasources properly.
 *
 * @avalon.component
 * @avalon.service type=DataSourceComponent
 * @x-avalon.info name=j2ee-datasource
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 14:13:58 $
 * @since 4.0
 */
public class J2eeDataSource
    extends AbstractLogEnabled
    implements DataSourceComponent
{
    public static final String JDBC_NAME = "java:comp/env/jdbc/";
    protected DataSource m_dataSource = null;
    protected String m_user;
    protected String m_password;

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

