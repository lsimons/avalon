/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class provides basic facilities for manipulating exceptions.
 *
 * Some exception handling stuff thieved from Turbine...
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 */
public final class ExceptionUtil
{
    /**
     * Private constructor to prevent instantiation.
     */
    private ExceptionUtil()
    {
    }

    public static String printStackTrace( final Throwable throwable )
    {
        return printStackTrace( throwable, 0, true );
    }

    public static String printStackTrace( final Throwable throwable,
                                          final boolean printCascading )
    {
        return printStackTrace( throwable, 0, printCascading );
    }

    public static String printStackTrace( final Throwable throwable, int depth )
    {
        final String[] lines = captureStackTrace( throwable );

        if( 0 == depth || depth > lines.length ) depth = lines.length;

        final StringBuffer sb = new StringBuffer();

        for( int i = 0; i < depth; i++ )
        {
            sb.append( lines[ i ] );
            sb.append( '\n' );
        }

        return sb.toString();
    }

    public static String printStackTrace( Throwable throwable,
                                          final int depth,
                                          final boolean printCascading )
    {
        final String result = printStackTrace( throwable, depth );

        if( !printCascading || !(throwable instanceof CascadingThrowable) )
        {
            return result;
        }
        else
        {
            final StringBuffer sb = new StringBuffer();
            sb.append( result );

            throwable = ((CascadingThrowable)throwable).getCause();

            while( null != throwable )
            {
                sb.append( "rethrown from\n" );
                sb.append( printStackTrace( throwable, depth ) );

                if( throwable instanceof CascadingThrowable )
                {
                    throwable = ((CascadingThrowable)throwable).getCause();
                }
                else
                {
                    throwable = null;
                }
            }

            return sb.toString();
        }
    }

    /**
     * Captures the stack trace associated with this exception.
     *
     * @return an array of Strings describing stack frames.
     */
    public static String[] captureStackTrace( final Throwable throwable )
    {
        final StringWriter sw = new StringWriter();
        throwable.printStackTrace( new PrintWriter( sw, true ) );
        return splitString( sw.toString(), "\n" );
    }

    /**
     * Splits the string on every token into an array of stack frames.
     *
     * @param string the string
     * @param onToken the token
     * @return the resultant array
     */
    public static String[] splitString( final String string, final String onToken )
    {
        final StringTokenizer tokenizer = new StringTokenizer( string, onToken );

        final ArrayList lines = new ArrayList();

        while( tokenizer.hasMoreTokens() )
        {
            lines.add( tokenizer.nextToken() );
        }

        return (String[])lines.toArray( new String[ 0 ] );
    }
}
