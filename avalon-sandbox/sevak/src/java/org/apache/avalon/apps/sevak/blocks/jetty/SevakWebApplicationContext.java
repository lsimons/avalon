/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.jetty;

import org.mortbay.jetty.servlet.WebApplicationContext;

import java.io.IOException;
import java.io.File;

/**
 *
 * Override for Jetty's WebApplicationContext to kludge Jasper JSP compilation ability.
 *
 *
 * @see <a href="http://jetty.mortbay.com/">Jetty Project Page</a>
 *
 * @author  Paul Hammant
 * @author  Jules Gosnell
 * @version 1.0
 */

public class SevakWebApplicationContext extends WebApplicationContext
{

    private File m_sarRoot;
    private File m_phoenixLib;

    public SevakWebApplicationContext(File sarRoot, String webAppURL) throws IOException
    {
        super(webAppURL);
        m_sarRoot = sarRoot;
        m_phoenixLib = new File(sarRoot.getParentFile().getParentFile(),"lib");
    }


    /**
     * Make a classpath for Jasper to use during compilation. This is minimalist
     * @return the classpath.
     */
    public String getFileClassPath()
    {
        String classpath = "";
        classpath += new File(m_sarRoot, "jsplibs" + File.separator + "jasper-runtime.jar") + File.pathSeparator;
        classpath += new File(m_sarRoot, "jsplibs" + File.separator + "jasper-compile.jar") + File.pathSeparator;
        classpath += new File(m_sarRoot, "jsplibs" + File.separator + "javax.servlet.jar") + File.pathSeparator;
        classpath += new File(m_phoenixLib, "tools.jar");
        return classpath;
    }

}
