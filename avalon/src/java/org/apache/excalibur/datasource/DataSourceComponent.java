/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.avalon.component.Component;
import org.apache.avalon.thread.ThreadSafe;
import org.apache.avalon.configuration.Configurable;

/**
 * The standard interface for DataSources in Avalon.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2001/04/18 13:15:48 $
 */
public interface DataSourceComponent
    extends Component, Configurable, ThreadSafe
{
    /**
     * Gets the Connection to the database
     */
    Connection getConnection()
        throws SQLException;
}
