/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.io.rotate;

import java.io.File;

/**
 * Strategy for naming log files.
 * For a given base file name an implementation calculates
 * the real file name.
 *
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public interface FilenameStrategy 
{
    /**
     * Get 'calculated' log file name.
     *
     * @param baseFileName the base file name
     * @return File file which has been calculated from a given
     *         base file name.
     */
    File getLogFileName( File baseFileName );
}


