/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.excalibur.container.State;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.lang.ThreadContext;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.components.frame.ApplicationFrame;
import org.apache.avalon.phoenix.metadata.BlockMetaData;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
class ShutdownPhase
    extends AbstractLoggable
    implements BlockVisitor
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ShutdownPhase.class );

    private ApplicationFrame  m_frame;

    protected ShutdownPhase( final ApplicationFrame frame )
    {
        m_frame = frame;
    }

    /**
     * This is called when a block is reached whilst walking the tree.
     *
     * @param name the name of block
     * @param entry the BlockEntry
     * @exception Exception if walking is to be stopped
     */
    public void visitBlock( final BlockEntry entry )
        throws Exception
    {
        if( State.STARTED != entry.getState() ) return;

        final BlockMetaData metaData = entry.getMetaData();
        final String name = metaData.getName();

        final BlockEvent event =
            new BlockEvent( name, entry.getProxy(), metaData.getBlockInfo() );
        m_frame.blockRemoved( event );

        final Block block = entry.getBlock();

        //Invalidate entry. This will invalidate
        //and null out Proxy object aswell as nulling out
        //block property
        entry.invalidate();

        //Stoppable stage
        if( block instanceof Startable )
        {
            notice( name, 7 );
            try
            {
                entry.setState( State.STOPPING );
                ((Startable)block).stop();
                entry.setState( State.STOPPED );
            }
            catch( final Throwable t )
            {
                entry.setState( State.FAILED );
                fail( name, 7, t );
            }
        }

        //Disposable stage
        if( block instanceof Disposable )
        {
            notice( name, 8 );
            try
            {
                entry.setState( State.DESTROYING );
                ((Disposable)block).dispose();
            }
            catch( final Throwable t )
            {
                entry.setState( State.FAILED );
                fail( name, 8, t );
            }
        }

        notice( name, 9 );
        entry.setState( State.DESTROYED );
    }

    private void notice( final String name, final int stage )
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.getString( "lifecycle-stage.notice", name, new Integer( stage ) );
            getLogger().debug( message );
        }
    }

    private void fail( final String name, final int stage, final Throwable t )
    {
        final String reason = t.getMessage();
        final String message =
            REZ.getString( "lifecycle-fail.error", name, new Integer( stage ), reason );
        getLogger().error( message );
    }
}
