/* 
 * Copyright 2004 Apache Software Foundation
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

package org.apache.avalon.tools.tasks;

import org.apache.avalon.tools.model.Definition;
import org.apache.avalon.tools.model.Policy;
import org.apache.avalon.tools.model.Resource;
import org.apache.avalon.tools.model.ResourceRef;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Property;

/**
 * Build a set of projects taking into account dependencies within the 
 * supplied fileset. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class PropertyTask extends SystemTask
{
    private String m_key;
    private String m_feature;
    private String m_property;

    public void init()
    {
        if( !isInitialized() )
        {
            super.init();
        }
    }

    public void setKey( final String key )
    {
        m_key = key;
    }

    public void setFeature( final String feature )
    {
        m_feature = feature;
    }

    public void setProperty( final String property )
    {
        m_property = property;
    }

    public void execute() throws BuildException 
    {
        if( null == m_key )
        {
            m_key = getContext().getKey();
        }
        if( null == m_feature )
        {
            log( "Ignoring request due to missing 'feature' attribute." );
            return;
        }
        if( null == m_property )
        {
            log( "Ignoring request due to missing 'property' attribute." );
            return;
        }

        final String value = getFeature();
        if( null != value )
        {
            final Property property = (Property) getProject().createTask( "property" );
            property.init();
            property.setName( m_property );
            property.setValue( value );
            property.setTaskName( getTaskName() );
            property.execute();
        }
        else
        {
            log( "Unrecognized or unsupported feature [" + m_feature + "]." );
        }
    }

    private String getFeature()
    {
        final ResourceRef ref = new ResourceRef( m_key );
        final Resource resource = getHome().getResource( ref );
        if( m_feature.equals( "name" ) )
        {
            return resource.getInfo().getName();
        }
        else if( m_feature.equals( "group" ) )
        {
            return resource.getInfo().getGroup();
        }
        else if( m_feature.equals( "version" ) )
        {
            final String version = resource.getInfo().getVersion();
            if( null == version ) return "";
            return version;
        }
        else if( m_feature.equals( "uri" ) )
        {
            return resource.getInfo().getURI();
        }
        else if( m_feature.equals( "spec" ) )
        {
            return resource.getInfo().getSpec();
        }
        else if( resource instanceof Definition )
        {
            final Definition def = (Definition) resource;
            if( m_feature.equals( "system-classpath-for-windows" ) )
            {
                return getPath( def, true );
            }
            else if( m_feature.equals( "system-classpath-for-unix" ) )
            {
                return getPath( def, false );
            }
        }
        return null;
    }

    private String getPath( final Definition def, final boolean windows )
    {
        final StringBuffer buffer = new StringBuffer();
        final ResourceRef[] refs =
          def.getResourceRefs( getProject(), Policy.RUNTIME, ResourceRef.ANY, true );
        for( int i=0; i<refs.length; i++ )
        {
            if( i>0 )
            {
                buffer.append( ";" );
            }

            final ResourceRef ref = refs[i];
            final Resource resource = getHome().getResource( ref );
            final String path = getNativePath( windows, resource );
            buffer.append( path );
        }

        if( refs.length > 0 )
        {
            buffer.append( ";" );
        }

        buffer.append( getNativePath( windows, def ) ); 
        return buffer.toString();
    }

    private String getNativePath( final boolean windows, final Resource resource )
    {
        final String symbol = getPlatformCacheSymbol( windows );
        final StringBuffer buffer = new StringBuffer( symbol );
        final String path = resource.getInfo().getPath();
        if( windows )
        {
            buffer.append( "\\" );
            buffer.append( path.replace( '/', '\\' ) );
        }
        else
        {
            buffer.append( "/" );
            buffer.append( path );
        }
        return buffer.toString();
    }

    private String getPlatformCacheSymbol( final boolean windows )
    {
        if( windows )
        {
            return "%MAGIC_SCD%";
        }
        else
        {
            return "$MAGIC_SCD";
        } 
    }
    
    public static class Attribute
    {
        private String m_name;
        private String m_value;
       
        public Attribute()
        {
        }

        public Attribute( final String name, final String value )
        {
            m_name = name;
            m_value = value;
        }

        public void setName( final String name )
        {
            m_name = name;
        }

        public void setValue( final String value )
        {
            m_value = value;
        }

        public String getName()
        {
            return m_name;
        }

        public String getValue()
        {
            return m_value;
        }
    }
}
