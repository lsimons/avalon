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
 * This descriptor defines the type of service offerend or required
 * by a component. The type corresponds to the class name of the
 * class/interface implemented by component.
 *
 * <p>Also associated with each service is a set of arbitrary
 * Attributes that can be used to store extra information
 * about service. See {@link ComponentDescriptor} for example
 * of how to declare the container specific Attributes.</p>
 *
 * <p>Possible uses for the Attributes are to declare a service
 * as "stateless", "pass-by-value", "remotable" or even to attach
 * Attributes such as security or transaction constraints. These
 * Attributes are container specific and should not be relied
 * upon to work in all containers.</p>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/22 12:07:13 $
 */
public final class ServiceDescriptor
    extends FeatureDescriptor
{
    /**
     * Constant set of 0 service descriptors.
     */
    public static final ServiceDescriptor[] EMPTY_SET = new ServiceDescriptor[ 0 ];

    /**
     * The implementationKey for the service.
     * This usually indicates the name of the service
     * class.
     */
    private final String m_type;

    /**
     * Construct a service with specified name and Attributes.
     *
     * @param type the type of Service
     * @param attributes the attributes of service
     */
    public ServiceDescriptor( final String type,
                              final Attribute[] attributes )
    {
        super( attributes );

        if( null == type )
        {
            throw new NullPointerException( "type" );
        }

        m_type = type;
    }

    /**
     * Return the implementationKey of service.
     *
     * @return the implementationKey of service.
     */
    public String getType()
    {
        return m_type;
    }

    /**
     * Overide toString to perform a reasonable strinigifcation of service.
     *
     * @return string representing service
     */
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( m_type );
        sb.append( attributesToString() );
        return sb.toString();
    }
}
