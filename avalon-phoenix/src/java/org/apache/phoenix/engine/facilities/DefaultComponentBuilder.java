/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities;

import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Component;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.Initializable;
import org.apache.avalon.atlantis.ApplicationException;
import org.apache.avalon.atlantis.Facility;
import org.apache.avalon.camelot.Entry;
import org.apache.avalon.camelot.Factory;
import org.apache.avalon.camelot.FactoryException;
import org.apache.avalon.camelot.pipeline.ComponentBuilder;
import org.apache.log.Logger;
import org.apache.phoenix.Block;
import org.apache.phoenix.engine.blocks.BlockEntry;
import org.apache.phoenix.metainfo.BlockUtil;
import org.apache.phoenix.metainfo.ServiceInfo;

/**
 * Component responsible for building componentManager information for entry.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultComponentBuilder
    extends AbstractLoggable
    implements Facility, ComponentBuilder, Composer, Initializable
{
    protected SarBlockFactory      m_factory = new SarBlockFactory();

    public void setLogger( final Logger logger )
    {
        super.setLogger( logger );
        setupLogger( m_factory, "<core>.factory" );
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
    {
        m_factory.compose( componentManager );
    }

    public void init()
        throws Exception
    {
        m_factory.init();
    }

    public Object createComponent( final String name, final Entry entry )
        throws Exception
    {
        getLogger().info( "Creating block " + name );

        final BlockEntry blockEntry = (BlockEntry)entry;

        Block block = null;
        
        try { block = (Block)m_factory.create( blockEntry.getLocator(), Block.class ); }
        catch( final FactoryException fe )
        {
            throw new ApplicationException( "Failed to create block " + name, fe );
        }
        
        getLogger().debug( "Created block " + block );
        verifyBlockServices( name, blockEntry, block );

        return block;
    }

    /**
     * Verify that all the services that a block 
     * declares it provides are actually provided.
     *
     * @param name the name of block
     * @param blockEntry the blockEntry
     * @param block the Block
     * @exception ApplicationException if verification fails
     */
    protected void verifyBlockServices( final String name, 
                                        final BlockEntry blockEntry,  
                                        final Block block )
        throws ApplicationException
    {
        final ServiceInfo[] services = blockEntry.getBlockInfo().getServices();
        for( int i = 0; i < services.length; i++ )
        {
            if( false == BlockUtil.implementsService( block, services[ i ] ) )
            {
                final String message = "Block " + name + " fails to implement " + 
                    "advertised service " + services[ i ];
                getLogger().warn( message );
                throw new ApplicationException( message );
            }
        }        
    }
}
