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
package org.apache.excalibur.instrument.manager.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentSampleException;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/09/10 10:03:17 $
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
     * @param os The PrintStream to write the result to.
     */
    public void doGet( String path, Map parameters, PrintStream out )
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

