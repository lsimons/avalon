/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.xdoclet;

/**
 * Generates Manifest file for Blocks
 *
 * @author <a href="mailto:vinay_chandran@users.sourceforge.net">Vinay Chandrasekharan</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author Paul Hammant
 * @version $Revision: 1.14 $ $Date: 2002/11/05 23:56:03 $
 */
public class ManifestSubTask
{
    private String m_manifestFile;

    public String getManifestFile()
    {
        return m_manifestFile;
    }

    public void setManifestFile( final String manifestFile )
    {
        m_manifestFile = manifestFile;
    }

}
