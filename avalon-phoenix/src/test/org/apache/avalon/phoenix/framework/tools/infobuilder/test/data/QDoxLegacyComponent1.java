/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.framework.tools.infobuilder.test.data;

import java.io.Serializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.otherpkg.Service2;
import org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.otherpkg.Service3;

/**
 * A simple avalon component to test QDox loading of info etc.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/03/01 04:20:14 $
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.Service1"
 * @phoenix:service name="org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.otherpkg.Service2"
 * @phoenix:service name="org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.otherpkg.Service3"
 */
public class QDoxLegacyComponent1
    extends AbstractLogEnabled
    implements Serializable, Service1, Service2, Service3, Serviceable, Configurable
{
    /**
     * @phoenix:dependency role="foo" name="org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.otherpkg.Service3"
     * @phoenix:dependency name="org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.otherpkg.Service3"
     * @phoenix:dependency name="org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.otherpkg.Service2"
     */
    public void service( ServiceManager manager )
        throws ServiceException
    {
    }

    /**
     * @phoenix:configuration-schema type="relax-ng"
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
    }
}
