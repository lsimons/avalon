/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.io;

import java.io.*;
import java.net.URL;

/**
 * This class provides basic facilities for manipulating files.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class FileUtil
{
    /**
     * Private constructor to prevent instantiation.
     *
     */
    private FileUtil()
    {
    }

    public static File toFile( final URL url )
    {
        if( !url.getProtocol().equals( "file" ) )
        {
            return null;
        }
        else
        {
            final String filename = url.getFile().replace( '/', File.separatorChar );
            return new File( filename );
        }
    }

    /**
     * Remove extention from filename.
     * ie
     * fo.txt --> foo
     * a\b\c.jpg --> a\b\c
     * a\b\c --> a\b\c
     *
     * @param filename the filename
     * @return the filename minus extention
     */
    public static String removeExtention( final String filename )
    {
        final int index = filename.lastIndexOf( '.' );

        if( -1 == index )
        {
            return filename;
        }
        else
        {
            return filename.substring( 0, index );
        }
    }

    /**
     * remove path from filename.
     * ie.
     * a/b/c.txt --> c.txt
     * a.txt --> a.txt
     *
     * @param filepath the filepath
     * @return the filename minus path
     */
    public static String removePath( final String filepath )
    {
        final int index = filepath.lastIndexOf( File.separator );

        if( -1 == index )
        {
            return filepath;
        }
        else
        {
            return filepath.substring( index + 1 );
        }
    }

    /**
     * Copy file from source to destination.
     */
    public static void copyFileToDirectory( final String source,
                                            final String destinationDirectory )
        throws IOException
    {
        copyFileToDirectory( new File( source ), new File( destinationDirectory ) );
    }

    /**
     * Copy file from source to destination.
     */
    public static void copyFileToDirectory( final File source,
                                            final File destinationDirectory )
        throws IOException
    {
        if( destinationDirectory.exists() && !destinationDirectory.isDirectory() )
        {
            throw new IllegalArgumentException( "Destination is not a directory" );
        }

        copyFile( source, new File( destinationDirectory, source.getName() ) );
    }

    /**
     * Copy file from source to destination.
     */
    public static void copyFile( final File source, final File destination )
        throws IOException
    {
        //check source exists
        if( !source.exists() )
        {
            throw new IOException( "File " + source + " does not exist" );
        }

        //does destinations directory exist ?
        if( !destination.getParentFile().exists() )
        {
            destination.mkdirs();
        }

        //make sure we can write to destination
        if( destination.exists() && !destination.canWrite() )
        {
            throw new IOException( "Unable to open file " + destination + " for writing." );
        }

        IOUtil.copy( new FileInputStream( source ), new FileOutputStream( destination ) );

        if( source.length() != destination.length() )
        {
            throw new IOException( "Failed to copy full contents from " + source +
                                   " to " + destination );
        }
    }

    public static void copyURLToFile( final URL source, final File destination )
        throws IOException
    {
        //does destinations directory exist ?
        if( !destination.getParentFile().exists() )
        {
            destination.mkdirs();
        }

        //make sure we can write to destination
        if( destination.exists() && !destination.canWrite() )
        {
            throw new IOException( "Unable to open file " + destination + " for writing." );
        }

        IOUtil.copy( source.openStream(), new FileOutputStream( destination ) );
    }

    public static String normalize( String location )
    {
        location = replaceSubString( location, "/./", "/" );

        final StringBuffer sb = new StringBuffer();

        int trail = 0;
        int end = location.indexOf( "/../" );
        int start = 0;

        while( end != -1 )
        {
            //TODO: fix when starts with /../
            trail = location.lastIndexOf( "/", end - 1 );
            sb.append( location.substring( start, trail ) );
            sb.append( '/' );
            start = end + 4;
            end = location.indexOf( "/../", start );
        }

        end = location.length();
        sb.append( location.substring( start, end ) );

        return sb.toString();
    }

    /** Will concatenate 2 paths, dealing with ..
     * ( /a/b/c + d = /a/b/d, /a/b/c + ../d = /a/d )
     *
     * Thieved from Tomcat sources...
     *
     * @return null if error occurs
     */
    public static String catPath( String lookupPath, String path )
    {
        // Cut off the last slash and everything beyond
        int index = lookupPath.lastIndexOf( "/" );
        lookupPath = lookupPath.substring( 0, index );

        // Deal with .. by chopping dirs off the lookup path
        while( path.startsWith( "../" ) )
        {
            if( lookupPath.length() > 0 )
            {
                index = lookupPath.lastIndexOf( "/" );
                lookupPath = lookupPath.substring( 0, index );
            }
            else
            {
                // More ..'s than dirs, return null
                return null;
            }

            index = path.indexOf( "../" ) + 3;
            path = path.substring( index );
        }

        return lookupPath + "/" + path;
    }

    public static File resolveFile( final File baseFile, String filename )
    {
        if( '/' != File.separatorChar )
        {
            filename = filename.replace( '/', File.separatorChar );
        }

        if( '\\' != File.separatorChar )
        {
            filename = filename.replace( '\\', File.separatorChar );
        }

        // deal with absolute files
        if( filename.startsWith( File.separator ) )
        {
            File file = new File( filename );

            try { file = file.getCanonicalFile(); }
            catch( final IOException ioe ) {}

            return file;
        }

        final char[] chars = filename.toCharArray();
        final StringBuffer sb = new StringBuffer();

        //remove duplicate file seperators in succession - except
        //on win32 as UNC filenames can be \\AComputer\AShare\myfile.txt
        int start = 0;
        if( '\\' == File.separatorChar )
        {
            sb.append( filename.charAt( 0 ) );
            start++;
        }

        for( int i = start; i < chars.length; i++ )
        {
            final boolean doubleSeperator =
                File.separatorChar == chars[ i ] && File.separatorChar == chars[ i - 1 ];

            if( !doubleSeperator ) sb.append( chars[ i ] );
        }

        filename = sb.toString();

        //must be relative
        File file = (new File( baseFile, filename )).getAbsoluteFile();

        try { file = file.getCanonicalFile(); }
        catch( final IOException ioe ) {}

        return file;
    }

    /**
     * Delete a file. If file is directory delete it and all sub-directories.
     */
    public static void forceDelete( final String file )
        throws IOException
    {
        forceDelete( new File( file ) );
    }

    /**
     * Delete a file. If file is directory delete it and all sub-directories.
     */
    public static void forceDelete( final File file )
        throws IOException
    {
        if( file.isDirectory() ) deleteDirectory( file );
        else
        {
            if( false == file.delete() )
            {
                throw new IOException( "File " + file + " unable to be deleted." );
            }
        }
    }

    /**
     * Recursively delete a directory.
     */
    public static void deleteDirectory( final String directory )
        throws IOException
    {
        deleteDirectory( new File( directory ) );
    }

    /**
     * Recursively delete a directory.
     */
    public static void deleteDirectory( final File directory )
        throws IOException
    {
        if( !directory.exists() ) return;

        cleanDirectory( directory );
        if( false == directory.delete() )
        {
            throw new IOException( "Directory " + directory + " unable to be deleted." );
        }
    }

    /**
     * Clean a directory without deleting it.
     */
    public static void cleanDirectory( final String directory )
        throws IOException
    {
        cleanDirectory( new File( directory ) );
    }

    /**
     * Clean a directory without deleting it.
     */
    public static void cleanDirectory( final File directory )
        throws IOException
    {
        if( !directory.exists() )
        {
            throw new IllegalArgumentException( directory + " does not exist" );
        }

        if( !directory.isDirectory() )
        {
            throw new IllegalArgumentException( directory + " is not a directory" );
        }

        final File[] files = directory.listFiles();

        for( int i = 0; i < files.length; i++ )
        {
            final File file = files[ i ];

            if( file.isFile() ) file.delete();
            else if( file.isDirectory() )
            {
                cleanDirectory( file );
                if( false == file.delete() )
                {
                    throw new IOException( "Directory " + file + " unable to be deleted." );
                }
            }
        }
    }

    /**
     * Replace substrings of one string with another string and return altered string.
     *
     * @param original input string
     * @param oldString the substring section to replace
     * @param newString the new substring replacing old substring section
     * @return converted string
     */
    private static String replaceSubString( final String original,
                                            final String oldString,
                                            final String newString )
    {
        final StringBuffer sb = new StringBuffer();

        int end = original.indexOf( oldString );
        int start = 0;
        final int stringSize = oldString.length();

        while( end != -1 )
        {
            sb.append( original.substring( start, end ) );
            sb.append( newString );
            start = end + stringSize;
            end = original.indexOf( oldString, start );
        }

        end = original.length();
        sb.append( original.substring( start, end ) );

        return sb.toString();
    }
}
