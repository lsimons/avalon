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

package org.apache.metro.transit;

import java.io.Serializable;

/**
 * Defintion of a artifact that maintains a logical identifier
 * of a resource together with a set of assigned properties.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Artifact.java 30977 2004-07-30 08:57:54Z niclas $
 */
public final class Artifact implements Serializable, Comparable
{
    // ------------------------------------------------------------------------
    // static
    // ------------------------------------------------------------------------

   /**
    * Group separator character.
    */
    public static final String SEP = "/";

   /**
    * Standard artifact protocol name.
    */
    public static final String PROTOCOL = "artifact";

   /**
    * Token used to separate a type from the group.
    */
    public static final String TOKEN = ":";

   /**
    * URI fragment token.
    */
    public static final String REF = "#";

   /**
    * URI query token.
    */
    public static final String QUERY = "?";

   /**
    * Lookup key used to resolve an artifact group attribute.
    */
    public static final String GROUP_KEY = "avalon.artifact.group";

   /**
    * Lookup key used to resolve an artifact name attribute.
    */
    public static final String NAME_KEY = "avalon.artifact.name";

   /**
    * Lookup key used to resolve an artifact version attribute.
    */
    public static final String VERSION_KEY = "avalon.artifact.version";

   /**
    * Lookup key used to resolve an artifact type attribute.
    */
    public static final String TYPE_KEY = "avalon.artifact.type";

   /**
    * The "artifact:" pattern.
    */
    private static final String HEAD = PROTOCOL + TOKEN;

   /**
    * The default jar type.
    */
    private static final String JAR = "jar";

    // ------------------------------------------------------------------------
    // static operations
    // ------------------------------------------------------------------------

   /**
    * Creation of a new artifact instance using a supplied uri specification. An 
    * artifact uri contains the protocol identifier, an optional type, a group 
    * designator, a name, and an optional version identifier.
    * <p>The following represent valid artifact uri examples:</p>
    *
    * <ul>
    * <li>artifact:jar:metro/cache/metro-cache-main#1.0.0</li>
    * <li>artifact:metro/cache/metro-cache-main#1.0.0</li>
    * <li>artifact:metro/cache/metro-cache-main</li>
    * </ul>
    *
    * @param uri the artifact uri
    * @return the new artifact
    * @exception NullPointerException if the supplied uri is null
    * @exception IllegalArgumentException if either the specification does not commence with
    *   the 'artifact' protocol identifier, or, the artifact spec does not contain
    *   a group identifier.
    */
    public static final Artifact createArtifact( String uri ) 
      throws IllegalArgumentException, NullPointerException
    {
        return new Artifact( uri );
    }

   /**
    * Creation of a new artifact instance using a supplied group, name,
    * version using the default "jar" type.
    *
    * @param group the artifact group identifier
    * @param name the artifact name
    * @param version the version
    * @return the new artifact
    * @exception NullPointerException if the supplied group or name are null
    */
    public static Artifact createArtifact( 
      String group, String name, String version ) 
      throws NullPointerException
    {
        return createArtifact( group, name, version, null );
    }

   /**
    * Creation of a new artifact instance using a supplied group, name,
    * version and type arguments.
    *
    * @param group the artifact group identifier
    * @param name the artifact name
    * @param version the version
    * @param type the type
    * @return the new artifact
    * @exception NullPointerException if the supplied group or name are null
    */
    public static Artifact createArtifact( 
      String group, String name, String version, String type ) 
      throws NullPointerException
    {
        return new Artifact( group, name, version, type );
    }

    // ------------------------------------------------------------------------
    // state
    // ------------------------------------------------------------------------

   /**
    * The artifact group.
    */
    private final String m_group;

   /**
    * The artifact name.
    */
    private final String m_name;

   /**
    * The artifact version.
    */
    private final String m_version;

   /**
    * The artifact type.
    */
    private final String m_type;

   /**
    * The artifact uri spec.
    */
    private final String m_uri;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

