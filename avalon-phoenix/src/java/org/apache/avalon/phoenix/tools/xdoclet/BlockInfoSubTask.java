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
 * Generates BlockInfo 'xinfo' for Blocks
 *
 * @author <a href="mailto:vinay_chandran@users.sourceforge.net">Vinay Chandrasekharan</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.11 $ $Date: 2002/08/25 05:56:40 $
 */
public class BlockInfoSubTask
    extends TemplateSubTask
{
    private static final String DEFAULT_TEMPLATE_FILE =
        "/org/apache/avalon/phoenix/tools/xdoclet/blockinfo.xdt";

    public BlockInfoSubTask()
    {
        setupParams();
    }

    private void setupParams()
    {
        setSubTaskName( "blockinfo" );
        final URL resource = getClass().getResource( DEFAULT_TEMPLATE_FILE );
        setTemplateURL( resource );
        setDestinationFile( "{0}.xinfo" );
        setAcceptAbstractClasses( false );
        setHavingClassTag( "phoenix:block" );
    }

    protected void engineStarted() throws XDocletException
    {
        System.out.println( "Generating BlockInfo file: " + getGeneratedFileName( getCurrentClass() ) );
    }
}
