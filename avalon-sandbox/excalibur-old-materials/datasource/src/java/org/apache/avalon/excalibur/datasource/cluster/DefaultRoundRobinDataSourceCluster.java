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
 * The DefaultRoundRobinDataSourceCluster allows the user to specify a cluster of DataSources
 *  which all act as one.  The Cluster works by cycling through its member DataSources returning
 *  a connection from a different one with each call to getConnection().
 * <p>
 * This form of Clustering has the benefit that it can be used by components without requiring
 *  any changes.  But care must be taken as to the kind of data written or read from the database.
 *  Wich this clustering method, there is no control over which DataSource will provide a
 *  connection for any given call.
 * <p>
 * Round Robin Clusters are useful in cases where lots of read-only data needs to be accessed and
 *  multiple copies of the data can be stored on different database servers to balance load.
 * <p>
 * The Configuration for a 2 database cluster is like this:
 *
 * <pre>
 *   &lt;datasources&gt;
 *     &lt;roundrobin-cluster name="mydb-cluster" size="2"&gt;
 *       &lt;dbpool index="0"&gt;mydb-0&lt;/dbpool&gt;
 *       &lt;dbpool index="1"&gt;mydb-1&lt;/dbpool&gt;
 *     &lt;/roundrobin-cluster&gt;
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
 *       shorthand="datasources"
 *       default-class="org.apache.avalon.excalibur.component.ExcaliburComponentSelector"&gt;
 *     &lt;hint shorthand="jdbc" class="org.apache.avalon.excalibur.datasource.JdbcDataSource"/&gt;
 *     &lt;hint shorthand="j2ee" class="org.apache.avalon.excalibur.datasource.J2eeDataSource"/&gt;
 *     &lt;hint shorthand="roundrobin-cluster"
 *         class="org.apache.avalon.excalibur.datasource.cluster.DefaultRoundRobinDataSourceCluster"/&gt;
 *   &lt;/role&gt;
 *   &lt;role name="org.apache.avalon.excalibur.datasource.DataSourceComponentClusterSelector"
 *       shorthand="cluster-datasources"
 *       default-class="org.apache.avalon.excalibur.component.ExcaliburComponentSelector"&gt;
 *     &lt;hint shorthand="jdbc" class="org.apache.avalon.excalibur.datasource.JdbcDataSource"/&gt;
 *     &lt;hint shorthand="j2ee" class="org.apache.avalon.excalibur.datasource.J2eeDataSource"/&gt;
 *   &lt;/role&gt;
 * </pre>
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/11/05 04:34:02 $
 * @since 4.1
 */
public class DefaultRoundRobinDataSourceCluster
    extends AbstractDataSourceCluster
    implements RoundRobinDataSourceCluster
{
    private Object m_semaphore = new Object();
    private int m_nextIndex;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public DefaultRoundRobinDataSourceCluster()
    {
    }

    /*---------------------------------------------------------------
     * DataSourceComponent Methods
     *-------------------------------------------------------------*/
    /**
     * Returns a Connection to one of the Cluster's member DataSources.
     *
     * @throws NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader.
     *
     * @throws NoValidConnectionException when there are no more available
     *         Connections in the pool.
     */
    public Connection getConnection() throws SQLException
    {
        int index;
        synchronized( m_semaphore )
        {
            index = m_nextIndex;
            if( ( ++m_nextIndex ) >= m_size )
            {
                m_nextIndex = 0;
            }
        }

        return getConnectionForIndex( index );
    }
}
