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
import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2003/09/10 10:03:17 $
 * @since 4.1
 */
public class HTMLInstrumentHandler
    extends AbstractHTMLHandler
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new HTMLInstrumentHandler.
     *
     * @param manager Reference to the InstrumentManagerClient.
     */
    public HTMLInstrumentHandler( InstrumentManagerClient manager )
    {
        super( "/instrument.html", manager );
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
        InstrumentDescriptor desc;
        try
        {
            desc = getInstrumentManagerClient().locateInstrumentDescriptor( name );
        }
        catch ( NoSuchInstrumentException e )
        {
            // Sample no longer exists, go back to the parent instrument.
            int pos = name.lastIndexOf( '.' );
            if ( pos >= 0 )
            {
                throw new HTTPRedirect(
                    "instrumentable.html?name=" + urlEncode( name.substring( 0,  pos ) ) );
            }
            else
            {
                throw new HTTPRedirect( "instrument-manager.html" );
            }
        }
        
        String type;
        StringBuffer types = new StringBuffer();
        types.append( "<select name='type' onKeyPress=\"javascript:fieldChanged()\">" );
        StringBuffer presets = new StringBuffer();
        presets.append( "<select name='preset' onChange=\"javascript:applyPreset(this.options[this.selectedIndex].value)\">" );
        presets.append( "<option value='0-0' selected>-- Select a Preset --</option>" );
        switch ( desc.getType() )
        {
        case InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER:
            type = "Counter";
            types.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER
                + "' selected>Count</option>" );
            
            presets.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER
                + "-0'>Count / Second Over 10 Minutes</option>" );
            presets.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER
                + "-1'>Count / Minute Over 1 Day</option>" );
            presets.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER
                + "-2'>Count / Hour Over 1 Month</option>" );
            break;
            
        case InstrumentManagerClient.INSTRUMENT_TYPE_VALUE:
            type = "Value";
            types.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM
                + "' selected>Maximum Value</option>" );
            types.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM
                + "'>Minimum Value</option>" );
            types.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN
                + "'>Mean Value</option>" );
            
            presets.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM
                + "-0'>Max Value / Second Over 10 Minutes</option>" );
            presets.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM
                + "-1'>Max Value / Minute Over 1 Day</option>" );
            presets.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM
                + "-2'>Max Value / Hour Over 1 Month</option>" );
            
            presets.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM
                + "-0'>Min Value / Second Over 10 Minutes</option>" );
            presets.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM
                + "-1'>Min Value / Minute Over 1 Day</option>" );
            presets.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM
                + "-2'>Min Value / Hour Over 1 Month</option>" );
            
            presets.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN
                + "-0'>Mean Value / Second Over 10 Minutes</option>" );
            presets.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN
                + "-1'>Mean Value / Minute Over 1 Day</option>" );
            presets.append( "<option value='"
                + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN
                + "-2'>Mean Value / Hour Over 1 Month</option>" );
            break;
            
        default:
            type = "Unknown";
            break;
        }
        types.append( "</select>" );
        presets.append( "</select>" );
        
        out.println( "<html>" );
        out.println( "<head><title>" + desc.getDescription() + "</title></head>" );
        out.println( "<body>" );
        
        breadCrumbs( out, desc, false );
        
        out.println( "<h2>Instrument</h2>" );
        startTable( out );
        tableRow( out, 0, "Name", desc.getName() );
        tableRow( out, 0, "Description", desc.getDescription() );
        tableRow( out, 0, "Type", type );
        endTable( out );
        
        InstrumentSampleDescriptor[] samples = desc.getInstrumentSampleDescriptors();
        if ( samples.length > 0 )
        {
            out.println( "<h2>Registered Samples</h2>" );
            outputInstrumentSamples( out, samples );
        }
        
        out.println( "<h2>Register Sample</h2>" );
        out.println( "<SCRIPT LANGUAGE=\"JavaScript\">" );
        out.println( "function fieldChanged() {" );
        out.println( "  var form = document.forms[0];" );
        out.println( "  form.preset.value=\"0-0\";" );
        out.println( "}" );
        out.println( "function applyPreset(preset) {" );
        out.println( "  var form = document.forms[0];" );
        out.println( "  var pos = preset.indexOf('-');" );
        out.println( "  var type = preset.substring(0, pos);" );
        out.println( "  var spec = preset.substring(pos + 1);" );
        out.println( "  var prefix;" );
        out.println( "  if (type == 0) {" );
        out.println( "    return;" );
        out.println( "  } else if (type == " + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_COUNTER + ") {" );
        out.println( "    typeLbl = \"Count\"" );
        out.println( "  } else if (type == " + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MAXIMUM + ") {" );
        out.println( "    typeLbl = \"Max Value\"" );
        out.println( "  } else if (type == " + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MINIMUM + ") {" );
        out.println( "    typeLbl = \"Min Value\"" );
        out.println( "  } else if (type == " + InstrumentManagerClient.INSTRUMENT_SAMPLE_TYPE_MEAN + ") {" );
        out.println( "    typeLbl = \"Mean Value\"" );
        out.println( "  } else {" );
        out.println( "    typeLbl = \"Unknown\"" );
        out.println( "  }" );
        out.println( "  var intervalLbl;" );
        out.println( "  var interval;" );
        out.println( "  var size;" );
        out.println( "  var lease;" );
        out.println( "  if (spec == 1) {" );
        out.println( "    intervalLbl = \"Minute\";" );
        out.println( "    interval = 60000;" );
        out.println( "    size = 1440;" );
        out.println( "    lease = 86400000;" );
        out.println( "  } else if (spec == 2) {" );
        out.println( "    intervalLbl = \"Hour\";" );
        out.println( "    interval = 3600000;" );
        out.println( "    size = 672;" );
        out.println( "    lease = 86400000;" );
        out.println( "  } else {" );
        out.println( "    intervalLbl = \"Second\";" );
        out.println( "    interval = 1000;" );
        out.println( "    size = 600;" );
        out.println( "    lease = 600000;" );
        out.println( "  }" );
        out.println( "  form.description.value = typeLbl + \" / \" + intervalLbl;" );
        out.println( "  form.interval.value = interval;" );
        out.println( "  form.size.value = size;" );
        out.println( "  form.lease.value = lease;" );
        out.println( "  form.type.value = type;" );
        out.println( "}" );
        out.println( "</SCRIPT>" );

        out.println( "<form action='create-sample.html' method='GET'>" );
        startTable( out );
        tableRow( out, 0, "Description", "<input name='description' type='text' size='40' value='' onKeyPress=\"javascript:fieldChanged()\">" );
        tableRow( out, 0, "Interval (ms.)", "<input name='interval' type='text' size='10' value='' onKeyPress=\"javascript:fieldChanged()\">" );
        tableRow( out, 0, "Size", "<input name='size' type='text' size='10' value='' onKeyPress=\"javascript:fieldChanged()\">" );
        tableRow( out, 0, "Lease (ms.)", "<input name='lease' type='text' size='10' value='' onKeyPress=\"javascript:fieldChanged()\">" );
        tableRow( out, 0, "Type", types.toString() );
        tableRow( out, 0, "Presets", presets.toString() ); 
        endTable( out );
        out.println( "<input type='hidden' name='name' value='" + desc.getName() + "'>" );
        out.println( "<input type='submit' value='Submit'>" );
        
        out.println( "</form>" );
        
        footer( out );
        
        out.println( "</body>" );
        out.println( "</html>" );
    }
            
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}

