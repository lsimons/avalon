/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.blocks;

import org.apache.avalon.framework.camelot.Entry;
import org.apache.avalon.framework.camelot.Locator;
import org.apache.avalon.framework.camelot.State;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.metainfo.BlockInfo;

/**
 * This is the structure describing each block before it is loaded.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class BlockEntry
    extends Entry
{
    //A list of constants representing phases in Blocks lifecycle.
    //Each phase is made up of a number of stages.
    public final static State  BASE       = new State( "BASE", 0 );
    public final static State  STARTEDUP  = new State( "STARTEDUP", 10 );
    public final static State  SHUTDOWN   = new State( "SHUTDOWN", 20 );

    private final RoleEntry[]   m_roleEntrys;

    private final String        m_name;

    //UGLY HACK should be stored in another server Facility (ie ConfigurationRepository)
    private Configuration       m_configuration;

    public BlockEntry( final String name,
                       final RoleEntry[] roleEntrys,
                       final Locator locator )
    {
        m_name = name;
        m_roleEntrys = roleEntrys;
        setLocator( locator );
        setState( BASE );
    }

    public String getName()
    {
        return m_name;
    }

    public BlockInfo getBlockInfo()
    {
        return (BlockInfo)getInfo();
    }

    public void setBlockInfo( final BlockInfo blockInfo )
    {
        setInfo(  blockInfo );
    }

    /**
     * Get a RoleEntry from entry with a particular role.
     *
     * @param role the role of RoleEntry to look for
     * @return the matching deendency else null
     */
    public RoleEntry getRoleEntry( final String role )
    {
        for( int i = 0; i < m_roleEntrys.length; i++ )
        {
            if( m_roleEntrys[ i ].getRole().equals( role ) )
            {
                return m_roleEntrys[ i ];
            }
        }

        return null;
    }

    public RoleEntry[] getRoleEntrys()
    {
        return m_roleEntrys;
    }

    public Configuration getConfiguration()
    {
        return m_configuration;
    }

    public void setConfiguration( final Configuration configuration )
    {
        m_configuration = configuration;
    }
}
