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
 * strategy for naming log files.
 * For a given base file name an implementation calculates
 * the real file name.
 *
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public interface FilenameStrategy 
{
    /**
     * If no filename is specified use this default filename.
     * ie. default.log
     */
    String BASE_FILE_NAME_DEFAULT = "default.log";

    /**
     *  get 'calculated' log file name
     *  @return File file which has been calculated from a given
     *  base file name.
     */
    File getLogFileName();

    /**
     * get log file name as specifed by user.
     * @return File return base file name
     */
    File getBaseFileName();

    /**
     * set log file name.
     * @param base_file_name the new base file name, used in 'calculating'
     * the real file name.
     */
    void setBaseFileName( File base_file_name );
}


