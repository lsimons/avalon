/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import java.io.File;

/**
 * Descriptor for installation.
 * This descriptor contains all the information relating to
 * installed application. In particular it locates all the
 * jars in Classpath, config files and installation directory.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.2.2.1 $ $Date: 2002/12/03 08:14:23 $
 */
public final class Installation
{
    ///The source of installation (usually a directory in .sar format or a .sar file)
    private final File m_source;

    ///Directory in which application is installed
    private final File m_homeDirectory;

    ///Directory in which application temporary/work data is stored
    private final File m_workDirectory;

    ///URL to block configuration data
    private final String m_config;

    ///URL to assembly data
    private final String m_assembly;

    ///URL to application configuration data
    private final String m_environment;

    public Installation( final File source,
                         final File directory,
                         final File workDirectory,
                         final String config,
                         final String assembly,
                         final String environment )
    {
        m_source = source;
        m_homeDirectory = directory;
        m_workDirectory = workDirectory;
        m_config = config;
        m_assembly = assembly;
        m_environment = environment;
    }

    /**
     * Get the source of application. (Usually a
     * directory in .sar format or a .sar)
     *
     * @return the source of application
     */
    public File getSource()
    {
        return m_source;
    }

    /**
     * Get directory application is installed into.
     *
     * @return the applications base directory
     */
    public File getHomeDirectory()
    {
        return m_homeDirectory;
    }

    /**
     * Get the directory in which temporary data for this application
     * is stored.
     *
     * @return the work directory for application.
     */
    public File getWorkDirectory()
    {
        return m_workDirectory;
    }

    /**
     * Retrieve location of applications config.xml file.
     *
     * @return url to config.xml file
     */
    public String getConfig()
    {
        return m_config;
    }

    /**
     * Retrieve location of applications assembly.xml file.
     *
     * @return url to assembly.xml file
     */
    public String getAssembly()
    {
        return m_assembly;
    }

    /**
     * Retrieve location of applications environment.xml file.
     *
     * @return url to environment.xml file
     */
    public String getEnvironment()
    {
        return m_environment;
    }
}
