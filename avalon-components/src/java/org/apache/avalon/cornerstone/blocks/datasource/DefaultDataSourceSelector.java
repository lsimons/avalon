/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.datasource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.cornerstone.services.datasource.DataSourceSelector;
import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;

/**
 * The Default implementation for DataSourceSelector.
 * The Configuration is like this:
 *
 * <pre>
 * &lt;myBlock&gt;
 *   &lt;data-source name="<i>default</i>"
 *     class="<i>org.apache.avalon.excalibur.datasource.JdbcDataSource</i>"&gt;
 *     &lt;!-- configuration for JdbcDataSource --&gt;
 *     &lt;pool-controller min="<i>5</i>" max="<i>10</i>" connection-class="<i>my.overrided.ConnectionClass</i>"&gt;
 *       &lt;keep-alive&gt;select 1&lt;/keep-alive&gt;
 *     &lt;/pool-controller&gt;
 *     &lt;driver&gt;<i>com.database.jdbc.JdbcDriver</i>&lt;/driver&gt;
 *     &lt;dburl&gt;<i>jdbc:driver://host/mydb</i>&lt;/dburl&gt;
 *     &lt;user&gt;<i>username</i>&lt;/user&gt;
 *     &lt;password&gt;<i>password</i>&lt;/password&gt;
 *   &lt;/data-source&gt;
 * &lt;/myBlock&gt;
 * </pre>
 *
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.cornerstone.services.datasource.DataSourceSelector"
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @deprecated Use {@link org.apache.avalon.cornerstone.blocks.source.DefaultDataSourceSelector} instead.
 */
public class DefaultDataSourceSelector
    extends AbstractLogEnabled
    implements DataSourceSelector, Contextualizable, Configurable, Initializable, Disposable
{
    private Configuration m_configuration;
    private Map m_dataSources;
    private String m_blockName;

    public void contextualize( final Context context )
        throws ContextException
    {
        m_blockName = (String) context.get("block.name");
    }

    /**
     * @phoenix:configuration-schema type="relax-ng"
     */
    public void configure( final Configuration configuration )
    {
        m_configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        m_dataSources = new HashMap();

        Configuration[] dataSourceConfs = getDataSourceConfig();

        for( int i = 0; i < dataSourceConfs.length; i++ )
        {
            final Configuration dataSourceConf = dataSourceConfs[ i ];

            final String name = dataSourceConf.getAttribute( "name" );
            final String clazz = dataSourceConf.getAttribute( "class" );
            final String driver = dataSourceConf.getChild( "driver", true ).getValue( "" );

            final ClassLoader classLoader =
                Thread.currentThread().getContextClassLoader();

            DataSourceComponent component = null;
            if( null == classLoader )
            {
                if( !"".equals( driver ) )
                {
                    Class.forName( driver, true, Thread.currentThread().getContextClassLoader() );
                }

                component = (DataSourceComponent)Class.forName( clazz ).newInstance();
            }
            else
            {
                if( !"".equals( driver ) )
                {
                    classLoader.loadClass( driver );
                }

                component = (DataSourceComponent)classLoader.loadClass( clazz ).newInstance();
            }

            if( component instanceof LogEnabled )
            {
                setupLogger( component, name );
            }
            component.configure( dataSourceConf );
            m_dataSources.put( name, component );

            if( getLogger().isInfoEnabled() )
            {
                getLogger().info( "DataSource " + name + " ready" );
            }
        }
    }

    private Configuration[] getDataSourceConfig()
    {
        final Configuration head =
            m_configuration.getChild( "data-sources" );
        if( 0 != head.getChildren().length )
        {

            final String message =
                "WARNING: Child node <data-sources/> in " +
                "configuration of component named " + m_blockName +
                " has been deprecated. Please put <data-source/> elements" +
                " in root configuration element";
            getLogger().warn( message );
            System.out.println( message );
            return head.getChildren( "data-source" );
        }
        else
        {
            return m_configuration.getChildren( "data-source" );
        }
    }

    public void dispose()
    {
        final Iterator keys = m_dataSources.keySet().iterator();
        while( keys.hasNext() )
        {
            final DataSourceComponent dsc =
                (DataSourceComponent)m_dataSources.get( keys.next() );
            if( dsc instanceof Disposable )
            {
                ((Disposable)dsc).dispose();
            }
        }
    }

    public boolean hasComponent( final Object hint )
    {
        return m_dataSources.containsKey( hint );
    }

    public Component select( final Object hint )
        throws ComponentException
    {
        final Component component = (Component)m_dataSources.get( hint );

        if( null == component )
        {
            throw new ComponentException( "Unable to provide DataSourceComponent for " + hint );
        }

        return component;
    }

    public void release( final Component component )
    {
        //do nothing
    }

}
