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
import org.apache.log.output.io.WriterTarget;

/**
 * This is a basic Output log target that writes to a stream.
 * The format is specified via a string.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @deprecated Use WriterTarget or StreamTarget as appropriate as this class 
 *             encourages unsafe behaviour
 */
public class DefaultOutputLogTarget
    extends WriterTarget
{
    private static final String  FORMAT = 
        "%7.7{priority} %5.5{time}   [%8.8{category}] (%{context}): %{message}\\n%{throwable}";

    /**
     * Initialize the default pattern.
     *
     * @deprecated This is no longer the recomended way to set formatter. It is recomended
     *             that it be passed into constructor.
     */
    protected void initPattern()
    {
    }

    public DefaultOutputLogTarget( final Formatter formatter )
    {
        this( new OutputStreamWriter( System.out ), formatter );
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
        this( new OutputStreamWriter( output ) );
    }

    /**
     * Constructor that takes a writer parameter.
     *
     * @param writer the Writer
     */
    public DefaultOutputLogTarget( final Writer writer )
    {
        this( writer, new PatternFormatter( FORMAT ) );
    }

    public DefaultOutputLogTarget( final Writer writer, final Formatter formatter )
    {
        super( writer, formatter );
        initPattern();
    }

    /**
     * Set the format string for this target.
     *
     * @param format the format string
     * @deprecated This method is unsafe as it assumes formatter is PatternFormatter 
     *             and accesses a protected attribute. Instead of calling this method
     *             It is recomended that a fully configured formatter is passed into 
     *             constructor.
     */
    public void setFormat( final String format )
    {
        ((PatternFormatter)m_formatter).setFormat( format );
    }
}
