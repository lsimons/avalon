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

import junit.framework.Assert;
import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.phoenix.framework.info.ComponentDescriptor;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;
import org.apache.avalon.phoenix.framework.info.ContextDescriptor;
import org.apache.avalon.phoenix.framework.info.DependencyDescriptor;
import org.apache.avalon.phoenix.framework.info.EntryDescriptor;
import org.apache.avalon.phoenix.framework.info.LoggerDescriptor;
import org.apache.avalon.phoenix.framework.info.SchemaDescriptor;
import org.apache.avalon.phoenix.framework.info.ServiceDescriptor;

/**
 * A set of utilities for asserting  facts about info objects.
 *
 * @author Peter Donald
 * @version $Revision: 1.5 $ $Date: 2003/12/05 15:14:39 $
 */
public class InfoAssert
{
    public static void assertEqualStructure( final String message,
                                             final ComponentInfo expected,
                                             final ComponentInfo actual )
    {
        final ComponentDescriptor expectedComponent = expected.getDescriptor();
        final ComponentDescriptor actualComponent = actual.getDescriptor();
        assertEqualAttributes( message + ": Component.attribute",
                               expectedComponent.getAttributes(),
                               actualComponent.getAttributes() );

        assertEqualFeatures( message, expected, actual );
    }

    public static void assertEqualInfos( final String message,
                                         final ComponentInfo expected,
                                         final ComponentInfo actual )
    {
        final ComponentDescriptor expectedComponent = expected.getDescriptor();
        final ComponentDescriptor actualComponent = actual.getDescriptor();
        assertEqualComponents( message, expectedComponent, actualComponent );

        assertEqualFeatures( message, expected, actual );
    }

    public static void assertEqualFeatures( final String message,
                                            final ComponentInfo expected,
                                            final ComponentInfo actual )
    {
        final LoggerDescriptor[] expectedLoggers = expected.getLoggers();
        final LoggerDescriptor[] actualLoggers = actual.getLoggers();
        assertEqualLoggers( message, expectedLoggers, actualLoggers );

        final SchemaDescriptor expectedSchema = expected.getConfigurationSchema();
        final SchemaDescriptor actualSchema = actual.getConfigurationSchema();
        assertEqualSchema( message + "/Configuration", expectedSchema, actualSchema );

        final SchemaDescriptor expectedPSchema = expected.getParametersSchema();
        final SchemaDescriptor actualPSchema = actual.getParametersSchema();
        assertEqualSchema( message + "/Parameters", expectedPSchema, actualPSchema );

        final ContextDescriptor expectedContext = expected.getContext();
        final ContextDescriptor actualContext = actual.getContext();
        assertEqualContext( message, expectedContext, actualContext );

        final ServiceDescriptor[] expectedServices = expected.getServices();
        final ServiceDescriptor[] actualServices = actual.getServices();
        assertEqualServices( message, expectedServices, actualServices );

        final DependencyDescriptor[] expectedDeps = expected.getDependencies();
        final DependencyDescriptor[] actualDeps = actual.getDependencies();
        assertEqualDeps( message, expectedDeps, actualDeps );
    }

    private static void assertEqualSchema( final String message,
                                           final SchemaDescriptor expected,
                                           final SchemaDescriptor actual )
    {
        if( null == expected && null == actual )
        {
            return;
        }
        else if( null == expected )
        {
            Assert.fail( "Null expected but non-null actual" );
        }
        else if( null == actual )
        {
            Assert.fail( "Null actual but non-null expected" );
        }

        Assert.assertEquals( message + ": Schema.type",
                             expected.getType(),
                             actual.getType() );

        Assert.assertEquals( message + ": Schema.type",
                             expected.getType(),
                             actual.getType() );
        Assert.assertEquals( message + ": Schema.location",
                             expected.getLocation(),
                             actual.getLocation() );
    }

    public static void assertEqualDeps( final String message,
                                        final DependencyDescriptor[] expected,
                                        final DependencyDescriptor[] actual )
    {
        Assert.assertEquals( message + ": Dependencys.length", expected.length, actual.length );
        for( int i = 0; i < expected.length; i++ )
        {
            Assert.assertEquals( message + ": Dependencys[ " + i + "].service",
                                 expected[ i ].getType(),
                                 actual[ i ].getType() );
            Assert.assertEquals( message + ": Dependencys[ " + i + "].key",
                                 expected[ i ].getKey(),
                                 actual[ i ].getKey() );
            assertEqualAttributes( message + ": Dependencys[ " + i + "].attributes",
                                   expected[ i ].getAttributes(),
                                   actual[ i ].getAttributes() );
        }
    }

