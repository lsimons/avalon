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

package org.apache.avalon.composition.data;

import java.io.Serializable;


/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/24 23:25:24 $
 */
public class ResourceDirective implements Serializable
{

    /**
     * Group identifier.
     */
    private final String m_group;

    /**
     * The name identifier.
     */
    private final String m_name;

    /**
     * The version identifier.
     */
    private final String m_version;

    /**
     * The type identifier.
     */
    private final String m_type;

    /**
     * Creation of a new resource directive.
     * @param group the artifact group
     * @param name the artifact name
     * @param version the artifact version
     */
    public ResourceDirective( 
      final String group, final String name, final String version )
    {
        this( group, name, version, "jar" );
    }

    /**
     * Creation of a new resource directive.
     * @param group the artifact group
     * @param name the artifact name
     * @param version the artifact version
     */
    public ResourceDirective( 
      final String group, final String name, final String version, final String type )
    {
        if( group == null )
        {
            throw new NullPointerException( "group" );
        }
        if( name == null )
        {
            throw new NullPointerException( "name" );
        }
        if( type == null )
        {
            throw new NullPointerException( "type" );
        }

        m_group = group;
        m_name = name;
        m_version = version;
        m_type = type;
    }

    /**
     * Return the composite identifier.
     * @return the identifier
     */
    public String getId()
    {
        return m_group + ":" + m_name;
    }

    /**
     * Return the artifact name
     * @return the artifact name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the group of the artifact.
     * @return the artifact group
     */
    public String getGroup()
    {
        return m_group;
    }

    /**
     * Return the version of the artifact.
     * @return the artifact version
     */
    public String getVersion()
    {
        return m_version;
    }

    /**
     * Return the type of the artifact.
     * @return the artifact type
     */
    public String getType()
    {
        return m_type;
    }

    /**
     * Creation of a new resource directive.
     * @param id the artifact id
     * @param version the artifact version
     */
    public static ResourceDirective createResourceDirective( 
      final String id, final String version )
    {
        return createResourceDirective( id, version, "jar" );
    }

    /**
     * Creation of a new resource directive.
     * @param id the artifact id
     * @param version the artifact version
     */
    public static ResourceDirective createResourceDirective( 
      final String id, final String version, final String type )
    {
        if( id == null )
        {
            throw new NullPointerException( "id" );
        }
        if( type == null )
        {
            throw new NullPointerException( "type" );
        }

        String group = null; 
        String name = null; 
        int n = id.indexOf( ":" );
        if( id.indexOf( ":" ) > 0 )
        {
            group = id.substring( 0, n );
            name = id.substring( n+1, id.length() );
        }
        else
        {
            group = id;
            name = id;
        }
        return new ResourceDirective( group, name, version, type );
    }

}
