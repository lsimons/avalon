/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.framework.configuration.Configurable;

/**
 * The standard interface for DataSources in Avalon.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2001/08/07 10:57:07 $
 * @since 4.0
 */
public interface DataSourceComponent
    extends Component, Configurable, ThreadSafe
{
    /**
     * The name of the role for convenience
     */
    String ROLE = "org.apache.avalon.excalibur.datasource.DataSourceComponent";

    /**
     * Gets the Connection to the database
     */
    Connection getConnection()
        throws SQLException;
}
