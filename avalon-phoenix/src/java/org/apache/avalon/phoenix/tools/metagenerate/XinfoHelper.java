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

    private static final String[] HEADER1 = new String[] {
        "<?xml version=\"1.0\"?>",
        "<!DOCTYPE blockinfo PUBLIC \"-//PHOENIX/Block Info DTD Version 1.0//EN\"",
        "                  \"http://jakarta.apache.org/avalon/dtds/phoenix/blockinfo_1_0.dtd\">",
        "",
        "<blockinfo>",
        "",
        "  <!-- section to describe block -->",
        "  <block>",
        "    <version>1.0</version>"};

    private static final String[] HEADER2 = new String[]{
        "    <schema-type>@CONFIG-TYPE@</schema-type>"};

    private static final String[] HEADER3 = new String[]{
        "  </block>",
        "",
        "  <!-- services that are offered by this block -->",
        "  <services>"};

    private static final String[] SERVICE_LINES = new String[]{
        "    <service name=\"@SERVICE-CLASS@\"@VERSION@/>"};

    private static final String[] END_OF_SERVICES = new String[]{
        "  </services>",
        "",
        "  <!-- interfaces that may be exported to manange this block -->",
        "  <management-access-points>"};

    private static final String[] MANAGEMENT_LINE = new String[]{
        "     <service name=@INTERFACE-NAME@/>"};

    private static final String[] END_OF_MGMT = new String[]{
        "  </management-access-points>",
        "",
        "  <!-- services that are required by this block -->",
        "  <dependencies>"};

    private static final String[] DEPENDENCY_SECTION = new String[]{

        "    <dependency>",
        "      <role>@ROLE-NAME@</role>",
        "      <service name=\"@SERVICE-CLASS@\"@VERSION@/>",
        "    </dependency>"};

    private static final String[] FOOTER = new String[]{
        "  </dependencies>",
        "</blockinfo>"};


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
    public void writeHeader(String configType) throws IOException
    {
        for (int i = 0; i < HEADER1.length; i++)
        {
            m_output.write(HEADER1[i] + "\n");
        }
        if (configType != null)
        {
            for (int i = 0; i < HEADER2.length; i++)
            {
                String line = HEADER2[i];
                line = replaceString(line, "@CONFIG-TYPE@", configType);
                m_output.write(line + "\n");

            }
        }
        for (int i = 0; i < HEADER3.length; i++)
        {
            m_output.write(HEADER3[i] + "\n");
        }
    }

    /**
     * Write the Service Lines
     * @param service The service name
     * @throws IOException If a problem writing output
     */
    public void writeServiceLines(String service, String version) throws IOException
    {
        version = version == null ? "" : " version=" + version;
        for (int i = 0; i < SERVICE_LINES.length; i++)
        {
            String line = SERVICE_LINES[i];
            line = replaceString(line, "\"@SERVICE-CLASS@\"", service);
            line = replaceString(line, "@VERSION@", version);
            m_output.write(line + "\n");
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
            String line = MANAGEMENT_LINE[i];
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
     * @param version The version
     * @param role The role name
     * @throws IOException If a problem writing output
     */
    public void writeDependencyLines(String dependency, String version, String role)
            throws IOException
    {
        version = version == null ? "" : " version=" + version;
        for (int i = 0; i < DEPENDENCY_SECTION.length; i++)
        {
            String line = DEPENDENCY_SECTION[i];
            line = replaceString(line, "\"@SERVICE-CLASS@\"", dependency);
            line = replaceString(line, "@VERSION@", version);
            line = replaceString(line, "@ROLE-NAME@", role);
            if (line.indexOf("<role>") == -1 | role != null)
            {
                m_output.write(line + "\n");
            }
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