    public static void assertEqualServices( final String message,
                                            final ServiceDescriptor[] expected,
                                            final ServiceDescriptor[] actual )
    {
        Assert.assertEquals( message + ": Services.length", expected.length, actual.length );
        for( int i = 0; i < expected.length; i++ )
        {
            final String prefix = message + ": Services[ " + i + "]";
            final ServiceDescriptor expectedService = expected[ i ];
            final ServiceDescriptor actualService = actual[ i ];
            assertEqualService( prefix, expectedService, actualService );
        }
    }

    private static void assertEqualService( final String message,
                                            final ServiceDescriptor expected,
                                            final ServiceDescriptor actual )
    {
        Assert.assertEquals( message + ".type",
                             expected.getType(),
                             actual.getType() );
        assertEqualAttributes( message + ".attributes",
                               expected.getAttributes(),
                               actual.getAttributes() );
    }

    public static void assertEqualLoggers( final String message,
                                           final LoggerDescriptor[] expected,
                                           final LoggerDescriptor[] actual )
    {
        Assert.assertEquals( message + ": Loggers.length", expected.length, actual.length );
        for( int i = 0; i < expected.length; i++ )
        {
            Assert.assertEquals( message + ": Loggers[ " + i + "].name",
                                 expected[ i ].getName(), actual[ i ].getName() );
            assertEqualAttributes( message + ": Loggers[ " + i + "].attributes",
                                   expected[ i ].getAttributes(), actual[ i ].getAttributes() );
        }
    }

    public static void assertEqualContext( final String message,
                                           final ContextDescriptor expected,
                                           final ContextDescriptor actual )
    {
        Assert.assertEquals( message + ": Context.type", expected.getType(), actual.getType() );
        assertEqualEntrys( message + ": Context.entrys", expected.getEntrys(), expected.getEntrys() );
        assertEqualAttributes( message + ": Context.attribute",
                               expected.getAttributes(),
                               actual.getAttributes() );
    }

    public static void assertEqualEntrys( final String message,
                                          final EntryDescriptor[] expected,
                                          final EntryDescriptor[] actual )
    {
        Assert.assertEquals( message + " Length", expected.length, actual.length );
        for( int i = 0; i < expected.length; i++ )
        {
            Assert.assertEquals( message + " [" + i + "].key",
                                 expected[ i ].getKey(), actual[ i ].getKey() );
            Assert.assertEquals( message + " [" + i + "].type",
                                 expected[ i ].getType(), actual[ i ].getType() );
            assertEqualAttributes( message + " [" + i + "].attribute",
                                   expected[ i ].getAttributes(),
                                   actual[ i ].getAttributes() );
        }
    }

    public static void assertEqualComponents( final String message,
                                              final ComponentDescriptor expected,
                                              final ComponentDescriptor actual )
    {
        Assert.assertEquals( message + ": Component.type", expected.getImplementationKey(),
                             actual.getImplementationKey() );
        assertEqualAttributes( message + ": Component.attribute",
                               expected.getAttributes(),
                               actual.getAttributes() );
    }

    public static void assertEqualParameters( final String message,
                                              final Attribute expected,
                                              final Attribute actual )
    {
        final String[] expectedNames = expected.getParameterNames();
        final String[] actualNames = actual.getParameterNames();
        Assert.assertEquals( message + " Length", expectedNames.length, actualNames.length );

        for( int i = 0; i < expectedNames.length; i++ )
        {
            final String name = expectedNames[ i ];
            Assert.assertEquals( message + " value",
                                 expected.getParameter( name ),
                                 actual.getParameter( name ) );
        }
    }

    protected static void assertEqualAttributes( final String message,
                                                 final Attribute[] expected,
                                                 final Attribute[] actual )
    {
        Assert.assertEquals( message + " Length", expected.length, actual.length );
        for( int i = 0; i < expected.length; i++ )
        {
            Assert.assertEquals( message + " [" + i + "].name",
                                 expected[ i ].getName(), actual[ i ].getName() );
            assertEqualParameters( message + " [" + i + "].parameters",
                                   expected[ i ], actual[ i ] );
        }
    }
}
