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
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.log.Hierarchy;

/**
 * This is the interface via which you can manager 
 * the root container of Applications.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface KernelMBean
{
    String ROLE = "org.apache.avalon.phoenix.interfaces.KernelMBean";

    String[] getApplicationNames();

    void removeApplication( String name )
        throws Exception;
}
