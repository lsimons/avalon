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

package org.apache.avalon.tools.model;

import org.apache.tools.ant.BuildException;

/**
 * Project info.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Info 
{
    public static final String PROTOCOL = "artifact";

    public static Info create( final String id )
    {
        final int i = id.indexOf( ":" );
        if( i<0 )
        {
            final String error =
              "Missing protocol in id [" + id + "]";
            throw new BuildException( error );
        }
        final String protocol = id.substring( 0, i );
        final String spec = id.substring( i+1 );
        return Info.create( protocol, spec);
    }

    public static Info create( final String type, final String id )
    {
        final int n = getGroupIndex( id );
        final String group = getGroupFromId( id, n );
        final String name = getNameFromId( id, n );
        final String version = getVersionFromId( id );
        return new Info( group, name, version, type );
    }

    private String m_name;
    private String m_group;
    private String m_version;
    private String m_type;

    public Info( final String group, final String name, final String version, final String type )
    {
        assertNotNull( "group", group );
        assertNotNull( "name", name );

        m_group = group;
        m_name = name;
        m_version = version;
        m_type = type;
    }

    public String getGroup()
    {
        return m_group;
    }
    
    public String getName()
    {
        return m_name;
    }

    public String getVersion()
    {
        return m_version;
    }

    public String getType()
    {
        if( null == m_type )
        {   
            return "jar";
        }
        else
        {
            return m_type;
        } 
    }

    public String getPath()
    {
        final StringBuffer buffer = new StringBuffer( getGroup() );
        buffer.append( "/" );
        buffer.append( getType() );
        buffer.append( "s/" );
        buffer.append( getName() );
        if( null != getVersion() )
        {
            buffer.append( "-" );
            buffer.append( getVersion() );
        }
        buffer.append( "." );
        buffer.append( getType() );
        return buffer.toString();
    }

    public String getURI()
    {
        final StringBuffer buffer = new StringBuffer( PROTOCOL );
        buffer.append( ":" );
        buffer.append( getType() );
        buffer.append( ":" );
        buffer.append( getGroup() );
        buffer.append( "/" );
        buffer.append( getName() );
        if( null != getVersion() )
        {
            buffer.append( "#" );
            buffer.append( getVersion() );
        }
        return buffer.toString();
    }

    public String getSpec()
    {
        final StringBuffer buffer = new StringBuffer( getGroup() );
        buffer.append( "/" );
        buffer.append( getName() );
        if( null != getVersion() )
        {
            buffer.append( "#" );
            buffer.append( getVersion() );
        }
        return buffer.toString();
    }

    public String toString()
    {
        return getURI();
    }

    public boolean equals( final Object other )
    {
        if( other instanceof Info )
        {
            final Info info = (Info) other;
            if( !getName().equals( info.getName() ) ) return false;
            if( !getGroup().equals( info.getGroup() ) ) return false;
            if( !getType().equals( info.getType() ) ) return false;
            if( null == m_version ) 
            {
                return ( null == info.getVersion() );
            }
            else
            {
                return m_version.equals( info.getVersion() );
            }
        }
        else
        {
            return false;
        }
    }

    private void assertNotNull( final String key, final Object object )
    {
        if( null == object ) throw new NullPointerException( key );
    }

    private static int getGroupIndex( final String id )
    {
        final int n = id.lastIndexOf( "/" );
        if( n < 0 )
        {
            final String error = 
              "Invalid resource identifier \"" + id + "\". "
              + "A resource identifier must be in for for [group]/[name]#[version]";
            throw new BuildException( error );
        }
        else
        {
            return n;
        }
    }

    private static String getGroupFromId( final String id, final int n )
    {
        return id.substring( 0, n );
    }

    private static String getNameFromId( final String id, final int n )
    {
        final int j = id.indexOf( "#" );
        if( j < 0 )
        {
            return id.substring( n+1 );
        }
        else
        {
            return id.substring( n+1, j );
        }
    }

    private static String getVersionFromId( final String id )
    {
        final int j = id.indexOf( "#" );
        if( j < 0 )
        {
            return null;
        }
        else
        {
            return id.substring( j+1 );
        }
    }

}
