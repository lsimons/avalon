/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.metainfo;

/**
 * This describes some one who contributed to creating Block.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Contributor
{
    /**
     * Retrieve name of identity/person.
     *
     * @return the name of identity/person
     */
    String getName();
    
    /**
     * Return their contact details. (Usually an email address).
     *
     * @return the contact details
     */
    String getContactDetails();
    
    /**
     * Retrieve what role the contributor played in creating block.
     *
     * Valid values are "author" and "patcher".
     *
     * @return the role of contributor
     */
    String getRole();
}
