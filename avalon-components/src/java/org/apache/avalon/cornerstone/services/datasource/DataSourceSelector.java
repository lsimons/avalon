/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.services.datasource;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.phoenix.Service;

/**
 * DataSourceSelector
 *
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public interface DataSourceSelector
    extends ComponentSelector
{
    String ROLE = "org.apache.avalon.cornerstone.services.datasource.DataSourceSelector";
}
