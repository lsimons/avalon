/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.demo;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.containerkit.factory.ComponentFactory;
import org.apache.avalon.phoenix.containerkit.factory.DefaultComponentFactory;
import org.apache.avalon.phoenix.containerkit.kernel.AbstractServiceKernel;
import org.apache.avalon.phoenix.containerkit.lifecycle.ResourceProvider;
import org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData;
import org.apache.avalon.phoenix.containerkit.metadata.MetaDataBuilder;
import org.apache.avalon.phoenix.containerkit.metadata.PartitionMetaData;
import java.util.Map;
import java.util.HashMap;

/**
 * This is a simple ServiceKernel.
 *
 * <p>It loads components from the current ClassLoader.
 * The Assembly information is passed in via Configuration object
 * in a format similar to merged assembly.xml/config.xml from Phoenix.</p>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/01/18 16:43:43 $
 */
public class SimpleServiceKernel
    extends AbstractServiceKernel
    implements Parameterizable
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( SimpleServiceKernel.class );

    private MetaDataBuilder m_metaDataBuilder;
    private String m_configURL;

    public void parameterize( final Parameters parameters )
        throws ParameterException
    {
        m_configURL = parameters.getParameter( "config-url" );
    }

    public void initialize()
        throws Exception
    {
        super.initialize();

        m_metaDataBuilder = new SimpleMetaDataBuilder();
        setupLogger( getFactory(), "builder" );

        final Map parameters = new HashMap();
        parameters.put( SimpleMetaDataBuilder.CONFIG_LOCATION, m_configURL );
        final PartitionMetaData partition = m_metaDataBuilder.buildAssembly( parameters );
        final ComponentMetaData[] components = partition.getComponents();
        for( int i = 0; i < components.length; i++ )
        {
            final ComponentMetaData component = components[ i ];
            addComponent( component );
        }

        startupAllComponents();
    }

    public void dispose()
    {
        try
        {
            shutdownAllComponents();
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "provider-shutdown.error" );
            getLogger().warn( message, e );
        }
        super.dispose();
    }

    protected ComponentFactory prepareFactory()
    {
        final DefaultComponentFactory factory =
            new DefaultComponentFactory( getClass().getClassLoader() );
        setupLogger( factory, "factory" );
        return factory;
    }

    protected ResourceProvider prepareResourceProvider()
    {
        final SimpleResourceProvider resourceProvider =
            new SimpleResourceProvider( this, getFactory() );
        setupLogger( resourceProvider, "provider" );
        return resourceProvider;
    }
}
