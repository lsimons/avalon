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

package org.apache.avalon.repository ;


import java.io.Serializable ;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Defintion of a artifact that maintains a relative url
 * to some nominally remote file together with a set of assigned
 * properties.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $
 */
public class Artifact implements Serializable
{
    // ------------------------------------------------------------------------
    // static
    // ------------------------------------------------------------------------

    public static final String SEP = "/";

    public static final String GROUP_KEY = "avalon.artifact.group";
    public static final String NAME_KEY = "avalon.artifact.name";
    public static final String VERSION_KEY = "avalon.artifact.version";
    public static final String TYPE_KEY = "avalon.artifact.type";

    public static Artifact createArtifact( String spec )
    {
        if ( null == spec ) throw new NullPointerException( "spec" );
        
        String version = getVersion( spec );
        String group = getGroup( spec );
        String name = getName( spec );

        return createArtifact( group, name, version );
    }

    public static Artifact createArtifact( 
      String group, String name, String version )
    {
        return createArtifact( group, name, version, "jar" );
    }

    public static Artifact createArtifact( 
      String group, String name, String version, String type )
    {
        if( group == null ) throw new NullPointerException( "group" );
        if( name == null ) throw new NullPointerException( "name" );

        return new Artifact( group, name, version, type );
    }

    // ------------------------------------------------------------------------
    // state
    // ------------------------------------------------------------------------

   /** 
    * the artifact base path 
    */
    private final String m_base;

   /** 
    * the name of the the artifact relative to the base 
    */
    private final String m_filename;

   /** 
    * the computed path
    */
    private final String m_path;

   /** 
    * The artifact group. 
    */
    private final String m_group;

   /** 
    * The artifact name. 
    */
    private final String m_name;

   /** 
    * The artifact type.
    */
    private final String m_type;

   /** 
    * The artifact version.
    */
    private final String m_version;


    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------
    
    /**
     * Creation of a new classified artifact.
     *
     * @param group the artifact group 
     * @param name the artifact name
     * @param type the artifact type
     * @param version the artifact version
     */
    private Artifact( 
      final String group, final String name, 
      final String version, final String type ) 
    {
        m_group = group;
        m_name = name;
        m_version = version;
        m_type = type;

        String base = createBase( group, type );
        m_base = getCleanPath( base );

        String filename = createFilename( name, version, type );
        if( filename.indexOf( SEP ) > 0 )
        {
            final String error = 
              "Invalid name - illegal character '/' in filename: " + filename;
            throw new IllegalArgumentException( error );
        }

        m_filename = filename;
        m_path = m_base + SEP + m_filename;
    }

    /**
     * Gets the relative artifact URL.
     * 
     * @return the relative url
     */
    public String getURL()
    {
        return getURL( null );
    }

    /**
     * Gets the URL to the artifact given a base URL for a 
     * remote repository.
     * 
     * @param host the base repository URL
     * @return the full URL to the artifact
     */
    public String getURL( final String host )
    {
        if( null == host )
        {
            return getURL( "" );
        }
        else
        {
            if( host.endsWith( SEP ) )
            {
                return host + getPath();
            }
            else
            {
                return host + SEP + getPath();
            }
        }
    }

    
    // ------------------------------------------------------------------------
    // accessors
    // ------------------------------------------------------------------------

    public String getGroup()
    {
        return m_group;
    }

    public String getName()
    {
        return m_name;
    }

    public String getType()
    {
        return m_type;
    }

    public String getVersion()
    {
        return m_version;
    }

    /**
     * Gets the artifact specification for this Artifact
     * in the form <group>[:<name>][;<version>].
     * 
     * @return the artifact specification
     */
    public String getSpecification()
    {
        final String group = getGroup();
        final String name = getName();

        StringBuffer buffer = new StringBuffer() ;
        buffer.append( getGroup() ) ;
        if( !name.equals( group ) )
        {
            buffer.append( ':' ) ;
            buffer.append( name ) ;
        }

        String version = getVersion();
        if( version != null )
        {
            buffer.append( ';' ) ;
            buffer.append( version ) ;
        }

        //String type = getType();
        //if( null != type && !type.equals( "jar" ) )
        //{
        //    buffer.append( '#' ) ;
        //    buffer.append( type ) ;
        //}

        return buffer.toString() ;
    }

    /**
     * Return the base path to the artifact. This is equivelent to the 
     * a logic directory path without a leading or trailing seperator.
     *
     * @return the base path.
     */
    public String getBase()
    {
        return m_base;
    }
    
    /**
     * Return the filename of the artifact.
     * @return the name.
     */
    public String getFilename()
    {
        return m_filename ;
    }
    
    /**
     * Gets the artifact path.  The value returned is equal to 
     * the base path, seperator and filename.
     *
     * @return the artifact path
     */
    public String getPath()
    {
        return m_path;
    }

   /**
    * Return a stringified representation of the instance.
    * @return the string representation
    */
    public String toString()
    {
        if( null != getType() )
        {
            return "[" + getType() + ": " + getSpecification() + "]";
        }
        else
        {
            return "[artifact: " + getSpecification() + "]";
        }
    }

    // ------------------------------------------------------------------------
    // static private
    // ------------------------------------------------------------------------

    private static String createBase( String group, String type )
    {
        if( type == null ) return group;
        return group + Artifact.SEP + type + "s";
    }

    private static String createFilename( String name, String version, String type )
    {
        if( name == null ) throw new NullPointerException( "name" );

        StringBuffer buffer = new StringBuffer( name );
        if( version != null )
        {
            buffer.append( "-" );
            buffer.append( version );
        }
        if( type != null )
        {
            buffer.append( "." );
            buffer.append( type );
        }
        return buffer.toString();
    }

    private static String getGroup( String spec )
    {
        int semiColon = spec.indexOf( ';' ) ;
        if ( -1 == semiColon )
        {
            int colon = spec.indexOf( ':' ) ;
            if( -1 == colon ) return spec;
            return spec.substring( 0, colon ); 
        }
        else
        {
            return getGroup( spec.substring( 0, semiColon-1 ) );
        }
    }

    private static String getName( String spec )
    {
        int semiColon = spec.indexOf( ';' ) ;
        if ( -1 == semiColon )
        {
            int colon = spec.indexOf( ':' ) ;
            if( -1 == colon ) return spec;
            return spec.substring( colon+1, spec.length() ); 
        }
        else
        {
            return getName( spec.substring( 0, semiColon ) );
        }
    }

    private static String getVersion( String spec )
    {
        int semiColon = spec.indexOf( ';' ) ;
        if ( -1 == semiColon )
        {
            return null;
        }
        else
        {
            return spec.substring( semiColon+1, spec.length() );
        }
    }

   /**
    * Remove leading and trailing seperators.
    * @param the path value to clean
    * @return the clean path
    */
    private String getCleanPath( final String path )
    {
        if( path.startsWith( SEP ) ) return getCleanPath( path.substring( 1, path.length() ) );
        if( path.endsWith( SEP ) ) return getCleanPath( path.substring( 0, path.length() -1 ) );
        return path;
    }

}

