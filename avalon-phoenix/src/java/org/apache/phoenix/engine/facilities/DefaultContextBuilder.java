/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities;

import java.io.File;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.DefaultContext;
import org.apache.avalon.atlantis.Facility;
import org.apache.avalon.camelot.Entry;
import org.apache.avalon.util.thread.ThreadManager;
import org.apache.phoenix.BlockContext;
import org.apache.phoenix.engine.SarContextResources;
import org.apache.phoenix.engine.blocks.DefaultBlockContext;

/**
 * Component responsible for building context information for block.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultContextBuilder
    extends AbstractLoggable
    implements Facility, ContextBuilder, Contextualizable, Composer
{
    //context used to setup hosted blocks
    protected DefaultContext           m_baseBlockContext;

    //thread manager used to build contexts with
    protected ThreadManager            m_threadManager;

    public void contextualize( final Context context )
    {
        final File baseDirectory = (File)context.get( SarContextResources.APP_HOME_DIR );
        final String name = (String)context.get( SarContextResources.APP_NAME );
        
        //base contxt that all block contexts inherit from 
        final DefaultContext blockContext = new DefaultContext();
        blockContext.put( BlockContext.APP_NAME, name );
        blockContext.put( BlockContext.APP_HOME_DIR, baseDirectory );
        m_baseBlockContext = blockContext;
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
    {
        m_threadManager = (ThreadManager)componentManager.
            lookup( "org.apache.avalon.util.thread.ThreadManager" );
    }

    public Context createContext( String name, Entry entry )
    {
        final DefaultBlockContext context =
            new DefaultBlockContext( getLogger(), m_threadManager, m_baseBlockContext );
        context.put( BlockContext.NAME, name );
        return context;        
    }
}
