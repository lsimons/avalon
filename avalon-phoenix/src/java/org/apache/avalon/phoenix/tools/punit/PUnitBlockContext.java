/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.phoenix.BlockContext;

import java.io.File;
import java.io.InputStream;

/**
 * PUnitBlockContext
 * @author Paul Hammant
 */
public class PUnitBlockContext
    implements BlockContext
{

    public File getBaseDirectory()
    {
        // TODO
        return null;
    }

    public String getName()
    {
        // TODO
        return null;
    }


    public void requestShutdown()
    {
        //TODO
    }

    // TODO
    public InputStream getResourceAsStream(String name)
    {
        return null;
    }

    public Logger getLogger(String name)
    {
        throw new UnsupportedOperationException();
    }

    public ClassLoader getClassLoader(String name)
            throws Exception
    {
        // TODO
        return null;
    }

    public Object get(Object o) throws ContextException
    {
        //TODO
        return null;
    }

}
