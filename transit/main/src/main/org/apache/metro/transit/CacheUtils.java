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


import java.net.URL;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Enumeration;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;


/**
 * Various static utility methods used throughout repository related programing 
 * interfaces.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
final class CacheUtils
{
    private CacheUtils()
    {
    }

    /** meta extension tag for meta-data containing artifacts */
    public static final String META = "meta";
    
    /**
     * Transforms a Properties into a Attributes using a simple enumeration 
     * convention for property names which appends a numeric enumeration name
     * component to the dotted property key.  Note that changes to the 
     * Attributes object do not have any effect on the Properties object and 
     * vice versa.  All values are copied.
     * 
     * @param properties the properties to be transformed
     * @return the Attributes representing the properties
     */
    public static Attributes getAsAttributes( Properties properties )
    {
        if( null == properties )
        {
            throw new NullPointerException( "properties" );
        }

        Attributes attributes = new BasicAttributes( false );
        Enumeration enumeration = properties.propertyNames();
        
        while ( enumeration .hasMoreElements() )
        {
            String key = (String) enumeration .nextElement();
            
            if( isEnumerated( key ) )
            {
                String base = getEnumeratedBase( key );
                Attribute attribute = attributes.get( base );
                
                if( null == attribute )
                {
                    attribute = new BasicAttribute( base, false );
                }
                
                attribute.add( properties.getProperty( key ) );
                attributes.put( attribute );
            }
            else 
            {
                attributes.put( key, properties.getProperty( key ) );
            }
        }
        
        return attributes;
    }

    /**
     * Gets the Attribues in a remote artifact.
     * 
     * @param repositories the reprositories to search against
     * @param artifact the artifact to load meta data from
     * @return the meta data as attributes
     * @throws CacheException if there is execution failure
     */
    public static Attributes getAttributes( 
      String [] repositories, Artifact artifact ) 
      throws CacheException
    {
        return getAsAttributes( getProperties( repositories, artifact ) );
    }
    
    /**
     * Gets the Attribues from the cache.
     * 
     * @param cache the reprository cache
     * @param artifact the artifact to load meta data from
     * @return the meta data as attributes
     * @throws CacheException if there is execution failure
     */
    public static Attributes getAttributes( 
      File cache, Artifact artifact ) 
      throws CacheException
    {
        return getAsAttributes( getProperties( cache, artifact ) );
    }

    /**
     * Gets the Properties in the local cache.
     * 
     * @param cache the local cache
     * @param artifact the artifact to load meta data from
     * @return the loaded properties 
     * @throws CacheException if there is any problem loading the 
     *    properties
     */
    public static Properties getProperties( 
      File cache, Artifact artifact ) 
      throws CacheException
    {
        File local = new File( cache, artifact.getPath() + "." + META );
        if( !local.exists() )
        {
            final String error = "Cannot load metadata due to missing resource.";
            Throwable cause = new FileNotFoundException( local.toString() );
            throw new CacheException( error, cause );
        }

        try
        {
            Properties properties = new Properties();
            InputStream input = new FileInputStream( local );
            properties.load( input );
            if( input != null )
            {
                input.close();
            }
            return properties;
        }
        catch( Throwable e )
        {
            final String error = 
              "Unexpected error while attempting to load properties from local meta: "
              + local.toString();
            throw new CacheException( error, e );
        }
    }

    /**
     * Gets the Properties in a remote properties file.
     * 
     * @param repositories the reprositories to search against
     * @param artifact the artifact to load meta data from
     * @return the loaded properties 
     * @throws CacheException if there is any problem loading the 
     *    properties
     */
    public static Properties getProperties( 
      String [] repositories, Artifact artifact ) 
      throws CacheException
    {
        if( null == repositories )
        {
            throw new NullPointerException( "repositories" );
        }
        if( null == artifact )
        {
            throw new NullPointerException( "artifact" );
        }

        Throwable throwable = null;
        Properties props = null;

        for ( int ii = 0; ii < repositories.length; ii++ )
        {
            StringBuffer buf = new StringBuffer();
            buf.append( artifact.getURL( repositories[ii] ) );
            buf.append( "." );
            buf.append( META );

            try
            {
                URL url = new URL( buf.toString() );
                props = getProperties( url );
                return props;
            }
            catch ( Throwable e )
            {
                throwable = e;
            }
        }

        StringBuffer repos = new StringBuffer();
        for ( int ii = 0; ii < repositories.length; ii++ )
        {
            repos.append( repositories[ii] ).append( ',' );
        }

        throw new CacheException( 
            "None of the repositories [" 
            + repos.toString() 
            + "] contained the metadata properties for "
            + artifact, throwable );
    }
    
    /**
     * Gets the Properties in a remote properties file.
     * 
     * @param url the url to the properties file
     * @return the loaded properties for the file
     * @throws IOException indicating a IO error during property loading
     */
    public static Properties getProperties( URL url ) throws IOException
    {
        Properties props = new Properties();
        InputStream input  = url.openStream();
        props.load( input );
        if( input != null )
        {
            input.close();
        }
        return props;
    }
    
    /**
     * Detects whether or not a property key is of the multivalued enumeration 
     * kind.  A multivalued key simply enumerates values by appending a '.' and
     * a number after the dot: i.e. artifact.dependency.2 and artifact.alias.23
     * et. cetera.
     * 
     * @param key the property name or key
     * @return true if the property conforms to the enumerated property 
     * convention, false otherwise
     */
    public static boolean isEnumerated( String key )
    {
        int lastDot = key.lastIndexOf( '.' );
        String lastComponent = null;
        
        if( -1 == lastDot )
        {
            return false;
        }
    
        lastComponent = key.substring( lastDot + 1 );
        
        // names like .123 are not really considered enumerated without a base
        if( key.equals( key.substring( lastDot ) ) )
        {
            return false;
        }
        
        try 
        {
            Integer.parseInt( lastComponent );
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
        
        return true;
    }

    
    /**
     * Gets the key base of an enumerated property using the multivalued 
     * property key naming convention.
     * 
     * @param key the enumerated key whose last name component is a number
     * @return the base name of the enumerated property
     */
    public static String getEnumeratedBase( String key )
    {
        if( null == key )
        {
            return null;
        }
        
        if( !isEnumerated( key ) )
        {
            return key;
        }
        
        int lastDot = key.lastIndexOf( '.' );
        String base = null;
        
        if( -1 == lastDot )
        {
            return key;
        }
    
        return key.substring( 0, lastDot );
    }

   /**
    * Returns a delimited strain as an array of elements.
    * @param delim the token delimiter
    * @param substrate the delimited string
    * @return the elements in an array
    */
    public static String [] getDelimited( char delim, String substrate )
    {
        int start = 0, end = 0;
        ArrayList enumeration  = new ArrayList();
        
        if( null == substrate || substrate.equals( "" ) )
        {
            return null;
        }

        while ( end < substrate.length() )
        {
            end = substrate.indexOf( ',', start );
            
            if( -1 == end )
            {
                end = substrate.length();
                enumeration .add( substrate.substring( start, end ) );
                break;
            }

            enumeration .add( substrate.substring( start, end ) );
            start = end + 1;
        } 
        
        return (String[]) enumeration .toArray( new String[0] ); 
    }

   /**
    * Convert a set of host path statements to formal urls.
    * @param hosts the set of host names
    * @return the equivalent URL array
    */
    public static URL[] convertToURLs( String[] hosts )
    {
        ArrayList list = new ArrayList();
        for ( int i = 0; i < hosts.length; i++ )
        {
            URL url = convertToURL( hosts[i] );
            if( url != null )
            {
                list.add( url );
            }
        }
        return (URL[]) list.toArray( new URL[0] );
    }

   /**
    * Convert a path to a url.
    * @param host the host address
    * @return the equivalent url
    * @exception IllegalArgumentException if the path cannot 
    *   be converted to a url
    */
    public static URL convertToURL( String host )
      throws IllegalArgumentException
    {
        try
        {
            return new URL( host );
        }
        catch( Throwable e )
        {
            final String error = 
             "Unable to convert a supplied host spec to a url: "
             + host;
            throw new IllegalArgumentException( error );
        }
    }

   /**
    * For a given set of host paths, returns an equivalent set 
    * ensuring that the path ends with the "/" character.
    * @param hosts the set of host path names
    * @return the equivalent host path names
    */
    public static String[] getCleanPaths( String[] hosts )
    {
        String[] paths = new String[ hosts.length ];
        for ( int i = 0; i < hosts.length; i++ )
        {
            String path = hosts[i];
            if( !path.endsWith( "/" ) ) 
            {
                paths[i] = path + "/";
            }
            else
            {
                paths[i] = path;
            }
        }
        return paths;
    }

}
