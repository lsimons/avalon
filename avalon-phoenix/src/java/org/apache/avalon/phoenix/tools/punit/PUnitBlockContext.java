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
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.phoenix.BlockContext;

import java.io.File;
import java.io.InputStream;

/**
 * PUnitBlockContext
 * @author Paul Hammant
 */
public class PUnitBlockContext
    extends DefaultContext
    implements BlockContext
{

    public File getBaseDirectory()
    {
        try
        {
            return (File)get( BlockContext.APP_HOME_DIR );
        }
        catch( ContextException e )
        {
            return new File( "." );
        }
    }

    public String getName()
    {
        try
        {
            return (String)get( BlockContext.APP_NAME );
        }
        catch( ContextException e )
        {
            return "myBlock";
        }
    }

    public void requestShutdown()
    {
    }

    public InputStream getResourceAsStream( String name )
    {
        return null;
    }

    public Logger getLogger( String name )
    {
        throw new UnsupportedOperationException();
    }

    public ClassLoader getClassLoader( String name )
        throws Exception
    {
        return null;
    }
}
