/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.ExtensionFileFilter;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.io.IOUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * An Installer is responsible for taking a URL for Sar
 * and installing it as appropriate.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class Installer
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( Installer.class );

    private static final String OLD_ASSEMBLY_XML = "conf" + File.separator + "assembly.xml";

    private static final String OLD_CONFIG_XML = "conf" + File.separator + "config.xml";
    private static final String OLD_SERVER_XML = "conf" + File.separator + "server.xml";
    private static final String OLD_BLOCKS = "blocks";
    private static final String OLD_LIB = "lib";

    private static final String META_INF = "META-INF";
    private static final String SAR_INF = "SAR-INF";
    private static final String LIB = "SAR-INF/lib";
    private static final String CLASSES = "SAR-INF/classes/";
    private static final String ASSEMBLY_XML = "SAR-INF/assembly.xml";
    private static final String CONFIG_XML = "SAR-INF/config.xml";
    private static final String SERVER_XML = "SAR-INF/server.xml";
    private static final String ENV_XML = "SAR-INF/environment.xml";

    //The names on the native filesystem
    private static final String FS_CONFIG_XML = "SAR-INF" + File.separator + "config.xml";
    private static final String FS_ASSEMBLY_XML = "SAR-INF" + File.separator + "assembly.xml";
    private static final String FS_SERVER_XML = "SAR-INF" + File.separator + "server.xml";
    private static final String FS_ENV_XML = "SAR-INF" + File.separator + "environment.xml";

    /**
     * Uninstall the Sar designated installation.
     *
     * @param installation the installation
     * @exception InstallationException if an error occurs
     */
    public void uninstall( final Installation installation )
        throws InstallationException
    {
        final FileDigest[] infos = installation.getFileDigests();
        final Checksum checksum = new CRC32();

        if( infos != null )
        {
            for( int i = 0; i < infos.length; i++ )
            {
                final File file = infos[ i ].getFile();
                final File parent = file.getParentFile();

                final String message = REZ.getString( "skip-removal", file );

                if( file.exists() )
                {
                    if( file.lastModified() <= installation.getTimestamp() )
                    {
                        getLogger().debug( message );
                        continue;
                    }

                    checksum( file, checksum );

                    if( checksum.getValue() != infos[ i ].getChecksum() )
                    {
                        getLogger().debug( message );
                        continue;
                    }

                    file.delete();
                    if( 0 == parent.list().length ) parent.delete();
                }
            }
        }
    }

    /**
     * Utility method to compute the checksum for a given file.
     * @param file the computed file.
     * @param checksum the checksum algorithm.
     */
    private void checksum( final File file, final Checksum checksum )
    {
        checksum.reset();

        InputStream input = null;
        try
        {
            input = new CheckedInputStream( new FileInputStream( file ), checksum );
            IOUtil.toByteArray( input );
        }
        catch( final IOException ioe )
        {
            final String message = REZ.getString( "checksum-failure", file );
            getLogger().warn( message );
        }
        finally
        {
            IOUtil.shutdownStream( input );
        }
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
     * Check if zipfile represents the deprecated sar format or
     * whether it conforms to new format of using "SAR-INF/".
     *
     * @param zipFile the zipfile
     * @return true if old format, else false
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

        final ArrayList digests = new ArrayList();
        final ArrayList jars = new ArrayList();

        boolean classesAdded = false;

        final Enumeration entries = zipFile.entries();
        while( entries.hasMoreElements() )
        {
            final ZipEntry entry = (ZipEntry)entries.nextElement();
            if( entry.isDirectory() ) continue;

            final String name = fixName( entry.getName() );

            boolean expand = true;
            boolean isJar = false;

            if( name.startsWith( CLASSES ) )
            {
                expand = false;
                if( false == classesAdded )
                {
                    final String classes = 
                        "jar:" + getURLAsString( file ) + "!/" + CLASSES;
                    jars.add( classes );
                    classesAdded = true;
                }
            }

            //Grab all the jar files in the
            //directory SAR-INF/lib
            if( name.startsWith( LIB ) &&
                name.endsWith( ".jar" ) &&
                LIB.length() == name.lastIndexOf( "/" ) )
            {
                isJar = true;
                //HACK: expand files until ClassLoader works properly
                //final String jar = baseURL + name;
                //jars.add( jar );
            }

            //Don't expand anything below SAR-INF directory unless they
            //are the config.xml or server.xml files which will be expanded
            //as a special case atm.
            //NOTE: We expand everything at this time now but this will change
            //in the future

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
                    expandZipEntry( zipFile, entry, destination, digests );
                }
                else
                {
                    final String message = REZ.getString( "file-in-the-way", url, name, directory );
                    getLogger().warn( message );
                }

                if( isJar )
                {
                    jars.add( getURLAsString( destination ) );
                }
            }
        }

        //Retrieve name of environment file
        //need to check existence to support backwards compatability
        File envFile = new File( directory, FS_ENV_XML );
        if( !envFile.exists() )
        {
            final String message = REZ.getString( "deprecated-environment-xml", url );
            System.err.println( message );
            getLogger().warn( message );
            envFile = new File( directory, FS_SERVER_XML );
        }

        //Prepare and create Installation
        final String[] classPath = (String[])jars.toArray( new String[ 0 ] );

        //final String assembly = "jar:" + getURLAsString( file ) + "!/" + ASSEMBLY_XML;
        final String assembly = getURLAsString( new File( directory, FS_ASSEMBLY_XML ) );
        final String config = getURLAsString( new File( directory, FS_CONFIG_XML ) );
        final String environment = getURLAsString( envFile );
        final FileDigest[] fileDigests = (FileDigest[])digests.toArray( new FileDigest[ 0 ] );
        final long timestamp = System.currentTimeMillis();

        return new Installation( file, directory, config, assembly, environment,
                                 classPath, fileDigests, timestamp );
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
        if( name.startsWith( "/" ) )
            return name.substring( 1 );
        else
            return name;
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
        final ArrayList digests = new ArrayList();
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
                    expandZipEntry( zipFile, entry, destination, digests );
                }
                else
                {
                    final String message = REZ.getString( "file-in-the-way", url, name, directory );
                    getLogger().warn( message );
                }
            }
        }

        final String[] classPath = getClassPathForDirectory( directory );
        final String config = getURLAsString( new File( directory, OLD_CONFIG_XML ) );
        final String assembly = getURLAsString( new File( directory, OLD_ASSEMBLY_XML ) );
        final String server = getURLAsString( new File( directory, OLD_SERVER_XML ) );
        final FileDigest[] fileDigests = (FileDigest[])digests.toArray( new FileDigest[ 0 ] );
        final long timestamp = System.currentTimeMillis();

        return new Installation( file, directory, config, assembly, server, classPath, fileDigests, timestamp );
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
        final String[] classPath = getClassPathForDirectory( directory );
        final String config = getURLAsString( new File( directory, OLD_CONFIG_XML ) );
        final String assembly = getURLAsString( new File( directory, OLD_ASSEMBLY_XML ) );
        final String server = getURLAsString( new File( directory, OLD_SERVER_XML ) );
        final long timestamp = System.currentTimeMillis();

        return new Installation( directory, directory, config, assembly, server, classPath, null, timestamp );
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
            final String message = REZ.getString( "install-nourl", file );
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
     * @param digests the digests for the expanded files.
     * @exception IOException if an error occurs
     */
    private void expandZipEntry( final ZipFile zipFile,
                                 final ZipEntry entry,
                                 final File file,
                                 final ArrayList digests )
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

        final long checksum = entry.getCrc();
        final long modified = file.lastModified();
        final FileDigest info = new FileDigest( file, checksum );

        digests.add( info );
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

        return ( new File( file.getParentFile(), base ) ).getAbsoluteFile();
    }

    /**
     * Get Classpath for application.
     *
     * @return the list of URLs in ClassPath
     */
    private String[] getClassPathForDirectory( final File directory )
    {
        final File blockDir = new File( directory, "blocks" );
        final File libDir = new File( directory, "lib" );

        final ArrayList urls = new ArrayList();
        getURLsAsStrings( urls, blockDir, new String[]{ ".bar" } );
        getURLsAsStrings( urls, libDir, new String[]{ ".jar", ".zip" } );
        return (String[])urls.toArray( new String[ 0 ] );
    }

    /**
     * Add all matching files in directory to url list.
     *
     * @param urls the url list
     * @param directory the directory to scan
     * @param extentions the list of extensions to match
     * @exception MalformedURLException if an error occurs
     */
    private void getURLsAsStrings( final ArrayList urls, final File directory, final String[] extensions )
    {
        final ExtensionFileFilter filter = new ExtensionFileFilter( extensions );
        final File[] files = directory.listFiles( filter );
        if( null == files ) return;
        for( int i = 0; i < files.length; i++ )
        {
            urls.add( getURLAsString( files[ i ] ) );
        }
    }

    /**
     * Utility method to extract URL from file in safe manner.
     *
     * @param file the file
     * @return the URL representation of file
     */
    private String getURLAsString( final File file )
    {
        try
        {
            return file.toURL().toExternalForm();
        }
        catch( final MalformedURLException mue )
        {
            return null;
            //should never occur
        }
    }
}
