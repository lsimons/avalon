/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.datasource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.cornerstone.services.datasource.DataSourceSelector;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.phoenix.Block;

/**
 *
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public class DefaultDataSourceSelector
    extends AbstractLoggable
    implements DataSourceSelector, Block, Configurable, Initializable, Disposable
{
    private Configuration m_configuration;
    private Map           m_dataSources;

    public void configure( final Configuration configuration )
    {
        m_configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        m_dataSources = new HashMap();

        final Configuration[] dataSourceConfs =
            m_configuration.getChild( "data-sources" ).getChildren( "data-source" );

        for ( int i = 0; i < dataSourceConfs.length; i++ )
        {
            final Configuration dataSourceConf = dataSourceConfs[ i ];

            final String name = dataSourceConf.getAttribute( "name" );
            final String clazz = dataSourceConf.getAttribute( "type" );
            final String driver = dataSourceConf.getChild( "driver" ).getValue();

            Class.forName( driver );
            final DataSourceComponent component =
                (DataSourceComponent)Class.forName( clazz ).newInstance();
            setupLogger( component, name );
            component.configure( dataSourceConf );
            m_dataSources.put( name, component );

            if( getLogger().isInfoEnabled() )
            {
                getLogger().info( "DataSource " + name + " ready" );
            }
        }
    }

    public void dispose()
        throws Exception
    {
        final Iterator keys = m_dataSources.keySet().iterator();
        while ( keys.hasNext() )
        {
            final DataSourceComponent dsc =
                (DataSourceComponent)m_dataSources.get( keys.next() );
            if ( dsc instanceof Disposable )
            {
                ((Disposable)dsc).dispose();
            }
        }
    }

    public DataSourceComponent selectDataSource( final Object hint )
        throws ComponentException
    {
        return (DataSourceComponent)select( hint );
    }

    public Component select( final Object hint )
        throws ComponentException
    {
        final Component component = (Component)m_dataSources.get( hint );

        if( null == component )
        {
            throw new ComponentException( "Unable to provide implementation for " + hint );
        }

        return component;
    }

    public void release( final Component component )
    {
        //do nothing
    }

}
