/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.log.format.Formatter;
import org.apache.log.LogEvent;

/**
 * A target that will open and close a file for each logevent.
 * This is slow but a more reliable form of logging on some
 * filesystems/OSes. It should only be used when there is a
 * small number of log events.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class SafeFileTarget
    extends FileTarget
{
    /**
     * Construct file target to write to a file with a formatter.
     *
     * @param file the file to write to
     * @param append true if file is to be appended to, false otherwise
     * @param formatter the Formatter
     * @exception IOException if an error occurs
     */
    public SafeFileTarget( final File file, final boolean append, final Formatter formatter )
        throws IOException
    {
        super( file, append, formatter );
        shutdownStream();
    }

    /**
     * Process a log event, via formatting and outputting it.
     *
     * @param event the log event
     */
    public synchronized void processEvent( final LogEvent event )
    {
        if( !isOpen() )
        {
            getErrorHandler().error( "Writing event to closed stream.", null, event );
            return;
        }

        try
        {
            final FileOutputStream outputStream =
                new FileOutputStream( getFile().getPath(), true );
            setOutputStream( outputStream );
        }
        catch( final Throwable throwable )
        {
            getErrorHandler().error( "Unable to open file to write log event.", throwable, event );
            return;
        }

        //write out event
        super.processEvent( event );

        shutdownStream();
    }
}
