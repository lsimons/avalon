/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.util.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

/**
 * General utilities supporting the packaging of exception messages.
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 */
public class ExceptionHelper
{
    private static final String LINE_SEPARATOR = 
      System.getProperty( "line.separator" );

    private static final String HEADER = "----";
    private static final String EXCEPTION = HEADER + " exception report ";
    private static final String COMPOSITE = HEADER + " composite report ";
    private static final String RUNTIME = HEADER + " runtime exception report ";
    private static final String ERROR = HEADER + " error report ";
    private static final String CAUSE = HEADER + " cause ";
    private static final String TRACE = HEADER + " stack trace ";
    private static final String END = "";

    private static final int WIDTH = 80;

    /**
     * Returns the exception and causal exceptions as a formatted string.
     * @param message the header message
     * @param e the exception
     * @return String the formatting string
     */
    public static String packException( final Throwable e )
    {
        return packException( null, e );
    }

    /**
     * Returns the exception and causal exceptions as a formatted string.
     * @param e the exception
     * @param stack TRUE to generate a stack trace
     * @return String the formatting string
     */
    public static String packException( final Throwable e, boolean stack )
    {
        return packException( null, e, stack );
    }


    /**
     * Returns the exception and causal exceptions as a formatted string.
     * @param message the header message
     * @param e the exception
     * @return String the formatting string
     */
    public static String packException( final String message, final Throwable e )
    {
        return packException( message, e, false );
    }

    /**
     * Returns the exception and causal exceptions as a formatted string.
     * @param message the header message
     * @param e the exception
     * @param stack TRUE to generate a stack trace
     * @return String the formatting string
     */
    public static String packException( 
       final String message, final Throwable e, boolean stack )
    {
        StringBuffer buffer = new StringBuffer();
        packException( buffer, 0, message, e, stack );
        buffer.append( getLine( END ) );
        return buffer.toString();
    }


    /**
     * Returns the exception and causal exceptions as a formatted string.
     * @param message the header message
     * @param e the exception
     * @param stack TRUE to generate a stack trace
     * @return String the formatting string
     */
    public static String packException( 
       final String message, final Throwable[] e, boolean stack )
    {
        final String lead = COMPOSITE + "(" + e.length + " entries) ";
        StringBuffer buffer = new StringBuffer( getLine( lead ) );
        if( null != message )
        {
            buffer.append( message );
            buffer.append( "\n" );
        }
        for( int i=0; i<e.length; i++ )
        {
            packException( buffer, i+1, null, e[i], stack );
        }
        buffer.append( getLine( END ) );
        return buffer.toString();
    }

    /**
     * Returns the exception and causal exceptions as a formatted string.
     * @param message the header message
     * @param e the exception
     * @param stack TRUE to generate a stack trace
     * @return String the formatting string
     */
    private static void packException( 
       StringBuffer buffer, int j, final String message, final Throwable e, boolean stack )
    {
        if( e instanceof Error )
        {
            buffer.append( getLine( ERROR, j ) );
        }
        else if( e instanceof RuntimeException )
        {
            buffer.append( getLine( RUNTIME, j ) );
        }
        else
        {
            buffer.append( getLine( EXCEPTION, j ) );
        }

        if( null != message )
        {
            buffer.append( message );
            buffer.append( "\n" );
        }
        if( e == null ) return;

        buffer.append( "Exception: " + e.getClass().getName() + "\n" );
        buffer.append( "Message: " + e.getMessage() + "\n" );
        packCause( buffer, getCause( e ) ).toString();
        Throwable root = getLastThrowable( e );
        if( (root != null) && stack )
        {
            buffer.append( getLine( TRACE ) );
            String[] trace = captureStackTrace( root );
            for( int i = 0; i < trace.length; i++ )
            {
                buffer.append( trace[i] + "\n" );
            }
        }
    }

    private static StringBuffer packCause( StringBuffer buffer, Throwable cause )
    {
        if( cause == null )
        {
            return buffer;
        }
        buffer.append( getLine( CAUSE ) );
        buffer.append( "Exception: " + cause.getClass().getName() + "\n" );
        buffer.append( "Message: " + cause.getMessage() + "\n" );
        return packCause( buffer, getCause( cause ) );
    }

    private static Throwable getLastThrowable( Throwable exception )
    {
        Throwable cause = getCause( exception );
        if( cause != null )
          return getLastThrowable( cause );
        return exception;
    }

    private static Throwable getCause( Throwable exception )
    {
        if( exception == null )
          throw new NullPointerException( "exception" );

        try
        {
            Method method = 
              exception.getClass().getMethod( "getCause", new Class[0] );
            return (Throwable) method.invoke( exception, new Object[0] );
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
            String token = tokenizer.nextToken();
            if( token.startsWith( "\tat " ) )
            {
                result[i] = token.substring( 4 );
            }
            else
            {
                result[i] = token;
            }
        }

        return result;
    }

    private static String getLine( String lead )
    {
        return getLine( lead, 0 );
    }

    private static String getLine( String lead, int count )
    {
        StringBuffer buffer = new StringBuffer( lead );
        int q = 0;
        if( count  > 0 )
        {
            String v = "" + count + " ";
            buffer.append( "" + count );
            buffer.append( " " );
            q = v.length() + 1;
        }
        int j = WIDTH - ( lead.length() + q );
        for( int i=0; i<j; i++ )
        {
            buffer.append( "-" );
        }
        buffer.append( "\n" );
        return buffer.toString();
    }
}
