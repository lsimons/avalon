/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.configuration.validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidator;

/**
 * Default ConfigurationValidator implementation that allows schemas to be plugged-in
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class DelegatingConfigurationValidator extends AbstractLogEnabled
    implements Configurable, Initializable, Disposable, ConfigurationValidator
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DelegatingConfigurationValidator.class );

    private Map m_blockTypeMap = Collections.synchronizedMap( new HashMap() );
    private Map m_delegates = new HashMap();
    private String m_supportedTypes;

    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] delegates = configuration.getChildren( "delegate" );
        final StringBuffer types = new StringBuffer();

        for( int i = 0; i < delegates.length; i++ )
        {
            final String type = delegates[i].getAttribute( "schema-type" );

            this.m_delegates.put( type,
                                  new DelegateEntry( type,
                                                     delegates[i].getAttribute( "class" ),
                                                     delegates[i] )
            );

            if( i > 0 )
            {
                types.append( "," );
            }

            types.append( type );
        }

        this.m_supportedTypes = types.toString();
    }

    public void initialize()
        throws Exception
    {
        for( Iterator i = m_delegates.values().iterator(); i.hasNext(); )
        {
            final DelegateEntry entry = ( DelegateEntry ) i.next();
            final Class clazz = Class.forName( entry.getClassName() );
            final ConfigurationValidator validator = ( ConfigurationValidator ) clazz.newInstance();

            ContainerUtil.enableLogging( validator, getLogger() );
            ContainerUtil.configure( validator, entry.getConfiguration() );
            ContainerUtil.initialize( validator );

            entry.setValidator( validator );
        }
    }

    public void dispose()
    {
        for( Iterator i = m_delegates.values().iterator(); i.hasNext(); )
        {
            ContainerUtil.dispose( ( ( DelegateEntry ) i.next() ).getValidator() );
        }
    }

    public void addSchema( String application, String block, String schemaType, String url )
        throws ConfigurationException
    {
        final DelegateEntry entry = ( DelegateEntry ) this.m_delegates.get( schemaType );

        if( entry == null )
        {
            final String msg = REZ.getString( "jarv.error.badtype",
                                              schemaType,
                                              this.m_supportedTypes );

            throw new ConfigurationException( msg );
        }

        entry.getValidator().addSchema( application, block, schemaType, url );
        this.m_blockTypeMap.put( createKey( application, block ), schemaType );
    }

    public boolean isFeasiblyValid( String application, String block, Configuration configuration )
        throws ConfigurationException
    {
        final ConfigurationValidator delegate = getDelegate( application, block );

        return delegate.isFeasiblyValid( application, block, configuration );
    }

    public boolean isValid( String application, String block, Configuration configuration )
        throws ConfigurationException
    {
        final ConfigurationValidator delegate = getDelegate( application, block );

        return delegate.isValid( application, block, configuration );
    }

    public void removeSchema( String application, String block )
    {
        try
        {
            getDelegate( application, block ).removeSchema( application, block );
        }
        catch( ConfigurationException e )
        {
            getLogger().warn( "Unable to remove schema [app: " + application
                              + ", block: " + block + "]",
                              e );
        }
    }

    private ConfigurationValidator getDelegate( String application, String block )
        throws ConfigurationException
    {
        final String type = ( String ) this.m_blockTypeMap.get( createKey( application, block ) );

        if( null == type )
        {
            final String msg = REZ.getString( "jarv.error.noschema", application, block );

            throw new ConfigurationException( msg );
        }

        return ( ( DelegateEntry ) this.m_delegates.get( type ) ).getValidator();
    }

    private String createKey( String application, String block )
    {
        return application + "." + block;
    }
}
