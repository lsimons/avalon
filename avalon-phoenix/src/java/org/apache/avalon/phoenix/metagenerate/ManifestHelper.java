/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metagenerate;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

/**
 * A Xinfo Helper.
 * @author Paul Hammant
 */
public class ManifestHelper extends AbstractHelper
{

    private FileWriter m_output;

    private static final String[] HEADER = new String[]{
        "Manifest-Version: 1.0",
        "Created-By: Apache Avalon Project (Automatically via MetaGenerate)",
        ""};

    private static final String[] BLOCK_LINES = new String[]{
        "Name: @FULL-CLASS-PATH@.class",
        "Avalon-Block: true"};


    /**
     * Construct
     * @param file The File to create
     * @throws IOException If a problem writing output
     */
    public ManifestHelper(File file) throws IOException
    {
        m_output = new FileWriter(file);
    }

    /**
     * Write the header
     * @throws IOException If a problem writing output
     */
    public void writeHeader() throws IOException
    {
        for (int i = 0; i < HEADER.length; i++)
        {
            m_output.write(HEADER[i] + "\n");
        }
    }


    /**
     * Write Block lines
     * @param className The class name
     * @throws IOException If a problem writing output
     */
    public void writeBlockLines(String className) throws IOException
    {

        for (int i = 0; i < BLOCK_LINES.length; i++)
        {
            String line = BLOCK_LINES[i];
            line = replaceString(line, "@FULL-CLASS-PATH@", className.replace('.', '/'));
            m_output.write(line + "\n");
        }
    }

    /**
     * Close the file.
     * @throws IOException If a problem writing output
     */
    public void close() throws IOException
    {
        m_output.close();
    }

}
