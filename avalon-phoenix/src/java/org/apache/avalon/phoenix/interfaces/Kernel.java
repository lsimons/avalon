/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.log.Hierarchy;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Kernel
    extends Component, Initializable, Disposable
{
    String ROLE = "org.apache.avalon.phoenix.interfaces.Kernel";

    void addApplication( SarMetaData metaData,
                         ClassLoader classLoader,
                         Hierarchy hierarchy,
                         Configuration server )
        throws Exception;

    Application getApplication( String name );

    String[] getApplicationNames();
}
