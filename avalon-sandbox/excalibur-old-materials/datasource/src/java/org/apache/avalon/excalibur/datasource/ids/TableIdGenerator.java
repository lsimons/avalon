/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource.ids;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * The TableIdGenerator requests blocks of ids from a Database table.  The table consists of two
 *  columns one called <code>table_name</code> of type CHAR or VARCHAR, and the second called
 *  <code>next_id</code> of an integer type large enough to hold your largest ids.
 * <p>
 * The Configuration to use a TableIdGenerator looks like the following:
 * <pre>
 *   &lt;id-generators&gt;
 *       &lt;table name="user-ids" big-decimals="true" block-size="1" table="ids"
 *           key-table="event-type" logger="cm.ids"&gt;
 *           &lt;dbpool&gt;user-db&lt;/dbpool&gt;
 *       &lt;/table&gt;
 *   &lt;/id-generators&gt;
 * </pre>
 * Where user-db is the name of a DataSource configured in a datasources element, block-size is
 *  the number if ids that are allocated with each query to the databse (defaults to "10"),
 *  table is the name of the table which contains the ids (defaults to "ids"), and key-table is
 *  the table_name of the row from which the block of ids are allocated (defaults to "id").
 * <p>
 *
 * With the following roles declaration:
 * <pre>
 *   &lt;role name="org.apache.avalon.excalibur.datasource.ids.IdGeneratorSelector"
 *         shorthand="id-generators"
 *         default-class="org.apache.avalon.excalibur.component.ExcaliburComponentSelector"&gt;
 *       &lt;hint shorthand="table"
 *             class="org.apache.avalon.excalibur.datasource.ids.TableIdGenerator"/&gt;
 *   &lt;/role&gt;
 * </pre>
 *
 * To configure your component to use the IdGenerator declared above, its configuration should look
 *  something like the following:
 * <pre>
 *   &lt;user-service logger="cm"&gt;
 *       &lt;dbpool&gt;user-db&lt;/dbpool&gt;
 *       &lt;id-generator&gt;user-ids&lt;/id-generator&gt;
 *   &lt;/user-service&gt;
 * </pre>
 *
 * Your component obtains a reference to an IdGenerator using the same method as it obtains a
 *  DataSource, by making use of a ComponentSelector.
 * <p>
 * Depending on your database, the ids table should look something like the following:
 * <pre>
 *   CREATE TABLE ids (
 *       table_name varchar(16) NOT NULL,
 *       next_id INTEGER NOT NULL,
 *       PRIMARY KEY (table_name)
 *   );
 * </pre>
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/22 03:04:27 $
 * @since 4.1
 */
