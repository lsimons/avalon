/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.xdoclet;

import java.util.List;
import xdoclet.DocletTask;

/**
 * @author <a href="mailto:vinay_chandran@users.sourceforge.net">Vinay Chandrasekharan</a>
 * @version $Revision: 1.7 $ $Date: 2002/07/30 12:17:21 $
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
        return m_blockInfoSubTask;
    }

    public ManifestSubTask createManifest()
    {
        m_manifestSubTask = new ManifestSubTask();
        return m_manifestSubTask;
    }

    public MxInfoSubTask createMxInfo()
    {
        m_mxinfoSubTask = new MxInfoSubTask();
        return m_mxinfoSubTask;
    }

    protected List getSubTasks()
    {
        final List subtasks = super.getSubTasks();
        subtasks.add( m_blockInfoSubTask );
        subtasks.add( m_manifestSubTask );
        subtasks.add( m_mxinfoSubTask );
        return subtasks;
    }
}

