/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.datasource;

import org.apache.avalon.framework.component.ComponentSelector;

/**
 * DataSourceSelector
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @deprecated Use {@link org.apache.avalon.cornerstone.services.source.DataSourceSelector} instead.
 */
public interface DataSourceSelector
    extends ComponentSelector
{
    String ROLE = DataSourceSelector.class.getName();
}
