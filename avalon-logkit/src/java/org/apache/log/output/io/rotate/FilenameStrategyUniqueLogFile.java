/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.io.rotate;

import java.io.File;
import java.io.IOException;

/**
 * strategy for naming log files based on appending time suffix
 *
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class FilenameStrategyUniqueLogFile implements FilenameStrategy {
    /**
     * the base file name.
     */
    File baseFileName;

    FilenameStrategyUniqueLogFile() {
        setBaseFileName( new File(FilenameStrategy.BASE_FILE_NAME_DEFAULT) );
    }
    FilenameStrategyUniqueLogFile( FilenameStrategy fs ) {
        this();
        if (fs != null) {
            File bfn = fs.getBaseFileName();
            if (bfn != null) {
                setBaseFileName( bfn );
            }
        }
    }
    FilenameStrategyUniqueLogFile( File base_file_name ) {
        baseFileName = base_file_name;
    }
    public File getBaseFileName() {
        return baseFileName;
    }
    public void setBaseFileName( File base_file_name ) {
        baseFileName = base_file_name;
    }
    /**
     * calculate the real file name from the base filename.
     * @return File the calculated file name
     */
    public File getLogFileName() {
        StringBuffer sb = new StringBuffer();
        sb.append( baseFileName );
        sb.append( getCurrentValue() );
        return new File( sb.toString() );
    }
    protected String getCurrentValue() {
        long current_value = System.currentTimeMillis();
        return String.valueOf( current_value );
    }
}

