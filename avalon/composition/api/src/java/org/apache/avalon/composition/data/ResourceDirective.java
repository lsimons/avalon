/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.composition.data;

import java.io.Serializable;


/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:11 $
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
