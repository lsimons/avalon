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

package org.apache.avalon.phoenix.containerkit.demo;

import java.util.ArrayList;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData;
import org.apache.avalon.phoenix.containerkit.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.containerkit.metadata.MetaDataBuilder;
import org.apache.avalon.phoenix.containerkit.metadata.PartitionMetaData;
import org.xml.sax.InputSource;

/**
 * Load metadata from some source. The source is usually
 * one or more xml config files.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003/03/22 12:07:11 $
 */
public class SimpleMetaDataBuilder
    implements MetaDataBuilder
{
    public static final String CONFIG_LOCATION = "simple:location";

    public PartitionMetaData buildAssembly( final Map parameters )
        throws Exception
    {
        final String location = (String)parameters.get( CONFIG_LOCATION );
        final ComponentMetaData[] components = loadMetaData( location );
        return new PartitionMetaData( "main", new String[ 0 ],
                                      new PartitionMetaData[ 0 ],
                                      components,
                                      Attribute.EMPTY_SET );
    }

    private ComponentMetaData[] loadMetaData( final String location )
        throws Exception
    {
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        final InputSource input = new InputSource( location );

        final Configuration configuration = builder.build( input );
        final Configuration[] children =
            configuration.getChildren( "component" );
        return loadComponentDatas( children );
    }

    private ComponentMetaData[] loadComponentDatas( final Configuration[] components )
        throws Exception
    {
        final ArrayList profiles = new ArrayList();

        for( int i = 0; i < components.length; i++ )
        {
            final ComponentMetaData component =
                loadComponentData( components[ i ] );
            profiles.add( component );
        }

        return (ComponentMetaData[])profiles.toArray( new ComponentMetaData[ profiles.size() ] );
    }

    private ComponentMetaData loadComponentData( final Configuration component )
        throws Exception
    {
        final String name = component.getAttribute( "name" );
        final String impl = component.getAttribute( "impl" );
        final Configuration config = component.getChild( "config" );
        final DependencyMetaData[] dependencies =
            parseAssociations( component.getChildren( "provide" ) );

        return new ComponentMetaData( name, impl, dependencies, null, config, null );
    }

    private DependencyMetaData[] parseAssociations( final Configuration[] provides )
        throws ConfigurationException
    {
        final ArrayList associations = new ArrayList();
        for( int i = 0; i < provides.length; i++ )
        {
            final Configuration provide = provides[ i ];
            final String key = provide.getAttribute( "key" );
            final String provider = provide.getAttribute( "provider" );
            final DependencyMetaData association =
                new DependencyMetaData( key, provider, key, Attribute.EMPTY_SET );
            associations.add( association );
        }
        return (DependencyMetaData[])associations.toArray( new DependencyMetaData[ associations.size() ] );
    }
}
