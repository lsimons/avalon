/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.metainfo;

import javax.swing.Icon;
import java.net.URL;
import java.util.Locale;
import org.apache.avalon.util.Version;

/**
 * This descrbes information about the block that is used by administration 
 * tools during configuration and upgrade but is not neccesary for running.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultBlockDescriptor 
    implements BlockDescriptor
{
    protected Contributor[]   m_contributors;
    protected Version         m_version;
    protected Icon            m_icon;
    protected Icon            m_largeIcon;
    protected URL             m_documentationLocation;
    protected URL             m_licenseLocation;
    protected URL             m_updateLocation;

    public DefaultBlockDescriptor( final Version version )
    {
        m_version = version;
    }

    /**
     * Get a list of contributors who helped create block.
     *
     * @return an array of Contributors
     */
    public Contributor[] getContributors()
    {
        return m_contributors;
    }

    public void setContributors(  final Contributor contributors[] )
    {
        m_contributors = contributors;
    }

    /**
     * Get a 16x16 Color Icon for block.
     *
     * @return a 16x16 Color Icon for block
     */
    public Icon getIcon()
    {
        return m_icon;
    }

    public void setIcon( final Icon icon )
    {
        m_icon = icon;
    }

    /**
     * Get a 32x32 Color Icon for block.
     *
     * @return a 32x32 Color Icon for block
     */
    public Icon getLargeIcon()
    {
        return m_largeIcon;
    }

    public void setLargeIcon( final Icon largeIcon )
    {
        m_largeIcon = largeIcon;
    }

    /**
     * Get URL of documentation.
     *
     * @return URL to documentation (if any)
     */
    public URL getDocumentationLocation()
    {
        return m_documentationLocation;
    }

    public void setDocumentationLocation( final URL documentationLocation )
    {
        m_documentationLocation = documentationLocation;
    }

    /**
     * Get URL of License.
     *
     * @return URL to License (if any)
     */
    public URL getLicenseLocation()
    {
        return m_licenseLocation;
    }

    public void setLicenseLocation( final URL licenseLocation )
    {
        m_licenseLocation = licenseLocation;
    }

    /**
     * Get URL of Update. Allows a block to be automatically updated from a URL.
     *
     * @return URL to Update (if any)
     */
    public URL getUpdateLocation()
    {
        return m_updateLocation;
    }

    public void setUpdateLocation( final URL updateLocation )
    {
        m_updateLocation = updateLocation;
    }

    /**
     * Retrieve Version of current Block.
     *
     * @return the version of block
     */
    public Version getVersion()
    {
        return m_version;
    }
}

