/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.metainfo;

import java.net.URL;
import javax.swing.Icon;
import org.apache.avalon.util.Version;

/**
 * This descrbes information about the block that is used by administration 
 * tools during configuration and upgrade but is not neccesary for running.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface MetaInfo 
    extends org.apache.avalon.camelot.MetaInfo
{
    /**
     * Get a list of contributors who helped create block.
     *
     * @return an array of Contributors
     */
    Contributor[] getContributors();

    /**
     * Get a 16x16 Color Icon for block.
     *
     * @return a 16x16 Color Icon for block
     */
    Icon getIcon();

    /**
     * Get a 32x32 Color Icon for block.
     *
     * @return a 32x32 Color Icon for block
     */
    Icon getLargeIcon();

    /**
     * Get URL of documentation.
     *
     * @return URL to documentation (if any)
     */
    URL getDocumentationLocation();

    /**
     * Get URL of License.
     *
     * @return URL to License (if any)
     */
    URL getLicenseLocation();

    /**
     * Get URL of Update. Allows a block to be automatically updated from a URL.
     *
     * @return URL to Update (if any)
     */
    URL getUpdateLocation();


    /**
     * Retrieve Version of current Block.
     *
     * @return the version of block
     */
    Version getVersion();
}
