/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.source;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.apache.avalon.framework.CascadingException;

/**
 * This Exception is thrown every time there is a problem in processing
 * the source.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/05/13 12:17:40 $
 */
public class SourceException
    extends CascadingException
{

    /**
     * Construct a new <code>SourceException</code> instance.
     *
     * @param message The detail message for this exception.
     */
    public SourceException( final String message )
    {
        super( message, null );
    }

    /**
     * Construct a new <code>SourceException</code> instance.
     *
     * @param message The detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public SourceException( final String message, final Throwable throwable )
    {
        super( message, throwable );
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( super.toString() );
        if( getCause() != null )
        {
            s.append( ": " );
            s.append( getCause().toString() );
        }
        return s.toString();
    }

    public void printStackTrace()
    {
        super.printStackTrace();
        if( getCause() != null )
            getCause().printStackTrace();
    }

    public void printStackTrace( PrintStream s )
    {
        super.printStackTrace( s );
        if( getCause() != null )
            getCause().printStackTrace( s );
    }

    public void printStackTrace( PrintWriter s )
    {
        super.printStackTrace( s );
        if( getCause() != null )
            getCause().printStackTrace( s );
    }
}
