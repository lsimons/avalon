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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentSampleException;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/03/06 14:01:28 $
 * @since 4.1
 */
public class XMLSnapshotsHandler
    extends AbstractXMLHandler
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new XMLSnapshotsHandler.
     *
     * @param path The path handled by this handler.
     * @param contentType The content type.
     */
    public XMLSnapshotsHandler( InstrumentManagerClient manager )
    {
        super( "/snapshots.xml", manager );
    }
    
    /*---------------------------------------------------------------
     * AbstractHTTPURLHandler Methods
     *-------------------------------------------------------------*/
    /**
     * Handles the specified request.
     *
     * @param The full path being handled.
     * @param parameters A Map of the parameters in the request.
     * @param os The PrintWriter to write the result to.
     */
    public void doGet( String path, Map parameters, PrintWriter out )
        throws IOException
    {
        String[] names = getParameters( parameters, "name" );
        long[] baseTimes = getLongParameters( parameters, "base-time", 0 );
        boolean packed = ( getParameter( parameters, "packed", null ) != null );
        boolean compact = ( getParameter( parameters, "compact", null ) != null );
        
        if ( ( baseTimes.length == 0 ) && ( baseTimes.length > 0 ) )
        {
            baseTimes = new long[baseTimes.length];
        }
        else if ( names.length != baseTimes.length )
        {
            throw new FileNotFoundException(
                "The number of base-time values not equal to the number of names." );
        }
        
        out.println( InstrumentManagerHTTPConnector.XML_BANNER );
        if ( names.length > 0 )
        {
            outputLine( out, "", packed, "<samples>" );
            
            for ( int i = 0; i < names.length; i++ )
            {
                InstrumentSampleDescriptor desc;
                try
                {
                    desc =
                        getInstrumentManagerClient().locateInstrumentSampleDescriptor( names[i] );
                    
                    outputSampleHistory( out, desc, "", baseTimes[i], packed, compact );
                }
                catch ( NoSuchInstrumentSampleException e )
                {
                    outputLine( out, "  ", packed,
                        "<sample name=\"" + names[i] + "\" expired=\"true\"/>" );
                }
            }
            
            outputLine( out, "", packed, "</samples>" );
        }
        else
        {
            outputLine( out, "", packed, "<samples/>" );
        }
    }
            
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}

