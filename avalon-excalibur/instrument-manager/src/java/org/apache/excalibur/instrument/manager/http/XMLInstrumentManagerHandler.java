/* 
 * Copyright 2002-2004 The Apache Software Foundation
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

package org.apache.excalibur.instrument.manager.http;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/02/29 18:11:04 $
 * @since 4.1
 */
public class XMLInstrumentManagerHandler
    extends AbstractXMLHandler
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new XMLInstrumentManagerHandler.
     *
     * @param path The path handled by this handler.
     * @param contentType The content type.
     */
    public XMLInstrumentManagerHandler( InstrumentManagerClient manager )
    {
        super( "/instrument-manager.xml", manager );
    }
    
    /*---------------------------------------------------------------
     * AbstractHTTPURLHandler Methods
     *-------------------------------------------------------------*/
    /**
     * Handles the specified request.
     *
     * @param The full path being handled.
     * @param parameters A Map of the parameters in the request.
     * @param os The PrintStream to write the result to.
     */
    public void doGet( String path, Map parameters, PrintStream out )
        throws IOException
    {
        boolean packed = ( getParameter( parameters, "packed", null ) != null );
        boolean recurse = ( getParameter( parameters, "recurse", null ) != null );
        
        out.println( InstrumentManagerHTTPConnector.XML_BANNER );
        outputInstrumentManager( out, getInstrumentManagerClient(), "", recurse, packed );
    }
            
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}

