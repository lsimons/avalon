/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.io.rotate;

import java.io.File;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;

/**
 * strategy for naming log files based on appending revolving suffix.
*/
public class FilenameStrategyRevolvingLogFile
    implements FilenameStrategy
{
    /**
     * current revolving suffix
     */
    long rotationValue = 1;

    /**
     * the base file name.
     */
    File baseFileName;

    /**
     * starting revolving value. ie. 1
     */
    long rotationMinValue = 1;

    /**
     * max revolving value. ie 1000
     */
    long rotationMaxValue = 1000;

    /**
     * revolving suffix formatting pattern. ie. "'.'000000"
     */
    String rotationFormatPattern = "'.'000000";

    /**
     * a revolving suffix formatter
     */
    DecimalFormat df;

    FilenameStrategyRevolvingLogFile() 
    {
        rotationValue = rotationMinValue;
        df = new DecimalFormat( rotationFormatPattern );
        setBaseFileName( new File(FilenameStrategy.BASE_FILE_NAME_DEFAULT) );
    }

    FilenameStrategyRevolvingLogFile( FilenameStrategy fs ) {
        this();
        if (fs != null) {
            File bfn = fs.getBaseFileName();
            if (bfn != null) {
                setBaseFileName( bfn );
            }
        }
    }

    FilenameStrategyRevolvingLogFile( File base_file_name ) 
    {
        setBaseFileName( base_file_name );
    }

    public File getBaseFileName() 
    {
        return baseFileName;
    }

    public void setBaseFileName( File base_file_name ) 
    {
        baseFileName = base_file_name;
    }

    /**
     * calculate the real file name from the base filename.
     * @return File the calculated file name
     */
    public File getLogFileName() 
    {
        StringBuffer sb = new StringBuffer();
        FieldPosition fp = new FieldPosition( NumberFormat.INTEGER_FIELD );
        sb.append( baseFileName );
        sb = df.format( rotationValue, sb, fp );
        rotationValue += 1;
        if (rotationValue >= rotationMaxValue) {
            rotationValue = rotationMinValue;
        }
        return new File( sb.toString() );
    }
}

