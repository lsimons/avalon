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
                    BigDecimal id = rs.getBigDecimal( 1 );
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

