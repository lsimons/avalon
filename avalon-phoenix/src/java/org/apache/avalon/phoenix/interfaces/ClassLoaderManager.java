/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import java.io.File;
import org.apache.avalon.framework.configuration.Configuration;

/**
 * Interface for component that creates and manages the
 * <code>ClassLoader</code> for an Application. The specific
 * mechanism by which the <code>ClassLoader</code> is created
 * is dependent on the type of <code>Embeddor</code> and the
 * deployment format.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface ClassLoaderManager
{
    String ROLE = ClassLoaderManager.class.getName();

    /**
     * Create a <code>ClassLoader</code> for a specific application.
     *
     * @param server the configuration "server.xml" for the application
     * @param source the source of application. (usually the name of the .sar file
     *               or else the same as baseDirectory)
     * @param baseDirectory the base directory of application
     * @param classPath the list of URLs in applications deployment
     * @return the ClassLoader created
     * @throws Exception if an error occurs
     */
    ClassLoader createClassLoader( Configuration server,
                                   File source,
                                   File baseDirectory,
                                   String[] classPath )
        throws Exception;
}
