/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.deployer.installer;

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
 * @version $Revision: 1.5 $ $Date: 2002/05/15 12:16:51 $
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

    //The names on the native filesystem
    private static final String FS_CONFIG_XML = "SAR-INF" + File.separator + "config.xml";
    private static final String FS_ASSEMBLY_XML = "SAR-INF" + File.separator + "assembly.xml";
    private static final String FS_SERVER_XML = "SAR-INF" + File.separator + "server.xml";
    private static final String FS_ENV_XML = "SAR-INF" + File.separator + "environment.xml";
    private static final String FS_CLASSES =
        "SAR-INF" + File.separator + "classes" + File.separator;

    /**
     * Base directory in which to install extracted application.
     */
    private File m_baseDirectory;

    /**
     * Base directory in which to install temporary/work files.
     */
    private File m_baseWorkDirectory;

    /**
     * Set the baseDirectory in which to install applications.
     *
     * @param baseDirectory the baseDirectory in which to install applications.
     */
    public void setBaseDirectory( File baseDirectory )
    {
        m_baseDirectory = baseDirectory;
    }

    /**
     * Set the baseDirectory in which to install applications temporary Data.
     *
     * @param baseWorkDirectory the baseDirectory in which to install applications temporary Data.
     */
    public void setBaseWorkDirectory( File baseWorkDirectory )
    {
        m_baseWorkDirectory = baseWorkDirectory;
    }

    /**
     * Uninstall the Sar designated installation.
     *
     * @param installation the installation
     * @throws InstallationException if an error occurs
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
                    if( 0 == parent.list().length )
                    {
                        parent.delete();
                    }
                }
            }
        }

        try
        {
            FileUtil.deleteDirectory( installation.getWorkDirectory() );
        }
        catch( final IOException ioe )
        {
            final String message =
                REZ.getString( "nodelete-workdir.error",
                               installation.getWorkDirectory(),
                               ioe.getMessage() );
            getLogger().warn( message, ioe );
        }
    }

    /**
     * Install the Sar designated by url.
     *
     * @param url the url of instalation
     * @throws InstallationException if an error occurs
     */
    public Installation install( final String name, final URL url )
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
                final String message =
                    REZ.getString( "deprecated-sar-format", url );
                System.err.println( message );
                getLogger().warn( message );
                return installDeprecated( file );
            }

            //Get Zipfile representing .sar file
            final ZipFile zipFile = new ZipFile( file );
            if( isDeprecated( zipFile ) )
            {
                final String message =
                    REZ.getString( "deprecated-sar-format", url );
                System.err.println( message );
                getLogger().warn( message );
                return installDeprecated( url, file, zipFile );
            }
            else
            {
                return install( name, url, file, zipFile );
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
    private Installation install( final String name,
                                  final URL url,
                                  final File file,
                                  final ZipFile zipFile )
        throws InstallationException
    {
        final File directory =
            new File( m_baseDirectory, name ).getAbsoluteFile();

        //Question: Should we be making sure that
        //this directory is created?
        directory.mkdirs();

        final ArrayList digests = new ArrayList();
        final ArrayList jars = new ArrayList();

        final File workDir =
            getRelativeWorkDir( m_baseWorkDirectory, name );

        expandZipFile( zipFile, directory, workDir, jars, digests, url );

        //Retrieve name of environment file
        //need to check existence to support backwards compatability
        File envFile = new File( directory, FS_ENV_XML );
        if( !envFile.exists() )
        {
            final String message =
                REZ.getString( "deprecated-environment-xml", url );
            System.err.println( message );
            getLogger().warn( message );
            envFile = new File( directory, FS_SERVER_XML );
        }

        //Prepare and create Installation
        final String[] classPath =
            (String[])jars.toArray( new String[ jars.size() ] );

        final String assembly = getURLAsString( new File( directory, FS_ASSEMBLY_XML ) );
        final String config = getURLAsString( new File( directory, FS_CONFIG_XML ) );
        final String environment = getURLAsString( envFile );
        final FileDigest[] fileDigests = (FileDigest[])digests.toArray( new FileDigest[ 0 ] );
        final long timestamp = System.currentTimeMillis();

        return new Installation( file, directory, workDir,
                                 config, assembly, environment,
                                 classPath, fileDigests, timestamp );
    }

    /**
     * Expand the specified Zip file.
     *
     * @param zipFile the zip file
     * @param directory the directory where to extract non-jar,
     *        non-classes files
     * @param workDir the directory to extract classes/jar files
     * @param classpath the list to add classpath entries to
     * @param digests the list to add file digests to
     * @param url the url of deployment (for error reporting purposes)
     * @throws InstallationException if an error occurs extracting files
     */
    private void expandZipFile( final ZipFile zipFile,
                                final File directory,
                                final File workDir,
                                final ArrayList classpath,
                                final ArrayList digests,
                                final URL url )
        throws InstallationException
    {
        final Enumeration entries = zipFile.entries();
        while( entries.hasMoreElements() )
        {
            final ZipEntry entry = (ZipEntry)entries.nextElement();
            final String name = fixName( entry.getName() );

            if( name.startsWith( META_INF ) )
            {
                continue;
            }

            if( handleDirs( entry, name, directory ) )
            {
                continue;
            }

            if( handleClasses( zipFile,
                               entry,
                               name,
                               workDir,
                               classpath ) )
            {
                continue;
            }

            if( handleJars( zipFile, entry, name, workDir, classpath ) )
            {
                continue;
            }

            //Expand the file if necesasry and issue a warning
            //if there is a file in the way
            final File destination = new File( directory, name );
            handleFile( zipFile, entry, destination, digests, url );
        }
    }

    /**
     * Handle the extraction of normal resources
     * from zip file/
     */
    private void handleFile( final ZipFile zipFile,
                             final ZipEntry entry,
                             final File destination,
                             final ArrayList digests,
                             final URL url )
        throws InstallationException
    {
        if( !destination.exists() )
        {
            expandFile( zipFile, entry, destination );
            calculateDigest( entry, destination, digests );
        }
        else
        {
            final String message =
                REZ.getString( "file-in-the-way",
                               url,
                               entry.getName(),
                               destination );
            getLogger().warn( message );
        }
    }

    /**
     * Handle extraction of jars.
     *
     * @param zipFile the zipFIle to exrtact from
     * @param entry the entry to extract
     * @param name the normalized name of entry
     * @param workDir the working directory to extract to
     * @param jars the classpath list
     * @return true if handled, false otherwise
     */
    private boolean handleJars( final ZipFile zipFile,
                                final ZipEntry entry,
                                final String name,
                                final File workDir,
                                final ArrayList jars )
        throws InstallationException
    {
        if( name.startsWith( LIB ) &&
            name.endsWith( ".jar" ) &&
            LIB.length() == name.lastIndexOf( "/" ) )
        {
            final File jar = new File( workDir, name );
            jars.add( getURLAsString( jar ) );

            final File file = new File( workDir, name );
            expandFile( zipFile, entry, file );
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Handle extraction of jars.
     *
     * @param zipFile the zipFIle to exrtact from
     * @param entry the entry to extract
     * @param name the normalized name of entry
     * @param workDir the working directory to extract to
     * @param jars the classpath list
     * @return true if handled, false otherwise
     */
    private boolean handleClasses( final ZipFile zipFile,
                                   final ZipEntry entry,
                                   final String name,
                                   final File workDir,
                                   final ArrayList jars )
        throws InstallationException
    {
        if( name.startsWith( CLASSES ) )
        {
            final File classDir = new File( workDir, FS_CLASSES );
            if( !classDir.exists() )
            {
                jars.add( getURLAsString( classDir ) );
                final File file = new File( workDir, name );
                expandFile( zipFile, entry, file );
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Handle expansion of dirs in the zipfile.
     *
     * @param entry the current ZipEntry
     * @param name the name of entry
     * @param directory the base directory extraacting to
     * @return true if handled, false otherwise
     */
    private boolean handleDirs( final ZipEntry entry,
                                final String name,
                                final File directory )
    {
        if( entry.isDirectory() )
        {
            if( !name.startsWith( SAR_INF ) )
            {
                final File newDir =
                    new File( directory, name );
                newDir.mkdirs();
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Create working directory inside baseWorkDir
     * for specified application.
     *
     * @param baseWorkDir the base workDir for all apps
     * @param name the name of the application
     * @return the working directory for app
     */
    private File getRelativeWorkDir( final File baseWorkDir,
                                     final String name )
    {
        final String filename =
            name + "-" + System.currentTimeMillis();
        return new File( baseWorkDir, filename );
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
        {
            return name.substring( 1 );
        }
        else
        {
            return name;
        }
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
                    expandFile( zipFile, entry, destination );
                    calculateDigest( entry, destination, digests );
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

        return new Installation( file, directory, directory,
                                 config, assembly, server,
                                 classPath, fileDigests, timestamp );
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

        return new Installation( directory, directory, directory,
                                 config, assembly, server,
                                 classPath, null, timestamp );
    }

    /**
     * Get File object for URL.
     * Currently it assumes that URL is a file URL but in the
     * future it will allow downloading of remote URLs thus enabling
     * a deploy from anywhere functionality.
     *
     * @param url the url of deployment
     * @return the File for deployment
     * @throws InstallationException if an error occurs
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
     * Calculate digest for specific entry.
     *
     * @param entry the entry
     * @param file the extracted file
     * @param digests the list of digests already
     *        calculated
     */
    private void calculateDigest( final ZipEntry entry,
                                  final File file,
                                  final ArrayList digests )
    {
        final long checksum = entry.getCrc();
        digests.add( new FileDigest( file, checksum ) );
    }

    /**
     * Expand a single zipEntry to a file.
     */
    private void expandFile( final ZipFile zipFile,
                             final ZipEntry entry,
                             final File file )
        throws InstallationException
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
        catch( final IOException ioe )
        {
            final String message =
                REZ.getString( "failed-to-expand",
                               entry.getName(),
                               file,
                               ioe.getMessage() );
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

        return ( new File( m_baseDirectory, base ) ).getAbsoluteFile();
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
        getURLsAsStrings( urls, blockDir, new String[]{".bar"} );
        getURLsAsStrings( urls, libDir, new String[]{".jar", ".zip"} );
        return (String[])urls.toArray( new String[ 0 ] );
    }

    /**
     * Add all matching files in directory to url list.
     *
     * @param urls the url list
     * @param directory the directory to scan
     * @param extensions the list of extensions to match
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
