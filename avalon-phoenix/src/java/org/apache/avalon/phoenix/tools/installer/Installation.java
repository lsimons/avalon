/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.installer;

import java.io.File;

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
    ///The source of installation (usually a directory in .sar format or a .sar file)
    private File     m_source;

    ///Directory in which application is installed
    private File     m_directory;

    ///URL to block configuration data
    private String   m_config;

    ///URL to assembly data
    private String   m_assembly;

    ///URL to application configuration data
    private String   m_server;

    ///ClassPath for application
    private String[] m_classPath;
        
    ///Info for expanded files
    private FileDigest[] m_fileDigests;

    public Installation( final File source,
                         final File directory,
                         final String config,
                         final String assembly,
                         final String server,
                         final String[] classPath,
                         final FileDigest[] fileDigests )
    {
        m_source = source;
        m_directory = directory;
        m_config = config;
        m_assembly = assembly;
        m_server = server;
        m_classPath = classPath;
        m_fileDigests = fileDigests;
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
    public File getDirectory()
    {
        return m_directory;
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
     * Retrieve location of applications server.xml file.
     *
     * @return url to server.xml file
     */
    public String getServer()
    {
        return m_server;
    }

    /**
     * Retrieve ClassPath for application.
     *
     * @return the classpath
     */
    public String[] getClassPath()
    {
        return m_classPath;
    }
    
    /** Retrieve file digests.
     *
     * @return the file digest list.
     */
    FileDigest[] getFileDigests()
    {
        return m_fileDigests;
    }    
}
