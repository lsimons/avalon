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
 * Generates MxBean info for Blocks and other classes
 *
 * @author <a href="mailto:huw@mmlive.com">Huw Roberts</a>
 * @ant.element display-name="MxInfo" name="mxinfo" parent="org.apache.avalon.phoenix.tools.xdoclet.PhoenixXDocletTask"
 */
public class MxInfoSubTask
    extends TemplateSubTask
{
    private static final String DEFAULT_TEMPLATE_FILE =
        "/org/apache/avalon/phoenix/tools/xdoclet/mxinfo.xdt";

    public MxInfoSubTask()
    {
        setupParams();
    }

    private void setupParams()
    {
        setSubTaskName( "mxinfo" );
        final URL resource = getClass().getResource( DEFAULT_TEMPLATE_FILE );
        setTemplateURL( resource );
        setDestinationFile( "{0}.mxinfo" );

        // need to do this here instead of setting setHavingClassTag() because want to match on either
        // phoenix:mx-topic or phoenix:mx-proxy.
        setHavingClassTag( "phoenix:mx-topic" );
    }

    protected void engineStarted() throws XDocletException
    {
        System.out.println( "Generating MxInfo file: " + getGeneratedFileName( getCurrentClass() ) );
    }
}
