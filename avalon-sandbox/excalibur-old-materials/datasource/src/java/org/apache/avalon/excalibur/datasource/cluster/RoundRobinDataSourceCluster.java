/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource.cluster;

import org.apache.avalon.excalibur.datasource.DataSourceComponent;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/22 03:04:27 $
 * @since 4.1
 */
public interface RoundRobinDataSourceCluster
    extends DataSourceComponent
{
    /**
     * The name of the role for convenience
     */
    String ROLE = "org.apache.avalon.excalibur.datasource.cluster.RoundRobinDataSourceCluster";
}
