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

/**
 * This is a basic Output log target that writes to a stream.
 * The format is specified via a string.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultOutputLogTarget
    extends AbstractOutputTarget
{
    protected Writer             m_output;

    /**
     * Initialize the default pattern.
     *
     */
    protected void initPattern()
    {
        final PatternFormatter formatter = new PatternFormatter();
        formatter.setFormat( "%7.7{priority} %5.5{time}   [%8.8{category}] " +
                             "(%{context}): %{message}\\n%{throwable}" );
        m_formatter = formatter;
    }

    /**
     * Default Constructor.
     *
     */
    public DefaultOutputLogTarget()
    {
        this( new OutputStreamWriter( System.out ) );
    }

    /**
     * Constructor that takes a stream arguement.
     *
     * @param output the output stream
     */
    public DefaultOutputLogTarget( final OutputStream output )
    {
        this( new OutputStreamWriter( output) );
    }

    /**
     * Constructor that takes a writer parameter.
     *
     * @param writer the Writer
     */
    public DefaultOutputLogTarget( final Writer writer )
    {
        m_output = writer;

        initPattern();
    }

    /**
     * Concrete implementation of output that writes out to underlying writer.
     *
     * @param data the data to output
     */
    protected void output( final String data )
    {
        try
        {
            m_output.write( data );
            m_output.flush();
        }
        catch (IOException ioe)
        {
            Hierarchy.getDefaultHierarchy().log("Caught an IOException", ioe);
        }
    }

    /**
     * Set the format string for this target.
     *
     * @param format the format string
     */
    public void setFormat( final String format )
    {
        ((PatternFormatter)m_formatter).setFormat( format );
    }
}
