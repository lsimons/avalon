
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.transport.authentication;



import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.phoenix.Block;
import org.apache.commons.altrmi.server.AltrmiAuthenticator;
import org.apache.commons.altrmi.common.AltrmiAuthentication;
import org.apache.commons.altrmi.common.AltrmiAuthenticationException;


/**
 * Class DefaultAuthenticator
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.1 $
 */
public class DefaultAuthenticator extends AbstractLogEnabled
        implements AltrmiAuthenticator, Initializable, Block
{

    protected AltrmiAuthenticator mAltrmiAuthenticator;

    /**
     * Initialialize the component. Initialization includes
     * allocating any resources required throughout the
     * components lifecycle.
     *
     * @exception Exception if an error occurs
     */
    public void initialize() throws Exception
    {
        mAltrmiAuthenticator = new org.apache.commons.altrmi.server.impl.DefaultAuthenticator();
    }

    /**
     * Method checkAuthority
     *
     *
     * @param authentication
     * @param publishedName
     *
     * @return
     *
     * @throws AltrmiAuthenticationException
     *
     */
    public Long checkAuthority(AltrmiAuthentication authentication, String publishedName)
            throws AltrmiAuthenticationException
    {
        return mAltrmiAuthenticator.checkAuthority(authentication, publishedName);
    }

    /**
     * Method getTextToSign
     *
     *
     * @return
     *
     */
    public String getTextToSign()
    {
        return mAltrmiAuthenticator.getTextToSign();
    }
}
