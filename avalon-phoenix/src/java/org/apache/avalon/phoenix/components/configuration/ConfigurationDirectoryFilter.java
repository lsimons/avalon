/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.configuration;

import java.io.File;
import java.io.FileFilter;

class ConfigurationDirectoryFilter implements FileFilter
{
    public boolean accept( File pathname )
    {
        return pathname.isDirectory();
    }
}
