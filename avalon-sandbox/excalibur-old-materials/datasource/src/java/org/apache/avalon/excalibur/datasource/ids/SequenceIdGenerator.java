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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * The SequenceIdGenerator requests each Id using a sequence in a database.  While not
 *  actually pooling batches of Ids like other IdGenerator implementations, making use of this class
 *  does make code compatable with other IdGenerators on a configuration basis.
 * <p>
 * The Configuration to use a SequenceIdGenerator look like the following:
 * <pre>
 *   &lt;id-generators&gt;
 *       &lt;sequence name="user-ids" logger="cm.ids"&gt;
 *           &lt;dbpool&gt;user-db&lt;/dbpool&gt;
 *           &lt;query&gt;SELECT NEXTVAL('category_ids')&lt;/query&gt;
 *       &lt;/sequence&gt;
 *   &lt;/id-generators&gt;
 * </pre>
 * Where user-db is the name of a DataSource configured in a datasources element, and query is
 *  any query which will return a single id while maintaining state so that successive calls
 *  will continue to return incremented ids.
 * <p>
 *
 * With the following roles declaration:
 * <pre>
 *   &lt;role name="org.apache.avalon.excalibur.datasource.ids.IdGeneratorSelector"
 *         shorthand="id-generators"
 *         default-class="org.apache.avalon.excalibur.component.ExcaliburComponentSelector"&gt;
 *       &lt;hint shorthand="sequence"
 *             class="org.apache.avalon.excalibur.datasource.ids.SequenceIdGenerator"/&gt;
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
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/22 03:04:27 $
 * @since 4.1
 */
public class SequenceIdGenerator
    extends AbstractDataSourceIdGenerator
{
    private String m_query;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public SequenceIdGenerator()
    {
    }

    /*---------------------------------------------------------------
     * AbstractIdGenerator Methods
     *-------------------------------------------------------------*/
    /**
     * Gets the next id as a Big Decimal.  This method will only be called
     *  when synchronized and when the data type is configured to be BigDecimal.
     *
     * @return the next id as a BigDecimal.
     *
     * @throws IdException if an Id could not be allocated for any reason.
     */
    protected BigDecimal getNextBigDecimalIdInner()
        throws IdException
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Requesting an Id using query: " + m_query );
        }

        try
        {
            Connection conn = getConnection();
            try
            {
                PreparedStatement stmt = conn.prepareStatement( m_query );
                ResultSet rs = stmt.executeQuery();
                if( rs.next() )
                {
                    return rs.getBigDecimal( 1 );
                }
                else
                {
                    String msg = "Query for Id did not return a value";
                    getLogger().error( msg );
                    throw new IdException( msg );
                }
            }
            finally
            {
                conn.close();
            }
        }
        catch( SQLException e )
        {
            throw new IdException( "Unable to allocate an Id", e );
        }
    }

    /**
     * Gets the next id as a long.  This method will only be called
     *  when synchronized and when the data type is configured to be long.
     *
     * @return the next id as a long.
     *
     * @throws IdException if an Id could not be allocated for any reason.
     */
    protected long getNextLongIdInner()
        throws IdException
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Requesting an Id using query: " + m_query );
        }

        try
        {
            Connection conn = getConnection();
            try
            {
                PreparedStatement stmt = conn.prepareStatement( m_query );
                ResultSet rs = stmt.executeQuery();
                if( rs.next() )
                {
                    return rs.getLong( 1 );
                }
                else
                {
                    String msg = "Query for Id did not return a value";
                    getLogger().error( msg );
                    throw new IdException( msg );
                }
            }
            finally
            {
                conn.close();
            }
        }
        catch( SQLException e )
        {
            String msg = "Unable to allocate an Id";
            getLogger().error( msg );
            throw new IdException( msg );
        }
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

        // Obtain the query to use to obtain an id from a sequence.
        m_query = configuration.getChild( "query" ).getValue();
    }
}
