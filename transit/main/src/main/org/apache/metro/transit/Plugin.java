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
import java.util.NoSuchElementException;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.NamingException;

/**
 * A Plugin declares information about a plugin.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Plugin.java 30977 2004-07-30 08:57:54Z niclas $
 */
public class Plugin implements Serializable
{
    //-----------------------------------------------------------
    // static
    //-----------------------------------------------------------

    public static final String DOMAIN_KEY = "meta.domain";
    public static final String VERSION_KEY = "meta.version";
    public static final String BUILD_KEY = "avalon.artifact.signature";
    public static final String API_KEY = "avalon.artifact.dependency.api";
    public static final String SPI_KEY = "avalon.artifact.dependency.spi";
    public static final String IMP_KEY = "avalon.artifact.dependency";
    public static final String FACTORY_KEY = "avalon.artifact.factory";
    public static final String EXPORT_KEY = "avalon.artifact.export";

    //-----------------------------------------------------------
    // immutable state
    //-----------------------------------------------------------

    private final String c_domain;
    private final String c_version;
    private final Artifact[] c_api;
    private final Artifact[] c_spi;
    private final Artifact[] c_imp;

    private final String m_group;
    private final String m_name;
    private final String m_version;
    private final String m_build;

    private final Artifact m_artifact;
    private final String m_factory;
    private final String m_interface;

    //-----------------------------------------------------------
    // constructor
    //-----------------------------------------------------------

    /**
     * Creates a new RelationalDescriptor.
     * 
     * @param attributes the meta data attributes
     */
    public Plugin( final Attributes attributes ) 
      throws CacheException
    {
        if( null == attributes )
          throw new NullPointerException( "attributes" );

        try
        {
            c_domain = getValue( attributes, DOMAIN_KEY );
            if( null == c_domain ) 
            {
                final String error = 
                  "Missing attribute: " + DOMAIN_KEY;
                throw new CacheException( error );
            }

            c_version = getValue( attributes, VERSION_KEY );
            if( null == c_version ) 
            {
                final String error = 
                  "Missing attribute: " + VERSION_KEY;
                throw new CacheException( error );
            }

            m_group = getAttribute( attributes, Artifact.GROUP_KEY, "" );
            m_name = getAttribute( attributes, Artifact.NAME_KEY, "" );
            m_version = getAttribute( attributes, Artifact.VERSION_KEY, "" );
            m_build = getAttribute( attributes, BUILD_KEY, "" );

            m_artifact = Artifact.createArtifact( m_group, m_name, m_version, null );

        }
        catch( NamingException e )
        {
            final String error = 
              "Unexpected naming exception during metadata creation.";
            throw new CacheException( error, e );
        }
        catch( NoSuchElementException e )
        {
            final String error = 
              "Unexpected exception during metadata creation.";
            throw new CacheException( error, e );
        }

        c_api = buildDependents( attributes, API_KEY );
        c_spi = buildDependents( attributes, SPI_KEY );
        c_imp = buildDependents( attributes, IMP_KEY );

        m_factory = getFactory( attributes );
        m_interface = getInterface( attributes );
    }

    //-----------------------------------------------------------
    // public
    //-----------------------------------------------------------

   /**
    * Return the artifact reference.
    * @return the artifact
    */
    public Artifact getArtifact()
    {
        return m_artifact;
    }

   /**
    * Return the meta data domain value.
    * @return the domain
    */
    public String getDomain()
    {
        return c_domain;
    }

   /**
    * Return the meta data version
    * @return the version
    */
    public String getVersion()
    {
        return c_version;
    }

   /**
    * Return the build identifier
    * @return the identifier
    */
    public String getBuild()
    {
        return m_build;
    }

   /**
    * Test is the supplied object is equal to this object.
    * @param other the object to compare this object with
    * @return true if the objects are equivalent
    */
    public boolean equals( Object other )
    {
        boolean isEqual = other instanceof Plugin;
        if ( isEqual )
        {
            Plugin meta = (Plugin) other;
            isEqual = isEqual && c_domain.equals( meta.c_domain );
            isEqual = isEqual && c_version.equals( meta.c_version );
            isEqual = isEqual && m_group.equals( meta.m_version );
            isEqual = isEqual && m_name.equals( meta.m_name );
            isEqual = isEqual && m_version.equals( meta.m_version );
        }
        return isEqual;
    }

