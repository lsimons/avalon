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

package org.apache.avalon.repository.meta;

import java.io.Serializable;
import java.util.NoSuchElementException;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.NamingException;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.RepositoryRuntimeException;

/**
 * A RelationalDescriptor represents a set of metadata describing the 
 * structural relationships that an artifact has on other 
 * artifacts.
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $
 */
public class FactoryDescriptor extends ArtifactDescriptor
{
    //-----------------------------------------------------------
    // static
    //-----------------------------------------------------------

    public static final String API_KEY = 
      "avalon.artifact.dependency.api";

    public static final String SPI_KEY = 
      "avalon.artifact.dependency.spi";

    public static final String IMP_KEY = 
      "avalon.artifact.dependency";

    public static final String FACTORY_KEY = 
      "avalon.artifact.factory";

    public static final String BUILD_KEY = 
      "avalon.artifact.signature";


    //-----------------------------------------------------------
    // immutable state
    //-----------------------------------------------------------

    private final String m_group;
    private final String m_name;
    private final String m_version;
    private final String m_build;

    private final Artifact[] c_api;

    private final Artifact[] c_spi;

    private final Artifact[] c_imp;
    
    private final String m_factory;

    //-----------------------------------------------------------
    // constructor
    //-----------------------------------------------------------

    /**
     * Creates a new RelationalDescriptor.
     * 
     * @param attributes the meta data attributes
     */
    public FactoryDescriptor( final Attributes attributes ) 
      throws MetaException
    {
        super( attributes );

        m_group = getAttribute( attributes, Artifact.GROUP_KEY, "" );
        m_name = getAttribute( attributes, Artifact.NAME_KEY, "" );
        m_version = getAttribute( attributes, Artifact.VERSION_KEY, "" );
        m_build = getAttribute( attributes, BUILD_KEY, "" );

        c_api = buildDependents( attributes, API_KEY );
        c_spi = buildDependents( attributes, SPI_KEY );
        c_imp = buildDependents( attributes, IMP_KEY );

        m_factory = getFactory( attributes );
    }

    //-----------------------------------------------------------
    // public
    //-----------------------------------------------------------

   /**
    * Return the build identifier
    * @return the identifier
    */
    public String getBuild()
    {
        return m_build;
    }

   /**
    * Return the factory classname.
    * @return the classname
    */
    public String getFactory()
    {
        return m_factory;
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

    public String toString()
    {
        return "[meta:"
          + " group:" + m_group 
          + " name:" + m_name 
          + " version:" + m_version 
          + " build:" + m_build 
          + " factory:" + m_factory 
          + "]";
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
            throw new RepositoryRuntimeException( 
              "Failed to resolve dependencies for [" + key 
              + "] on the attribute set [" + attributes + "].", e ) ;
        }
    }

    private String getFactory( Attributes attributes )
    {
        try
        {
            return getValue( attributes, FACTORY_KEY ); 
        }
        catch( Throwable e )
        {
            return null;
        }
    }

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
}
