/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.datasource;

import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.ComponentSelector;
import org.apache.excalibur.datasource.DataSourceComponent;
import org.apache.phoenix.Service;

/**
 * DataSourceSelector
 *
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public interface DataSourceSelector
    extends Service, ComponentSelector
{
    /**
     *
     * @param the name of data source
     * @return data source
     */
    DataSourceComponent selectDataSource( Object hint )
        throws ComponentManagerException;
}
