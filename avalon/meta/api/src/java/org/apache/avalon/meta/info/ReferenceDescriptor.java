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

package org.apache.avalon.meta.info;

import java.io.Serializable;

import org.apache.avalon.framework.Version;

/**
 * This reference defines the type of interface required
 * by a component. The type corresponds to the class name of the
 * interface implemented by component. Associated with each
 * classname is a version object so that different versions of same
 * interface can be represented.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:15:09 $
 */
public final class ReferenceDescriptor
        implements Serializable
{
    /**
     * The name of service class.
     */
    private final String m_classname;

    /**
     * The version of service class.
     */
    private final Version m_version;

    /**
     * Construct a service with specified type.
     *
     * @param type the service type spec
     * @exception NullPointerException if the classname is null
     */
    public ReferenceDescriptor( final String type ) throws NullPointerException
    {
        this( parseClassname(type), parseVersion(type) );
    }

    /**
     * Construct a service with specified name, version and attributes.
     *
     * @param classname the name of the service
     * @param version the version of service
     * @exception NullPointerException if the classname or version is null
     * @exception IllegalArgumentException if the classname string is invalid
     */
    public ReferenceDescriptor( final String classname,
                                final Version version ) throws NullPointerException
    {
        if ( null == classname )
        {
            throw new NullPointerException( "classname" );
        }
        if( classname.equals( "" ) )
        {
            throw new IllegalArgumentException( "classname" );
        }
        if( classname.indexOf( "/" ) > -1 )
        {
            throw new IllegalArgumentException( "classname" );
        }

        m_classname = classname;

        if ( null == version )
        {
            m_version = Version.getVersion( "" );
        }
        else
        {
            m_version = version;
        }
    }

    /**
     * Return classname of interface this reference refers to.
     *
     * @return the classname of the Service
     */
    public String getClassname()
    {
        return m_classname;
    }

    /**
     * Return the version of interface.
     *
     * @return the version of interface
     */
    public Version getVersion()
    {
        return m_version;
    }

    /**
     * Determine if specified service will match this service.
     * To match a service has to have same name and must comply with version.
     *
     * @param other the other ServiceInfo
     * @return true if matches, false otherwise
     */
    public boolean matches( final ReferenceDescriptor other )
    {
        return m_classname.equals( other.m_classname )
                && other.getVersion().complies( getVersion() );
    }

    /**
     * Convert to a string of format name:version
     *
     * @return string describing service
     */
    public String toString()
    {
        return getClassname() + ":" + getVersion();
    }

    /**
     * Compare this object with another for equality.
     * @param other the object to compare this object with
     * @return TRUE if the supplied object is a reference, service, or service
     *   descriptor that matches this objct in terms of classname and version
     */
    public boolean equals( Object other )
    {
        boolean match = false;

        //
        // TODO: check validity of the following - this is 
        // assuming the equality is equivalent to compliance
        // which is not true
        //

        if ( other instanceof ReferenceDescriptor )
        {
            match = ( (ReferenceDescriptor) other ).matches( this );
        }
        else if ( other instanceof Service )
        {
            match = ( (Service) other ).matches( this );
        }
        else if ( other instanceof ServiceDescriptor )
        {
            match = ( (ServiceDescriptor) other ).getReference().matches( this );
        }

        return match;
    }

    /**
     * Returns the cashcode.
     * @return the hascode value
     */
    public int hashCode()
    {
        return getClassname().hashCode() ^ getVersion().hashCode();
    }

    private static final String parseClassname( final String type )
    {
        if( type == null ) throw new NullPointerException( "type" );

        int index = type.indexOf( ":" );
        if( index == -1 )
        {
            return type;
        }
        else
        {
            return type.substring( 0, index ); 
        }
    }

    private static final Version parseVersion( final String type )
    {
        if( type.indexOf( ":" ) == -1 )
        {
            return Version.getVersion( "" );
        }
        else
        {
            return Version.getVersion( type.substring( getColonIndex( type ) + 1) );
        }
    }

    private static final int getColonIndex( final String type )
    {
        if ( null == type ) throw new NullPointerException( "type" );
        return Math.min( type.length(), Math.max( 0, type.indexOf( ":" ) ) );
    }
}
