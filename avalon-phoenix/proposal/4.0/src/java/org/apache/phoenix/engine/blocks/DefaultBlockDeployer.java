/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.blocks;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;
import org.apache.avalon.camelot.AbstractDeployer;
import org.apache.avalon.camelot.DeployerUtil;
import org.apache.avalon.camelot.DeploymentException;
import org.apache.avalon.camelot.DefaultLocator;
import org.apache.avalon.camelot.Registry;
import org.apache.avalon.camelot.RegistryException;
import org.apache.avalon.aut.io.IOUtil;
import org.apache.phoenix.engine.metainfo.BlockInfo;
import org.apache.phoenix.engine.metainfo.BlockInfoBuilder;
import org.apache.framework.component.ComponentManager;
import org.apache.framework.component.ComponentException;
import org.apache.framework.component.Composer;

/**
 * This class deploys a .bar file into a registry.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class DefaultBlockDeployer
    extends AbstractDeployer
    implements Composer
{
    private Registry            m_registry;
    private BlockInfoBuilder    m_builder;


    /**
     * Default constructor.
     */
    public DefaultBlockDeployer()
    {
        m_builder = new BlockInfoBuilder();
        m_autoUndeploy = true;
        m_type = "Block";
    }

    /**
     * Retrieve relevent services needed to deploy.
     *
     * @param componentManager the ComponentManager
     * @exception ComponentManagerException if an error occurs
     */
    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_registry = (Registry)componentManager.
            lookup( "org.apache.avalon.camelot.Registry" );
    }

    /**
     * Deploy a file.
     * Eventually this should be cached for performance reasons.
     *
     * @param location the location
     * @param file the file
     * @exception DeploymentException if an error occurs
     */
    protected void deployFromFile( final String location, final File file )
        throws DeploymentException
    {
        final ZipFile zipFile = DeployerUtil.getZipFileFor( file );

        URL url = null;

        try
        {
            try { url = file.toURL(); }
            catch( final MalformedURLException mue )
            {
                throw new DeploymentException( "Unable to form url", mue );
            }

            handleBlocks( zipFile, DeployerUtil.loadManifest( zipFile ), url );
        }
        finally
        {
            try { zipFile.close(); }
            catch( final IOException ioe ) {}
        }
    }

    /**
     * Create and register Infos for all blocks stored in deployment.
     *
     * @param properties the properties
     * @param url the url of deployment
     * @exception DeploymentException if an error occurs
     */
    protected void handleBlocks( final ZipFile zipFile, final Manifest manifest, final URL url )
        throws DeploymentException
    {
        final Map entries = manifest.getEntries();
        final Iterator sections = entries.keySet().iterator();

        //for every section (aka resource)
        // check to see if the attribute "Avalon-Block" is set to true
        while( sections.hasNext() )
        {
            final String section = (String)sections.next();
            final Attributes attributes = manifest.getAttributes( section );
            final String blockValue = attributes.getValue( "Avalon-Block" );
            final boolean isBlock = Boolean.valueOf( blockValue ).booleanValue();

            if( isBlock )
            {
                handleBlock( zipFile, section, url );
            }
        }
    }

    /**
     * Handle the addition of a block from .bar file.
     *
     * @param zipFile the .bar zip
     * @param block the block filename
     * @param url the url of .bar file
     * @exception DeploymentException if an error occurs
     */
    protected void handleBlock( final ZipFile zipFile, final String block, final URL url )
        throws DeploymentException
    {
        final String classname = block.substring( 0, block.length() - 6 ).replace('/','.');
        addLocator( classname, classname, url );

        final BlockInfo info = loadBlockInfo( zipFile, classname, url );
        addInfo( classname, info );
    }

    /**
     * Create a blockinfo object by loading a .xinfo file.
     *
     * @param zipFile the zipFile to load it from
     * @param classname the name of the block class
     * @param url the url for zip
     * @return the created block info
     * @exception DeploymentException if an error occurs
     */
    protected BlockInfo loadBlockInfo( final ZipFile zipFile,
                                       final String classname,
                                       final URL url )
        throws DeploymentException
    {
        final String resource = classname.replace( '.', '/' ) + ".xinfo";
        final InputStream inputStream = DeployerUtil.loadResourceStream( zipFile, resource );

        try { return m_builder.build( inputStream ); }
        catch( final Exception e )
        {
            throw new DeploymentException( "Failed to build BlockInfo for " + classname +
                                           " in location " + url, e );
        }
        finally
        {
            IOUtil.shutdownStream( inputStream );
        }
    }


    protected void addLocator( final String name, final String classname, final URL url )
        throws DeploymentException
    {
        final DefaultLocator locator = new DefaultLocator( classname, url );

        try { m_registry.register( name + "/Locator", locator ); }
        catch( final RegistryException re )
        {
            throw new DeploymentException( "Error registering " + name + " due to " + re,
                                           re );
        }

        getLogger().debug( "Registered Locator for " + m_type + " " + name + " as " + classname );
    }

    protected void addInfo( final String name, final BlockInfo info )
        throws DeploymentException
    {
        try { m_registry.register( name, info ); }
        catch( final RegistryException re )
        {
            throw new DeploymentException( "Error registering " + name + " due to " + re,
                                           re );
        }

        getLogger().debug( "Registered Info " + m_type + " " + name );
    }
}
