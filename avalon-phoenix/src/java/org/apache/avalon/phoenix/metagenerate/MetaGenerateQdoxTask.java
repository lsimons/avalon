/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metagenerate;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.ant.AbstractQdoxTask;
import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.IOException;

/**
 * MetaInfo Generation Ant Taskdef
  * @author Paul Hammant
 */
public class MetaGenerateQdoxTask extends AbstractQdoxTask
{

    private File m_destDir;
    private String m_manifestName;

    /**
     * Execute
     */
    public void execute()
    {
        super.execute();
        try
        {
            m_destDir.mkdirs();
            outputClasses();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new BuildException("IOException " + e.getMessage());
        }
    }

    /**
     * Set the desitation
     * @param destinationDir The destination directory
     */
    public void setDest(File destinationDir)
    {
        m_destDir = destinationDir;
    }

    /**
     * Set the manifest name
     * @param manifestName The Manifest Name
     */
    public void setManifestName(String manifestName)
    {
        m_manifestName = manifestName;
    }

    /**
     * Output the classes
     * @throws IOException If a problem writing output
     */
    protected void outputClasses() throws IOException
    {
        ManifestFactory manifestFactory = new ManifestFactory(m_destDir, m_manifestName);

        for (int i = 0; i < allClasses.size(); i++)
        {
            JavaClass javaClass = (JavaClass) allClasses.get(i);
            DocletTag block = javaClass.getTagByName("phoenix:block");
            if (block != null)
            {
                XinfoFactory factory = new XinfoFactory(m_destDir, javaClass);
                factory.generate();
                manifestFactory.addBlock(javaClass.getFullyQualifiedName());
            }
            DocletTag topic = javaClass.getTagByName("phoenix:mx-topic");
            if (topic != null)
            {
                MxinfoFactory factory = new MxinfoFactory(m_destDir, javaClass);
                factory.generate();
            }
            if (m_manifestName != null)
            {
                manifestFactory.generate();
            }
        }
    }
}