   /**
    * Creation of a new Artifact using a supplied uri.
    * @param uri a uri of the form artifact:[type]:[group]/[name]#[version]
    * @exception IllegalArgumentException if the supplied uri does not 
    *    declare the 'artifact:' protocol
    */
    private Artifact( String uri ) throws IllegalArgumentException
    {
        if( null == uri )
        {
            throw new NullPointerException( "uri" );
        }

        if( !uri.startsWith( HEAD ) )
        {
            final String error = 
              "Supplied uri [" + uri + "] does not declare the 'artifact:' protocol";
            throw new IllegalArgumentException( error );
        }

        //
        // get the string without the schmeme
        //

        String remainder = uri.substring( HEAD.length() );

        if( remainder.indexOf( "//" ) > -1 )
        {
            final String error = 
              "Invalid character sequence '//' in uri ["
              + uri + "].";
            throw new IllegalArgumentException( error );
        }

        //
        // the last item in the uri is the fragment - retrieve this and 
        // continue with a string without the fragement
        //

        int fragmentIndex = remainder.indexOf( REF );
        if( fragmentIndex > -1 )
        {
            String version = remainder.substring( fragmentIndex + 1 );
            m_version = getVersionValue( version );
            remainder = remainder.substring( 0, fragmentIndex );
        }
        else
        {
            m_version = null;
        }

        //
        // the remainder is the combination of type, group and name
        //

        int lastSeparatorIndex = remainder.lastIndexOf( SEP );
        if( lastSeparatorIndex > -1 )
        {
            m_name = remainder.substring( lastSeparatorIndex + 1 );
            remainder = remainder.substring( 0, lastSeparatorIndex );
        }
        else
        {
            final String error = 
              "Supplied artifact specification ["
              + uri + "] does not contain a group.";
            throw new IllegalArgumentException( error );
        }

        //
        // the remainder now contains the type and the group
        //

        int typeIndex = remainder.indexOf( TOKEN );
        if( typeIndex > -1 )
        {
            String type = remainder.substring( 0, typeIndex );
            String group = remainder.substring( typeIndex + 1 );
            m_group = parseGroupValue( group );
            m_type = getValue( type, JAR );
        }
        else
        {
            m_group = parseGroupValue( remainder );
            m_type = JAR;
        }

        //
        // construct the immutable string representation of the artifact
        //

        m_uri = getExternalForm();
    }

    /**
     * Internal creation of a new artifact.
     *
     * @param group the artifact group 
     * @param name the artifact name
     * @param version the artifact version
     * @param type the artifact type
     */
    private Artifact( 
      final String group, final String name, final String version, final String type ) 
    {
        if( null == group )
        {
            throw new NullPointerException( "group" );
        }
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }

