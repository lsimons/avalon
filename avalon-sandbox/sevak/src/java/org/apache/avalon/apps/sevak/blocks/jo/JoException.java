/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.jo;

import org.apache.avalon.framework.CascadingException;

/**
 *
 * Date: Jan 15, 2002
 * Time: 7:04:54 PM
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 * @version $Id: JoException.java,v 1.2 2002/09/29 11:38:43 hammant Exp $
 */
public class JoException extends CascadingException
{

    /**
     * Construct a Jo Exception
     * @param message the message
     */
    public JoException(String message)
    {
        super(message);

    }

    /**
     * Construct a Jo Exception
     * @param message the message*
     * @param nestedException a nested exception
     */
    public JoException(String message, Exception nestedException)
    {
        super(message, nestedException);
    }

}
