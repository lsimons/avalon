/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.xdoclet;

import java.net.URL;
import xdoclet.TemplateSubTask;
import xdoclet.XDocletException;

/**
 * Generates Manifest file for Blocks
 *
 * @author <a href="mailto:vinay_chandran@users.sourceforge.net">Vinay Chandrasekharan</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.9 $ $Date: 2002/08/25 05:56:40 $
 * @ant.element display-name="Manifest" name="manifest" parent="org.apache.avalon.phoenix.tools.xdoclet.PhoenixXDocletTask"
 */
public class ManifestSubTask
    extends TemplateSubTask
{
    private static final String DEFAULT_TEMPLATE_FILE =
        "/org/apache/avalon/phoenix/tools/xdoclet/manifest.xdt";
    private String m_manifestFile;

    public ManifestSubTask()
    {
        setupParams();
    }

    public void setManifestFile( final String manifestFile )
    {
        m_manifestFile = manifestFile;
        setDestinationFile( m_manifestFile );
    }

    private void setupParams()
    {
        setSubTaskName( "manifest" );
        final URL resource = getClass().getResource( DEFAULT_TEMPLATE_FILE );
        setTemplateURL( resource );
        setDestinationFile( "manifest.mf" );
        setOfType( "org.apache.avalon.phoenix.Block" );
        setAcceptAbstractClasses( false );
    }

    protected void engineStarted() throws XDocletException
    {
        System.out.println( "Generating Manifest file: " + getDestinationFile() );
    }
}
