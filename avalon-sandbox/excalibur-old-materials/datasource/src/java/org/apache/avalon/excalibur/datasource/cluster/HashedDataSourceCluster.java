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
import org.apache.avalon.excalibur.datasource.DataSourceComponent;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/11/05 04:34:02 $
 * @since 4.1
 */
public interface HashedDataSourceCluster
    extends DataSourceComponent
{
    /**
     * The name of the role for convenience
     */
    String ROLE = "org.apache.avalon.excalibur.datasource.cluster.HashedDataSourceCluster";

    /**
     * Returns the number of DataSources in the cluster.
     *
     * @return size of the cluster.
     */
    int getClusterSize();

    /**
     * Gets a Connection to a database given a hash object.
     *
     * @param hashObject Object whose hashCode will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     *
     * @throws org.apache.avalon.excalibur.datasource.NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader or when the index is not valid.
     *
     * @throws org.apache.avalon.excalibur.datasource.NoAvailableConnectionException when there are no more available
     *         Connections in the pool.
     */
    Connection getConnectionForHashObject( Object hashObject ) throws SQLException;

    /**
     * Gets a Connection to a database given a hash code.
     *
     * @param hashCode HashCode which will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     *
     * @throws org.apache.avalon.excalibur.datasource.NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader or when the index is not valid.
     *
     * @throws org.apache.avalon.excalibur.datasource.NoAvailableConnectionException when there are no more available
     *         Connections in the pool.
     */
    Connection getConnectionForHashCode( int hashCode ) throws SQLException;

    /**
     * Gets a Connection to a database given an index.
     *
     * @param index Index of the DataSource for which a connection is to be returned.
     *
     * @throws org.apache.avalon.excalibur.datasource.NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader or when the index is not valid.
     *
     * @throws org.apache.avalon.excalibur.datasource.NoAvailableConnectionException when there are no more available
     *         Connections in the pool.
     */
    Connection getConnectionForIndex( int index ) throws SQLException;

    /**
     * Gets the index which will be resolved for a given hashCode.  This can be used
     *  by user code to optimize the use of DataSource Clusters.
     *
     * @param hashObject Object whose hashCode will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     */
    int getIndexForHashObject( Object hashObject );

    /**
     * Gets the index which will be resolved for a given hashCode.  This can be used
     *  by user code to optimize the use of DataSource Clusters.
     *
     * @param hashCode HashCode which will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     */
    int getIndexForHashCode( int hashCode );
}
