/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.source;

import org.apache.avalon.framework.service.ServiceSelector;

/**
 * DataSourceSelector
 *
 * @author <a href="mailto:avalon-dev@jakarta.apache.org">Avalon Development Team</a>
 */
public interface DataSourceSelector
    extends ServiceSelector
{
    String ROLE = DataSourceSelector.class.getName();
}
