/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.camelot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URL;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.component.ComponentException;
import org.apache.avalon.component.Composable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.configuration.DefaultConfigurationBuilder;
import org.xml.sax.SAXException;

/**
 * This class deploys resources from camelot based system.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class DeployerUtil
{
    protected static DefaultConfigurationBuilder  c_configurationBuilder;

    /**
     * Private constructor to block instantiation.
     */
    private DeployerUtil()
    {
    }

    protected static DefaultConfigurationBuilder getBuilder()
    {
        if( null == c_configurationBuilder )
        {
            c_configurationBuilder = new DefaultConfigurationBuilder();
        }

        return c_configurationBuilder;
    }

    /**
     * Get zipFile represented by URL.
     *
     * @param url the URL
     * @return the ZipFile
     * @exception DeploymentException if an error occurs
     */
/*
  public final static ZipFile getZipFileFor( final URL url )
  throws DeploymentException
  {
  final File file = getFileFor( url );
  return getZipFileFor( file );
  }
*/
    /**
     * Retrieve zip file for file.
     *
     * @param file the file
     * @return the zipFile
     * @exception DeploymentException if an error occurs
     */
    public final static ZipFile getZipFileFor( final File file )
        throws DeploymentException
    {
        try { return new ZipFile( file ); }
        catch( final IOException ioe )
        {
            throw new DeploymentException( "Error opening " + file +
                                           " due to " + ioe.getMessage(),
                                           ioe );
        }
    }

    /**
     * Utility method to load configuration from zip.
     *
     * @param zipFile the zip file
     * @param filename the property filename
     * @return the Configuration
     * @exception DeploymentException if an error occurs
     */
    public final static Configuration loadConfiguration( final ZipFile zipFile,
                                                         final String filename )
        throws DeploymentException
    {
        return buildConfiguration( loadResourceStream( zipFile, filename ) );
    }

    /**
     * Build a configuration tree based on input stream.
     *
     * @param input the InputStream
     * @return the Configuration tree
     * @exception DeploymentException if an error occurs
     */
    public final static Configuration buildConfiguration( final InputStream input )
        throws DeploymentException
    {
        try { return getBuilder().build( input ); }
        catch( final SAXException se )
        {
            throw new DeploymentException( "Malformed configuration data", se );
        }
        catch( final ConfigurationException ce )
        {
            throw new DeploymentException( "Error building configuration", ce );
        }
        catch( final IOException ioe )
        {
            throw new DeploymentException( "Error reading configuration", ioe );
        }
    }

    /**
     * Utility method to load a manifest from a zip file.
     *
     * @param zipFile the zip file
     * @return the Manifest
     */
    public final static Manifest loadManifest( final ZipFile zipFile )
        throws DeploymentException
    {
        final InputStream input = loadResourceStream( zipFile, "META-INF/MANIFEST.MF" );

        try { return new Manifest( input ); }
        catch( final IOException ioe )
        {
            throw new DeploymentException( "Error reading manifest", ioe );
        }
        finally
        {
            try { input.close(); }
            catch( final IOException ioe ) {}
        }
    }

    /**
     * Utility method to load properties from zip.
     *
     * @param zipFile the zip file
     * @param filename the property filename
     * @return the Properties
     * @exception DeploymentException if an error occurs
     */
    public final static Properties loadProperties( final ZipFile zipFile,
                                                   final String filename )
        throws DeploymentException
    {
        final Properties properties = new Properties();

        try { properties.load( loadResourceStream( zipFile, filename ) ); }
        catch( final IOException ioe )
        {
            throw new DeploymentException( "Error reading " + filename +
                                           " from " + zipFile.getName(),
                                           ioe );
        }

        return properties;
    }

    /**
     * Load a resource from a zip file.
     *
     * @param zipFile the ZipFile
     * @param filename the filename
     * @return the InputStream
     * @exception DeploymentException if an error occurs
     */
    public final static InputStream loadResourceStream( final ZipFile zipFile,
                                                        final String filename )
        throws DeploymentException
    {
        final ZipEntry entry = zipFile.getEntry( filename );

        if( null == entry )
        {
            throw new DeploymentException( "Unable to locate " + filename +
                                           " in " + zipFile.getName() );
        }

        try { return zipFile.getInputStream( entry ); }
        catch( final IOException ioe )
        {
            throw new DeploymentException( "Error reading " + filename +
                                           " from " + zipFile.getName(),
                                           ioe );
        }
    }
}
