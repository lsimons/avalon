/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.framework.tools.infobuilder.test;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import junit.framework.TestCase;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.phoenix.framework.info.ComponentDescriptor;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;
import org.apache.avalon.phoenix.framework.info.ContextDescriptor;
import org.apache.avalon.phoenix.framework.info.DependencyDescriptor;
import org.apache.avalon.phoenix.framework.info.EntryDescriptor;
import org.apache.avalon.phoenix.framework.info.LoggerDescriptor;
import org.apache.avalon.phoenix.framework.info.SchemaDescriptor;
import org.apache.avalon.phoenix.framework.info.ServiceDescriptor;
import org.apache.avalon.phoenix.framework.tools.infobuilder.InfoBuilder;
import org.apache.avalon.phoenix.framework.tools.infobuilder.InfoReader;
import org.apache.avalon.phoenix.framework.tools.infobuilder.InfoWriter;
import org.apache.avalon.phoenix.framework.tools.infobuilder.LegacyBlockInfoReader;
import org.apache.avalon.phoenix.framework.tools.infobuilder.LegacyBlockInfoWriter;
import org.apache.avalon.phoenix.framework.tools.infobuilder.SerializedInfoReader;
import org.apache.avalon.phoenix.framework.tools.infobuilder.SerializedInfoWriter;
import org.apache.avalon.phoenix.framework.tools.infobuilder.XMLInfoReader;
import org.apache.avalon.phoenix.framework.tools.infobuilder.XMLInfoWriter;
import org.apache.avalon.phoenix.framework.tools.qdox.DefaultInfoBuilder;
import org.apache.avalon.phoenix.framework.tools.qdox.LegacyInfoBuilder;

/**
 * Abstract class which TestCases can extend.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003/04/05 04:25:44 $
 */
