/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.transport.authentication;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.phoenix.Block;
import org.apache.excalibur.altrmi.common.AltrmiAuthentication;
import org.apache.excalibur.altrmi.common.AltrmiAuthenticationException;
import org.apache.excalibur.altrmi.server.AltrmiAuthenticator;

/**
 *
 * @phoenix:service name="org.apache.excalibur.altrmi.server.AltrmiAuthenticator"
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.5 $
 */
public class DefaultAuthenticator
    implements AltrmiAuthenticator, Initializable, Block
{
    private AltrmiAuthenticator m_altrmiAuthenticator;

    /**
     * Initialialize the component. Initialization includes
     * allocating any resources required throughout the
     * components lifecycle.
     *
     * @exception Exception if an error occurs
     */
    public void initialize()
        throws Exception
    {
        m_altrmiAuthenticator =
            new org.apache.excalibur.altrmi.server.impl.DefaultAuthenticator();
    }

    public void checkAuthority( AltrmiAuthentication authentication, String publishedName )
        throws AltrmiAuthenticationException
    {
        m_altrmiAuthenticator.checkAuthority( authentication, publishedName );
    }

    public String getTextToSign()
    {
        return m_altrmiAuthenticator.getTextToSign();
    }
}
