/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.xdoclet;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class ManifestWriter
{
    private static final String[] LINES = new String[]{
        "Manifest-Version: 1.0",
        "Created-By: Apache Avalon Project (Automatically via PhoenixXDoclet)",
        ""};

    /**
     * Construct
     * @param destDir The desitnatin dir
     * @param fileName The File to create
     * @throws IOException If a problem writing output
     */
    public void write(File destDir, String fileName) throws IOException
    {
        File file= new File(destDir, fileName);
        FileWriter output = new FileWriter(file);
        for (int i = 0; i < LINES.length; i++)
        {
            output.write(LINES[i] + "\n");
        }
        output.close();
    }
}
