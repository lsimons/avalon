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

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/09/08 09:00:44 $
 * @since 4.1
 */
public abstract class AbstractHTMLHandler
    extends AbstractHandler
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractHTMLHandler.
     *
     * @param path The path handled by this handler.
     * @param manager Reference to the instrument manager client interface.
     */
    public AbstractHTMLHandler( String path, InstrumentManagerClient manager )
    {
        super( path, CONTENT_TYPE_TEXT_HTML, manager );
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    protected String urlEncode( String str )
    {
        try
        {
            return URLEncoder.encode( str, getEncoding() );
        }
        catch ( UnsupportedEncodingException e )
        {
            // Should never happen as the encoding is controlled.
            throw new IllegalStateException( e.getMessage() );
        }
    }
    
    protected void breadCrumbs( PrintStream out, boolean link )
    {
        if ( link )
        {
            out.println( "<a href='instrumentable.html'>"
                + getInstrumentManagerClient().getDescription() + "</a>" );
        }
        else
        {
            out.println( getInstrumentManagerClient().getDescription() );
        }
    }
    
    protected void breadCrumbs( PrintStream out, InstrumentableDescriptor desc, boolean link )
    {
        InstrumentableDescriptor parent = desc.getParentInstrumentableDescriptor();
        if ( parent == null )
        {
            breadCrumbs( out, true );
        }
        else
        {
            breadCrumbs( out, parent, true );
        }
        out.print( " <b>&gt;</b> " );
        if ( link )
        {
            out.println( "<a href='instrumentable.html?name=" + urlEncode( desc.getName() ) + "'>"
                + desc.getDescription() + "</a>" );
        }
        else
        {
            out.println( desc.getDescription() );
        }
    }
    
    protected void breadCrumbs( PrintStream out, InstrumentDescriptor desc, boolean link )
    {
        breadCrumbs( out, desc.getInstrumentableDescriptor(), true );
        out.print( " <b>&gt;</b> " );
        if ( link )
        {
            out.println( "<a href='instrument.html?name=" + urlEncode( desc.getName() ) + "'>"
                + desc.getDescription() + "</a>" );
        }
        else
        {
            out.println( desc.getDescription() );
        }
    }
    
    protected void breadCrumbs( PrintStream out, InstrumentSampleDescriptor desc, boolean link )
    {
        breadCrumbs( out, desc.getInstrumentDescriptor(), true );
        out.print( " <b>&gt;</b> " );
        if ( link )
        {
            out.println( "<a href='sample.html?name=" + urlEncode( desc.getName() ) + "'>"
                + desc.getDescription() + "</a>" );
        }
        else
        {
            out.println( desc.getDescription() );
        }
    }
    
    protected void startTable( PrintStream out )
        throws IOException
    {
        out.println( "<table cellpadding='1' cellspacing='0'><tr><td bgcolor='#bbbbbb'><table cellpadding='2' cellspacing='1'>" );
    }
    protected void endTable( PrintStream out )
        throws IOException
    {
        out.println( "</table></td></tr></table>" );
    }
    
    protected void startTableHeaderRow( PrintStream out )
        throws IOException
    {
        out.println( "<tr>" );
    }
    
    protected void endTableHeaderRow( PrintStream out )
        throws IOException
    {
        out.println( "</tr>" );
    }
    
    protected void tableHeaderCell( PrintStream out, String value )
        throws IOException
    {
        out.print( "<td bgcolor='#dddddd' nowrap><b>" + value + "</b></td>" );
    }
    
    protected void startTableRow( PrintStream out, int row )
        throws IOException
    {
        String color;
        if ( row % 2 == 0 )
        {
            color = "#eeeeee";
        }
        else
        {
            color = "#e4e4e4";
        }
        out.println( "<tr bgcolor='" + color + "'>" );
    }
    
    protected void endTableRow( PrintStream out )
        throws IOException
    {
        out.println( "</tr>" );
    }
    
    protected void tableCell( PrintStream out, String value )
        throws IOException
    {
        out.print( "<td nowrap>" + value + "</td>" );
    }
    
    protected void tableRow( PrintStream out, int row, String label, String value )
        throws IOException
    {
        startTableRow( out, row );
        tableHeaderCell( out, label );
        tableCell( out, value );
        endTableRow( out );
    }
    
    protected void footer( PrintStream out )
    {
        out.println( "<br>" );
        out.print( "<font size='-1' color='#888888'>" );
        out.print( "<center>" );
        out.print( "<a href='http://avalon.apache.org/excalibur/instrument-manager/index.html'>" );
        out.print( "Avalon Instrument Manager HTTP Client" );
        out.print( "</a><br>" );
        out.print( "Copyright c 2002-2003 The Apache Software Foundation.. All rights reserved." );
        out.print( "</center>" );
        out.println( "</font>" );
    }
}