        m_group = parseGroupValue( group );
        m_name = name;
        m_version = getVersionValue( version );
        m_type = getValue( type, JAR );
        m_uri = getExternalForm();
    }

    // ------------------------------------------------------------------------
    // implementation
    // ------------------------------------------------------------------------

   /**
    * Return the group identifier for the artifact.  The group identifier
    * is composed of a sequence of named separated by the '/' character.
    *
    * @return the group identifier
    */
    public final String getGroup()
    {
        return m_group;
    }

   /**
    * Return the name of the artifact.  
    *
    * @return the artifact name
    */
    public final String getName()
    {
        return m_name;
    }

   /**
    * Return the type of the artifact.  Unless declared during artifact
    * creation, the value returned will default to "jar".
    *
    * @return the artifact type
    */
    public final String getType()
    {
        return m_type;
    }

   /**
    * Return the posssibly null version identifier.  The value of the version 
    * is an opaque string.
    * @return the artifact version
    */
    public final String getVersion()
    {
        return m_version;
    }

   /**
    * Return the base path for this artifact.  The base path is derived from 
    * the artifact group and type.  For an artifact group of "metro/cache" and a 
    * type equal to "jar", the base value will be translated using the pattern 
    * "[group]/[type]s" to form "metro/cache/jars".  The base path value represents
    * the directory path relative to a repository root of the directory containing
    * this artifact.
    *
    * @return the base path
    */ 
    public final String getBase()
    {
        return getGroup() + SEP + getType() + "s";
    }

   /**
    * Returns the full path of the artifact relative to a logical root directory.
    * The full path is equivalent to the base path and artifact filename using the 
    * pattern "[base]/[filename]".  Path values may be used to resolve an artifact 
    * from a remote repository or local cache relative to the repository or cache 
    * root. An artifact such as <code>artifact:jar:metro/cache/metro-cache-main#1.0.0</code>
    * would return the path <code>metro/cache/jars/metro-cache-main-1.0.0.jar</code>.
    * 
    * @see #getBase
    * @see #getFilename
    * @return the logical artifact path
    */
    public final String getPath()
    {
        return getBase() + SEP + getFilename();
    }

   /**
    * Return the expanded filename of the artifact. The filename is expressed 
    * as [name]-[version].[type] or in case of a null version simply [name].[type].
    *
    * @return the artifact expanded filename
    */
    public String getFilename()
    {
        if( null == m_version )
        {
            return m_name + "." + m_type;
        }
        else
        {
            return m_name + "-" + m_version + "." + m_type;
        }
    }

    /**
     * Gets the string representation of a url to the artifact given a base URL for a 
     * remote repository.  The value return will match the the [host]/[path] pattern.
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

        if( host.endsWith( SEP ) )
        {
            return host + getPath();
        }
        else
        {
            return host + SEP + getPath();
        }
    }

    // ------------------------------------------------------------------------
    // Comparable
    // ------------------------------------------------------------------------

   /**
    * Compare this artifact with another artifact.  Artifact comparisom is
    * based on a comparisom of the string representation of the artifact with 
    * the string representation of the supplied object.
    *
    * @param object the object to compare with this instance 
    * @return the comparative order of the supplied object relative to this 
    *   artifact
    * @exception NullPointerException if the supplied object is null
    */
    public int compareTo( Object object ) throws NullPointerException
    {
        if( null == object ) 
        {
            throw new NullPointerException( "object" );
        }

        String name = this.toString();
        return name.compareTo( object.toString() );
    }

    // ------------------------------------------------------------------------
    // Object
    // ------------------------------------------------------------------------

   /**
    * Return a string representation of the artifact.
    * @return the artifact as a uri
    */
    public String toString()
    {
         return m_uri;
    }

   /**
    * Compare this artifact with the supplied object for equality.  This method 
    * will return true if the supplied object is an Artifact and has an equal
    * uri.
    * 
    * @param other the object to compare with this instance 
    * @return TRUE if this artifact is equal to the supplied object
    */
    public boolean equals( Object other )
    {
         if( null == other )
         {
              return false;
         }
         else if( this == other )
         {
              return true;
         }
         else if( other instanceof Artifact )
         {
              return toString().equals( ( (Artifact) other).toString() );
         }
         else
         {
              return false;
         }
    }

    // ------------------------------------------------------------------------
    // private
    // ------------------------------------------------------------------------

   /**
    * Return the external form of the artifact.
    * @return the artifact uri
    */
    private String getExternalForm()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( HEAD );
        buffer.append( getType() );
        buffer.append( TOKEN );
        buffer.append( getGroup() );
        buffer.append( SEP );
        buffer.append( getName() );
        if( null != getVersion() )
        {
            buffer.append( REF );
            buffer.append( getVersion() );
        }
        return buffer.toString();
    }

    private String getValue( String str )
    {
        return getValue( str, null );
    }

    private String getValue( String str, String fallback )
    {
        if( null == str )
        {
            return fallback;
        }
        if( str.length() == 0 )
        {
            return fallback;
        }
        return str;
    }

    private String getVersionValue( String str )
    {
        String version = getValue( str );
        if( null != version )
        {
            if( version.indexOf( SEP ) > -1 )
            {
                final String error = 
                  "Version identifier may not contain the forward slash character.";
                throw new IllegalArgumentException( error );
            }
        }
        return version;
    }

    private String parseGroupValue( String group )
    {
        String value = group.trim();
        if( group.startsWith( SEP ) )
        {
            return parseGroupValue( value.substring( 1 ) );
        }
        if( group.endsWith( SEP ) )
        {
            return parseGroupValue( value.substring( 0, value.length() - 1 ) );
        }
        if( value.length() > 0 )
        {
            return value;
        }
        else
        {
            final String error = 
              "Invalid group value [" + group + "].";
            throw new IllegalArgumentException( error );
        }
    }

    private boolean equalType( String type )
    {
        if( null == m_type ) 
        {
             return ( null == type );
        }
        else
        {
             return m_type.equals( type );
        }
    }

    private boolean equalVersion( String version )
    {
        if( null == m_version ) 
        {
             return ( null == version );
        }
        else
        {
             return m_version.equals( version );
        }
    }
}

