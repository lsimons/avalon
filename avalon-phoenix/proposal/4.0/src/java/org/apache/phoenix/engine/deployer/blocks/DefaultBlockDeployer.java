/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.blocks;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;
import org.apache.avalon.camelot.AbstractZipDeployer;
import org.apache.avalon.camelot.DeployerUtil;
import org.apache.framework.container.DeploymentException;
import org.apache.framework.container.LocatorRegistry;
import org.apache.framework.container.Registry;
import org.apache.framework.container.RegistryException;
import org.apache.aut.io.IOUtil;
import org.apache.phoenix.metainfo.BlockInfo;
import org.apache.phoenix.metainfo.BlockInfoBuilder;

/**
 * This class deploys a .bar file into a registry.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultBlockDeployer
    extends AbstractZipDeployer
{
    protected BlockInfoBuilder    m_builder;

    /**
     * Default constructor.
     */
    public DefaultBlockDeployer()
    {
        m_builder = new BlockInfoBuilder();

        //Indicate that this deployer should deploy to respective types
        m_deployToLocatorRegistry = true;
        m_deployToInfoRegistry = true;

        m_autoUndeploy = true;
        m_type = "Block";
    }

    /**
     * Load resources from jar required to deploy file.
     *
     * @param zipFile the zipFile
     * @param location the location deploying to
     * @param url the URL
     * @exception DeploymentException if an error occurs
     */
    protected void loadResources( final ZipFile zipFile, final String location, final URL url )
        throws DeploymentException
    {
        handleBlocks( zipFile, DeployerUtil.loadManifest( zipFile ), url );
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
}
