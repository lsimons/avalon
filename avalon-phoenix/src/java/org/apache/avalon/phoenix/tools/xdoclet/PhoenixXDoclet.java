/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.xdoclet;

import java.util.Vector;
import xdoclet.DocletTask;

/**
 *
 * @author <a href="mailto:vinay_chandran@users.sourceforge.net">Vinay Chandrasekharan</a>
 * @version $Revision: 1.4 $ $Date: 2002/04/17 06:09:34 $
 */
public class PhoenixXDoclet
    extends DocletTask
{
    private BlockInfoSubTask m_blockInfoSubTask;
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

    protected Vector getSubTasks()
    {
        final Vector subtasks = super.getSubTasks();
        subtasks.add( m_blockInfoSubTask );
        subtasks.add( m_manifestSubTask );
        return subtasks;
    }
}

