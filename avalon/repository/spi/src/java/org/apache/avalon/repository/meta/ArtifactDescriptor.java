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

/**
 * An abstract descriptor holds attributes about an artifact.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.2 $
 */
public class ArtifactDescriptor implements Serializable
{
    //-----------------------------------------------------------
    // static
    //-----------------------------------------------------------

    public static final String DOMAIN_KEY = "meta.domain";
    public static final String VERSION_KEY = "meta.version";
    public static final String BUILD_KEY = 
      "avalon.artifact.signature";

    //-----------------------------------------------------------
    // immutable state
    //-----------------------------------------------------------

    private final String c_domain;
    private final String c_version;

    private final String m_group;
    private final String m_name;
    private final String m_version;
    private final String m_build;


    //-----------------------------------------------------------
    // constructor
    //-----------------------------------------------------------

    /**
     * Creates a new Meta descriptor.
     * 
     * @param attributes the metadata attributes
     * @exception NullPointerException if the supplied 
     *   attributes argument is null
     * @exception MetaException if an attribute is inconsitent
     */
    public ArtifactDescriptor( Attributes attributes ) 
      throws MetaException
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
                throw new MetaException( error );
            }

            c_version = getValue( attributes, VERSION_KEY );
            if( null == c_version ) 
            {
                final String error = 
                  "Missing attribute: " + VERSION_KEY;
                throw new MetaException( error );
            }

            m_group = getAttribute( attributes, Artifact.GROUP_KEY, "" );
            m_name = getAttribute( attributes, Artifact.NAME_KEY, "" );
            m_version = getAttribute( attributes, Artifact.VERSION_KEY, "" );
            m_build = getAttribute( attributes, BUILD_KEY, "" );

        }
        catch( NamingException e )
        {
            final String error = 
              "Unexpected naming exception during metadata creation.";
            throw new MetaException( error, e );
        }
        catch( NoSuchElementException e )
        {
            final String error = 
              "Unexpected exception during metadata creation.";
            throw new MetaException( error, e );
        }
    }

    //-----------------------------------------------------------
    // public
    //-----------------------------------------------------------

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
    * @param other the obhject to compare this object with
    * @return true if the objects are equivalent
    */
    public boolean equals( Object other )
    {
        boolean isEqual = other instanceof ArtifactDescriptor;
        if ( isEqual )
        {
            ArtifactDescriptor meta = (ArtifactDescriptor) other;
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
    * Return a stringified representation of the instance.
    * @return the string representation
    */
    public String toString()
    {
        return "[artifact: " + getDomain() 
          + "::" + getVersion() + "]";
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
