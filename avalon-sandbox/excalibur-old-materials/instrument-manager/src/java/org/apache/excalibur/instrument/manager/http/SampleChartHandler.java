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

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.excalibur.instrument.manager.http.server.AbstractHTTPURLHandler;
import org.apache.excalibur.instrument.manager.http.server.HTTPRedirect;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleSnapshot;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentSampleException;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/09/08 09:00:44 $
 * @since 4.1
 */
public class SampleChartHandler
    extends AbstractHTTPURLHandler
{
    /** The instrument manager */
    private InstrumentManagerClient m_manager;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new SampleChartHandler.
     *
     * @param path The path handled by this handler.
     * @param manager Reference to the instrument manager client interface.
     */
    public SampleChartHandler( InstrumentManagerClient manager )
    {
        super( "/sample-chart.jpg", CONTENT_TYPE_IMAGE_JPEG,
            InstrumentManagerHTTPConnector.ENCODING );
        
        m_manager = manager;
    }
    
    /*---------------------------------------------------------------
     * AbstractHandler Methods
     *-------------------------------------------------------------*/
    /**
     * Handles the specified request.
     *
     * @param The full path being handled.
     * @param parameters A Map of the parameters in the request.
     * @param os The OutputStream to write the result to.
     */
    public void doGet( String path, Map parameters, OutputStream os )
        throws IOException
    {
        String name = getParameter( parameters, "name" );
        InstrumentSampleDescriptor desc;
        try
        {
            desc = m_manager.locateInstrumentSampleDescriptor( name );
        }
        catch ( NoSuchInstrumentSampleException e )
        {
            // Sample no longer exists, go back to the parent instrument.
            int pos = name.lastIndexOf( '.' );
            if ( pos >= 0 )
            {
                String iName;
                try
                {
                    iName = URLEncoder.encode( name.substring( 0,  pos ), InstrumentManagerHTTPConnector.ENCODING );
                }
                catch ( UnsupportedEncodingException e2 )
                {
                    // Should not happen
                    getLogger().error( "Bad encoding", e2 );
                    iName = name;
                }
                
                throw new HTTPRedirect( "instrument.html?name=" + iName );
            }
            else
            {
                throw new HTTPRedirect( "instrumentable.html" );
            }
        }
        
        InstrumentSampleSnapshot snapshot = desc.getSnapshot();
        
        // Decide on a line interval based on the interval of the sample.
        long interval = snapshot.getInterval();
        int hInterval;
        String format;
        String detailFormat;
        if( interval < 1000 )
        {
            // Once per 10 seconds.
            hInterval = (int)( 10000 / interval );
            format = "{2}:{3}:{4}";
            detailFormat = "{0}/{1} {2}:{3}:{4}.{5}";
        }
        else if( interval < 60000 )
        {
            // Once per minute.
            hInterval = (int)( 60000 / interval );
            format = "{2}:{3}:{4}";
            detailFormat = "{0}/{1} {2}:{3}:{4}";
        }
        else if( interval < 600000 )
        {
            // Once per 10 minutes
            hInterval = (int)( 600000 / interval );
            format = "{0}/{1} {2}:{3}";
            detailFormat = "{0}/{1} {2}:{3}";
        }
        else if( interval < 3600000 )
        {
            // Once per hour.
            hInterval = (int)( 3600000 / interval );
            format = "{0}/{1} {2}:{3}";
            detailFormat = "{0}/{1} {2}:{3}";
        }
        else if( interval < 86400000 )
        {
            // Once per day.
            hInterval = (int)( 86400000 / interval );
            format = "{0}/{1}";
            detailFormat = "{0}/{1} {2}:{3}";
        }
        else
        {
            // Default to every 10 points.
            hInterval = 10;
            format = "{0}/{1} {2}:{3}";
            detailFormat = "{0}/{1} {2}:{3}";
        }
            
        // Actually create the chart and add it to the content pane
        LineChart chart = new LineChart( hInterval, interval, format, detailFormat, 20 );
        chart.setValues( snapshot.getSamples(), snapshot.getTime() );
        
        byte[] imageData = null;
        
        // Create a new BufferedImage onto which the plant will be painted.
        BufferedImage bi = new BufferedImage( 600, 120, BufferedImage.TYPE_INT_RGB );
        
        // Paint the chart onto the Graphics object of the BufferedImage.
        chart.setSize( bi.getWidth(), bi.getHeight() );
        chart.paintComponent( bi.createGraphics() );

        // Encode the BufferedImage as a JPEG image and write it to the output stream.
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder( os );
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam( bi );
        param.setQuality( 0.90f, true );
        encoder.encode( bi, param );
    }
}