public class InfoBuilderTestCase
    extends TestCase
{
    private static final String BASE_PACKAGE =
        "org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.";

    private static final String BASE_DIR = '/' + BASE_PACKAGE.replace( '.', '/' );

    private static final String COMPONENT1 = BASE_PACKAGE + "component1";
    private static final String COMPONENT2 = BASE_PACKAGE + "component2";
    private static final String COMPONENT3 = BASE_PACKAGE + "component3";
    private static final String COMPONENT4 = BASE_PACKAGE + "component4";

    private static final String SOURCE1 = BASE_DIR + "QDoxComponent1.java";
    private static final String SOURCE1_INFO = BASE_PACKAGE + "QDoxComponent1";

    private static final String LSOURCE1 = BASE_DIR + "QDoxLegacyComponent1.java";
    private static final String LSOURCE1_INFO = BASE_PACKAGE + "QDoxLegacyComponent1";

    public InfoBuilderTestCase( String name )
    {
        super( name );
    }

    public void testLoadXMLComponent1()
        throws Exception
    {
        final ComponentInfo actual = loadComponentInfo( COMPONENT1 );
        final ComponentInfo expected = createDummyComponentInfo();

        InfoAssert.assertEqualInfos( COMPONENT1 + " should be equal to constructed actual",
                                     expected,
                                     actual );
    }

    public void testLoadLegacyComponent()
        throws Exception
    {
        final ComponentInfo actual = loadComponentInfo( COMPONENT2 );
        final ComponentInfo expected = loadComponentInfo( COMPONENT3 );

        InfoAssert.assertEqualStructure( COMPONENT2 + " should be identical to " + COMPONENT3,
                                         expected,
                                         actual );
    }

    public void testLoadParametersComponent()
        throws Exception
    {
        final ComponentInfo actual = loadComponentInfo( COMPONENT4 );
        final ComponentInfo expected = createComponentInfoWithParameters();

        InfoAssert.assertEqualStructure( COMPONENT4 + " should be identical to " + COMPONENT4,
                                         expected,
                                         actual );
    }

    private ComponentInfo createComponentInfoWithParameters()
    {
        final ComponentDescriptor component =
            new ComponentDescriptor( "org.realityforge.Component1", Attribute.EMPTY_SET );

        final SchemaDescriptor schema =
            new SchemaDescriptor( "",
                                  "",
                                  Attribute.EMPTY_SET );

        return new ComponentInfo( component,
                                  ServiceDescriptor.EMPTY_SET,
                                  LoggerDescriptor.EMPTY_SET,
                                  ContextDescriptor.EMPTY_CONTEXT,
                                  DependencyDescriptor.EMPTY_SET,
                                  null,
                                  schema );
    }

    public void testWriteSerComponent1()
        throws Exception
    {
        runWriteReadTest( createDummyComponentInfo(),
                          new SerializedInfoWriter(),
                          new SerializedInfoReader() );
    }

    public void testWriteXMLComponent1()
        throws Exception
    {
        runWriteReadTest( createDummyComponentInfo(),
                          new XMLInfoWriter(),
                          new XMLInfoReader() );
    }

    public void testWriteLegacyXMLComponent1()
        throws Exception
    {
        final ComponentInfo info = loadComponentInfo( COMPONENT2 );
        runWriteReadTest( info,
                          new LegacyBlockInfoWriter(),
                          new LegacyBlockInfoReader() );
    }

    public void testQDoxScan()
        throws Exception
    {
        final ComponentInfo expected = loadComponentInfo( SOURCE1_INFO );
        final JavaClass javaClass = loadJavaSource( SOURCE1 );
        final DefaultInfoBuilder infoBuilder = new DefaultInfoBuilder();
        final ComponentInfo actual = infoBuilder.buildComponentInfo( javaClass );

        InfoAssert.assertEqualInfos( " ComponentInfo generated from source file",
                                     expected,
                                     actual );
    }

    public void testLegacyQDoxScan()
        throws Exception
    {
        final ComponentInfo expected = loadComponentInfo( LSOURCE1_INFO );
        final JavaClass javaClass = loadJavaSource( LSOURCE1 );
        final LegacyInfoBuilder infoBuilder = new LegacyInfoBuilder();
        final ComponentInfo actual = infoBuilder.buildComponentInfo( javaClass );

        InfoAssert.assertEqualInfos( " ComponentInfo generated from source file",
                                     expected,
                                     actual );
    }

    private JavaClass loadJavaSource( final String resource )
    {
        final JavaDocBuilder builder = new JavaDocBuilder();
        final InputStream inputStream = getClass().getResourceAsStream( resource );
        assertNotNull( "resource " + resource + " not null", inputStream );
        final Reader reader = new InputStreamReader( inputStream );
        builder.addSource( reader );

        final JavaSource[] sources = builder.getSources();
        assertEquals( "sources.length", 1, sources.length );
        final JavaSource source = sources[ 0 ];
        final JavaClass[] classes = source.getClasses();
        assertEquals( "source.getClasses()", 1, classes.length );
        return classes[ 0 ];
    }

    private void runWriteReadTest( final ComponentInfo expected,
                                   final InfoWriter writer,
                                   final InfoReader reader )
        throws Exception
    {
        ContainerUtil.enableLogging( writer, new ConsoleLogger() );
        final File output = File.createTempFile( "info-test", ".xml" );
        final FileOutputStream outputStream = new FileOutputStream( output );
        writer.writeComponentInfo( expected, outputStream );
        outputStream.close();

        ContainerUtil.enableLogging( reader, new ConsoleLogger() );
        final String implementationKey = expected.getDescriptor().getImplementationKey();
        final FileInputStream inputStream = new FileInputStream( output );
        final ComponentInfo actual = reader.createComponentInfo( implementationKey, inputStream );
        inputStream.close();
        //output.deleteOnExit();
        //output.delete();

        InfoAssert.assertEqualInfos( " Dummy ComponentInfo written out and read back " +
                                     "in again should be equal",
                                     expected,
                                     actual );
    }

    private ComponentInfo createDummyComponentInfo()
    {
        final ComponentDescriptor component =
            new ComponentDescriptor( "org.realityforge.Component1", Attribute.EMPTY_SET );

        final LoggerDescriptor logger1 = new LoggerDescriptor( "", Attribute.EMPTY_SET );
        final LoggerDescriptor logger2 = new LoggerDescriptor( "audit", Attribute.EMPTY_SET );
        final LoggerDescriptor[] loggers = new LoggerDescriptor[]{logger1, logger2};

        final EntryDescriptor entry1 = new EntryDescriptor( "mbean",
                                                            "javax.jmx.MBeanServer",
                                                            false,
                                                            Attribute.EMPTY_SET );

        final EntryDescriptor[] entrys = new EntryDescriptor[]{entry1};
        final ContextDescriptor context =
            new ContextDescriptor( "org.apache.avalon.phoenix.BlockContext",
                                   entrys,
                                   Attribute.EMPTY_SET );

        final ServiceDescriptor service1 = createServiceDescriptor();

        final ServiceDescriptor[] services = new ServiceDescriptor[]{service1};
        final DependencyDescriptor dependency1 =
            new DependencyDescriptor( "org.realityforge.Service2",
                                      "org.realityforge.Service2",
                                      true,
                                      Attribute.EMPTY_SET );
        final DependencyDescriptor dependency2 =
            new DependencyDescriptor( "foo",
                                      "org.realityforge.Service3",
                                      false,
                                      Attribute.EMPTY_SET );
        final DependencyDescriptor[] deps =
            new DependencyDescriptor[]{dependency1, dependency2};

        final SchemaDescriptor schema =
            new SchemaDescriptor( "",
                                  "http://relaxng.org/ns/structure/1.0",
                                  Attribute.EMPTY_SET );

        return new ComponentInfo( component, services, loggers,
                                  context, deps, schema, null );
    }

    private ServiceDescriptor createServiceDescriptor()
    {
        final Properties parameters = new Properties();
        parameters.setProperty( "display-name", "Special Service" );
        parameters.setProperty( "description-key", "service1.desc" );
        final Attribute attribute = new Attribute( "doc", parameters );

        final Attribute[] attributes = new Attribute[]{attribute};
        return new ServiceDescriptor( "org.realityforge.Service1", attributes );
    }

    protected ComponentInfo loadComponentInfo( final String classname )
        throws Exception
    {
        final ClassLoader classLoader = getClass().getClassLoader();
        return createInfoBuilder().buildComponentInfo( classname, classLoader );
    }

    private InfoBuilder createInfoBuilder()
    {
        final InfoBuilder builder = new InfoBuilder();
        builder.enableLogging( new ConsoleLogger() );
        return builder;
    }
}
