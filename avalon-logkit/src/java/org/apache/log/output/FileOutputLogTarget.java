/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * This is a basic Output log target that writes to a file.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 */
public class FileOutputLogTarget 
    extends DefaultOutputLogTarget
{
    public FileOutputLogTarget()
    {
    }

    public FileOutputLogTarget( final String filename )
        throws IOException
    {
        setFilename( filename );
    }

   /**
    * Overwrites the m_output value in the super class through
    * assignment of an OutputStreamWriter based on a supplied
    * file path.  Side-effects include the creation of a 
    * directory path based relative to the supplied filename.
    *
    * @param filename path and filename for log destination
    */
    public void setFilename( final String filename )
        throws IOException
    {
        final File file = new File( filename );
        final File parent = file.getAbsoluteFile().getParentFile();
        if( !parent.exists() ) parent.mkdirs();
        m_output = 
            new OutputStreamWriter( new FileOutputStream( file ) );
    }
}
