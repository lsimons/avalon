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

import com.thoughtworks.xstream.alias.DefaultClassMapper;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.alias.DefaultElementMapper;
import com.thoughtworks.xstream.alias.ElementMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.basic.*;
import com.thoughtworks.xstream.converters.collections.ArrayConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.composite.ObjectWithFieldsConverter;
import com.thoughtworks.xstream.converters.lookup.DefaultConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.objecttree.reflection.ObjectFactory;
import com.thoughtworks.xstream.objecttree.reflection.ReflectionObjectGraph;
import com.thoughtworks.xstream.objecttree.reflection.SunReflectionObjectFactory;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLReaderDriver;
import com.thoughtworks.xstream.xml.XMLWriter;
import com.thoughtworks.xstream.xml.dom.DomXMLReaderDriver;
import com.thoughtworks.xstream.xml.text.PrettyPrintXMLWriter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class XStream {

    private ConverterLookup converterLookup = new DefaultConverterLookup();
    private XMLReaderDriver xmlReaderDriver = new DomXMLReaderDriver();
    private ClassMapper classMapper;
    private ObjectFactory objectFactory;

    public XStream() {
        this(new SunReflectionObjectFactory(), new DefaultClassMapper(), new DefaultElementMapper());
    }

    public XStream(ObjectFactory objectFactory, ClassMapper classMapper, ElementMapper elementMapper) {
        this.classMapper = classMapper;
        this.objectFactory = objectFactory;

        alias("int", Integer.class);
        alias("float", Float.class);
        alias("double", Double.class);
        alias("long", Long.class);
        alias("short", Short.class);
        alias("char", Character.class);
        alias("byte", Byte.class);
        alias("boolean", Boolean.class);
        alias("number", Number.class);
        alias("object", Object.class);

        alias("string-buffer", StringBuffer.class);
        alias("string", String.class);
        alias("java-class", Class.class);
        alias("date", Date.class);

        alias("map", Map.class, HashMap.class);
        alias("list", List.class, ArrayList.class);
        alias("set", Set.class, HashSet.class);

        alias("linked-list", LinkedList.class);
        alias("tree-map", TreeMap.class);
        alias("tree-set", TreeSet.class);

        registerConverter(new ObjectWithFieldsConverter(classMapper,elementMapper));
        // added to work with attributes. MerlinDeveloper
        registerConverter(new AttributeContainerConverter(classMapper,elementMapper));        

        registerConverter(new IntConverter());
        registerConverter(new FloatConverter());
        registerConverter(new DoubleConverter());
        registerConverter(new LongConverter());
        registerConverter(new ShortConverter());
        registerConverter(new CharConverter());
        registerConverter(new BooleanConverter());
        registerConverter(new ByteConverter());

        registerConverter(new StringConverter());
        registerConverter(new StringBufferConverter());
        registerConverter(new DateConverter());
        registerConverter(new JavaClassConverter());

        registerConverter(new ArrayConverter(classMapper));
        registerConverter(new CollectionConverter(classMapper));
        registerConverter(new MapConverter(classMapper));

    }

    public void alias(String elementName, Class type, Class defaultImplementation) {
        classMapper.alias(elementName, type, defaultImplementation);
    }

    public void alias(String elementName, Class type) {
        alias(elementName, type, type);
    }

    public String toXML(Object obj) {
        Writer stringWriter = new StringWriter();
        XMLWriter xmlWriter = new PrettyPrintXMLWriter(stringWriter);
        toXML(obj, xmlWriter);
        return stringWriter.toString();
    }

    public void toXML(Object obj, XMLWriter xmlWriter) {
        ObjectTree objectGraph = new ReflectionObjectGraph(obj, objectFactory);
        Converter rootConverter = converterLookup.lookupConverterForType(obj.getClass());
        xmlWriter.startElement(classMapper.lookupName(obj.getClass()));
        rootConverter.toXML(objectGraph, xmlWriter, converterLookup);
        xmlWriter.endElement();
    }

    public Object fromXML(String xml) {
        return fromXML(xmlReaderDriver.createReader(xml));
    }

    public Object fromXML(XMLReader xmlReader) {
        Class type = classMapper.lookupType(xmlReader.name());
        ObjectFactory objectFactory = new SunReflectionObjectFactory();
        ObjectTree objectGraph = new ReflectionObjectGraph(type, objectFactory);
        Converter rootConverter = converterLookup.lookupConverterForType(type);
        rootConverter.fromXML(objectGraph, xmlReader, converterLookup, type);
        return objectGraph.get();
    }

    public void registerConverter(Converter converter) {
        converterLookup.registerConverter(converter);
    }

}
