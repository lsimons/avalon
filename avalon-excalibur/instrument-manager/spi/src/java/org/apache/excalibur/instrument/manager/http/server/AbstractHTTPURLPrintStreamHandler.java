/* 
 * Copyright 2002-2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.excalibur.instrument.manager.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.3 $ $Date: 2004/02/25 10:33:15 $
 * @since 4.1
 */
public abstract class AbstractHTTPURLPrintStreamHandler
    extends AbstractHTTPURLHandler
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractHTTPURLPrintStreamHandler.
     *
     * @param path The path handled by this handler.
     * @param contentType The content type.
     * @param encoding The encoding to use when writing servlet results.
     */
    public AbstractHTTPURLPrintStreamHandler( String path, String contentType, String encoding )
    {
        super( path, contentType + "; charset=" + encoding, encoding );
    }
    
    /*---------------------------------------------------------------
     * AbstractHTTPURLHandler Methods
     *-------------------------------------------------------------*/
    /**
     * Handles the specified request.
     *
     * @param The full path being handled.
     * @param parameters A Map of the parameters in the request.
     * @param os The OutputStream to write the result to.
     */
    public final void doGet( String path, Map parameters, OutputStream os )
        throws IOException
    {
        PrintStream out = new PrintStream( os, false, getEncoding() );
        doGet( path, parameters, out );
    }
            
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Handles the specified request.
     *
     * @param The full path being handled.
     * @param parameters A Map of the parameters in the request.
     * @param os The PrintStream to write the result to.
     */
    public abstract void doGet( String path, Map parameters, PrintStream out )
        throws IOException;
}

