/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.io.IOUtil;
import org.apache.avalon.excalibur.io.ExtensionFileFilter;
import org.apache.avalon.framework.logger.AbstractLoggable;

/**
 * An Installer is responsible for taking a URL (ie a jar/war/ear) and
 * installing it.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultInstaller
    extends AbstractLoggable
    implements Installer
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultInstaller.class );

    private File                     m_deployDirectory;

    private static final String    ASSEMBLY_XML  = "conf" + File.separator + "assembly.xml";
    private static final String    CONFIG_XML    = "conf" + File.separator + "config.xml";
    private static final String    SERVER_XML    = "conf" + File.separator + "server.xml";

    /**
     * Install a resource indicate by url to location.
     *
     * @param name the name of instalation
     * @param url the url of instalation
     * @exception InstallationException if an error occurs
     */
    public Installation install( final String name, final URL url )
        throws InstallationException
    {
        final File file = getFileFor( url );
        final String message = REZ.getString( "install.notice.installing", file, name );
        getLogger().info( message );

        URL[] classPath = null;
        File baseDirectory = null;

        if( file.isDirectory() )
        {
            baseDirectory = file;
            classPath = getClassPathForDirectory( file );
        }
        else
        {
            baseDirectory = getDestinationFor( name, file );

            final ArrayList codeBase = new ArrayList();
            expand( file, baseDirectory, codeBase );
            
            classPath = (URL[])codeBase.toArray( new URL[ 0 ] );
        }

        final URL config = getURL( new File( baseDirectory, CONFIG_XML ) );
        final URL assembly = getURL( new File( baseDirectory, ASSEMBLY_XML ) );
        final URL server = getURL( new File( baseDirectory, SERVER_XML ) );

        return new Installation( baseDirectory, config, assembly, server, classPath );
    }

    /**
     * undeploy a resource from a location.
     *
     * @param name the name of instalation
     * @exception InstallationException if an error occurs
     */
    public void uninstall( final Installation installation )
        throws InstallationException
    {
        final String message = REZ.getString( "install.error.uninstall.unsupported" );
        throw new InstallationException( message );
    }

    /**
     * Get File object for URL.
     * Currently it assumes that URL is a file URL but in the
     * future it will allow downloading of remote URLs thus enabling
     * a deploy from anywhere functionality.
     *
     * @param url the url of deployment
     * @return the File for deployment
     * @exception DeploymentException if an error occurs
     */
    private File getFileFor( final URL url )
        throws InstallationException
    {
        if( !url.getProtocol().equals( "file" ) )
        {
            final String message = REZ.getString( "install.error.install.nonlocal" );
            throw new InstallationException( message );
        }

        File file = new File( url.getFile() );
        file = file.getAbsoluteFile();

        if( !file.exists() )
        {
            final String message = REZ.getString( "install.error.install.nofile", file );
            throw new InstallationException( message );
        }

        return file;
    }

    /**
     * Expand matching files from a zip file to directory.
     *
     * @param file the zip file
     * @param directory the directory to expand to
     * @param filter the filter used to match files
     * @exception IOException if an error occurs
     */
    private void expand( final File file, final File directory, final ArrayList codeBase )
        throws InstallationException
    {
        try
        {
            final String message = REZ.getString( "install.notice.expanding", file, directory );
            getLogger().info( message );

            //final String url = file.toURL().toString();
            final String directoryUrl = directory.toURL().toString();
            final ZipFile zipFile = new ZipFile( file );

            directory.mkdirs();
            
            final Enumeration entries = zipFile.entries();
            while( entries.hasMoreElements() )
            {
                final ZipEntry entry = (ZipEntry)entries.nextElement();

                String entryName = entry.getName();
                if( entryName.startsWith( "/" ) )
                {
                    entryName = entryName.substring( 1 );
                }
                
                if( entry.isDirectory() ||
                    false == handleZipEntry( entryName, directoryUrl, codeBase ) )
                {
                    continue;
                }

                final String name = entryName.replace( '/', File.separatorChar );
                //TODO: Do this before filter and use getParentFile(), getName()
                final File destination = new File( directory, name );
                expandZipEntry( zipFile, entry, destination );
            }
        }
        catch( final IOException ioe )
        {
            final String message = REZ.getString( "install.error.expanding", file, directory );
            throw new InstallationException( message, ioe );
        }
        
        final String message = REZ.getString( "install.notice.expanded", file, directory );
        getLogger().info( message );
    }

    /**
     * Handle an entry in zip.
     * Zips/jars/bars extracted are added to codeBase list.
     * In future these zipz/jars/bars will not be extracted but accessed from inside
     * .sar.
     *
     * @param entryName the name of entry
     * @param url the url string for deployment directory
     * @param codeBase the list of codeBase entries
     * @return true if entry should be extracted, false otherwise
     * @exception IOException if an error occurs
     */
    private boolean handleZipEntry( final String entryName,
                                    final String url,
                                    final ArrayList codeBase )
        throws IOException
    {
        if( entryName.startsWith( "META-INF" ) )
        {
            return false;
        }
        else if( entryName.startsWith( "lib/" ) &&
                 ( entryName.endsWith( ".jar" ) ||
                   entryName.endsWith( ".zip" ) ) )
        {
            final URL classPathEntry = new URL( url + "/" + entryName );
            codeBase.add( classPathEntry );
        }
        else if( entryName.startsWith( "blocks/" ) && entryName.endsWith( ".bar" ) )
        {
            final URL classPathEntry = new URL( url + "/" + entryName );
            codeBase.add( classPathEntry );
        }
        else if ( entryName.startsWith( "conf/" ) )
        {
            return false;
        }
        
        return true;
    }

    private void expandZipEntry( final ZipFile zipFile, final ZipEntry entry, final File file )
        throws IOException
    {
        InputStream input = null;
        OutputStream output = null;
            
        try
        {
            file.getParentFile().mkdirs();
            output = new FileOutputStream( file );
            input = zipFile.getInputStream( entry );
            IOUtil.copy( input, output );
        }
        finally
        {
            IOUtil.shutdownStream( input );
            IOUtil.shutdownStream( output );
        }
    }

    /**
     * Get destination that .sar should be expanded to.
     *
     * @param name the name of server application
     * @param file the file object representing .sar archive
     * @return the destination to expand archive
     */
    private File getDestinationFor( final String name, final File file )
    {
        final String base =
            FileUtil.removeExtension( FileUtil.removePath( file.getName() ) );

        if( null != m_deployDirectory )
        {
            return (new File( m_deployDirectory, base )).getAbsoluteFile();
        }
        else
        {
            return (new File( file.getParentFile(), base )).getAbsoluteFile();
        }
    }

    /**
     * Get Classpath for application.
     *
     * @return the list of URLs in ClassPath
     */
    private URL[] getClassPathForDirectory( final File directory )
    {
        final File blockDir = new File( directory, "blocks" );
        final File libDir = new File( directory, "lib" );

        final ArrayList urls = new ArrayList();
        getURLs( urls, blockDir, new String[] { ".bar" } );
        getURLs( urls, libDir, new String[] { ".jar", ".zip" } );
        return (URL[])urls.toArray( new URL[0] );
    }

    /**
     * Add all matching files in directory to url list.
     *
     * @param urls the url list
     * @param directory the directory to scan
     * @param extentions the list of extensions to match
     * @exception MalformedURLException if an error occurs
     */
    private void getURLs( final ArrayList urls, final File directory, final String[] extensions )
    {
        final ExtensionFileFilter filter = new ExtensionFileFilter( extensions );
        final File[] files = directory.listFiles( filter );
        if( null == files ) return;
        for( int i = 0; i < files.length; i++ )
        {
            urls.add( getURL( files[ i ] ) );
        }
    }

    private URL getURL( final File file )
    {
        try { return file.toURL(); }
        catch( final MalformedURLException mue )
        {
            return null;
            //should never occur
        }
    }
}
