/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file. 
 */
package org.apache.avalon.phoenix.tools.export;

import java.io.File;
import java.util.Map;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.BlockContext;

/**
 * Block context that can be used by external containers the need to deal 
 * with existing Phoenix block implementations that include direct references to 
 * the BlockContext interface.  External containers can use this class as an 
 * alternative implementation that is independent of the Phoenix framework. 
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 */
public class GenericBlockContext extends DefaultContext
    implements BlockContext
{

   /**
    * Creation of a block context indepedently of the Phoenix 
    * runtime environment.
    * @param map a map of the named value pairs to include in the context
    * @param context optional parent context
    */
    public GenericBlockContext( Map map, Context context )
    {
        super( map, context );    
    }

    /**
     * Base directory.
     *
     * @return the base directory
     */
    public File getBaseDirectory()
    {
        try
        {
            return (File) super.get( APP_HOME_DIR );
        }
        catch( Throwable e )
        {
            throw new RuntimeException( e.toString() );
        }
    }

    /**
     * Retrieve name of block.
     *
     * @return the name of block
     */
    public String getName()
    {
        try
        {
            return (String) super.get( NAME );
        }
        catch( Throwable e )
        {
            throw new RuntimeException( e.toString() );
        }
    }

    /**
     * Retrieve logger coresponding to named category.
     *
     * @return the logger
     * @deprecated This allows block writers to "break-out" of their logging
     *             hierarchy which is considered bad form. Replace by
     *             Logger.getChildLogger(String) where original logger is aquired
     *             via AbstractLogEnabled.
     * @exception UnsupportedOperationException is always thrown
     */
    public Logger getLogger( String name )
    {
        throw new UnsupportedOperationException("getLogger");
    }
}
