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

import java.util.Properties;

import org.apache.avalon.framework.Version;

/**
 * A descriptor that describes dependency information for
 * a particular Component. This class contains information
 * about;
 * <ul>
 *   <li>role: the name component uses to look up dependency</li>
 *   <li>service: the class/interface that the dependency must provide</li>
 * </ul>
 *
 * <p>Also associated with each dependency is a set of arbitrary
 * attributes that can be used to store extra information
 * about dependency. See {@link InfoDescriptor} for example
 * of how to declare the container specific attributes.</p>
 *
 * <p>Possible uses for the attributes are to declare container
 * specific constraints of component. For example a dependency on
 * a Corba ORB may also require that the Corba ORB contain the
 * TimeServer and PersistenceStateService at initialization. Or it
 * may require that the componenet be multi-thread safe or that
 * it is persistent etc. These are all container specific
 * demands.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:15:06 $
 */
public final class DependencyDescriptor extends Descriptor
{

    private static final Version DEFAULT_VERSION = Version.getVersion( "1.0" );


    /**
     * The name the component uses to lookup dependency.
     */
    private final String m_key;

    /**
     * The service class/interface that the dependency must provide.
     */
    private final ReferenceDescriptor m_service;

    /**
     * True if dependency is optional, false otherwise.
     */
    private final boolean m_optional;

    /**
     * Creation of a new dependency descriptor using the default 1.0 version
     * @param role the role name that will be used by the type when looking up a service
     * @param service the interface service
     */
    public DependencyDescriptor( final String role, String service )
    {
        this( role, service, DEFAULT_VERSION );
    }

    /**
     * Creation of a new dependency descriptor.
     * @param role the role name that will be used by the type when looking up a service
     * @param service the version insterface service reference
     */
    public DependencyDescriptor( final String role, String service, Version version )
    {
        this( role, new ReferenceDescriptor( service, version ), false, null );
    }

    /**
     * Creation of a new dependency descriptor.
     * @param role the role name that will be used by the type when looking up a service
     * @param service the version insterface service reference
     */
    public DependencyDescriptor( final String role,
                                 final ReferenceDescriptor service )
    {
        this( role, service, false, null );
    }

    /**
     * Creation of a new dependency descriptor.
     * @param role the role name that will be used by the type when looking up a service
     * @param service the version insterface service reference
     * @param optional TRUE if this depedency is optional
     * @param attributes a set of attributes to associate with the dependency
     */
    public DependencyDescriptor( final String role,
                                 final ReferenceDescriptor service,
                                 final boolean optional,
                                 final Properties attributes )
    {
        super( attributes );

        if ( null == role )
        {
            throw new NullPointerException( "role" );
        }

        if ( null == service )
        {
            throw new NullPointerException( "service" );
        }

        m_key = role;
        m_service = service;
        m_optional = optional;
    }

    /**
     * Return the name the component uses to lookup the dependency.
     *
     * @return the name the component uses to lookup the dependency.
     */
    public String getKey()
    {
        return m_key;
    }

    /**
     * Return the service class/interface descriptor that describes the
     * dependency that the provider provides.
     *
     * @return a reference to service reference that describes the fulfillment
     *  obligations that must be met by a service provider.
     * @deprecated use getReference()
     */
    public ReferenceDescriptor getService()
    {
        return m_service;
    }

    /**
     * Return the service class/interface descriptor that describes the
     * dependency must fulfilled by a provider.
     *
     * @return a reference to service reference that describes the fulfillment
     *  obligations that must be met by a service provider.
     */
    public ReferenceDescriptor getReference()
    {
        return m_service;
    }

    /**
     * Return true if dependency is optional, false otherwise.
     *
     * @return true if dependency is optional, false otherwise.
     */
    public boolean isOptional()
    {
        return m_optional;
    }

    /**
     * Return true if dependency is required, false otherwise.
     *
     * @return true if dependency is required, false otherwise.
     */
    public boolean isRequired()
    {
        return !isOptional();
    }

    public String toString()
    {
        return "[" + getKey() + "] " + getReference();
    }

    /**
     * Compare this object with another for equality.
     * @param other the object to compare this object with
     * @return TRUE if the supplied object is a reference, service, or service
     *   descriptor that matches this objct in terms of classname and version
     */
    public boolean equals( Object other )
    {
        boolean isEqual = super.equals( other ) && other instanceof DependencyDescriptor;
        if ( other instanceof DependencyDescriptor )
        {
            DependencyDescriptor dep = (DependencyDescriptor) other;

            isEqual = isEqual && m_optional == dep.m_optional;
            isEqual = isEqual && m_service.equals( dep.m_service );
        }

        return isEqual;
    }

   /**
    * Return the hashcode for the object.
    * @return the hashcode value
    */
    public int hashCode()
    {
        int hash = super.hashCode();
        hash >>>= 13;
        hash ^= m_service.hashCode();
        hash >>>= ( m_optional ) ? 1 : 0;

        return hash;
    }

}
