/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.UnresolvedPermission;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.property.PropertyUtil;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;

/**
 * Policy that extracts information from policy files.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
class DefaultPolicy
    extends AbstractPolicy
    implements Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultPolicy.class );

    private DefaultContext    m_context;

    protected DefaultPolicy( final File baseDirectory )
    {
        m_context = new DefaultContext( System.getProperties() );
        m_context.put( "/", File.separator );
        m_context.put( "app.home", baseDirectory );
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
            final String message = REZ.getString( "policy.notice.full-perms" );
            getLogger().info( message );
            final Permissions permissions = createPermissionSetFor( getInclusiveURL(), null );
            permissions.add( new java.security.AllPermission() );
        }
    }

    private URL getInclusiveURL()
    {
        try { return new URL( "file:/-" ); }
        catch( final MalformedURLException mue )
        {
            //Never happens
            return null;
        }
    }

    private void setupDefaultPermissions()
    {
        //these properties straight out ot ${java.home}/lib/security/java.policy
        final Permissions permissions = createPermissionSetFor( getInclusiveURL(), null );

        permissions.add( new PropertyPermission( "os.name", "read" ) );
        permissions.add( new PropertyPermission( "os.arch", "read" ) );
        permissions.add( new PropertyPermission( "os.version", "read" ) );
        permissions.add( new PropertyPermission( "file.separator", "read" ) );
        permissions.add( new PropertyPermission( "path.separator", "read" ) );
        permissions.add( new PropertyPermission( "line.separator", "read" ) );

        permissions.add( new PropertyPermission( "java.version", "read" ) );
        permissions.add( new PropertyPermission( "java.vendor", "read" ) );
        permissions.add( new PropertyPermission( "java.vendor.url", "read" ) );

        permissions.add( new PropertyPermission( "java.class.version", "read" ) );
        permissions.add( new PropertyPermission( "java.vm.version", "read" ) );
        permissions.add( new PropertyPermission( "java.vm.vendor", "read" ) );
        permissions.add( new PropertyPermission( "java.vm.name", "read" ) );

        permissions.add( new PropertyPermission( "java.specification.version", "read" ) );
        permissions.add( new PropertyPermission( "java.specification.vendor", "read" ) );
        permissions.add( new PropertyPermission( "java.specification.name", "read" ) );
        permissions.add( new PropertyPermission( "java.vm.specification.version", "read" ) );
        permissions.add( new PropertyPermission( "java.vm.specification.vendor", "read" ) );
        permissions.add( new PropertyPermission( "java.vm.specification.name", "read" ) );
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
                final KeyStore keyStore = KeyStore.getInstance( type );
                final URL url = new URL( location );
                final InputStream ins = url.openStream();

                keyStore.load( ins, null );

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
        }

        final Certificate[] signers = getSigners( signedBy, keyStoreName, keyStores );

        Permissions permissions = null;

        try { permissions = createPermissionSetFor( codeBase, signers ); }
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
        final Permission permission = createPermission( type, target, actions, signers );

        permissions.add( permission );
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

    private Permission createPermission( final String type,
                                         final String target,
                                         final String actions,
                                         final Certificate[] signers )
        throws ConfigurationException
    {
        if( null != signers )
        {
            return createUnresolvedPermission( type, target, actions, signers );
        }

        try
        {
            final Class c = Class.forName( type );

            Class paramClasses[] = null;
            Object params[] = null;

            if( null == actions && null == target )
            {
                paramClasses = new Class[ 0 ];
                params = new Object[ 0 ];
            }
            else if( null == actions )
            {
                paramClasses = new Class[1];
                paramClasses[0] = String.class;
                params = new Object[1];
                params[0] = target;
            }
            else
            {
                paramClasses = new Class[2];
                paramClasses[0] = String.class;
                paramClasses[1] = String.class;
                params = new Object[2];
                params[0] = target;
                params[1] = actions;
            }

            final Constructor constructor = c.getConstructor( paramClasses );
            final Object o = constructor.newInstance( params );
            return (Permission)o;
        }
        catch( final ClassNotFoundException cnfe )
        {
            return createUnresolvedPermission( type, target, actions, signers );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "policy.error.permission.create", type );
            throw new ConfigurationException( message, e );
        }
    }

    private Permission createUnresolvedPermission( final String type,
                                                   final String target,
                                                   final String actions,
                                                   final Certificate[] signers )
    {
        return new UnresolvedPermission( type, target, actions, signers );
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

            try { certificate = keyStore.getCertificate( alias ); }
            catch( final KeyStoreException kse )
            {
                final String message = REZ.getString( "policy.error.certificate.aquire", alias );
                throw new ConfigurationException( message, kse );
            }

            if( null == certificate )
            {
                final String message = REZ.getString( "policy.error.alias.missing", alias, keyStoreName );
                throw new ConfigurationException( message );
            }

            if( !certificateSet.contains( certificate ) )
            {
                certificateSet.add( certificate );
            }
        }

        return (Certificate[])certificateSet.toArray( new Certificate[ 0 ] );
    }
}
