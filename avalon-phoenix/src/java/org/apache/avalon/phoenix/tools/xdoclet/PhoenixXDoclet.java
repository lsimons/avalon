/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.xdoclet;

import xdoclet.DocletTask;
import org.apache.tools.ant.types.Path;

/**
 * @author <a href="mailto:vinay_chandran@users.sourceforge.net">Vinay Chandrasekharan</a>
 * @version $Revision: 1.8 $ $Date: 2002/08/25 05:56:40 $
 */
public class PhoenixXDoclet
    extends DocletTask
{
    private BlockInfoSubTask m_blockInfoSubTask;
    private MxInfoSubTask m_mxinfoSubTask;
    private ManifestSubTask m_manifestSubTask;

    public BlockInfoSubTask createBlockinfo()
    {
        m_blockInfoSubTask = new BlockInfoSubTask();
        addTemplate( m_blockInfoSubTask );
        return m_blockInfoSubTask;
    }

    public ManifestSubTask createManifest()
    {
        m_manifestSubTask = new ManifestSubTask();
        addTemplate( m_manifestSubTask );
        return m_manifestSubTask;
    }

    public MxInfoSubTask createMxInfo()
    {
        m_mxinfoSubTask = new MxInfoSubTask();
        addTemplate( m_mxinfoSubTask );
        return m_mxinfoSubTask;
    }

    public void addClasspathRef( final Path path )
    {
       System.out.println( "WARNING: classpathRef no longer required due to changes in underlying xdoclet" );
    }
}

