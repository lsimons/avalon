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
package org.apache.avalon.phoenix.components.configuration.test;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.avalon.phoenix.components.configuration.merger.ConfigurationMerger;
import org.apache.avalon.phoenix.components.configuration.merger.ConfigurationSplitter;

/**
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
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
