/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metagenerate;

import java.io.IOException;
import java.io.File;
import java.util.Vector;

/**
 * A Xinfo Factory
 * @author Paul Hammant
 */
public class ManifestFactory
{

    private String m_manifestName;
    private File m_destDir;
    private Vector m_blocks = new Vector();

    /**
     * Construct a factory for a class.
     * @param destDir
     * @param mainfestName
     */
    public ManifestFactory(File destDir, String mainfestName)
    {
        m_manifestName = mainfestName;
        m_destDir = destDir;
    }

    /**
     * Add a block
     * @param className
     */
    public void addBlock(String className)
    {
        m_blocks.add(className);
    }

    /**
     * Generate the xinfo file
     * @throws IOException If a problem writing output
     */
    public void generate() throws IOException
    {
        File file = new File(m_destDir, m_manifestName);
        file.getParentFile().mkdirs();
        ManifestHelper manifest = new ManifestHelper(file);
        manifest.writeHeader();
        for (int i = 0; i < m_blocks.size(); i++)
        {
            String block = (String) m_blocks.elementAt(i);
            manifest.writeBlockLines(block);

        }
        manifest.close();
    }


}
