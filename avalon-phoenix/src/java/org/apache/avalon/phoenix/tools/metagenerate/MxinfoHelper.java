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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A Xinfo Helper.
 * @author Paul Hammant
 */
public class MxinfoHelper extends AbstractHelper
{

    private FileWriter m_output;

    private static final String HEADER[] = new String[]{
        "<?xml version=\"1.0\"?>",
        "<!DOCTYPE mxinfo PUBLIC \"-//PHOENIX/Mx Info DTD Version 1.0//EN\"",
        "                  \"http://jakarta.apache.org/avalon/dtds/phoenix/mxinfo_1_0.dtd\">",
        "",
        "<mxinfo>",
        ""};

    private static final String TOPIC[] = new String[]{
        "    <topic name=\"@TOPIC@\" >"};

    private static final String ATTR_HEADER[] = new String[]{
        "",
        "      <!-- attributes -->"};

    private static final String ATTRIBUTE[] = new String[]{
        "      <attribute",
        "        name=\"@NAME@\"",
        "        description=\"@DESCRIPTION@\"",
        "        type=\"@RETURN@\"",
        "      />"};

    private static final String OPERATIONS_HEADER[] = new String[]{
        "",
        "      <!-- operations -->",
        "" };

    private static final String OPERATION_HEADER[] = new String[]{
        "      <operation",
        "        name=\"@NAME@\"",
        "        description=\"@DESCRIPTION@\"",
        "        type=\"@RETURN@\">" };

    private static final String PARAMETER[] = new String[]{
        "        <param",
        "           name=\"@NAME@\"",
        "           description=\"@DESCRIPTION@\"",
        "           type=\"@TYPE@\"",
        "        />" };

    private static final String OPERATION_FOOTER[] = new String[]{
        "      </operation>" };

    private static final String FOOTER[] = new String[]{
        "",
        "    </topic>",
        "",
        "</mxinfo>"};

    /**
     * Construct
     * @param file The File to create
     * @throws IOException If a problem writing output
     */
    public MxinfoHelper(File file) throws IOException
    {
        m_output = new FileWriter(file);
    }

    /**
     * Write the header
     * @param topic The topic
     * @throws IOException If a problem writing output
     */
    public void writeHeader(String topic) throws IOException
    {
        for (int i = 0; i < HEADER.length; i++)
        {
            m_output.write(HEADER[i] + "\n");
        }

        for (int i = 0; i < TOPIC.length; i++)
        {
            String line = TOPIC[i];
            line = replaceString(line, "\"@TOPIC@\"", topic);
            m_output.write(line + "\n");
        }

        for (int i = 0; i < ATTR_HEADER.length; i++)
        {
            m_output.write(ATTR_HEADER[i] + "\n");
        }

    }

    /**
     * Write the Attribute Lines
     * @param attrName The attribute name
     * @param description The description
     * @param type The type
     * @throws IOException If a problem writing output
     */
    public NamedXmlSnippet makeAttrLines(String attrName, String description, String type)
            throws IOException
    {
        String xml = "";
        for (int i = 0; i < ATTRIBUTE.length; i++)
        {
            String line = ATTRIBUTE[i];
            line = replaceString(line, "@NAME@", attrName);
            line = replaceString(line, "\"@DESCRIPTION@\"", description);
            line = replaceString(line, "@RETURN@", type);
            xml = xml + line + "\n";
        }
        return new NamedXmlSnippet(attrName, xml);
    }

    /**
     * Write attributes.
     * @param attributes A list of attributes
     * @throws IOException If a problem writing output
     */
    public void writeAttributes(List attributes) throws IOException
    {
        Collections.sort(attributes);
        for (Iterator iterator = attributes.iterator(); iterator.hasNext();)
        {
            NamedXmlSnippet attribute = (NamedXmlSnippet) iterator.next();
            m_output.write(attribute.getXml());
        }
    }


    /**
     * Write the operations headers
     * @throws IOException If a problem writing output
     */
    public void writeOperationsHeader() throws IOException
    {
        for (int i = 0; i < OPERATIONS_HEADER.length; i++)
        {
            m_output.write(OPERATIONS_HEADER[i] + "\n");
        }
    }

    /**
     * Write the operation headers
     * @param operName The attribute name
     * @param description The description
     * @param type The type
     * @throws IOException If a problem writing output
     */
    public String makeOperationHeader(String operName, String description, String type)
            throws IOException
    {
        String xml = "";
        for (int i = 0; i < OPERATION_HEADER.length; i++)
        {
            String line = OPERATION_HEADER[i];
            line = replaceString(line, "@NAME@", operName);
            line = replaceString(line, "@DESCRIPTION@", description);
            line = replaceString(line, "@RETURN@", type);
            xml = xml + line + "\n";
        }
        return xml;
    }

    /**
     * Write the operation footer
     * @throws IOException If a problem writing output
     */
    public String makeOperationFooter() throws IOException
    {
        String xml = "";
        for (int i = 0; i < OPERATION_FOOTER.length; i++)
        {
            xml = xml + OPERATION_FOOTER[i] + "\n";
        }
        return xml;
    }

    /**
     * Make a parameter for an operation
     * @param paramName The attribute name
     * @param description The description
     * @param type The type
     * @throws IOException If a problem writing output
     */
    public String makeOperationParameter(String paramName, String description, String type)
            throws IOException
    {
        String xml = "";
        for (int i = 0; i < PARAMETER.length; i++)
        {
            String line = PARAMETER[i];
            line = replaceString(line, "@NAME@", paramName);
            line = replaceString(line, "@DESCRIPTION@", description);
            line = replaceString(line, "@TYPE@", type);
            xml = xml + line + "\n";
        }
        return xml;
    }

    /**
     * Write operations
     * @param operations A list of operations
     * @throws IOException If a problem writing output
     */
    public void writeOperations(List operations) throws IOException
    {
        Collections.sort(operations);
        for (Iterator iterator = operations.iterator(); iterator.hasNext();)
        {
            NamedXmlSnippet operation = (NamedXmlSnippet) iterator.next();
            m_output.write(operation.getXml());
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
