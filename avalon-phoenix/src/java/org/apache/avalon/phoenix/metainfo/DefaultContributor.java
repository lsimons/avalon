/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.metainfo;

/**
 * This describes some one who contributed to creating Block.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultContributor
    implements Contributor
{
    protected final String              m_name;
    protected final String              m_contactDetails;
    protected final String              m_role;

    /**
     * Default constructor that takes components as parts.
     */
    public DefaultContributor( final String name, 
                               final String contactDetails, 
                               final String role )
    {
        m_name = name;
        m_contactDetails = contactDetails;
        m_role = role;
    }
    
    /**
     * Retrieve name of identity/person.
     *
     * @return the name of identity/person
     */
    public String getName()
    {
        return m_name;
    }
        
    /**
     * Return their contact details. (Usually an email address).
     *
     * @return the contact details
     */
    public String getContactDetails()
    {
        return m_contactDetails;
    }

    /**
     * Retrieve what role the contributor played in creating block.
     *
     * Valid values are "author" and "patcher".
     *
     * @return the role of contributor
     */
    public String getRole()
    {
        return m_role;        
    }
}

