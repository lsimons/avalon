/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.meta.info.test;

import junit.framework.TestCase;
import org.apache.avalon.meta.info.*;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

import java.util.Properties;
import java.io.*;

/**
 * TypeTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public class TypeTestCase extends TestCase
{
    private InfoDescriptor m_descriptor;
    private CategoryDescriptor[] m_loggers;
    private ContextDescriptor m_context;
    private ServiceDescriptor[] m_services;
    private DependencyDescriptor[] m_dependencies;
    private StageDescriptor[] m_stages;
    private ExtensionDescriptor[] m_extensions;
    private Configuration m_defaults;
    private ReferenceDescriptor m_reference;
    private String m_key;

    public TypeTestCase( String name )
    {
        super( name );
    }

    public void setUp()
    {
        m_reference = new ReferenceDescriptor(TypeTestCase.class.getName());
        m_key = TypeTestCase.class.getName();
        m_descriptor = createSimpleInfo(TypeTestCase.class.getName());
        m_loggers = new CategoryDescriptor[] {
            new CategoryDescriptor("name", new Properties())
        };
        m_context = new ContextDescriptor( 
          TypeTestCase.class.getName(), new EntryDescriptor[0]);
        m_services = new ServiceDescriptor[] {
            new ServiceDescriptor(m_reference)
        };
        m_dependencies = new DependencyDescriptor[] {
            new DependencyDescriptor("role", m_reference)
        };
        m_stages = new StageDescriptor[] {
            new StageDescriptor( m_key )
        };
        m_extensions = new ExtensionDescriptor[] {
            new ExtensionDescriptor( m_key )
        };
        m_defaults = new DefaultConfiguration("default");
    }

    private void checkType(Type type)
    {
        assertNotNull(type);
        checkArray(m_loggers, type.getCategories());
        assertEquals( m_defaults, type.getConfiguration() );
        assertEquals( m_context, type.getContext());
        checkArray(m_dependencies, type.getDependencies());
        assertEquals(m_dependencies[0], type.getDependency(m_dependencies[0].getKey()));
        assertEquals(m_extensions[0], type.getExtension( m_stages[0].getKey() ) );
        checkArray(m_extensions, type.getExtensions());
        assertEquals( m_descriptor, type.getInfo() );
        assertEquals( m_services[0], type.getService(m_reference));
        assertEquals( m_services[0], type.getService( m_services[0].getReference().getClassname()));
        checkArray(m_services, type.getServices());
        checkArray(m_stages, type.getStages());
        assertTrue(type.isaCategory(m_loggers[0].getName()));
        assertTrue( !type.isaCategory( "fake name" ) );
    }

    private void checkArray( Object[] orig, Object[] other )
    {
        assertEquals(orig.length, other.length);
        for (int i = 0; i < orig.length; i++)
        {
            assertEquals( orig[i], other[i] );
        }
    }

    public void testType()
    {
        Type type = 
          new Type(
            m_descriptor, m_loggers, m_context, m_services, m_dependencies, 
            m_stages, m_extensions, m_defaults);
        checkType(type);
    }

    public void testSerialization() throws IOException, ClassNotFoundException
    {
        Type type = 
          new Type( 
            m_descriptor, m_loggers, m_context, m_services, m_dependencies, 
            m_stages, m_extensions, m_defaults );

        checkType( type );

        File file = new File( "test.out" );
        ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( file ) );
        oos.writeObject( type );
        oos.close();

        ObjectInputStream ois = new ObjectInputStream( new FileInputStream( file ) );
        Type serialized = (Type) ois.readObject();
        ois.close();
        file.delete();

        checkType( serialized );

        assertEquals( "equality", type, serialized );
        assertEquals( "hashcode", type.hashCode(), serialized.hashCode() );

    }

    private static InfoDescriptor createSimpleInfo( String classname )
    {
        return new InfoDescriptor( null, classname, null, null, null, null, null);
    }
 }