/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.excalibur.configuration.merged.test;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.excalibur.configuration.ConfigurationUtil;
import org.apache.excalibur.configuration.merged.ConfigurationMerger;
import org.apache.excalibur.configuration.merged.ConfigurationSplitter;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class ConfigurationMergerTestCase extends TestCase
{
    public ConfigurationMergerTestCase()
    {
        this( "Configuration merger and branching test" );
    }

    public ConfigurationMergerTestCase( String s )
    {
        super( s );
    }

    public void testAttributeOnlyMerge() throws Exception
    {
        DefaultConfiguration result = new DefaultConfiguration( "a", "b" );
        result.setAttribute( "a", "1" );

        DefaultConfiguration base = new DefaultConfiguration( "a", "b" );
        base.setAttribute( "a", "2" );

        DefaultConfiguration layer = new DefaultConfiguration( "a", "b" );
        layer.setAttribute( "a", "1" );

        assertTrue( ConfigurationUtil.equals( result, ConfigurationMerger.merge( layer, base ) ) );
        assertTrue( ConfigurationUtil.equals( layer, ConfigurationSplitter.split( result, base ) ));
    }

    public void testAddChild() throws Exception
    {
        DefaultConfiguration result = new DefaultConfiguration( "a", "b" );
        result.addChild( new DefaultConfiguration( "kid1", "b" ) );
        result.addChild( new DefaultConfiguration( "kid2", "b" ) );

        DefaultConfiguration base = new DefaultConfiguration( "a", "b" );
        base.addChild( new DefaultConfiguration( "kid1", "b" ) );

        DefaultConfiguration layer = new DefaultConfiguration( "a", "b" );
        layer.addChild( new DefaultConfiguration( "kid2", "b" ) );

        assertTrue( ConfigurationUtil.equals( result, ConfigurationMerger.merge( layer, base ) ) );
        assertTrue( ConfigurationUtil.equals( layer, ConfigurationSplitter.split( result, base ) ));
    }

    public void testOverrideChild() throws Exception
    {
        DefaultConfiguration result = new DefaultConfiguration( "a", "b" );
        DefaultConfiguration rkid1 = new DefaultConfiguration( "kid1", "b" );
        rkid1.setAttribute( "test", "1" );
        result.addChild( rkid1 );

        DefaultConfiguration base = new DefaultConfiguration( "a", "b" );
        DefaultConfiguration bkid1 = new DefaultConfiguration( "kid1", "b" );
        bkid1.setAttribute( "test", "0" );
        base.addChild( bkid1 );

        DefaultConfiguration layer = new DefaultConfiguration( "a", "b" );
        DefaultConfiguration lkid1 = new DefaultConfiguration( "kid1", "b" );
        lkid1.setAttribute( "test", "1" );
        layer.addChild( lkid1 );

        assertTrue( !ConfigurationUtil.equals( result, ConfigurationMerger.merge( layer, base ) ) );

        lkid1.setAttribute( "excalibur-configuration:merge", "true" );

        assertTrue( ConfigurationUtil.equals( result, ConfigurationMerger.merge( layer, base ) ) );
        assertTrue( ConfigurationUtil.equals( layer, ConfigurationSplitter.split( result, base ) ) );
    }
}
