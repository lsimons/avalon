/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.installer;

import java.io.File;
import java.net.URL;

/**
 * Descriptor for installation.
 * This descriptor contains all the information relating to
 * installed application. In particular it locates all the
 * jars in Classpath, config files and installation directory.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class Installation
{
    ///Directory in which application is installed
    private File  m_directory;

    ///URL to block configuration data
    private URL   m_config;

    ///URL to assembly data
    private URL   m_assembly;

    ///URL to application configuration data
    private URL   m_server;

    ///ClassPath for application
    private URL[] m_classPath;

    public Installation( final File directory,
                         final URL config,
                         final URL assembly,
                         final URL server,
                         final URL[] classPath )
    {
        m_directory = directory;
        m_config = config;
        m_assembly = assembly;
        m_server = server;
        m_classPath = classPath;
    }

    /**
     * Get directory application is installed into.
     *
     * @return the applications base directory
     */
    public File getDirectory()
    {
        return m_directory;
    }

    /**
     * Retrieve location of applications config.xml file.
     *
     * @return url to config.xml file
     */
    public URL getConfig()
    {
        return m_config;
    }

    /**
     * Retrieve location of applications assembly.xml file.
     *
     * @return url to assembly.xml file
     */
    public URL getAssembly()
    {
        return m_assembly;
    }

    /**
     * Retrieve location of applications server.xml file.
     *
     * @return url to server.xml file
     */
    public URL getServer()
    {
        return m_server;
    }

    /**
     * Retrieve ClassPath for application.
     *
     * @return the classpath
     */
    public URL[] getClassPath()
    {
        return m_classPath;
    }
}
