/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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
 * @version $Revision: 1.1 $ $Date: 2003/03/01 03:39:46 $
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
