/*
 * 
 * ============================================================================
 * The Apache Software License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Jakarta", "Apache
 * Avalon", "Avalon Framework" and "Apache Software Foundation" must not be
 * used to endorse or promote products derived from this software without prior
 * written permission. For written permission, please contact
 * apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 */
package org.apache.avalon.ide.eclipse.core.xmlmodel;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.alias.ElementMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class AttributeContainerConverter implements Converter
{

    /**
	 * @uml property=classMapper associationEnd={multiplicity={(1 1)}}
	 */
    private ClassMapper classMapper;
    private ElementMapper elementMapper;

    public AttributeContainerConverter(ClassMapper classMapper, ElementMapper elementMapper)
    {
        this.classMapper = classMapper;
        this.elementMapper = elementMapper;
    }
    public boolean canConvert(Class type)
    {
        return AttributeContainer.class.isAssignableFrom(type);
    }

    public void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup)
    {
        String[] fieldNames = objectGraph.fieldNames();
        //        circularityTracker.track(objectGraph.get());
        for (int i = 0; i < fieldNames.length; i++)
        {
            String fieldName = fieldNames[i];

            objectGraph.push(fieldName);

            if (objectGraph.get() != null)
            {
                writeFieldAsXML(
                    xmlWriter,
                    elementMapper.toXml(fieldName),
                    objectGraph,
                    converterLookup);
            }

            objectGraph.pop();
        }
    }

    private void writeFieldAsXML(
        XMLWriter xmlWriter,
        String fieldName,
        ObjectTree objectGraph,
        ConverterLookup converterLookup)
    {
        xmlWriter.startElement(fieldName);

        writeClassAttributeInXMLIfNotDefaultImplementation(objectGraph, xmlWriter);
        Converter converter = converterLookup.lookupConverterForType(objectGraph.type());
        converter.toXML(objectGraph, xmlWriter, converterLookup);

        xmlWriter.endElement();
    }

    protected void writeClassAttributeInXMLIfNotDefaultImplementation(
        ObjectTree objectGraph,
        XMLWriter xmlWriter)
    {
        Class actualType = objectGraph.get().getClass();
        Class defaultType = classMapper.lookupDefaultType(objectGraph.type());
        if (!actualType.equals(defaultType))
        {
            xmlWriter.addAttribute("class", classMapper.lookupName(actualType));
        }
    }

    public void fromXML(
        ObjectTree objectGraph,
        XMLReader xmlReader,
        ConverterLookup converterLookup,
        Class requiredType)
    {
        objectGraph.create(requiredType);
        String[] fieldNames = objectGraph.fieldNames();
        for (int i = 0; i < fieldNames.length; i++)
        {
            String fieldName = fieldNames[i];
            if (xmlReader.attribute(fieldName) != null)
            {
                objectGraph.push(fieldName);
                objectGraph.set(xmlReader.attribute(fieldName));
                objectGraph.pop();
                //xmlReader.pop();
            } else
            {
                while (xmlReader.nextChild())
                {
                    objectGraph.push(elementMapper.fromXml(xmlReader.name()));

                    Class type = determineWhichImplementationToUse(xmlReader, objectGraph);
                    Converter converter = converterLookup.lookupConverterForType(type);
                    converter.fromXML(objectGraph, xmlReader, converterLookup, type);
                    objectGraph.pop();

                    xmlReader.pop();
                }
            }
        }
    }

    private Class determineWhichImplementationToUse(
        XMLReader xmlReader,
        final ObjectTree objectGraph)
    {
        String classAttribute = xmlReader.attribute("class");
        Class type;
        if (classAttribute == null)
        {
            type = objectGraph.type();
        } else
        {
            type = classMapper.lookupType(classAttribute);
        }
        return type;
    }
}
