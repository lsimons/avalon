/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Permission;
import java.security.Permissions;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.components.util.ResourceUtil;
import org.apache.excalibur.policy.runtime.AbstractPolicy;

/**
 * Policy that extracts information from policy files.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
class DefaultPolicy
    extends AbstractPolicy
    implements Configurable, LogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultPolicy.class );

    private final File m_baseDirectory;
    private final File m_workDirectory;
    private DefaultContext m_context;
    private Logger m_logger;

    protected DefaultPolicy( final File baseDirectory,
                             final File workDirectory )
    {
        final HashMap map = new HashMap();
        map.putAll( System.getProperties() );
        m_context = new DefaultContext( map );
        m_context.put( "/", File.separator );
        m_context.put( "app.home", baseDirectory );
        m_workDirectory = workDirectory;
        m_baseDirectory = baseDirectory;
    }

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        setupDefaultPermissions();

        final Configuration[] keyStoreConfigurations = configuration.getChildren( "keystore" );
        final HashMap keyStores = configureKeyStores( keyStoreConfigurations );

        final Configuration[] grants = configuration.getChildren( "grant" );
        if( 0 != grants.length )
        {
            configureGrants( grants, keyStores );
        }
        else
        {
            final String message =
                REZ.getString( "policy.notice.full-perms" );
            m_logger.info( message );
            try
            {
                final Permissions permissions = createPermissionSetFor( new URL( "file:/-" ), null );
                permissions.add( new java.security.AllPermission() );
            }
            catch( MalformedURLException e )
            {
                //never happens
            }
        }
    }

    private HashMap configureKeyStores( final Configuration[] configurations )
        throws ConfigurationException
    {
        final HashMap keyStores = new HashMap();

        for( int i = 0; i < configurations.length; i++ )
        {
            final Configuration configuration = configurations[ i ];
            final String type = configuration.getAttribute( "type" );
            final String location = configuration.getAttribute( "location" );
            final String name = configuration.getAttribute( "name" );

            try
            {
                final KeyStore keyStore =
                    createKeyStore( type, new URL( location ) );

                keyStores.put( name, keyStore );
            }
            catch( final Exception e )
            {
                final String message = REZ.getString( "policy.error.keystore.config", name );
                throw new ConfigurationException( message, e );
            }
        }

        return keyStores;
    }

    private void configureGrants( final Configuration[] configurations,
                                  final HashMap keyStores )
        throws ConfigurationException
    {
        for( int i = 0; i < configurations.length; i++ )
        {
            configureGrant( configurations[ i ], keyStores );
        }
    }

    private void configureGrant( final Configuration configuration, final HashMap keyStores )
        throws ConfigurationException
    {
        //<grant signed-by="Fred" code-base="file:${sar.home}/blocks/*" key-store="foo-keystore">
        //<permission class="java.io.FilePermission" target="/tmp/*" action="read,write" />
        //</grant>

        final String signedBy = configuration.getAttribute( "signed-by", null );
        final String keyStoreName = configuration.getAttribute( "key-store", null );

        String codeBase = configuration.getAttribute( "code-base", null );
        if( null != codeBase )
        {
            codeBase = expand( codeBase );
            codeBase = ResourceUtil.expandSarURL( codeBase,
                                                  m_baseDirectory,
                                                  m_workDirectory );
        }

        final Certificate[] signers = getSigners( signedBy, keyStoreName, keyStores );

        Permissions permissions = null;
        try
        {
            permissions = createPermissionSetFor( codeBase, signers );
        }
        catch( final MalformedURLException mue )
        {
            final String message = REZ.getString( "policy.error.codebase.malformed", codeBase );
            throw new ConfigurationException( message, mue );
        }

        configurePermissions( configuration.getChildren( "permission" ),
                              permissions,
                              keyStores );
    }

    private void configurePermissions( final Configuration[] configurations,
                                       final Permissions permissions,
                                       final HashMap keyStores )
        throws ConfigurationException
    {
        for( int i = 0; i < configurations.length; i++ )
        {
            configurePermission( configurations[ i ], permissions, keyStores );
        }
    }

    private void configurePermission( final Configuration configuration,
                                      final Permissions permissions,
                                      final HashMap keyStores )
        throws ConfigurationException
    {
        final String type = configuration.getAttribute( "class" );
        final String actions = configuration.getAttribute( "actions", null );
        final String signedBy = configuration.getAttribute( "signed-by", null );
        final String keyStoreName = configuration.getAttribute( "key-store", null );

        String target = configuration.getAttribute( "target", null );
        if( null != target )
        {
            target = expand( target );
        }

        final Certificate[] signers = getSigners( signedBy, keyStoreName, keyStores );
        try
        {
            final Permission permission =
                createPermission( type, target, actions, signers );
            permissions.add( permission );
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( e.getMessage(), e );
        }
    }

    private String expand( final String value )
        throws ConfigurationException
    {
        try
        {
            final Object resolvedValue = PropertyUtil.resolveProperty( value, m_context, false );
            return resolvedValue.toString();
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "policy.error.property.resolve", value );
            throw new ConfigurationException( message, e );
        }
    }

    private Certificate[] getSigners( final String signedBy,
                                      String keyStoreName,
                                      final HashMap keyStores )
        throws ConfigurationException
    {
        if( null != signedBy && null == keyStoreName )
        {
            keyStoreName = "default";
        }

        Certificate[] signers = null;

        if( null != signedBy )
        {
            signers = getCertificates( signedBy, keyStoreName, keyStores );
        }

        return signers;
    }

    private Certificate[] getCertificates( final String signedBy,
                                           final String keyStoreName,
                                           final HashMap keyStores )
        throws ConfigurationException
    {
        final KeyStore keyStore = (KeyStore)keyStores.get( keyStoreName );

        if( null == keyStore )
        {
            final String message = REZ.getString( "policy.error.keystore.aquire", keyStoreName );
            throw new ConfigurationException( message );
        }

        final ArrayList certificateSet = new ArrayList();

        final StringTokenizer tokenizer = new StringTokenizer( signedBy, "," );

        while( tokenizer.hasMoreTokens() )
        {
            final String alias = ((String)tokenizer.nextToken()).trim();
            Certificate certificate = null;

            try
            {
                certificate = keyStore.getCertificate( alias );
            }
            catch( final KeyStoreException kse )
            {
                final String message = REZ.getString( "policy.error.certificate.aquire", alias );
                throw new ConfigurationException( message, kse );
            }

            if( null == certificate )
            {
                final String message =
                    REZ.getString( "policy.error.alias.missing",
                                   alias,
                                   keyStoreName );
                throw new ConfigurationException( message );
            }

            if( !certificateSet.contains( certificate ) )
            {
                certificateSet.add( certificate );
            }
        }

        return (Certificate[])certificateSet.toArray( new Certificate[ 0 ] );
    }

    protected void error( final String message,
                          final Throwable throwable )
    {
        m_logger.error( message, throwable );
    }

    protected void debug( final String message )
    {
        m_logger.debug( message );
    }

    protected boolean isDebugEnabled()
    {
        return m_logger.isDebugEnabled();
    }
}
