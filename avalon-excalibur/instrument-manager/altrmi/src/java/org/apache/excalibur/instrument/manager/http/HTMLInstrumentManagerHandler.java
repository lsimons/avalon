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

package org.apache.excalibur.instrument.manager.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.apache.excalibur.instrument.manager.http.server.HTTPRedirect;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2004/02/25 09:20:22 $
 * @since 4.1
 */
public class HTMLInstrumentManagerHandler
    extends AbstractHTMLHandler
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new HTMLInstrumentManagerHandler.
     *
     * @param manager Reference to the InstrumentManagerClient.
     */
    public HTMLInstrumentManagerHandler( InstrumentManagerClient manager )
    {
        super( "/instrument-manager.html", manager );
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
        // This is the root
        out.println( "<html>" );
        out.println( "<head><title>" + getInstrumentManagerClient().getDescription()
            + "</title></head>" );
        out.println( "<body>" );
        
        breadCrumbs( out, false );
        
        out.println( "<h2>Instrument Manager</h2>" );
        startTable( out );
        tableRow( out, 0, "Name", getInstrumentManagerClient().getName() );
        tableRow( out, 0, "Description", getInstrumentManagerClient().getDescription() );
        endTable( out );
        
        InstrumentableDescriptor[] instrumentables =
            getInstrumentManagerClient().getInstrumentableDescriptors();
        if ( instrumentables.length > 0 )
        {
            out.println( "<h2>Instrumentables</h2>" );
            outputInstrumentables( out, instrumentables );
        }
        
        footer( out );
        
        out.println( "</body>" );
        out.println( "</html>" );
    }
            
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}

