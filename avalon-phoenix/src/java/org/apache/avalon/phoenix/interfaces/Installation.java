/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

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
 * @version $Revision: 1.5 $ $Date: 2003/03/22 12:07:14 $
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
