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
public interface IndexedDataSourceCluster
    extends DataSourceComponent
{
    /**
     * The name of the role for convenience
     */
    String ROLE = "com.silveregg.util.datasource.cluster.IndexedDataSourceCluster";

    /**
     * Returns the number of DataSources in the cluster.
     *
     * @return size of the cluster.
     */
    int getClusterSize();

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
}
