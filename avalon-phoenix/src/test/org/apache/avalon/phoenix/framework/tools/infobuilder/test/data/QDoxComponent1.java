/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.framework.tools.infobuilder.test.data;

import java.io.Serializable;
import org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.otherpkg.Service2;
import org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.otherpkg.Service3;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * A simple avalon component to test QDox loading of info etc.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/03/01 04:20:14 $
 * @avalon.component
 * @avalon.service type="Service1"
 * @avalon.service type="Service2"
 * @avalon.service type="Service3"
 */
public class QDoxComponent1
    extends AbstractLogEnabled
    implements Serializable, Service1, Service2, Service3, Serviceable, Contextualizable, Configurable
{
    /**
     * @avalon.logger
     * @avalon.logger name="foo"
     */
    public void enableLogging( Logger logger )
    {
        super.enableLogging( logger );
    }

    /**
     * @avalon.context type="Context"
     * @avalon.entry key="foo" type="ClassLoader"
     * @avalon.entry key="bar" type="Logger"
     * @avalon.entry key="baz" type="java.io.File"
     */
    public void contextualize( Context context )
        throws ContextException
    {
    }

    /**
     * @avalon.dependency key="foo" type="Service3"
     * @avalon.dependency type="Service3"
     * @avalon.dependency type="Service2" optional="true"
     */
    public void service( ServiceManager manager )
        throws ServiceException
    {
    }

    /**
     * @avalon.configuration type="http://relaxng.org/ns/structure/1.0"
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
    }
}
