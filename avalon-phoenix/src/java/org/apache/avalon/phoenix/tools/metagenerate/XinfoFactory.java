/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.metagenerate;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.Type;

import java.io.IOException;
import java.io.File;

/**
 * A Xinfo Factory
  * @author Paul Hammant
 */
public class XinfoFactory
{

    private JavaClass m_javaClass;
    private File m_destDir;

    /**
     * Construct a factory for a class.
     * @param destDir
     * @param javaClass
     */
    public XinfoFactory(File destDir, JavaClass javaClass)
    {
        m_javaClass = javaClass;
        m_destDir = destDir;
    }

    /**
     * Generate the xinfo file
     * @throws IOException If a problem writing output
     */
    public void generate() throws IOException
    {
        File file = new File(m_destDir,
                m_javaClass.getFullyQualifiedName().replace('.',File.separatorChar) + ".xinfo");
        file.getParentFile().mkdirs();
        XinfoHelper xinfo = new XinfoHelper(file);

        xinfo.writeHeader();

        // services

        processServiceInterfaces(xinfo);

        xinfo.writeEndOfServicesSection();

        processManagementInterfaces(xinfo);

        xinfo.writeEndOfManagementSection();

        processServiceMethod(xinfo);
        xinfo.writeFooter();
        xinfo.close();

    }

    /**
     * Process the service interfaces
     * @param xinfo the xinfo helper
     * @throws IOException If a problem
     */
    private void processServiceInterfaces(XinfoHelper xinfo) throws IOException
    {
        DocletTag[] services = m_javaClass.getTagsByName("phoenix:service");
        for (int i = 0; i < services.length; i++)
        {
            DocletTag service = services[i];
            xinfo.writeServiceLines(service.getNamedParameter("name"));
        }
    }

    /**
     * Process the management interface lines
     * @param xinfo the xinfo helper
     * @throws IOException If a problem
     */
    private void processManagementInterfaces(XinfoHelper xinfo) throws IOException
    {
        DocletTag[] managementInterfaces = m_javaClass.getTagsByName("phoenix:mx");
        for (int i = 0; i < managementInterfaces.length; i++)
        {
            xinfo.writeManagementLine(managementInterfaces[i].getNamedParameter("name"));
        }
    }

    /**
     * Process the service method. Cehck for the right signature.
     * @param xinfo The xinfo helper
     * @throws IOException If a problem
     */
    private void processServiceMethod(XinfoHelper xinfo) throws IOException
    {
        JavaMethod[] methods = m_javaClass.getMethods();
        for (int j = 0; j < methods.length; j++)
        {
            // dependencies

            JavaMethod method = methods[j];
            if (method.getName().equals("service")
                    && method.getReturns().equals(new Type("void",0))
                    && method.getParameters().length == 1
                    && method.getParameters()[0].getType().getValue().equals(
                            "org.apache.avalon.framework.service.ServiceManager"))
            {
                DocletTag[] dependencies = method.getTagsByName("phoenix:dependency");
                for (int i = 0; i < dependencies.length; i++)
                {
                    DocletTag dependency = dependencies[i];
                    xinfo.writeDependencyLines(dependency.getNamedParameter("name"));
                }
            }
        }
    }


}
