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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Map;

/**
 * The Connection object used in conjunction with the JdbcDataSource
 * object.
 *
 * TODO: Implement a configurable closed end Pool, where the Connection
 * acts like JDBC PooledConnections work.  That means we can limit the
 * total number of Connection objects that are created.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.10 $ $Date: 2003/02/27 15:20:55 $
 * @since 4.0
 */
public class JdbcConnection
    extends AbstractJdbcConnection
{
    /**
     * @param connection a driver specific JDBC connection to be wrapped.
     * @param keepAlive a query which will be used to check the statis of the connection after it
     *                  has been idle.  A null value will cause the keep alive feature to
     *                  be disabled.
     */
    public JdbcConnection( final Connection connection, final String keepAlive )
    {
        super( connection, keepAlive );
    }

    public final Statement createStatement()
        throws SQLException
    {
        final Statement temp = m_connection.createStatement();
        m_lastUsed = System.currentTimeMillis();
        registerAllocatedStatement( temp );
        return temp;
    }

    public final PreparedStatement prepareStatement( final String sql )
        throws SQLException
    {
        final PreparedStatement temp = m_connection.prepareStatement( sql );
        m_lastUsed = System.currentTimeMillis();
        registerAllocatedStatement( temp );
        return temp;
    }

    public final CallableStatement prepareCall( final String sql )
        throws SQLException
    {
        final CallableStatement temp = m_connection.prepareCall( sql );
        m_lastUsed = System.currentTimeMillis();
        registerAllocatedStatement( temp );
        return temp;
    }

    public final String nativeSQL( final String sql )
        throws SQLException
    {
        return m_connection.nativeSQL( sql );
    }

    public final void setAutoCommit( final boolean autoCommit )
        throws SQLException
    {
        m_connection.setAutoCommit( autoCommit );
    }

    public final boolean getAutoCommit()
        throws SQLException
    {
        return m_connection.getAutoCommit();
    }

    public final void commit()
        throws SQLException
    {
        m_connection.commit();
        m_lastUsed = System.currentTimeMillis();
    }

    public final void rollback()
        throws SQLException
    {
        m_connection.rollback();
        m_lastUsed = System.currentTimeMillis();
    }

    public final DatabaseMetaData getMetaData()
        throws SQLException
    {
        return m_connection.getMetaData();
    }

    public final void setReadOnly( final boolean readOnly )
        throws SQLException
    {
        m_connection.setReadOnly( readOnly );
    }

    public final boolean isReadOnly()
        throws SQLException
    {
        return m_connection.isReadOnly();
    }

    public final void setCatalog( final String catalog )
        throws SQLException
    {
        m_connection.setCatalog( catalog );
    }

    public final String getCatalog()
        throws SQLException
    {
        return m_connection.getCatalog();
    }

    public final void setTransactionIsolation( final int level )
        throws SQLException
    {
        m_connection.setTransactionIsolation( level );
    }

    public final int getTransactionIsolation()
        throws SQLException
    {
        return m_connection.getTransactionIsolation();
    }

    public final SQLWarning getWarnings()
        throws SQLException
    {
        return m_connection.getWarnings();
    }

    public final void clearWarnings()
        throws SQLException
    {
        m_connection.clearWarnings();
    }

    public final Statement createStatement( final int resultSetType,
                                            final int resultSetConcurrency )
        throws SQLException
    {
        final Statement temp = m_connection.createStatement(
            resultSetType, resultSetConcurrency
        );

        m_lastUsed = System.currentTimeMillis();
        registerAllocatedStatement( temp );
        return temp;
    }

    public final PreparedStatement prepareStatement( final String sql,
                                                     final int resultSetType,
                                                     final int resultSetConcurrency )
        throws SQLException
    {
        final PreparedStatement temp = m_connection.prepareStatement(
            sql, resultSetType, resultSetConcurrency
        );

        m_lastUsed = System.currentTimeMillis();
        registerAllocatedStatement( temp );
        return temp;
    }

    public final CallableStatement prepareCall( final String sql,
                                                final int resultSetType,
                                                final int resultSetConcurrency )
        throws SQLException
    {
        final CallableStatement temp = m_connection.prepareCall(
            sql, resultSetType, resultSetConcurrency
        );

        m_lastUsed = System.currentTimeMillis();
        registerAllocatedStatement( temp );
        return temp;
    }

    public final Map getTypeMap()
        throws SQLException
    {
        return m_connection.getTypeMap();
    }

    public final void setTypeMap( final Map map )
        throws SQLException
    {
        m_connection.setTypeMap( map );
    }

/*    @JDBC3_START@
    public final void setHoldability( int holdability )
        throws SQLException
    {
        throw new SQLException( "This is not a Jdbc 3.0 Compliant Connection" );
    }

    public final int getHoldability()
        throws SQLException
    {
        throw new SQLException( "This is not a Jdbc 3.0 Compliant Connection" );
    }

    public final java.sql.Savepoint setSavepoint()
        throws SQLException
    {
        throw new SQLException( "This is not a Jdbc 3.0 Compliant Connection" );
    }

    public final java.sql.Savepoint setSavepoint( String savepoint )
        throws SQLException
    {
        throw new SQLException( "This is not a Jdbc 3.0 Compliant Connection" );
    }

    public final void rollback( java.sql.Savepoint savepoint )
        throws SQLException
    {
        throw new SQLException( "This is not a Jdbc 3.0 Compliant Connection" );
    }

    public final void releaseSavepoint( java.sql.Savepoint savepoint )
        throws SQLException
    {
        throw new SQLException( "This is not a Jdbc 3.0 Compliant Connection" );
    }

    public final Statement createStatement( int resulSetType,
                                            int resultSetConcurrency,
                                            int resultSetHoldability )
        throws SQLException
    {
        throw new SQLException( "This is not a Jdbc 3.0 Compliant Connection" );
    }

    public final PreparedStatement prepareStatement( String sql,
                                                     int resulSetType,
                                                     int resultSetConcurrency,
                                                     int resultSetHoldability )
        throws SQLException
    {
        throw new SQLException( "This is not a Jdbc 3.0 Compliant Connection" );
    }

    public final CallableStatement prepareCall( String sql,
                                                int resulSetType,
                                                int resultSetConcurrency,
                                                int resultSetHoldability )
        throws SQLException
    {
        throw new SQLException( "This is not a Jdbc 3.0 Compliant Connection" );
    }

    public final PreparedStatement prepareStatement( String sql,
                                                     int autoGeneratedKeys )
        throws SQLException
    {
        throw new SQLException( "This is not a Jdbc 3.0 Compliant Connection" );
    }

    public final PreparedStatement prepareStatement( String sql,
                                                     int[] columnIndexes )
        throws SQLException
    {
        throw new SQLException( "This is not a Jdbc 3.0 Compliant Connection" );
    }

    public final PreparedStatement prepareStatement( String sql,
                                                     String[] columnNames )
        throws SQLException
    {
        throw new SQLException( "This is not a Jdbc 3.0 Compliant Connection" );
    }
    @JDBC3_END@ */
}

