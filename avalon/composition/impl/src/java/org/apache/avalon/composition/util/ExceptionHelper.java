/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.avalon.composition.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.excalibur.configuration.ConfigurationUtil;


/**
 * General utilities supporting the packaging of exception messages.
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 */
public class ExceptionHelper
{
    private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    /**
     * Prints the exception to the standard error out together with
     * cause statements.
     * @param e the exception to print
     */
    public static void printException( Throwable e )
    {
        printException( null, e );
    }

    /**
     * Prints the exception to the standard error out together with
     * cause statements.
     * @param label label identifying the error
     * @param e the exception to print
     */
    public static void printException( String label, Throwable e )
    {
        printException( label, e, null );
    }

    /**
     * Prints the exception to the standard error out together with
     * source and cause statements.
     * @param label label identifying the error
     * @param e the exception to print
     * @param source the source of the request
     */
    public static void printException( String label, Throwable e, Object source )
    {
        printException( label, e, source, false );
    }

    /**
     * Prints the exception to the standard error out together with
     * source and cause statements.
     * @param label label identifying the error
     * @param e the exception to print
     * @param source the source of the request
     * @param trace boolean value inidication if a stack trace though be included
     */
    public static void printException( String label, Throwable e, Object source, boolean trace )
    {
        if( e == null )
        {
            throw new NullPointerException( "throwable" );
        }
        java.io.PrintStream out = System.err;
        synchronized( out )
        {
            out.println( "===================================================================" );
            if( label != null )
            {
                out.println( "Message: " + label );
            }
            if( source != null )
            {
                out.println( "Source: " + source );
            }
            out.println( "Exception: " + e.toString() );
            if( getCause( e ) != null )
            {
                printCause( out, e );
            }
            out.println( "===================================================================" );
            if( trace )
            {
                Throwable root = getLastThrowable( e );
                root.printStackTrace();
                out.println( "===================================================================" );
            }
        }
    }

    /**
     * Returns the exception and causal exceptions as a formatted string.
     * @param message the header message
     * @param e the exception
     * @return String the formatting string
     */
    public static String packException( final String message, final Throwable e )
    {
        return packException( message, e, true );
    }

    /**
     * Returns the exception and causal exceptions as a formatted string.
     * @param message the header message
     * @param e the exception
     * @return String the formatting string
     */
    public static String packException( 
       final String message, final Throwable e, boolean stack )
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( message );
        if( e == null )
        {
            return buffer.toString();
        } 
        else
        {
            buffer.append( "\n" );
            buffer.append( 
"\n\n---- report -------------------------------------------------------\n" );
            buffer.append( "\nException: " + e.getClass().getName() );
            buffer.append( "\nMessage: " + e.getMessage() );
            packCause( buffer, getCause( e ) ).toString();
        }
        Throwable root = getLastThrowable( e );

        if( ( root != null ) && ( root instanceof ConfigurationException ) )
        {

            ConfigurationException ce = (ConfigurationException) root;
            Configuration conf = getConfiguration( ce );
            if( conf != null )
            {
                buffer.append( "\n" );
                buffer.append( 
"\n\n---- configuration ------------------------------------------------\n" );
                buffer.append( "location: " + conf.getLocation() );
                buffer.append( ConfigurationUtil.list( conf ) );
            }
        }
        if( (root != null) && stack )
        {
            buffer.append( 
"\n---- stack trace ---------------------------------------------------\n" );
            String[] trace = captureStackTrace( root );
            for( int i = 0; i < trace.length; i++ )
            {
                buffer.append( "\n" + trace[i] );
            }
        }
        buffer.append( 
"\n\n-------------------------------------------------------------------" );
        return buffer.toString();
    }

    private static StringBuffer packCause( StringBuffer buffer, Throwable cause )
    {
        if( cause == null )
        {
            return buffer;
        }
        buffer.append( "\n\nCause: " + cause.getClass().getName() );
        buffer.append( "\nMessage: " + cause.getMessage() );
        return packCause( buffer, getCause( cause ) );
    }

    private static Throwable getLastThrowable( Throwable exception )
    {
        Throwable cause = getCause( exception );
        if( cause != null )
        {
            return getLastThrowable( cause );
        }
        return exception;
    }

    private static Throwable getCause( Throwable exception )
    {
        if( exception == null )
        {
            throw new NullPointerException( "exception" );
        }

        try
        {
            Method method = exception.getClass().getMethod( "getCause", new Class[0] );
            return (Throwable) method.invoke( exception, new Object[0] );
        } catch( Throwable e )
        {
            return null;
        }
    }

    private static Configuration getConfiguration( ConfigurationException exception )
    {
        if( exception == null ) return null;

        try
        {
            Method method = exception.getClass().getMethod( "getOffendingConfiguration", new Class[0] );
            return (Configuration) method.invoke( exception, new Object[0] );
        } 
        catch( Throwable e )
        {
            return null;
        }
    }

    /**
     * Captures the stack trace associated with this exception.
     *
     * @param throwable a <code>Throwable</code>
     * @return an array of Strings describing stack frames.
     */
    private static String[] captureStackTrace( final Throwable throwable )
    {
        final StringWriter sw = new StringWriter();
        throwable.printStackTrace( new PrintWriter( sw, true ) );
        return splitString( sw.toString(), LINE_SEPARATOR );
    }

    /**
     * Splits the string on every token into an array of stack frames.
     *
     * @param string the string to split
     * @param onToken the token to split on
     * @return the resultant array
     */
    private static String[] splitString( final String string, final String onToken )
    {
        final StringTokenizer tokenizer = new StringTokenizer( string, onToken );
        final String[] result = new String[tokenizer.countTokens()];

        for( int i = 0; i < result.length; i++ )
        {
            result[i] = tokenizer.nextToken();
        }

        return result;
    }

    private static void printCause( java.io.PrintStream out, Throwable e )
    {
        Throwable cause = getCause( e );
        out.println( "Cause: " + cause.toString() );
        if( getCause( cause ) != null )
        {
            printCause( out, cause );
        }
    }
}
