/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource.cluster;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.avalon.excalibur.datasource.NoValidConnectionException;

/**
 * The DefaultHashedDataSourceCluster requires that the user specify an object or a hashCode
 *  which will be used consistantly select a member DataSource form a cluster for each connection
 *  request.  Calls to getConnection() will throw an exception.  Components which make use of
 *  this class must call either the getConnectionForHashObject( Object hashObject) or the
 *  getConnectionForHashCode( int hashCode ) methods instead.
 * <p>
 * This form of Clustering is useful in cases where data can be reliably accessed in a repeatable
 *  manner.  For example a web site's visitor information could be accessed by using a String
 *  containing the visitor's username as a hashObject.  This would allow visitor information to be
 *  spread across several database servers.
 * <p>
 * The Configuration for a 2 database cluster is like this:
 *
 * <pre>
 *   &lt;datasources&gt;
 *     &lt;hashed-cluster name="mydb-cluster" size="2"&gt;
 *       &lt;dbpool index="0"&gt;mydb-0&lt;/dbpool&gt;
 *       &lt;dbpool index="1"&gt;mydb-1&lt;/dbpool&gt;
 *     &lt;/hashed-cluster&gt;
 *   &lt;/datasources&gt;
 *   &lt;cluster-datasources&gt;
 *     &lt;jdbc name="mydb-0"&gt;
 *       &lt;pool-controller min="1" max="10"/&gt;
 *       &lt;auto-commit&gt;true&lt;/auto-commit&gt;
 *       &lt;driver&gt;com.database.jdbc.JdbcDriver&lt;/driver&gt;
 *       &lt;dburl&gt;jdbc:driver://host0/mydb&lt;/dburl&gt;
 *       &lt;user&gt;username&lt;/user&gt;
 *       &lt;password&gt;password&lt;/password&gt;
 *     &lt;/jdbc&gt;
 *     &lt;jdbc name="mydb-1"&gt;
 *       &lt;pool-controller min="1" max="10"/&gt;
 *       &lt;auto-commit&gt;true&lt;/auto-commit&gt;
 *       &lt;driver&gt;com.database.jdbc.JdbcDriver&lt;/driver&gt;
 *       &lt;dburl&gt;jdbc:driver://host1/mydb&lt;/dburl&gt;
 *       &lt;user&gt;username&lt;/user&gt;
 *       &lt;password&gt;password&lt;/password&gt;
 *     &lt;/jdbc&gt;
 *   &lt;/cluster-datasources&gt;
 * </pre>
 *
 * With the following roles declaration:
 *
 * <pre>
 *   &lt;role name="org.apache.avalon.excalibur.datasource.DataSourceComponentSelector"
 *     shorthand="datasources"
 *     default-class="org.apache.avalon.excalibur.component.ExcaliburComponentSelector"&gt;
 *     &lt;hint shorthand="jdbc" class="org.apache.avalon.excalibur.datasource.JdbcDataSource"/&gt;
 *     &lt;hint shorthand="j2ee" class="org.apache.avalon.excalibur.datasource.J2eeDataSource"/&gt;
 *     &lt;hint shorthand="hashed-cluster"
 *       class="org.apache.avalon.excalibur.datasource.cluster.DefaultHashedDataSourceCluster"/&gt;
 *   &lt;/role&gt;
 *   &lt;role name="org.apache.avalon.excalibur.datasource.DataSourceComponentClusterSelector"
 *       shorthand="cluster-datasources"
 *       default-class="org.apache.avalon.excalibur.component.ExcaliburComponentSelector"&gt;
 *     &lt;hint shorthand="jdbc" class="org.apache.avalon.excalibur.datasource.JdbcDataSource"/&gt;
 *     &lt;hint shorthand="j2ee" class="org.apache.avalon.excalibur.datasource.J2eeDataSource"/&gt;
 *   &lt;/role&gt;
 * </pre>
 *
 * A hashed-cluster definition enforces that the configuration specify a size.  This size must
 *  equal the number of datasources referenced as being members of the cluster.  Any datasource can
 *  be a member of the cluster.
 * <p>
 * The hashed-cluster can be obtained in the same manner as a non-clustered datasource.  The only
 *  difference is in how it is used.  The HashedDataSourceCluster requires that the caller provide
 *  an object or a hashCode to use when requesting a connection.
 * <p>
 * The following code demonstrates a change that can be made to database enabled components so that
 *  they will be able to work with both HashedDataSourceCluster DataSources and regular
 *  DataSources.
 * <p>
 * old:
 * <pre>
 *   Connection connection = m_dataSource.getConnection();
 * </pre>
 *
 * new:
 * <pre>
 *   Connection connection;
 *   if ( m_dataSource instanceof HashedDataSourceCluster )
 *   {
 *     connection = ((HashedDataSourceCluster)m_dataSource).getConnectionForHashObject( hashObject );
 *   }
 *   else
 *   {
 *     connection = m_dataSource.getConnection();
 *   }
 * </pre>
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/11/05 04:34:02 $
 * @since 4.1
 */
