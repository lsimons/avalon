/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.configuration;

import java.util.HashMap;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;

/**
 * Repository from which all configuration data is retrieved.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultConfigurationRepository
    implements ConfigurationRepository
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultConfigurationRepository.class );

    private final HashMap m_configurations = new HashMap();

    public synchronized void storeConfiguration( final String application,
                                                 final String block,
                                                 final Configuration configuration )
        throws ConfigurationException
    {
        final String name = application + "." + block;
        if( null == configuration )
        {
            m_configurations.remove( name );
        }
        else
        {
            m_configurations.put( name, configuration );
        }
    }

    public synchronized Configuration getConfiguration( final String application,
                                                        final String block )
        throws ConfigurationException
    {
        final String name = application + "." + block;
        final Configuration configuration = (Configuration)m_configurations.get( name );

        if( null == configuration )
        {
            final String message = REZ.getString( "config.error.noconfig", block, application );
            throw new ConfigurationException( message );
        }

        return configuration;
    }
    
    public synchronized void removeConfiguration( final String application,
                                                        final String block )
        throws ConfigurationException
    {
        final String name = application + "." + block;
        
        if( !m_configurations.containsKey( name ) )
        {
            final String message = REZ.getString( "config.error.remove", block, application );
            throw new ConfigurationException( message );
        }
        
        m_configurations.remove( name );
    }
}
