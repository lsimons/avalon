/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.deployer;

import java.io.File;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.phoenix.interfaces.DeploymentRecorder;
import org.apache.avalon.phoenix.interfaces.DeploymentException;
import org.apache.avalon.phoenix.tools.installer.Installation;
import org.apache.avalon.phoenix.tools.installer.FileDigest;

/**
 * Recorder for application deployment specific information (To avoid 
 * installation of applications every time Phoenix starts this class should 
 * persist the information in order to be reconstructed). 
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 */
public class DefaultDeploymentRecorder 
    extends AbstractLoggable 
    implements DeploymentRecorder, Parameterizable {

    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultDeploymentRecorder.class );

    private final static String PHOENIX_HOME      = System.getProperty( "phoenix.home", ".." );
    private final static String DEFAULT_APPS_PATH = PHOENIX_HOME + File.separator + "apps";
    private final static String DIGEST_FILE       = "install.log";
    
    private final static String SOURCE      = "source";
    private final static String DIRECTORY   = "directory";
    private final static String CONFIG      = "config";
    private final static String ASSEMBLY    = "assembly";
    private final static String SERVER      = "server";
    private final static String CLASSPATH   = "classpath";
    private final static String PATH        = "path";
    private final static String URL         = "url";
    private final static String DIGESTS     = "digests";
    private final static String DIGEST      = "digest";
    private final static String TIMESTAMP   = "timestamp";
    private final static String FILE        = "file";
    private final static String CHECKSUM    = "checksum";
    
    private File m_appsDirectory;
    private final DefaultConfigurationBuilder m_builder = new DefaultConfigurationBuilder();
    private final DefaultConfigurationSerializer m_serializer = new DefaultConfigurationSerializer();

    public void parameterize(Parameters parameters) 
        throws ParameterException
    {
        final String appsLocation =
            parameters.getParameter( "applications-directory", DEFAULT_APPS_PATH );
        m_appsDirectory = new File( appsLocation );
    }
    
    public synchronized void recordInstallation( final String name, final Installation installation ) 
       throws DeploymentException
    {
        final File file = getDigestFile( name );
            
        try 
        {            
            if ( null == installation ) 
            {
                //remove installation digest file
                if( file.exists() ) file.delete();
                return;
            }
            
            final DefaultConfiguration configuration = new DefaultConfiguration( "installation", null );
            configuration.setAttribute( SOURCE, installation.getSource().getCanonicalPath() );
            configuration.setAttribute( DIRECTORY, installation.getDirectory().getCanonicalPath() );
            configuration.setAttribute( CONFIG, installation.getConfig() );
            configuration.setAttribute( ASSEMBLY, installation.getAssembly() );
            configuration.setAttribute( SERVER, installation.getServer() );
            configuration.setAttribute( TIMESTAMP, Long.toString( installation.getTimestamp() ) );
            
            final DefaultConfiguration classpath = new DefaultConfiguration( CLASSPATH, null );
            final String[] urls = installation.getClassPath();
            for ( int i = 0; i < urls.length; i++ )
            {
                final DefaultConfiguration path = new DefaultConfiguration( PATH, null );
                path.setAttribute( URL, urls[i] );
                classpath.addChild( path );
            }
            configuration.addChild( classpath );
            
            final DefaultConfiguration digests = new DefaultConfiguration( DIGESTS, null );
            final FileDigest[] fileDigests = installation.getFileDigests();
            for ( int i = 0; i < fileDigests.length; i++ )
            {
                final DefaultConfiguration digest = new DefaultConfiguration( DIGEST, null );
                digest.setAttribute( FILE, fileDigests[i].getFile().getCanonicalPath() );
                digest.setAttribute( CHECKSUM, String.valueOf( fileDigests[i].getChecksum() ) );
                digests.addChild( digest );
            }            
            configuration.addChild( digests );
            
            m_serializer.serializeToFile( file, configuration );
        }
        catch ( Exception e )
        {
            final String message = REZ.getString( "recorder.warn.persist.failed", name );
            getLogger().warn( message, e );
            
            //delete traces
            file.delete();
        }
    }           

    public synchronized Installation fetchInstallation( final String name ) 
       throws DeploymentException
    {
        final File file = getDigestFile( name );
        if( !file.exists() ) return null;
            
        try 
        {
            final Configuration configuration = m_builder.buildFromFile( file );

            final File source = new File( configuration.getAttribute( SOURCE ) );
            final File directory = new File( configuration.getAttribute( DIRECTORY ) );
            final String config = configuration.getAttribute( CONFIG );
            final String assembly = configuration.getAttribute( ASSEMBLY );
            final String server = configuration.getAttribute( SERVER );
            final long timestamp = configuration.getAttributeAsLong( TIMESTAMP );

            final Configuration[] paths = configuration.getChild( CLASSPATH, true).getChildren( PATH );
            final String[] classPath = new String[ paths.length ];
            for ( int i = 0; i < paths.length; i++ )
            {
                classPath[i] = paths[i].getAttribute( URL );
            }            
            
            final Configuration[] digests = configuration.getChild( DIGESTS, true).getChildren( DIGEST );
            final FileDigest[] fileDigests = new FileDigest[digests.length];
            for ( int i = 0; i < digests.length; i++ )
            {
                final File installedFile = new File( digests[i].getAttribute( FILE ) );
                final long checksum = digests[i].getAttributeAsLong( CHECKSUM );
                final FileDigest digest = new FileDigest( installedFile, checksum );
            }            
            
            return new Installation( source, directory, config, assembly, server, classPath, fileDigests, timestamp );
        }
        catch ( Exception e )
        {
            final String message = REZ.getString( "recorder.warn.rebuild.failed", name );
            getLogger().warn( message, e );
            
            //force re-installation
            return null;
        }            
    }        
    
    private File getDigestFile( final String name )
    {
        return new File( m_appsDirectory, name + File.separator + DIGEST_FILE );
    }    
}
