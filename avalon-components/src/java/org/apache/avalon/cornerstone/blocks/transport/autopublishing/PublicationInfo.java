/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.transport.autopublishing;

/**
 * Class PublicationInfo
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.3 $
 */
public class PublicationInfo
{

    private final String mPublishAsName;
    private final String mInterfaceToPublish;

    /**
     * Constructor PublicationInfo
     *
     *
     * @param publishAsName
     * @param interfaceToPublish
     *
     */
    public PublicationInfo( String publishAsName, String interfaceToPublish )
    {
        mPublishAsName = publishAsName;
        mInterfaceToPublish = interfaceToPublish;
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
        return mPublishAsName;
    }

    /**
     * Method getInterfaceToPublish
     *
     *
     * @return
     *
     */
    public String getInterfaceToPublish()
    {
        return mInterfaceToPublish;
    }
}
