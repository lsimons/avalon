/*
   
      Copyright 2004. The Apache Software Foundation.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. 
   
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
