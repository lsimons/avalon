/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metagenerate;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.JavaParameter;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

/**
 * A Mxinfo Factory
 * @author Paul Hammant
 */
public class MxinfoFactory
{

    private JavaClass m_javaClass;
    private File m_destDir;
    private ArrayList m_attributes = new ArrayList();
    private ArrayList m_operations = new ArrayList();
    private MxinfoHelper m_mxinfo;

    /**
     * Construct a factory for a class.
     * @param destDir
     * @param javaClass
     */
    public MxinfoFactory(File destDir, JavaClass javaClass)
    {
        m_javaClass = javaClass;
        m_destDir = destDir;
    }

    /**
     * Generate the m_mxinfo file
     * @throws IOException If a problem writing output
     */
    public void generate() throws IOException
    {
        File file = new File(m_destDir,
                m_javaClass.getFullyQualifiedName().replace('.', File.separatorChar) + ".mxinfo");
        file.getParentFile().mkdirs();
        m_mxinfo = new MxinfoHelper(file);
        m_mxinfo.writeHeader(
                m_javaClass.getTagByName("phoenix:mx-topic").getNamedParameter("name"));
        // m_attributes
        JavaMethod[] methods = m_javaClass.getMethods();
        for (int j = 0; j < methods.length; j++)
        {
            makeAttribute(methods[j], m_mxinfo);
        }
        writeAttributes();
        m_mxinfo.writeOperationsHeader();
        // operations
        methods = m_javaClass.getMethods();
        for (int j = 0; j < methods.length; j++)
        {
            makeOperation(methods[j], m_mxinfo);
        }
        writeOperations();
        m_mxinfo.writeFooter();
        m_mxinfo.close();
    }

    private void writeOperations() throws IOException
    {
        m_mxinfo.writeOperations(m_operations);
    }

    private void makeAttribute(JavaMethod method, MxinfoHelper mxinfo) throws IOException
    {

        DocletTag attribute = method.getTagByName("phoenix:mx-attribute");
        if (attribute != null)
        {
            String attributeName = getName(method.getName());
            DocletTag tag = method.getTagByName("phoenix:mx-description");
            String comment;
            if (tag == null)
            {
                comment = method.getComment();
            }
            else
            {
                comment = tag.getValue();
            }
            Type attributeType = method.getReturns();
            String attributeTypeString =
                    attributeType.getValue() + (attributeType.isArray() ? "[]" : "");

            NamedXmlSnippet attr = mxinfo.makeAttrLines(attributeName,
                    "\"" + comment + "\"",
                    attributeTypeString);
            m_attributes.add(attr);
        }
    }

    private void writeAttributes() throws IOException
    {
        m_mxinfo.writeAttributes(m_attributes);
    }

    private String makeOperation(JavaMethod method, MxinfoHelper mxinfo) throws IOException
    {
        String xml = "";
        DocletTag attribute = method.getTagByName("phoenix:mx-operation");
        if (attribute != null)
        {
            String operationName = method.getName();
            String description = method.getComment();
            Type type = method.getReturns();

            String typeString = type.getValue() + (type.isArray() ? "[]" : "");

            xml = xml + mxinfo.makeOperationHeader(operationName, description, typeString);
            JavaParameter[] params = method.getParameters();
            for (int i = 0; i < params.length; i++)
            {
                xml = xml + makeOperationParameter(params[i], method, mxinfo);

            }
            xml = xml + mxinfo.makeOperationFooter();
            NamedXmlSnippet operation = new NamedXmlSnippet(operationName,xml);
            m_operations.add(operation);
        }
        return xml;
    }

    private String makeOperationParameter(JavaParameter param, JavaMethod method,
                                         MxinfoHelper mxinfo) throws IOException
    {
        String paramName = param.getName();
        DocletTag[] paramTags = method.getTagsByName("param");
        String paramDescription = "";
        for (int k = 0; k < paramTags.length; k++)
        {
            String paramTagValue = paramTags[k].getValue().trim();
            if (paramTagValue.startsWith(paramName))
            {
                paramDescription = paramTagValue.substring(
                        paramTagValue.indexOf(" ") + 1, paramTagValue.length());
            }
        }
        Type paramType = param.getType();
        String paramTypeString = paramType.getValue() + (paramType.isArray() ? "[]" : "");
        return mxinfo.makeOperationParameter(paramName, paramDescription, paramTypeString);
    }

    private String getName(final String name)
    {
        String retval = name;
        if (retval.startsWith("set") || retval.startsWith("get"))
        {
            retval = retval.substring(3, retval.length());
            retval = retval.substring(0, 1).toLowerCase() + retval.substring(1, retval.length());
        }
        else if (retval.startsWith("is"))
        {
            retval = retval.substring(2, retval.length());
            retval = retval.substring(0, 1).toLowerCase() + retval.substring(1, retval.length());
        }
        return retval;
    }
}
