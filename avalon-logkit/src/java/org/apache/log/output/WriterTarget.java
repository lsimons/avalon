/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.log.format.PatternFormatter;
import org.apache.log.Hierarchy;
import org.apache.log.Formatter;

/**
 * This target outputs to a writer.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class WriterTarget
    extends AbstractOutputTarget
{
    /**
     * @deprecated Accessing this variable in subclasses is no longer supported 
     *             and will become private in the future.
     */
    protected Writer             m_output;

    /**
     * COnstruct target with a specific writer and formatter.
     *
     * @param writer the writer
     * @param formatter the formatter
     */
    public WriterTarget( final Writer writer, final Formatter formatter )
    {
        super( formatter );
        m_output = writer;
    }

    /**
     * Concrete implementation of output that writes out to underlying writer.
     *
     * @param data the data to output
     */
    protected void write( final String data )
    {
        try
        {
            m_output.write( data );
            m_output.flush();
        }
        catch( final IOException ioe )
        {
            error( "Caught an IOException", ioe );
        }
    }
}
