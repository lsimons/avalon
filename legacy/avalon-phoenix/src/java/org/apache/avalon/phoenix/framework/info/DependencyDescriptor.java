/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.framework.info;

/**
 * A descriptor that describes dependency information for
 * a particular Component. This class contains information
 * about;
 * <ul>
 *   <li>key: the name component uses to look up dependency</li>
 *   <li>type: the class/interface that the dependency must provide</li>
 * </ul>
 *
 * <p>Also associated with each dependency is a set of arbitrary
 * Attributes that can be used to store extra information
 * about dependency. See {@link ComponentDescriptor} for example
 * of how to declare the container specific Attributes.</p>
 *
 * <p>Possible uses for the Attributes are to declare container
 * specific constraints of component. For example a dependency on
 * a Corba ORB may also require that the Corba ORB contain the
 * TimeServer and PersistenceStateService at initialization. Or it
 * may require that the componenet be multi-thread safe or that
 * it is persistent etc. These are all container specific
 * demands.</p>
 *
 * @author Peter Donald
 * @version $Revision: 1.4 $ $Date: 2003/12/05 15:14:37 $
 */
public final class DependencyDescriptor
    extends FeatureDescriptor
{
    /**
     * Constant with empty set of dependencys.
     */
    public static final DependencyDescriptor[] EMPTY_SET = new DependencyDescriptor[ 0 ];

    /**
     * The postfix indicating an array type.
     */
    public static final String ARRAY_POSTFIX = "[]";

    /**
     * The postfix indicating a "Map" type.
     */
    public static final String MAP_POSTFIX = "{}";

    /**
     * The name the component uses to lookup dependency.
     */
    private final String m_key;

    /**
     * The service class/interface that the dependency must provide.
     */
    private final String m_type;

    /**
     * True if dependency is optional, false otherwise.
     */
    private final boolean m_optional;

    /**
     * Constructor that has all parts sans parent.
     */
    public DependencyDescriptor( final String key,
                                 final String type,
                                 final boolean optional,
                                 final Attribute[] attributes )
    {
        super( attributes );

        if( null == key )
        {
            throw new NullPointerException( "key" );
        }
        if( null == type )
        {
            throw new NullPointerException( "type" );
        }

        m_key = key;
        m_type = type;
        m_optional = optional;
    }

    /**
     * Return the key the component uses to lookup dependency.
     *
     * @return the key the component uses to lookup dependency.
     */
    public String getKey()
    {
        return m_key;
    }

    /**
     * Return the service class/interface name that describes the
     * dependency must fulfilled by a provider.
     *
     * @return a reference to service descriptor that describes the fulfillment
     *  obligations that must be met by a service provider.
     */
    public String getType()
    {
        return m_type;
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
     * Return true if dependency type is an array.
     *
     * @return true if dependency type is an array.
     */
    public boolean isArray()
    {
        return getType().endsWith( ARRAY_POSTFIX );
    }

    /**
     * Return true if dependency type is a map.
     *
     * @return true if dependency type is a map.
     */
    public boolean isMap()
    {
        return getType().endsWith( MAP_POSTFIX );
    }

    /**
     * Return the type of component type if the service
     * is an array or Map Service. Otherwise just return the
     * name of service.
     *
     * @return the Service component type
     */
    public String getComponentType()
    {
        final String fullname = getType();
        if( isArray() )
        {
            final int end = fullname.length() - ARRAY_POSTFIX.length();
            return fullname.substring( 0, end );
        }
        else if( isMap() )
        {
            final int end = fullname.length() - MAP_POSTFIX.length();
            return fullname.substring( 0, end );
        }
        else
        {
            return fullname;
        }
    }

    /**
     * Convert dependency to human readable string representaiton.
     *
     * @return strinigified dependency
     */
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( m_key );
        sb.append( '/' );
        sb.append( m_type );
        if( m_optional )
        {
            sb.append( "(Optional)" );
        }
        sb.append( attributesToString() );
        return sb.toString();
    }
}