   /**
    * Return the hashcode for the object.
    * @return the hashcode value
    */
    public int hashCode()
    {
        int hash = 1;
        hash >>>= 13;
        hash ^= c_domain.hashCode();
        hash >>>= 13;
        hash ^= c_version.hashCode();
        hash >>>= 13;
        hash ^= m_group.hashCode();
        hash >>>= 13;
        hash ^= m_version.hashCode();
        hash >>>= 13;
        hash ^= m_build.hashCode();
        hash >>>= 13;
        return hash;
    }

   /**
    * Return the factory classname.
    * @return the classname
    */
    public String getFactoryClassname()
    {
        return m_factory;
    }

   /**
    * Return the factory interface.
    * @return the interface classname
    */
    public String getInterface()
    {
        return m_interface;
    }

   /**
    * Return the implementation dependencies
    * @return the artifacts
    */
    public Artifact[] getDependencies( String key )
    {
        if( key == API_KEY )
        {
            return c_api;
        }
        else if( key == SPI_KEY )
        {
            return c_spi;
        }
        else if( key == IMP_KEY )
        {
            return c_imp;
        }
        else
        {
            final String error = 
              "Invalid dependency key: " + key;
            throw new IllegalArgumentException( error ); 
        }
    }

    public Artifact[] getDependencies()
    {
        int j = c_api.length + c_spi.length + c_imp.length;
        Artifact[] all = new Artifact[ j ];
        int q = 0;
        for( int i=0; i<c_api.length; i++ )
        {
            all[q] = c_api[i];
            q++;
        }
        for( int i=0; i<c_spi.length; i++ )
        {
            all[q] = c_spi[i];
            q++;
        }
        for( int i=0; i<c_imp.length; i++ )
        {
            all[q] = c_imp[i];
            q++;
        }
        return all;
    }

   /**
    * Return a stringified representation of the instance.
    * @return the string representation
    */
    public String toString()
    {
        return "[factory: " + getDomain() 
          + "::" + getVersion() + "]";
    }

    //-----------------------------------------------------------
    // private
    //-----------------------------------------------------------

    private Artifact[] buildDependents( 
      Attributes attributes, String key )
    {
        try
        {
            Attribute attribute = attributes.get( key ) ;
            if( null == attribute ) return new Artifact[0];

            Artifact[] dependencies = 
              new Artifact[ attribute.size() ] ;
            for ( int i = 0; i < dependencies.length; i++ )
            {
                final String spec = (String) attribute.get( i );
                dependencies[i] = Artifact.createArtifact( spec ) ;
            }
            return dependencies;
        }
        catch ( NamingException e )
        {
            throw new CacheRuntimeException( 
              "Failed to resolve dependencies for [" + key 
              + "] on the attribute set [" + attributes + "].", e ) ;
        }
    }

    private String getFactory( Attributes attributes ) throws CacheException
    {
        try
        {
            return getValue( attributes, FACTORY_KEY ); 
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to resolve the plugin attribute ["
              + FACTORY_KEY
              + "].";
            throw new CacheException( error );
        }
    }

    private String getInterface( Attributes attributes )
    {
        try
        {
            return getValue( attributes, EXPORT_KEY ); 
        }
        catch( Throwable e )
        {
            return null;
        }
    }

    //-----------------------------------------------------------
    // utilities
    //-----------------------------------------------------------

    private String getAttribute( Attributes attributes, String key, String def )
    {
        try
        {
            return getValue( attributes, key ); 
        }
        catch( Throwable e )
        {
            return def;
        }
    }

    protected String getValue( Attributes attributes, String key )
      throws NamingException, NoSuchElementException
    {
        Attribute attribute = attributes.get( key );
        if( null == attribute ) return null;
        Object object = attribute.get();
        if( null == object ) return null;
        return object.toString();
    }

}
