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
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
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

    private static final String    OLD_ASSEMBLY_XML  = "conf" + File.separator + "assembly.xml";
    private static final String    OLD_CONFIG_XML    = "conf" + File.separator + "config.xml";
    private static final String    OLD_SERVER_XML    = "conf" + File.separator + "server.xml";
    private static final String    OLD_BLOCKS        = "blocks";
    private static final String    OLD_LIB           = "lib";

    private static final String    META_INF     = "META-INF";   
    private static final String    SAR_INF      = "SAR-INF";   
    private static final String    LIB          = "SAR-INF/lib";   
    private static final String    ASSEMBLY_XML = "SAR-INF/assembly.xml";
    private static final String    CONFIG_XML   = "SAR-INF/config.xml";
    private static final String    SERVER_XML   = "SAR-INF/server.xml";

    //The names on the native filesystem
    private static final String    FS_CONFIG_XML   = "SAR-INF" + File.separator + "config.xml";
    private static final String    FS_SERVER_XML   = "SAR-INF" + File.separator + "server.xml";

    /**
     * Uninstall the Sar designated installation.
     * Currently unimplemented.
     *
     * @param installation the installation
     * @exception InstallationException if an error occurs
     */
    public void uninstall( final Installation installation )
        throws InstallationException
    {
        final String message = REZ.getString( "uninstall-unsupported" );
        getLogger().error( message );
        //throw new InstallationException( message );
    }

    /**
     * Install the Sar designated by url.
     *
     * @param url the url of instalation
     * @exception InstallationException if an error occurs
     */
    public Installation install( final URL url )
        throws InstallationException
    {
        lock();
        try
        {
            final String notice = REZ.getString( "installing-sar", url );
            getLogger().info( notice );
            
            final File file = getFileFor( url );
            if( file.isDirectory() )
            {
                final String message = REZ.getString( "deprecated-sar-format", url );
                System.err.println( message );
                getLogger().warn( message );
                return installDeprecated( file );
            }

            //Get Zipfile representing .sar file
            final ZipFile zipFile = new ZipFile( file );
            if( isDeprecated( zipFile ) )
            {
                final String message = REZ.getString( "deprecated-sar-format", url );
                System.err.println( message );
                getLogger().warn( message );
                return installDeprecated( url, file, zipFile );
            }
            else
            {
                return install( url, file, zipFile );
            }
        }
        catch( final IOException ioe )
        {
            final String message = REZ.getString( "bad-zip-file", url );
            throw new InstallationException( message, ioe );
        }
        finally
        {
            unlock();
        }
    }

    /**
     * Describe 'isDeprecated' method here.
     *
     * @param zipFile a value of type 'ZipFile'
     * @return a value of type 'boolean'
     */
    private boolean isDeprecated( final ZipFile zipFile )
        throws InstallationException
    {
        boolean oldStyle = false;
        boolean newStyle = false;

        final Enumeration entries = zipFile.entries();
        while( entries.hasMoreElements() )
        {
            final ZipEntry entry = (ZipEntry)entries.nextElement();
            final String name = fixName( entry.getName() );

            if( name.startsWith( OLD_BLOCKS ) || 
                name.startsWith( OLD_LIB ) || 
                name.equals( "conf/assembly.xml" ) ||
                name.equals( "conf/config.xml" ) ||
                name.equals( "conf/server.xml" ) )
            {
                oldStyle = true;
            }

            if( name.startsWith( SAR_INF ) )
            {
                newStyle = true;
            }
        }

        if( oldStyle && newStyle )
        {
            final String message = REZ.getString( "mixed-sar" );
            throw new InstallationException( message );
        }
        else if( !oldStyle && !newStyle )
        {
            final String message = REZ.getString( "invalid-sar" );
            throw new InstallationException( message );
        }
        else
        {
            return oldStyle;
        }
    }

    /**
     * Utility method to lock repository to disallow other installers to access it.
     * Currently a no-op.
     */
    private void lock()
    {
    }
    
    /**
     * Utility method to unlock repository to allow other installers to access it.
     * Currently a no-op.
     */
    private void unlock()
    {
    }

    /**
     * Install a new style sar. 
     *
     * @param url the url designator of sar
     * @param file the file of sar
     * @param zipFile the ZipFile representing sar
     * @return the Installation object
     */
    private Installation install( final URL url, 
                                  final File file, 
                                  final ZipFile zipFile )
        throws InstallationException
    {
        final File directory = getDestinationFor( file );

        //Question: Should we be making sure that 
        //this directory is created?
        directory.mkdirs();

        final String baseURL = "sar:" + url.toExternalForm() + "|/";

        final ArrayList jars = new ArrayList();

        final Enumeration entries = zipFile.entries();
        while( entries.hasMoreElements() )
        {
            final ZipEntry entry = (ZipEntry)entries.nextElement();
            if( entry.isDirectory() ) continue;

            final String name = fixName( entry.getName() );

            boolean expand = true;

            //Don't expand anything below SAR-INF directory unless they
            //are the config.xml or server.xml files which will be expanded
            //as a special case atm.
            if( name.startsWith( SAR_INF ) && 
                !name.equals( SERVER_XML ) && 
                !name.equals( CONFIG_XML ) )
            {
                expand = false;

                //Grab all the jar files in the 
                //directory SAR-INF/lib
                if( name.startsWith( LIB ) && 
                    name.endsWith( ".jar" ) && 
                    LIB.length() == name.lastIndexOf( "/" ) )
                {
                    final URL jar = createURL( baseURL + name );
                    jars.add( jar );
                }
            }

            if( name.startsWith( META_INF ) )
            {
                expand = false;
            }

            //Expand the file if necesasry and issue a warning if there is
            //a file in the way
            if( expand )
            {
                final File destination = new File( directory, name );
                if( !destination.exists() )
                {
                    expandZipEntry( zipFile, entry, destination );
                }
                else
                {
                    final String message = REZ.getString( "file-in-the-way", url, name, directory );
                    getLogger().warn( message );
                }
            }
        }

        //Prepare and create Installation
        final URL[] classPath = (URL[])jars.toArray( new URL[ 0 ] );
        final URL assembly = createURL( baseURL + ASSEMBLY_XML );
        final URL config = getURL( new File( directory, FS_CONFIG_XML ) );
        final URL server = getURL( new File( directory, FS_SERVER_XML ) );

        return new Installation( directory, config, assembly, server, classPath );
    }

    /**
     * Utility method to create a URL.
     * Need to make sure creation does not throw a MalformedURLException.
     *
     * @param url the URL strun
     * @return the created URL object
     */
    private URL createURL( final String url )
    {
        try
        {
            return new URL( url );
        }
        catch( final MalformedURLException mue )
        {
            //Should never occur
            return null;
        }
    }

    /**
     * Fix the specified name so that it does not start 
     * with a "/" character.
     *
     * @param name the name to fix
     * @return the name stripped of initial "/" if necessary
     */
    private String fixName( final String name )
    {
        if( name.startsWith( "/" ) ) return name.substring( 1 );
        else return name;
    }

    /**
     * Create an Installation from a Sar in deprecated format.
     *
     * @param file the file designating the sar
     * @param zipFile the ZipFile object for sar
     * @return the Installaiton
     */
    private Installation installDeprecated( final URL url, 
                                            final File file, 
                                            final ZipFile zipFile )
        throws InstallationException
    {
        final File directory = getDestinationFor( file );

        final Enumeration entries = zipFile.entries();
        while( entries.hasMoreElements() )
        {
            final ZipEntry entry = (ZipEntry)entries.nextElement();
            if( entry.isDirectory() ) continue;

            final String name = fixName( entry.getName() );

            //Expand the file if not in META-INF directory
            if( !name.startsWith( META_INF ) )
            {
                final File destination = new File( directory, name );
                if( !destination.exists() )
                {
                    expandZipEntry( zipFile, entry, destination );
                }
                else
                {
                    final String message = REZ.getString( "file-in-the-way", url, name, directory );
                    getLogger().warn( message );
                }
            }
        }

        return installDeprecated( directory );
    }

    /**
     * Create an Installation from a Sar in deprecated format.
     *
     * @param directory the directory containing extracted .sar
     * @return the Installaiton
     */
    private Installation installDeprecated( final File directory )
        throws InstallationException
    {
        final URL[] classPath = getClassPathForDirectory( directory );
        final URL config = getURL( new File( directory, OLD_CONFIG_XML ) );
        final URL assembly = getURL( new File( directory, OLD_ASSEMBLY_XML ) );
        final URL server = getURL( new File( directory, OLD_SERVER_XML ) );

        return new Installation( directory, config, assembly, server, classPath );
    }
   
    /**
     * Get File object for URL.
     * Currently it assumes that URL is a file URL but in the
     * future it will allow downloading of remote URLs thus enabling
     * a deploy from anywhere functionality.
     *
     * @param url the url of deployment
     * @return the File for deployment
     * @exception InstallationException if an error occurs
     */
    private File getFileFor( final URL url )
        throws InstallationException
    {
        if( !url.getProtocol().equals( "file" ) )
        {
            final String message = REZ.getString( "install-nonlocal", url );
            throw new InstallationException( message );
        }

        File file = new File( url.getFile() );
        file = file.getAbsoluteFile();

        if( !file.exists() )
        {
            final String message = REZ.getString( "install-nofile", file );
            throw new InstallationException( message );
        }

        return file;
    }
    
    /**
     * Expand specified entry from specified zipfile into specified location.
     *
     * @param zipFile the zip file to extract from
     * @param entry the zip entry
     * @param file the file to extract to
     * @exception IOException if an error occurs
     */
    private void expandZipEntry( final ZipFile zipFile, final ZipEntry entry, final File file )
        throws InstallationException
    {
        if( entry.isDirectory() ) return;

        InputStream input = null;
        OutputStream output = null;

        try
        {
            file.getParentFile().mkdirs();
            output = new FileOutputStream( file );
            input = zipFile.getInputStream( entry );
            IOUtil.copy( input, output );
        }
        catch( final IOException ioe )
        {
            final String message = 
                REZ.getString( "failed-to-expand", entry.getName(), file, ioe.getMessage() );
            throw new InstallationException( message, ioe );
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
     * @param file the file object representing .sar archive
     * @return the destination to expand archive
     */
    private File getDestinationFor( final File file )
    {
        final String base =
            FileUtil.removeExtension( FileUtil.removePath( file.getName() ) );

        return (new File( file.getParentFile(), base )).getAbsoluteFile();
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

    /**
     * Utility method to extract URL from file in safe manner.
     *
     * @param file the file
     * @return the URL representation of file
     */
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
