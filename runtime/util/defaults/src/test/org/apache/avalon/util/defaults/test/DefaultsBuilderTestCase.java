/*
 * Copyright 2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.util.defaults.test;

import java.util.Properties;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase ;

import org.apache.avalon.util.defaults.DefaultsBuilder;

/**
 * DefaultsBuilderTestCase
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $
 */
public class DefaultsBuilderTestCase extends TestCase
{
    private static final String KEY = "test";

    private DefaultsBuilder m_builder;

    protected void setUp() throws Exception
    {
        File base = new File( System.getProperty( "basedir" ) );
        m_builder = new DefaultsBuilder( KEY, base );
    }

    public void testHomeDirectory() throws Exception
    {
        System.out.println( "inst: " + m_builder.getHomeDirectory() );
    }

    public void testHomeProperties() throws Exception
    {
        System.out.println( "home: " + m_builder.getHomeProperties() );
    }

    public void testUserProperties() throws Exception
    {
        System.out.println( "user: " + m_builder.getUserProperties() );
    }

    public void testDirProperties() throws Exception
    {
        System.out.println( "dir: " + m_builder.getDirProperties() );
    }

    public void testConsolidatedProperties() throws Exception
    {
        File base = new File( System.getProperty( "basedir" ) );
        File props = new File( base, "test.keys" );
        Properties properties = DefaultsBuilder.getProperties( props );
        String[] keys = (String[]) properties.keySet().toArray( new String[0] );
        Properties defaults = 
          DefaultsBuilder.getProperties(
            DefaultsBuilderTestCase.class.getClassLoader(),
            "static.properties" );
        System.out.println( 
          "con: " 
          + m_builder.getConsolidatedProperties( defaults, keys ) );
    }

}
