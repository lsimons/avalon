/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.phases;

import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.Initializable;
import org.apache.avalon.atlantis.ApplicationException;
import org.apache.avalon.camelot.pipeline.AvalonState;
import org.apache.avalon.camelot.pipeline.LifeCycleStage;
import org.apache.avalon.util.Enum;
import org.apache.avalon.util.thread.ThreadManager;
import org.apache.log.LogKit;
import org.apache.log.Logger;
import org.apache.phoenix.engine.blocks.BlockEntry;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultPhase
    extends AbstractLoggable
    implements Phase, Contextualizable, Composer, Initializable
{  
    protected final AvalonState           m_startState;
    protected final AvalonState           m_endState;
    protected final Traversal             m_traversal;
    protected final LifeCycleStage        m_pipeline;
    protected ThreadManager               m_threadManager;
    protected ClassLoader                 m_classLoader;

    public DefaultPhase( final Traversal traversal, 
                         final LifeCycleStage pipeline,
                         final AvalonState startState,
                         final AvalonState endState )
    {
        m_traversal = traversal;
        m_pipeline = pipeline;
        m_startState = startState;
        m_endState = endState;
    }

    public void setLogger( final Logger logger )
    {
        super.setLogger( logger );
        setupLogger( m_pipeline );
    }

    public void contextualize( final Context context )
    {
        if( m_pipeline instanceof Contextualizable )
        {
            ((Contextualizable)m_pipeline).contextualize( context );
        }
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
    {
        m_classLoader = (ClassLoader)componentManager.lookup( "java.lang.ClassLoader" );

        m_threadManager = (ThreadManager)componentManager.
            lookup( "org.apache.avalon.util.thread.ThreadManager" );

        if( m_pipeline instanceof Composer )
        {
            ((Composer)m_pipeline).compose( componentManager );
        }
    }

    public void init()
        throws Exception
    {
        if( m_pipeline instanceof Initializable )
        {
            ((Initializable)m_pipeline).init();
        }  
    }

    /**
     * Retrieve traversal that should be taken.
     *
     * @return the Traversal
     */
    public Traversal getTraversal()
    {
        return m_traversal;
    }

    /**
     * This is called when a block is reached whilst walking the tree.
     *
     * @param name the name of block
     * @param entry the BlockEntry
     * @exception ApplicationException if walking is to be stopped
     */
    public void visitBlock( final String name, final BlockEntry entry )
        throws ApplicationException
    {
        if( entry.getState() == m_endState ) return;
        else if( entry.getState() != m_startState ) 
        {
            final String message = "Block (" + name + ") not prepared for phase";
            getLogger().warn( message );
            throw new ApplicationException( message );
        }

        LogKit.getCurrentContext().push( name );

        final PipelineRunner runner =
            new PipelineRunner( name, entry, m_pipeline, m_classLoader );
        Exception exception = null;
        try
        {
            try
            {
                m_threadManager.getDefaultThreadPool().executeAndWait( runner );
                exception = runner.getException();
            }
            catch( final Exception e )
            {
                exception = e;
            }
            
            if( null != exception )
            {
                throw new ApplicationException( "Failed to load block " + name, exception );
            }
            else
            {
                getLogger().info( "Ran Phase " + name );
            }
        }
        finally
        {
            LogKit.getCurrentContext().pop();
        }
    }
}
