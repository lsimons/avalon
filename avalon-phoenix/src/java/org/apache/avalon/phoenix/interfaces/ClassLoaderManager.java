/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.interfaces;

import java.io.File;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;

/**
 * Interface for component that creates and manages the
 * <code>ClassLoader</code> for an Application. The specific
 * mechanism by which the <code>ClassLoader</code> is created
 * is dependent on the type of <code>Embeddor</code> and the
 * deployment format.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ClassLoaderManager
    extends Component
{
    String ROLE = "org.apache.avalon.phoenix.interfaces.ClassLoaderManager";

    /**
     * Create a <code>ClassLoader</code> for a specific application.
     *
     * @param server the configuration "server.xml" for the application
     * @param source the source of application. (usually the name of the .sar file
     *               or else the same as baseDirectory)
     * @param baseDirectory the base directory of application
     * @param classPath the list of URLs in applications deployment
     * @return the ClassLoader created
     * @exception Exception if an error occurs
     */
    ClassLoader createClassLoader( Configuration server,
                                   File source,
                                   File baseDirectory,
                                   String[] classPath )
        throws Exception;
}
