/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.net.URL;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ClassLoaderManager
    extends Component
{
    String ROLE = "org.apache.avalon.phoenix.components.classloader.ClassLoaderManager";

    ClassLoader createClassLoader( Configuration server, File baseDirectory, URL[] classPath )
        throws Exception;
}
