/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.framework.tools.infobuilder;

import java.io.OutputStream;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;

/**
 * Simple interface used to write {@link ComponentInfo}
 * objects to a stream. Different implementations will write to
 * different output formats.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/03/01 03:39:47 $
 */
public interface InfoWriter
{
    /**
     * Write a {@link ComponentInfo} to a stream
     *
     * @param info the Info to write out
     * @param outputStream the stream to write info to
     * @throws Exception if unable to write info
     */
    void writeComponentInfo( ComponentInfo info,
                             OutputStream outputStream )
        throws Exception;
}
