/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.ExtensionFileFilter;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.io.IOUtil;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.tools.protocols.sar.SarURLConnection;

/**
 * An Installer is responsible for taking a URL for Sar
 * and installing it as appropriate.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Installer
    extends AbstractLoggable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( Installer.class );

    private static final String    SAR_INF      = "SAR-INF/";    
    private static final String    ASSEMBLY_XML = "assembly.xml";
    private static final String    CONFIG_XML   = "config.xml";
    private static final String    SERVER_XML   = "server.xml";
    
    /**
     * Install a Sar indicate by url to location.
     *
     * @param url the url of instalation
     * @exception InstallationException if an error occurs
     */
    public Installation install( final URL url )
        throws InstallationException
    {
        try 
        {
            final String message = REZ.getString( "installing-sar", url);
            getLogger().info( message );
                        
            // hack: should get the specified baseDirectory from Deployer
            File baseDirectory = new File( System.getProperty("phoenix.home", "..") + 
                                           File.separator + "apps" + 
                                           File.separator + extractName( url ) );
             
            URL installURL = url;
            
            if ( !isContext( url ) )
            {
                installURL = new URL( "sar:" + url.toExternalForm() + "|/" );
                extract( installURL, baseDirectory, "" );
            }
            else
            {
                // cannot install from remote a directory
                if ( !"file".equals( url.getProtocol() ) )
                {
                    final String msg = REZ.getString( "install-nonlocal", url);
                    throw new InstallationException( msg );
                }
            }
                        
            String inf = SAR_INF;
            final ArrayList codeBase = new ArrayList();
            
            if ( isDeprecated( installURL ) )
            {                
                final String msg = REZ.getString("deprecated-sar-format", url);
                getLogger().warn( msg );
                
                inf = "conf/";
                
                final URL blocksURL = new URL( installURL, "blocks/" );
                codeBase.addAll( getCodeBase( blocksURL ) );            
                
                final URL librariesURL = new URL( installURL, "lib/" );
                codeBase.addAll( getCodeBase( librariesURL ) );            
            }
            else 
            {                
                final URL librariesURL = new URL( installURL, SAR_INF + "lib/" );
                codeBase.addAll( getCodeBase( librariesURL ) );            
            }
            
            final URL config = new URL( installURL, inf + CONFIG_XML );
            final URL assembly = new URL( installURL, inf + ASSEMBLY_XML );
            final URL server = new URL( installURL, inf + SERVER_XML );                            
            
            final URL[] classPath = ( URL[] ) codeBase.toArray( new URL[0] );
            
            return new Installation( baseDirectory, config, assembly, server, classPath );
        } 
        catch ( MalformedURLException mue )
        {
            throw new InstallationException( mue.getMessage(), mue );
        } 
        catch ( IOException ioe )
        {            
            final String msg = REZ.getString( "install-nourl", url );
            throw new InstallationException( msg, ioe );
        }
    }

    public void uninstall( final Installation installation )
        throws InstallationException
    {
        final String message = REZ.getString( "uninstall-unsupported" );
        getLogger().error( message );
        //throw new InstallationException( message );
    }
    
    /**
     * Check application's packaging format.
     *
     * @param url the URL to package.
     * @return true if packaging format is deprecated.
     */ 
    private boolean isDeprecated( final URL url ) 
        throws MalformedURLException, IOException
    {                
        final String[] dirs = list( url );
        for ( int i = 0; i < dirs.length; i++ )
        {
            if ( dirs[i].equals( SAR_INF ) ) 
            {
                return false;
            }
        }

        return true;        
    }
    
    private boolean isContext( final URL url ) throws InstallationException
    {
        return url.getFile().endsWith( "/" );
    }
    
    private Collection getCodeBase( final URL url ) 
        throws MalformedURLException, IOException
    {
        final ArrayList urls = new ArrayList();
        final String[] libraryNames = list( url );

        for (int i = 0; i < libraryNames.length; i++ ) 
        {
            if ( libraryNames[i].endsWith( ".zip" ) || 
                 libraryNames[i].endsWith( ".jar" ) || 
                 libraryNames[i].endsWith( ".bar" ) ) 
            {
                urls.add( new URL( url, libraryNames[i] ) );
            }
        }
        
        return urls;
    }
        
    public String extractName( final URL url )
    {
        final String filename = url.getFile();        
        int first = filename.lastIndexOf( '/' );        
        int last = filename.lastIndexOf( '.' );
        if ( -1 == last ) last = filename.length();
        
        return filename.substring( first + 1, last );        
    }

    private String[] list( final URL url ) 
        throws MalformedURLException, IOException
    {
        String[] names = new String[0];
        
        if ( "sar".equals( url.getProtocol() ) ) 
        {            
            final SarURLConnection connection = 
                (SarURLConnection) url.openConnection();
            names = connection.list();            
        }
        
        if ( "file".equals( url.getProtocol() ) ) 
        {
            final File directory = new File( url.getFile() );
            names = directory.list();
        }
        
        return names;
    }
    
    /**
     * Download resource into specified location.
     *
     * @param url the resource to download from
     * @param file the file to download to
     * @exception IOException if an error occurs
     */
    private void download( final URL url, final File file )
        throws IOException
    {
        InputStream input = null;
        OutputStream output = null;
            
        try
        {
            file.getParentFile().mkdirs();
            output = new FileOutputStream( file );
            input = url.openStream();
            IOUtil.copy( input, output );
        }
        finally
        {
            IOUtil.shutdownStream( input );
            IOUtil.shutdownStream( output );
        }
    }                   
    
    /**
     * Extract files recursively to the local filesystem. Avoids the extraction 
     * of configuration files, blocks and libraries.
     * 
     * @param resource the resource from where data is copied.
     * @param directory the directory where resource is be copied.
     * @param context the relative identifier for a resource.
     */
    private void extract( final URL resource, final File directory, final String context ) 
        throws MalformedURLException, InstallationException, IOException
    {
        if ( SAR_INF.equals( context ) ||
             "blocks/".equals( context ) ||
             "lib/".equals( context ) ||
             "conf/".equals( context ) ) return;
        
        final URL url = new URL( resource, context );
        
        if ( isContext( url ) ) 
        {
            final String[] contexts = list( url );
            
            for (int i = 0; i < contexts.length; i++ ) 
            {                
                final File file = new File( directory, context );
                file.mkdirs();
                extract( url, file, contexts[i] );
            }            
        }
        else 
        {
            final File file = new File( directory, context );
            download( url, file );
        }
    }
}