public class TableIdGenerator
    extends AbstractDataSourceBlockIdGenerator
{
    /**
     * The name of the table containing the ids.
     */
    private String m_table;

    /**
     * TableName used to reference which ids to allocate.
     */
    private String m_tableName;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public TableIdGenerator()
    {
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Allocates a block of ids of the given size and returns the first id.
     *
     * @param blockSize number of ids to allocate.
     * @param useBigDecimals returns the first id as a BigDecimal if true, otherwise as a Long.
     *
     * @return either a Long or a BigDecimal depending on the value of useBigDecimals
     *
     * @throws IdException if a block of ids can not be allocated.
     */
    private Object allocateIdBlock( int blockSize, boolean useBigDecimals )
        throws IdException
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Allocating a new block of " + blockSize +
                               " ids for key_table " + m_tableName + "." );
        }

        try
        {
            Connection conn = getConnection();
            try
            {
                // Turn off auto commit so that we are working in a transaction,
                //  but keep the old value.
                boolean oldAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit( false );
                try
                {
                    int oldIsolation = conn.getTransactionIsolation();
                    conn.setTransactionIsolation( Connection.TRANSACTION_SERIALIZABLE );
                    try
                    {
                        try
                        {
                            Statement stmt = conn.createStatement();

                            int tries = 0;
                            // May run into conflicts with other processes, so try this up to 50
                            //  times before giving up.
                            while( tries < 50 )
                            {
                                // Get the nextId from the table
                                ResultSet rs = stmt.executeQuery(
                                    "SELECT next_id FROM " + m_table + " WHERE table_name = '" + m_tableName + "'" );
                                if( !rs.next() )
                                {
                                    // The row does not exist.
                                    String msg = "Unable to allocate a block of Ids, no row with table_name='" +
                                        m_tableName + "' exists in the " + m_table + " table.";
                                    getLogger().error( msg );
                                    conn.rollback();

                                    throw new IdException( msg );
                                }

                                // Get the next_id using the appropriate data type.
                                Object nextId;
                                if( useBigDecimals )
                                {
                                    nextId = rs.getBigDecimal( 1 );
                                }
                                else
                                {
                                    nextId = new Long( rs.getLong( 1 ) );
                                }

                                // Update the value of next_id in the database so it reflects the full block
                                //  being allocated.  If another process has done the same thing, then this
                                //  will throw an exception due to transaction isolation.
                                try
                                {
                                    int updated = stmt.executeUpdate( "UPDATE " + m_table +
                                                                      " SET next_id = next_id + " + blockSize +
                                                                      " WHERE table_name = '" + m_tableName + "'" );
                                    if( updated >= 1 )
                                    {
                                        // Update was successful.
                                        conn.commit();

                                        // Return the next id obtained above.
                                        return nextId;
                                    }
                                    else
                                    {
                                        // May have been a transaction confict. Try again.
                                        if( getLogger().isDebugEnabled() )
                                        {
                                            getLogger().debug(
                                                "Update resulted in no rows being changed." );
                                        }
                                    }
                                }
                                catch( SQLException e )
                                {
                                    // Assume that this was caused by a transaction conflict.  Try again.
                                    if( getLogger().isDebugEnabled() )
                                    {
                                        getLogger().debug(
                                            "Encountered an exception attempting to update the " +
                                            m_table + " table.  May be a transaction confict.  " +
                                            "Trying again: " + e.getMessage() );
                                    }
                                }

                                // If we got here, then we failed, roll back the connection so we can
                                //  try again.
                                conn.rollback();

                                tries++;
                            }
                            // If we got here then we ran out of tries.
                            getLogger().error( "Unable to allocate a block of Ids.  Too many retries." );
                            return null;
                        }
                        catch( SQLException e )
                        {
                            // Need this catch so that the connection can be rolled back before
                            //  the transaction is set in the finally block.
                            String msg = "Unable to allocate a block of Ids.";
                            getLogger().error( msg, e );

                            // Rollback after the error is logged so that any problems rolling back
                            //  will not prevent the error from being logged.
                            conn.rollback();

                            throw new IdException( msg, e );
                        }
                    }
                    finally
                    {
                        // Restore the isolation level
                        conn.setTransactionIsolation( oldIsolation );
                    }
                }
                finally
                {
                    // restore Auto commit
                    conn.setAutoCommit( oldAutoCommit );
                }
            }
            finally
            {
                conn.close();
            }
        }
        catch( SQLException e )
        {
            String msg = "Unable to allocate a block of Ids.";
            getLogger().error( msg, e );
            throw new IdException( msg, e );
        }
    }

    /**
     * Allocates a block of ids of the given size and returns the first id.
     *  MySQL does not support transactions so this method handles synchronization
     *  by making use of table locking.
     *
     * @param blockSize number of ids to allocate.
     * @param useBigDecimals returns the first id as a BigDecimal if true, otherwise as a Long.
     *
     * @return either a Long or a BigDecimal depending on the value of useBigDecimals
     *
     * @throws IdException if a block of ids can not be allocated.
     */
    private Object allocateIdBlockMySQL( int blockSize, boolean useBigDecimals )
        throws IdException
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Allocating a new block of " + blockSize + " ids." );
        }

        try
        {
            Connection conn = getConnection();
            try
            {
                Statement stmt = conn.createStatement();

                // Obtain a lock on the table
                stmt.executeUpdate( "LOCK TABLES " + m_table + " WRITE" );
                try
                {
                    // Get the nextId from the table
                    ResultSet rs = stmt.executeQuery(
                        "SELECT next_id FROM " + m_table + " WHERE table_name = '" + m_tableName + "'" );
                    if( !rs.next() )
                    {
                        // The row does not exist.
                        String msg = "Unable to allocate a block of Ids, no row with table_name='" +
                            m_tableName + "' exists in the " + m_table + " table.";
                        getLogger().error( msg );

                        throw new IdException( msg );
                    }

                    // Get the next_id using the appropriate data type.
                    Object nextId;
                    Object nextSavedId;
                    if( useBigDecimals )
                    {
                        BigDecimal id = rs.getBigDecimal( 1 );
                        nextId = id;
                        nextSavedId = id.add( new BigDecimal( blockSize ) );
                    }
                    else
                    {
                        Long id = new Long( rs.getLong( 1 ) );
                        nextId = id;
                        nextSavedId = new Long( id.longValue() + blockSize );
                    }

                    // Update the value of next_id in the database so it reflects the full block
                    //  being allocated.
                    //
                    // Queries that set next_id = next_id + 10 do not work with large decimal values on MySQL 3.23.31
                    int updated = stmt.executeUpdate( "UPDATE " + m_table +
                                                      " SET next_id = " + nextSavedId +
                                                      " WHERE table_name = '" + m_tableName + "'" );
                    if( updated >= 1 )
                    {
                        // Return the next id obtained above.
                        return nextId;
                    }
                    else
                    {
                        String msg = "Update resulted in no rows being changed.";
                        getLogger().error( msg );

                        throw new IdException( msg );
                    }
                }
                finally
                {
                    // Make absolutely sure that the lock is always released.
                    stmt.executeUpdate( "UNLOCK TABLES" );
                }
            }
            finally
            {
                conn.close();
            }
        }
        catch( SQLException e )
        {
            String msg = "Unable to allocate a block of Ids.";
            getLogger().error( msg, e );
            throw new IdException( msg, e );
        }
    }

    /*---------------------------------------------------------------
     * AbstractDataSourceBlockIdGenerator Methods
     *-------------------------------------------------------------*/
    /**
     * Allocates a block, of the given size, of ids from the database.
     *
     * @param blockSize number of Ids which are to be allocated.
     *
     * @return The first id in the allocated block.
     *
     * @throws IdException if there it was not possible to allocate a block of ids.
     */
    protected BigDecimal allocateBigDecimalIdBlock( int blockSize )
        throws IdException
    {
        BigDecimal id;
        switch( m_dbType )
        {
            case AbstractDataSourceIdGenerator.DBTYPE_MYSQL:
                id = (BigDecimal)allocateIdBlockMySQL( blockSize, true );
                break;

            default:
                id = (BigDecimal)allocateIdBlock( blockSize, true );
        }

        return id;
    }

    /**
     * Allocates a block, of the given size, of ids from the database.
     *
     * @param blockSize number of Ids which are to be allocated.
     *
     * @return The first id in the allocated block.
     *
     * @throws IdException if there it was not possible to allocate a block of ids.
     */
    protected long allocateLongIdBlock( int blockSize )
        throws IdException
    {
        Long id;
        switch( m_dbType )
        {
            case AbstractDataSourceIdGenerator.DBTYPE_MYSQL:
                id = (Long)allocateIdBlockMySQL( blockSize, false );
                break;

            default:
                id = (Long)allocateIdBlock( blockSize, false );
        }

        return id.longValue();
    }

    /*---------------------------------------------------------------
     * Configurable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to configure the component.
     *
     * @param configuration configuration info used to setup the component.
     *
     * @throws ConfigurationException if there are any problems with the configuration.
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        super.configure( configuration );

        // Obtain the table name.
        m_table = configuration.getAttribute( "table", "ids" );

        // Obtain the key-table.
        m_tableName = configuration.getAttribute( "key-table", "id" );
    }
}
