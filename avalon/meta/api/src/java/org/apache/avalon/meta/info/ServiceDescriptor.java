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

/**
 * This descriptor defines the type of service offerend or required
 * by a component. The type corresponds to the class name of the
 * class/interface implemented by component. Associated with each
 * classname is a version object so that different versions of same
 * interface can be represented.
 *
 * <p>Also associated with each service is a set of arbitrary
 * attributes that can be used to store extra information
 * about service. See {@link InfoDescriptor} for example
 * of how to declare the container specific attributes.</p>
 *
 * <p>Possible uses for the attributes are to declare a service
 * as "stateless", "pass-by-value", "remotable" or even to attach
 * attributes such as security or transaction constraints. These
 * attributes are container specific and should not be relied
 * upon to work in all containers.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:15:09 $
 */
public class ServiceDescriptor
        extends Descriptor
{
    /**
     * The service reference that descriptor is describing.
     */
    private final ReferenceDescriptor m_designator;

    /**
     * Construct a service descriptor.
     *
     * @param descriptor the service descriptor
     * @exception NullPointerException if the descriptor argument is null
     */
    public ServiceDescriptor( final ServiceDescriptor descriptor )
            throws NullPointerException
    {
        super( descriptor.getProperties() );
        m_designator = descriptor.getReference();
    }


    /**
     * Construct a service descriptor for specified ReferenceDescriptor
     *
     * @param designator the service reference
     * @exception NullPointerException if the designator argument is null
     */
    public ServiceDescriptor( final ReferenceDescriptor designator )
            throws NullPointerException
    {
        this( designator, null );
    }

    /**
     * Construct a service with specified name, version and attributes.
     *
     * @param designator the ReferenceDescriptor
     * @param attributes the attributes of service
     * @exception NullPointerException if the designator argument is null
     */
    public ServiceDescriptor( final ReferenceDescriptor designator,
                              final Properties attributes )
            throws NullPointerException
    {
        super( attributes );

        if ( null == designator )
        {
            throw new NullPointerException( "designator" );
        }

        m_designator = designator;
    }

    /**
     * Retrieve the reference that service descriptor refers to.
     *
     * @return the reference that service descriptor refers to.
     */
    public ReferenceDescriptor getReference()
    {
        return m_designator;
    }

    /**
     * Return the cashcode for this instance.
     * @return the instance hashcode
     */
    public int hashCode()
    {
        return m_designator.hashCode();
    }

   /**
    * Test is the supplied object is equal to this object.
    * @return true if the object are equivalent
    */
    public boolean equals(Object other)
    {
        boolean isEqual = super.equals( other ) && other instanceof ServiceDescriptor;
        isEqual = isEqual && m_designator.equals( ( (ServiceDescriptor) other ).m_designator );
        return isEqual;
    }
}
