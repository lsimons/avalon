/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.transport.autopublishing;

/**
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.4 $
 */
public class PublicationInfo
{
    private final String m_publishAsName;
    private final String m_interfaceToPublish;

    public PublicationInfo( String publishAsName, String interfaceToPublish )
    {
        m_publishAsName = publishAsName;
        m_interfaceToPublish = interfaceToPublish;
    }

    /**
     * Method getPublishAsName
     *
     *
     * @return
     *
     */
    public String getPublishAsName()
    {
        return m_publishAsName;
    }

    public String getInterfaceToPublish()
    {
        return m_interfaceToPublish;
    }
}
