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
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import org.apache.excalibur.instrument.manager.http.server.HTTPRedirect;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleSnapshot;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentSampleException;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/09/10 10:03:17 $
 * @since 4.1
 */
public class HTMLSampleHandler
    extends AbstractHTMLHandler
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new HTMLSampleHandler.
     *
     * @param manager Reference to the InstrumentManagerClient.
     */
    public HTMLSampleHandler( InstrumentManagerClient manager )
    {
        super( "/sample.html", manager );
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
        String name = getParameter( parameters, "name" );
        InstrumentSampleDescriptor desc;
        try
        {
            desc = getInstrumentManagerClient().locateInstrumentSampleDescriptor( name );
        }
        catch ( NoSuchInstrumentSampleException e )
        {
            // Sample no longer exists, go back to the parent instrument.
            int pos = name.lastIndexOf( '.' );
            if ( pos >= 0 )
            {
                throw new HTTPRedirect(
                    "instrument.html?name=" + urlEncode( name.substring( 0,  pos ) ) );
            }
            else
            {
                throw new HTTPRedirect( "instrument-manager.html" );
            }
        }
        String chart = getParameter( parameters, "chart", null );
        
        InstrumentSampleSnapshot snapshot = desc.getSnapshot();
        
        String type;
        switch ( desc.getType() )
        {
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER:
            type = "Counter";
            break;
            
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM:
            type = "Max Value";
            break;
            
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM:
            type = "Min Value";
            break;
            
        case InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN:
            type = "Mean Value";
            break;
            
        default:
            type = "Unknown";
            break;
        }
        
        out.println( "<html>" );
        out.println( "<head><title>" + desc.getDescription() + "</title></head>" );
        out.println( "<body>" );
        
        breadCrumbs( out, desc, false );
        
        out.println( "<h2>Instrument Sample</h2>" );
        startTable( out );
        tableRow( out, 0, "Name", desc.getName() );
        tableRow( out, 0, "Description", desc.getDescription() );
        tableRow( out, 0, "Type", type );
        tableRow( out, 0, "Interval", desc.getInterval() + "ms." );
        tableRow( out, 0, "Size", Integer.toString( desc.getSize() ) );
        if ( desc.getLeaseExpirationTime() > 0 )
        {
            String renewUrl = "sample-lease.html?name=" + urlEncode( desc.getName() )
                + ( chart == null ? "" : "&chart=true" ) + "&lease=";
            
            String value = new Date( desc.getLeaseExpirationTime() ).toString()
                    + " (Renew <a href='" + renewUrl + "600000'>10min</a>, "
                    + "<a href='" + renewUrl + "3600000'>1hr</a>, "
                    + "<a href='" + renewUrl + "86400000'>1day</a>)";
            
            // Make the text red if it is about to expire.
            if ( desc.getLeaseExpirationTime() - System.currentTimeMillis() < 300000 )
            {
                value = "<font color='ff0000'>" + value + "</font>";
            }

            tableRow( out, 0, "Expiration", value );
        }
        else
        {
            tableRow( out, 0, "Expiration", "Permanent" );
        }
        endTable( out );
        
        if ( chart == null )
        {
            out.println( "<h2>Data Samples (<a href='sample.html?name="
                + urlEncode( desc.getName() ) + "&chart=true'>Chart</a>)</h2>" );
            
            startTable( out );
            startTableHeaderRow( out );
            tableHeaderCell( out, "Period" );
            tableHeaderCell( out, "Value" );
            endTableHeaderRow( out );
            long time = snapshot.getTime();
            int[] samples = snapshot.getSamples();
            for ( int i = 0; i < samples.length; i++ )
            {
                startTableRow( out, i );
                tableCell( out, new Date( time ).toString() );
                tableCell( out, Integer.toString( samples[samples.length - i - 1] ) );
                endTableRow( out );
                
                time -= snapshot.getInterval();
            }
            endTable( out );
        }
        else
        {
            out.println( "<h2>Data Samples (<a href='sample.html?name="
                + urlEncode( desc.getName() ) + "'>Plain</a>)</h2>" );
            
            out.println( "<SCRIPT LANGUAGE=\"JavaScript\">" );
            out.println( "var intervalId;" );
            out.println( "function refreshChart() {" );
            out.println( "  document.chart.src=\"sample-chart.jpg?name=" + urlEncode( desc.getName() ) + "&time=\" + new Date().getTime();" );
            out.println( "}" );
            out.println( "function setRefresh(refresh) {" );
            out.println( "  clearInterval(intervalId);" );
            out.println( "  intervalId = setInterval(\"refreshChart()\", refresh);" );
            out.println( "}" );
            out.println( "function chartError() {" );
            out.println( "  clearInterval(intervalId);" );
            out.println( "  document.location=\"instrument.html?name=" + urlEncode( desc.getInstrumentDescriptor().getName() ) + "\";" );
            out.println( "}" );
            // No auto refresh by default.
            //out.println( "setRefresh(5000);" );
            out.println( "</SCRIPT>" );
            
            out.println( "<form>" );
            startTable( out );
            tableCell( out, "<img name='chart' src='sample-chart.jpg?name=" + urlEncode( desc.getName() ) + "' onError='javascript:chartError()'>" );
            endTable( out );
            out.println( "Refresh rate:" );
            out.println( "<input type='button' value='No Refresh' onClick='javascript:clearInterval(intervalId)'>" );
            out.println( "<input type='button' value='1 Second' onClick='javascript:setRefresh(1000)'>" );
            out.println( "<input type='button' value='5 Seconds' onClick='javascript:setRefresh(5000)'>" );
            out.println( "<input type='button' value='10 Seconds' onClick='javascript:setRefresh(10000)'>" );
            out.println( "<input type='button' value='1 Minute' onClick='javascript:setRefresh(60000)'>" );
            out.println( "<input type='button' value='Refresh Now' onClick='javascript:refreshChart()'>" );
            out.println( "</form>" );
        }
        
        footer( out );
        
        out.println( "</body>" );
        out.println( "</html>" );
    }
            
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}

