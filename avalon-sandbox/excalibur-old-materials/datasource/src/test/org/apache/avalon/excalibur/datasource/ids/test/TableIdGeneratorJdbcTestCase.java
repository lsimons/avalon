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
package org.apache.avalon.excalibur.datasource.ids.test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.excalibur.datasource.ids.IdException;
import org.apache.avalon.excalibur.datasource.ids.IdGenerator;
import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;
import org.apache.avalon.framework.component.ComponentSelector;

/**
 * Test the TableIdGenerator Component.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 */
public class TableIdGeneratorJdbcTestCase
    extends ExcaliburTestCase
{
    private ComponentSelector m_dbSelector;
    private DataSourceComponent m_dataSource;

    private ComponentSelector m_idGeneratorSelector;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public TableIdGeneratorJdbcTestCase( String name )
    {
        super( name );
    }

    /*---------------------------------------------------------------
     * TestCase Methods
     *-------------------------------------------------------------*/
    public void setUp() throws Exception
    {
        super.setUp();

        // Get a reference to a data source
        m_dbSelector = (ComponentSelector)manager.lookup( DataSourceComponent.ROLE + "Selector" );
        m_dataSource = (DataSourceComponent)m_dbSelector.select( "test-db" );

        // We need to initialize an ids table in the database for these tests.
        try
        {
            Connection conn = m_dataSource.getConnection();
            try
            {
                Statement statement = conn.createStatement();

                // Try to drop the table.  It may not exist and throw an exception.
                getLogger().debug( "Attempting to drop old ids table" );
                try
                {
                    statement.executeUpdate( "DROP TABLE ids" );
                }
                catch( SQLException e )
                {
                    // The table was probably just not there.  Ignore this.
                }

                // Create the table that we will use in this test.
                // Different depending on the db. Please add new statements as new databases are
                //  tested.
                getLogger().debug( "Create new ids table" );
                statement.executeUpdate(
                    "CREATE TABLE ids ( " +
                    "table_name varchar(16) NOT NULL, " +
                    "next_id DECIMAL(30) NOT NULL, " +
                    "PRIMARY KEY (table_name))" );
            }
            finally
            {
                conn.close();
            }
        }
        catch( SQLException e )
        {
            getLogger().error( "Unable to initialize database for test.", e );
            fail( "Unable to initialize database for test. " + e );
        }

        // Get a reference to an IdGenerator Selector.
        // Individual IdGenerators are obtained in the tests.
        m_idGeneratorSelector = (ComponentSelector)manager.lookup( IdGenerator.ROLE + "Selector" );

    }

    public void tearDown() throws Exception
    {
        // Free up the IdGenerator Selector
        if( m_idGeneratorSelector != null )
        {
            manager.release( m_idGeneratorSelector );

            m_dbSelector = null;
        }

        try
        {
            Connection conn = m_dataSource.getConnection();
            try
            {
                Statement statement = conn.createStatement();

                // Delete the table that we will use in this test.
                getLogger().debug( "Drop ids table" );
                statement.executeUpdate( "DROP TABLE ids" );
            }
            finally
            {
                conn.close();
            }
        }
        catch( SQLException e )
        {
            getLogger().error( "Unable to cleanup database after test.", e );
            // Want to continue
        }

        // Free up the data source
        if( m_dbSelector != null )
        {
            if( m_dataSource != null )
            {
                m_dbSelector.release( m_dataSource );

                m_dataSource = null;
            }

            manager.release( m_dbSelector );

            m_dbSelector = null;
        }

        super.tearDown();
    }

    /*---------------------------------------------------------------
     * Test Cases
     *-------------------------------------------------------------*/
    public void testNonExistingTableName() throws Exception
    {
        getLogger().info( "testNonExistingTableName" );

        IdGenerator idGenerator =
            (IdGenerator)m_idGeneratorSelector.select( "ids-testNonExistingTableName" );
        try
        {
            try
            {
                idGenerator.getNextIntegerId();
                fail( "Should not have gotten an id" );
            }
            catch( IdException e )
            {
                // Got the expected error.
            }
        }
        finally
        {
            m_idGeneratorSelector.release( idGenerator );
        }
    }

    public void testSimpleRequestIdsSize1() throws Exception
    {
        getLogger().info( "testSimpleRequestIdsSize1" );

        IdGenerator idGenerator =
            (IdGenerator)m_idGeneratorSelector.select( "ids-testSimpleRequestIdsSize1" );
        try
        {
            int testCount = 100;

            // Initialize the counter in the database.
            initializeNextLongId( "test", 1 );

            for( int i = 1; i <= testCount; i++ )
            {
                int id = idGenerator.getNextIntegerId();
                assertEquals( "The returned id was not what was expected.", i, id );
            }

            assertEquals( "The next_id column in the database did not have the expected value.",
                          testCount + 1, peekNextLongId( "test" ) );
        }
        finally
        {
            m_idGeneratorSelector.release( idGenerator );
        }
    }

    public void testSimpleRequestIdsSize10() throws Exception
    {
        getLogger().info( "testSimpleRequestIdsSize10" );

        IdGenerator idGenerator =
            (IdGenerator)m_idGeneratorSelector.select( "ids-testSimpleRequestIdsSize10" );
        try
        {
            int testCount = 100;

            // Initialize the counter in the database.
            initializeNextLongId( "test", 1 );

            for( int i = 1; i <= testCount; i++ )
            {
                int id = idGenerator.getNextIntegerId();
                assertEquals( "The returned id was not what was expected.", i, id );
            }

            assertEquals( "The next_id column in the database did not have the expected value.",
                          testCount + 1, peekNextLongId( "test" ) );
        }
        finally
        {
            m_idGeneratorSelector.release( idGenerator );
        }
    }

    public void testSimpleRequestIdsSize100() throws Exception
    {
        getLogger().info( "testSimpleRequestIdsSize100" );

        IdGenerator idGenerator =
            (IdGenerator)m_idGeneratorSelector.select( "ids-testSimpleRequestIdsSize100" );
        try
        {
            int testCount = 100;

            // Initialize the counter in the database.
            initializeNextLongId( "test", 1 );

            for( int i = 1; i <= testCount; i++ )
            {
                int id = idGenerator.getNextIntegerId();
                assertEquals( "The returned id was not what was expected.", i, id );
            }

            assertEquals( "The next_id column in the database did not have the expected value.",
                          testCount + 1, peekNextLongId( "test" ) );
        }
        finally
        {
            m_idGeneratorSelector.release( idGenerator );
        }
    }

    public void testBigDecimalRequestIdsSize10() throws Exception
    {
        getLogger().info( "testBigDecimalRequestIdsSize10" );

        if( isBigDecimalImplemented() )
        {
            IdGenerator idGenerator =
                (IdGenerator)m_idGeneratorSelector.select( "ids-testBigDecimalRequestIdsSize10" );
            try
            {
                int testCount = 100;
                BigDecimal initial = new BigDecimal( Long.MAX_VALUE + "00" );

                // Initialize the counter in the database.
                initializeNextBigDecimalId( "test", initial );

                for( int i = 0; i < testCount; i++ )
                {
                    BigDecimal id = idGenerator.getNextBigDecimalId();
                    assertEquals( "The returned id was not what was expected.",
                                  initial.add( new BigDecimal( i ) ), id );
                }

                assertEquals( "The next_id column in the database did not have the expected value.",
                              initial.add( new BigDecimal( testCount ) ), peekNextBigDecimalId( "test" ) );
            }
            finally
            {
                m_idGeneratorSelector.release( idGenerator );
            }
        }
        else
        {
            getLogger().warn( "Test Skipped because BigDecimals are not implemented in current driver." );
        }
    }

    public void testMaxByteIds() throws Exception
    {
        getLogger().info( "testMaxByteIds" );

        IdGenerator idGenerator = (IdGenerator)m_idGeneratorSelector.select( "ids-testMaxByteIds" );
        try
        {
            int testCount = 100;
            long max = Byte.MAX_VALUE;
            long initial = max - testCount;

            // Initialize the counter in the database.
            initializeNextLongId( "test", initial );

            for( int i = 0; i <= testCount; i++ )
            {
                byte id = idGenerator.getNextByteId();
                assertEquals( "The returned id was not what was expected.", i + initial, id );
            }

            // Next one should throw an exception
            try
            {
                byte id = idGenerator.getNextByteId();
                fail( "Should not have gotten an id: " + id );
            }
            catch( IdException e )
            {
                // Good.  Got the exception.
            }
        }
        finally
        {
            m_idGeneratorSelector.release( idGenerator );
        }
    }

    public void testMaxShortIds() throws Exception
    {
        getLogger().info( "testMaxShortIds" );

        IdGenerator idGenerator = (IdGenerator)m_idGeneratorSelector.select( "ids-testMaxShortIds" );
        try
        {
            int testCount = 100;
            long max = Short.MAX_VALUE;
            long initial = max - testCount;

            // Initialize the counter in the database.
            initializeNextLongId( "test", initial );

            for( int i = 0; i <= testCount; i++ )
            {
                short id = idGenerator.getNextShortId();
                assertEquals( "The returned id was not what was expected.", i + initial, id );
            }

            // Next one should throw an exception
            try
            {
                short id = idGenerator.getNextShortId();
                fail( "Should not have gotten an id: " + id );
            }
            catch( IdException e )
            {
                // Good.  Got the exception.
            }
        }
        finally
        {
            m_idGeneratorSelector.release( idGenerator );
        }
    }

    public void testMaxIntegerIds() throws Exception
    {
        getLogger().info( "testMaxIntegerIds" );

        IdGenerator idGenerator = (IdGenerator)m_idGeneratorSelector.select( "ids-testMaxIntegerIds" );
        try
        {
            int testCount = 100;
            long max = Integer.MAX_VALUE;
            long initial = max - testCount;

            // Initialize the counter in the database.
            initializeNextLongId( "test", initial );

            for( int i = 0; i <= testCount; i++ )
            {
                int id = idGenerator.getNextIntegerId();
                assertEquals( "The returned id was not what was expected.", i + initial, id );
            }

            // Next one should throw an exception
            try
            {
                int id = idGenerator.getNextIntegerId();
                fail( "Should not have gotten an id: " + id );
            }
            catch( IdException e )
            {
                // Good.  Got the exception.
            }
        }
        finally
        {
            m_idGeneratorSelector.release( idGenerator );
        }
    }

    public void testMaxLongIds() throws Exception
    {
        getLogger().info( "testMaxLongIds" );

        IdGenerator idGenerator = (IdGenerator)m_idGeneratorSelector.select( "ids-testMaxLongIds" );
        try
        {
            int testCount = 100;
            long max = Long.MAX_VALUE;
            long initial = max - testCount;

            // Initialize the counter in the database.
            initializeNextLongId( "test", initial );

            for( int i = 0; i <= testCount; i++ )
            {
                long id = idGenerator.getNextLongId();
                assertEquals( "The returned id was not what was expected.", i + initial, id );
            }

            // Next one should throw an exception
            try
            {
                long id = idGenerator.getNextLongId();
                fail( "Should not have gotten an id: " + id );
            }
            catch( IdException e )
            {
                // Good.  Got the exception.
            }
        }
        finally
        {
            m_idGeneratorSelector.release( idGenerator );
        }
    }

    /*---------------------------------------------------------------
     * Utilitity Methods
     *-------------------------------------------------------------*/
    /**
     * Tests to see whether or not the current DataSource supports BigDecimal
     */
    private boolean isBigDecimalImplemented()
    {
        String tableName = "foorbar_table";

        // Add a row that can be selected.
        initializeNextLongId( tableName, 1 );

        try
        {
            Connection conn = m_dataSource.getConnection();
            try
            {
                Statement statement = conn.createStatement();

                ResultSet rs = statement.executeQuery( "SELECT next_id FROM ids " +
                                                       "WHERE table_name = '" + tableName + "'" );
                if( rs.next() )
                {
                    rs.getBigDecimal( 1 );
                }
                else
                {
                    fail( tableName + " row not in ids table." );
                    return false; // for compiler
                }
            }
            finally
            {
                conn.close();
            }

            // Implemented
            return true;
        }
        catch( SQLException e )
        {
            if( e.toString().toLowerCase().indexOf( "implemented" ) > 0 )
            {
                // Not implemented
                return false;
            }
            getLogEnabledLogger().error( "Unable to test for BigDecimal support.", e );
            fail( "Unable to test for BigDecimal support. " + e );
            return false; // for compiler
        }
    }

    private void initializeNextBigDecimalId( String tableName, BigDecimal nextId )
    {
        try
        {
            Connection conn = m_dataSource.getConnection();
            try
            {
                Statement statement = conn.createStatement();

                // Need to quote the BigDecimal as it is larger than normal numbers can be.
                //  Was causing problems with MySQL
                statement.executeUpdate( "INSERT INTO ids (table_name, next_id) VALUES ('" +
                                         tableName + "', '" + nextId.toString() + "')" );
            }
            finally
            {
                conn.close();
            }
        }
        catch( SQLException e )
        {
            getLogger().error( "Unable to initialize next_id.", e );
            fail( "Unable to initialize next_id. " + e );
        }
    }

    private void initializeNextLongId( String tableName, long nextId )
    {
        try
        {
            Connection conn = m_dataSource.getConnection();
            try
            {
                Statement statement = conn.createStatement();

                statement.executeUpdate( "INSERT INTO ids (table_name, next_id) VALUES ('" +
                                         tableName + "', " + nextId + ")" );
            }
            finally
            {
                conn.close();
            }
        }
        catch( SQLException e )
        {
            getLogger().error( "Unable to initialize next_id.", e );
            fail( "Unable to initialize next_id. " + e );
        }
    }

    private BigDecimal peekNextBigDecimalId( String tableName )
    {
        try
        {
            Connection conn = m_dataSource.getConnection();
            try
            {
                Statement statement = conn.createStatement();

                ResultSet rs = statement.executeQuery( "SELECT next_id FROM ids " +
                                                       "WHERE table_name = '" + tableName + "'" );
                if( rs.next() )
                {
                    return rs.getBigDecimal( 1 );
                }
                else
                {
                    fail( tableName + " row not in ids table." );
                    return null; // for compiler
                }
            }
            finally
            {
                conn.close();
            }
        }
        catch( SQLException e )
        {
            getLogger().error( "Unable to peek next_id.", e );
            fail( "Unable to peek next_id. " + e );
            return null; // for compiler
        }
    }

    private long peekNextLongId( String tableName )
    {
        try
        {
            Connection conn = m_dataSource.getConnection();
            try
            {
                Statement statement = conn.createStatement();

                ResultSet rs = statement.executeQuery( "SELECT next_id FROM ids " +
                                                       "WHERE table_name = '" + tableName + "'" );
                if( rs.next() )
                {
                    return rs.getLong( 1 );
                }
                else
                {
                    fail( tableName + " row not in ids table." );
                    return -1; // for compiler
                }
            }
            finally
            {
                conn.close();
            }
        }
        catch( SQLException e )
        {
            getLogger().error( "Unable to peek next_id.", e );
            fail( "Unable to peek next_id. " + e );
            return -1; // for compiler
        }
    }
}

