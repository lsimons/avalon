/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.atlantis;

import org.apache.avalon.framework.camelot.ContainerException;

/**
 * The Kernel is the core of any system.
 * The kernel is responsible for orchestrating low level services 
 * such as loading, configuring and destroying applications. It also 
 * gives access to basic facilities specific to that particular kernel.
 * A ServerKernel may offer scheduling, naming, security, classloading etc.
 * A JesktopKernel may offer inter-application drag-n-drop support.
 * A VEKernel may offer inter-VE transport for Avatars.
 *
 * Note that no facilities are available until after the Kernel has been initialized.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Kernel 
    extends Application
{
    String ROLE = "org.apache.avalon.framework.atlantis.Kernel";

    /**
     * Retrieve Application from container. 
     * The Application that is returned must be initialized 
     * and prepared for manipulation.
     *
     * @param name the name of application
     * @return the application
     * @exception ContainerException if an error occurs 
     */
    Application getApplication( String name )
        throws ContainerException;
}
