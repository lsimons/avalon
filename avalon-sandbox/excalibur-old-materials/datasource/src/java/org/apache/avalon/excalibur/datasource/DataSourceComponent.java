/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * The standard interface for DataSources in Avalon.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.6 $ $Date: 2002/03/16 00:05:40 $
 * @since 4.0
 */
public interface DataSourceComponent
    extends Component, Configurable, ThreadSafe
{
    /**
     * The name of the role for convenience
     */
    String ROLE = DataSourceComponent.class.getName();

    /**
     * Gets the Connection to the database
     *
     * @throws NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader.
     *
     * @throws NoAvailableConnectionException when there are no more available
     *         Connections in the pool.
     */
    Connection getConnection()
        throws SQLException;
}
