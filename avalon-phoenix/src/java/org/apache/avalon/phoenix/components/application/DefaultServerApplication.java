/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.application;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.lang.ThreadContext;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;

/**
 * This is the basic container of blocks. A server application
 * represents an aggregation of blocks that act together to form
 * an application.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class DefaultServerApplication
    extends AbstractLoggable
    implements Application
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultServerApplication.class );

    private static final String  PHASE_STARTUP  = "startup";
    private static final String  PHASE_SHUTDOWN = "shutdown";

    private ApplicationContext   m_context;
    private LifecycleHelper      m_lifecycle;
    private HashMap              m_entrys = new HashMap();

    public void setApplicationContext( final ApplicationContext context )
    {
        m_context = context;
        m_lifecycle = new LifecycleHelper( this, m_context );
        setupLogger( m_lifecycle, "lifecycle" );
    }

    public String[] getBlockNames()
    {
        return (String[])m_entrys.keySet().toArray( new String[ 0 ] );
    }

    public Block getBlock( final String name )
    {
        final BlockEntry entry = (BlockEntry)m_entrys.get( name );
        if( null == entry ) return null;
        return entry.getProxy();
    }

    public void initialize()
        throws Exception
    {
    }

    /**
     * Startup application by running startup phase on all the blocks.
     *
     * @exception Exception if an error occurs
     */
    public void start()
        throws Exception
    {
        final BlockMetaData[] blocks = m_context.getMetaData().getBlocks();
        for( int i = 0; i < blocks.length; i++ )
        {
            final String blockName = blocks[ i ].getName();
            final BlockEntry blockEntry = new BlockEntry( blocks[ i ] );
            m_entrys.put( blockName, blockEntry );
        }

        // load block listeners
        loadBlockListeners();

        // load blocks
        runPhase( PHASE_STARTUP );
    }

    /**
     * Shutdown the application.
     * This involves shutting down every contained block.
     *
     * @exception Exception if an error occurs
     */
    public void stop()
        throws Exception
    {
        runPhase( PHASE_SHUTDOWN );
    }

    public void dispose()
    {
        m_entrys.clear();
    }

    private void loadBlockListeners()
        throws Exception
    {
        //Setup thread context for calling visitors
        ThreadContext.setThreadContext( m_context.getThreadContext() );

        final BlockListenerMetaData[] listeners = m_context.getMetaData().getListeners();
        for( int i = 0; i < listeners.length; i++ )
        {
            try
            {
                m_lifecycle.startupListener( listeners[ i ] );
            }
            catch( final Exception e )
            {
                final String name = listeners[ i ].getName();
                final String message =
                    REZ.getString( "bad-listener", "startup", name, e.getMessage() );
                getLogger().error( message, e );
                throw e;
            }
        }
    }

    /**
     * Run a phase for application.
     * Each phase transitions application into new state and processes
     * all the blocks to make sure they are in that state aswell.
     * Exceptions leave the blocks in an indeterminate state.
     *
     * @param name the name of phase (for logging purposes)
     * @exception Exception if an error occurs
     */
    protected final void runPhase( final String name )
        throws Exception
    {
        final BlockMetaData[] blocks = m_context.getMetaData().getBlocks();
        final String[] order = DependencyGraph.walkGraph( PHASE_STARTUP == name, blocks );

        //Log message describing the number of blocks
        //the phase in and the order in which they will be
        //processed
        if( getLogger().isInfoEnabled() )
        {
            final Integer count = new Integer( blocks.length );
            final List pathList = Arrays.asList( order );
            final String message =
                REZ.getString( "blocks-processing", count, name, pathList );
            getLogger().info( message );
        }

        //Setup thread context for calling visitors
        ThreadContext.setThreadContext( m_context.getThreadContext() );

        for( int i = 0; i < order.length; i++ )
        {
            final String block = order[ i ];

            //Log message saying we are processing block
            if( getLogger().isDebugEnabled() )
            {
                final String message = REZ.getString( "process-block", block, name );
                getLogger().debug( message );
            }

            try
            {
                final BlockEntry entry = (BlockEntry)m_entrys.get( block );
                if( PHASE_STARTUP == name ) m_lifecycle.startup( entry );
                else m_lifecycle.shutdown( entry );
            }
            catch( final Exception e )
            {
                final String message =
                    REZ.getString( "app.error.run-phase", name, block, e.getMessage() );
                getLogger().error( message, e );
                throw e;
            }

            //Log message saying we have processed block
            if( getLogger().isDebugEnabled() )
            {
                final String message = REZ.getString( "processed-block", block, name );
                getLogger().debug( message );
            }
        }
    }
}
