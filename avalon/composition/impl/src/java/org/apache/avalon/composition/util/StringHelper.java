/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
