/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.metagenerate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A Xinfo Helper.
  * @author Paul Hammant
 */
public class XinfoHelper extends AbstractHelper
{

    private FileWriter m_output;

    private static final String[] HEADER = new String[] {
    "<?xml version=\"1.0\"?>",
    "<!DOCTYPE blockinfo PUBLIC \"-//PHOENIX/Block Info DTD Version 1.0//EN\"",
    "                  \"http://jakarta.apache.org/avalon/dtds/phoenix/blockinfo_1_0.dtd\">",
    "",
    "<blockinfo>",
    "",
    "  <!-- section to describe block -->",
    "  <block>",
    "    <version>1.0</version>",
    "  </block>",
    "",
    "  <!-- services that are offered by this block -->",
    "  <services>" };

    private static final String[] SERVICE_LINES = new String[] {
    "    <service name=\"@SERVICE-CLASS@\"/>" };

    private static final String[] END_OF_SERVICES = new String[] {
    "  </services>",
    "",
    "  <!-- interfaces that may be exported to manange this block -->",
    "  <management-access-points>" };

    private static final String[] MANAGEMENT_LINE = new String[] {
    "     <service name=@INTERFACE-NAME@/>" };

    private static final String[] END_OF_MGMT = new String[] {
    "  </management-access-points>",
    "",
    "  <!-- services that are required by this block -->",
    "  <dependencies>" };

    private static final String[] DEPENDENCY_SECTION = new String[] {

    "    <dependency>",
    "      <service name=\"@SERVICE-CLASS@\"/>",
    "    </dependency>" };

    private static final String[] FOOTER = new String[] {
    "  </dependencies>",
    "</blockinfo>" };

    /**
     * Construct
     * @param file The File to create
     * @throws IOException If a problem writing output
     */
    public XinfoHelper(File file) throws IOException
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
     * Write the Service Lines
     * @param service The service name
     * @throws IOException If a problem writing output
     */
    public void writeServiceLines(String service) throws IOException
    {
        for (int i = 0; i < SERVICE_LINES.length; i++)
        {
            String line =  SERVICE_LINES[i];
            line = replaceString(line, "\"@SERVICE-CLASS@\"", service);
            m_output.write(line  + "\n");
        }
    }

    /**
     * Write the end of services section
     * @throws IOException If a problem writing output
     */
    public void writeEndOfServicesSection() throws IOException
    {
        for (int i = 0; i < END_OF_SERVICES.length; i++)
        {
            m_output.write(END_OF_SERVICES[i] + "\n");
        }
    }

    public void writeManagementLine(String interfaceName) throws IOException
    {
        for (int i = 0; i < MANAGEMENT_LINE.length; i++)
        {
            String line =  MANAGEMENT_LINE[i];
            line = replaceString(line, "@INTERFACE-NAME@", interfaceName);
            m_output.write(line + "\n");
        }

    }

    /**
     * Write the end of management section
     * @throws IOException If a problem writing output
     */
    public void writeEndOfManagementSection() throws IOException
    {
        for (int i = 0; i < END_OF_MGMT.length; i++)
        {
            m_output.write(END_OF_MGMT[i] + "\n");
        }
    }


    /**
     * Write Dependency Lines
     * @param dependency The Dependency
     * @throws IOException If a problem writing output
     */
    public void writeDependencyLines(String dependency) throws IOException
    {
        for (int i = 0; i < DEPENDENCY_SECTION.length; i++)
        {
            String line =  DEPENDENCY_SECTION[i];
            line = replaceString(line, "\"@SERVICE-CLASS@\"", dependency);
            m_output.write(line + "\n");
        }
    }

    /**
     * Write footer
     * @throws IOException If a problem writing output
     */
    public void writeFooter() throws IOException
    {
        for (int i = 0; i < FOOTER.length; i++)
        {
            m_output.write(FOOTER[i] + "\n");
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
