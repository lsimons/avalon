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

package org.apache.avalon.composition.util;

import java.io.File;
import java.net.URL;

/**
 * General utilities supporting the packaging of string containing file references.
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 */
public class StringHelper
{
    private static final String USER_DIR = getUserDir();
    private static final String MERLIN_HOME = getMerlinHome();
    private static final String MERLIN_REPO_LOCAL = getRepositoryLocal();

   /**
    * Parse a supplied string for patterns matching ${user.dir}, ${merlin.home}, 
    * and ${merlin.repository.local} and return the string using the symbolic 
    * representation.
    *
    * @param name the string to parse
    * @return the parsed string
    */
    public static String toString( String name )
    {
        if( name == null ) return "";
        String str = name.replace( '\\', '/' );

        if( str.indexOf( USER_DIR ) > -1 )
        {
            str = getString( str, USER_DIR, "${user.dir}" );
        }

        if( ( MERLIN_HOME != null ) && str.indexOf( MERLIN_HOME ) > -1 )
        {
            str = getString( str, MERLIN_HOME, "${merlin.home}" );
        }
        else if( ( MERLIN_REPO_LOCAL != null ) && str.indexOf( MERLIN_REPO_LOCAL ) > -1 )
        {
            str = getString( str, MERLIN_REPO_LOCAL, "${merlin.repository.local}" );
        }

        return str;
    }

   /**
    * Parse a supplied file for patterns matching ${user.dir}, ${merlin.home}, 
    * and ${merlin.repository.local} and return the string using the symbolic 
    * representation.
    *
    * @param file the file to parse
    * @return the parsed string
    */
   public static String toString( File file )
    {
        if( file == null ) return "";
        return toString( file.toString() );
    }

   /**
    * Parse a supplied url for patterns matching ${user.dir}, ${merlin.home}, 
    * and ${merlin.repository.local} and return the string using the symbolic 
    * representation.
    *
    * @param url the url to parse
    * @return the parsed string
    */
    public static String toString( URL url )
    {
        if( url == null ) return "";
        return toString( url.toString() );
    }

   /**
    * Parse a supplied url sequence for patterns matching ${user.dir}, ${merlin.home}, 
    * and ${merlin.repository.local} and return a string using the symbolic 
    * representation.
    *
    * @param urls the urls to parse
    * @return the parsed string
    */
    public static String toString( URL[] urls )
    {
        StringBuffer buffer = new StringBuffer();
        for( int i=0; i<urls.length; i++ )
        {
            if( i > 0 ) buffer.append( ";" );
            buffer.append( toString( urls[i] ) );
        }
        return buffer.toString();
    }

   /**
    * Parse a supplied string sequence for patterns matching ${user.dir}, ${merlin.home}, 
    * and ${merlin.repository.local} and return a string using the symbolic 
    * representation.
    *
    * @param names the urls to parse
    * @return the parsed string
    */
    public static String toString( String[] names )
    {
        StringBuffer buffer = new StringBuffer();
        for( int i=0; i<names.length; i++ )
        {
            if( i > 0 ) buffer.append( ";" );
            buffer.append( toString( names[i] ) );
        }
        return buffer.toString();
    }

    private static String getString( String name, String pattern, String replacement )
    {
        final int n = name.indexOf( pattern );
        if( n == -1 ) return name;
        if( name.startsWith( pattern ) )
        {
            return replacement + name.substring( pattern.length() );
        }
        else
        {
            String header = name.substring( 0, n );
            String tail = name.substring( n + pattern.length() );
            return header + replacement + tail; 
        }
    }

    private static String getUserDir()
    {
         return System.getProperty( "user.dir" ).replace( '\\', '/' );
    }

    private static String getMerlinHome()
    {
         String home = System.getProperty( "merlin.home" );
         if( home == null ) return null;
         return home.replace( '\\', '/' );
    }

    private static String getRepositoryLocal()
    {
         String local = System.getProperty( "merlin.repository.local" );
         if( local== null ) return null;
         return local.replace( '\\', '/' );
    }


}
