/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
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
package org.apache.excalibur.instrument.manager.http.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.excalibur.instrument.AbstractLogEnabledInstrumentable;
import org.apache.excalibur.instrument.CounterInstrument;
import org.apache.excalibur.instrument.ValueInstrument;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2003/09/10 12:51:43 $
 * @since 4.1
 */
public abstract class AbstractHTTPURLHandler
    extends AbstractLogEnabledInstrumentable
    implements HTTPURLHandler
{
    /** The path handled by this handler. */
    private String m_path;
    
    /** The content type. */
    private String m_contentType;
    
    /** The encoding. */
    private String m_encoding;
    
    /** Number of requests. */
    private CounterInstrument m_instrumentRequests;
    
    /** Time it takes to process each request. */
    private ValueInstrument m_instrumentRequestTime;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractHTTPURLHandler.
     *
     * @param path The path handled by this handler.
     * @param contentType The content type.
     */
    public AbstractHTTPURLHandler( String path, String contentType, String encoding )
    {
        m_path = path;
        m_contentType = contentType;
        m_encoding = encoding;
        
        addInstrument( m_instrumentRequests = new CounterInstrument( "requests" ) );
        addInstrument( m_instrumentRequestTime = new ValueInstrument( "request-time" ) );
    }
    
    /*---------------------------------------------------------------
     * HTTPURLHandler Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the path handled by this handler.
     *
     * @return The path handled by this handler.
     */
    public String getPath()
    {
        return m_path;
    }
    
    /**
     * Returns the content type.
     *
     * @return The content type.
     */
    public String getContentType()
    {
        return m_contentType;
    }
    
    /**
     * Return the encoding to use.
     *
     * @return the encoding.
     */
    public String getEncoding()
    {
        return m_encoding;
    }
    
    /**
     * Handles the specified request.
     *
     * @param The full path being handled.
     * @param parameters A Map of the parameters in the request.
     * @param os The OutputStream to write the result to.
     */
    public final void handleRequest( String path, Map parameters, OutputStream os )
        throws IOException
    {
        long start = System.currentTimeMillis();
        try
        {
            doGet( path, parameters, os );
        }
        finally
        {
            m_instrumentRequests.increment();
            if ( m_instrumentRequestTime.isActive() )
            {
                m_instrumentRequestTime.setValue( (int)( System.currentTimeMillis() - start ) );
            }
        }
    }
            
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Handles the specified request.
     *
     * @param The full path being handled.
     * @param parameters A Map of the parameters in the request.
     * @param os The OutputStream to write the result to.
     */
    public abstract void doGet( String path, Map parameters, OutputStream os )
        throws IOException;
    
    public String getParameter( Map params, String name, String defaultValue )
    {
        Object param = params.get( name );
        if ( param == null )
        {
            return defaultValue;
        }
        else if ( param instanceof String )
        {
            return (String)param;
        }
        else
        {
            List list = (List)param;
            return (String)list.get( 0 );
        }
    }
    
    public String getParameter( Map params, String name )
        throws FileNotFoundException
    {
        String param = getParameter( params, name, null );
        if ( param == null )
        {
            throw new FileNotFoundException( "The " + name + " parameter was not specified." );
        }
        return param;
    }
    
    public int getIntegerParameter( Map params, String name )
        throws FileNotFoundException
    {
        try
        {
            return Integer.parseInt( getParameter( params, name ) );
        }
        catch ( NumberFormatException e )
        {
            throw new FileNotFoundException( "The specified " + name + " was invalid." );
        }
    }
    
    public long getLongParameter( Map params, String name )
        throws FileNotFoundException
    {
        try
        {
            return Long.parseLong( getParameter( params, name ) );
        }
        catch ( NumberFormatException e )
        {
            throw new FileNotFoundException( "The specified " + name + " was invalid." );
        }
    }
    
    public long getLongParameter( Map params, String name, long defaultValue )
    {
        String value = getParameter( params, name, null );
        if ( value == null )
        {
            return defaultValue;
        }
        
        try
        {
            return Long.parseLong( value );
        }
        catch ( NumberFormatException e )
        {
            return defaultValue;
        }
    }
    
    public String[] getParameters( Map params, String name )
    {
        Object param = params.get( name );
        if ( param == null )
        {
            return new String[0];
        }
        else if ( param instanceof String )
        {
            return new String[] { (String)param };
        }
        else
        {
            List list = (List)param;
            String[] ary = new String[list.size()];
            list.toArray( ary );
            return ary;
        }
    }
    
    public long[] getLongParameters( Map params, String name, long defaultValue )
    {
        String[] values = getParameters( params, name );
        long[] lValues = new long[values.length];
        
        for ( int i = 0; i < values.length; i++ )
        {
            try
            {
                lValues[i] = Long.parseLong( values[i] );
            }
            catch ( NumberFormatException e )
            {
                lValues[i] = defaultValue;
            }
        }
        
        return lValues;
    }
}