public class DefaultHashedDataSourceCluster
    extends AbstractDataSourceCluster
    implements HashedDataSourceCluster
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public DefaultHashedDataSourceCluster()
    {
    }

    /*---------------------------------------------------------------
     * DataSourceComponent Methods
     *-------------------------------------------------------------*/
    /**
     * Not supported in this component.  Will throw a NoValidConnectionException.
     */
    public Connection getConnection() throws SQLException
    {
        throw new NoValidConnectionException(
            "getConnection() should not be called for a " + getClass().getName() + ".  " +
            "Please verify your configuration." );
    }

    /*---------------------------------------------------------------
     * HashedDataSourceCluster Methods
     *-------------------------------------------------------------*/
    // public int getClusterSize()
    //   declared in AbstractDataSourceCluster

    /**
     * Gets a Connection to a database given a hash object.
     *
     * @param hashObject Object whose hashCode will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     *
     * @throws NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader or when the index is not valid.
     *
     * @throws NoValidConnectionException when there are no more available
     *         Connections in the pool.
     */
    public Connection getConnectionForHashObject( Object hashObject ) throws SQLException
    {
        return getConnectionForIndex( getIndexForHashObject( hashObject ) );
    }

    /**
     * Gets a Connection to a database given a hash code.
     *
     * @param hashCode HashCode which will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     *
     * @throws NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader or when the index is not valid.
     *
     * @throws NoValidConnectionException when there are no more available
     *         Connections in the pool.
     */
    public Connection getConnectionForHashCode( int hashCode ) throws SQLException
    {
        return getConnectionForIndex( getIndexForHashCode( hashCode ) );
    }

    // public Connection getConnectionForIndex( int index ) throws SQLException
    //   declared in AbstractDataSourceCluster

    /**
     * Gets the index which will be resolved for a given hashCode.  This can be used
     *  by user code to optimize the use of DataSource Clusters.
     * <p>
     * Subclasses can override this method to get different behavior.
     * <p>
     * By default the index = getIndexForHashCode( hashObject.hashCode() )
     *
     * @param hashObject Object whose hashCode will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     */
    public int getIndexForHashObject( Object hashObject )
    {
        return getIndexForHashCode( hashObject.hashCode() );
    }

    /**
     * Gets the index which will be resolved for a given hashCode.  This can be used
     *  by user code to optimize the use of DataSource Clusters.
     * <p>
     * Subclasses can override this method to get different behavior.
     * <p>
     * By default the index = hashCode % getClusterSize()
     *
     * @param hashCode HashCode which will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     */
    public int getIndexForHashCode( int hashCode )
    {
        // DEVELOPER WARNING:
        // If you change the way the hashCode is calculated, you WILL BREAK
        //  things for existing users, so please do so only after much thought.

        // Hash code may be negative, Make them all positive by using the unsigned int value.
        long lHashCode = ( (long)hashCode ) & 0xffffffffL;

        return (int)( lHashCode % getClusterSize() );
    }
}
