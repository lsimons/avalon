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
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleSnapshot;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/09/10 10:03:17 $
 * @since 4.1
 */
public abstract class AbstractXMLHandler
    extends AbstractHandler
{
    protected static final String INDENT = "  ";
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractXMLHandler.
     *
     * @param path The path handled by this handler.
     * @param manager Reference to the instrument manager client interface.
     */
    public AbstractXMLHandler( String path, InstrumentManagerClient manager )
    {
        super( path, CONTENT_TYPE_TEXT_XML, manager );
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    protected String makeSafeAttribute( String attribute )
    {
        // TODO:
        return attribute;
    }
    
    protected void outputLine( PrintStream out, String indent, boolean packed, String line )
    {
        if ( !packed )
        {
            out.print( indent );
        }
        out.print( line );
        if ( !packed )
        {
            out.println();
        }
    }
    
    protected void outputInstrumentManager( PrintStream out,
                                            InstrumentManagerClient manager,
                                            String indent,
                                            boolean recurse,
                                            boolean packed )
        throws IOException
    {
        outputLine( out, indent, packed, "<instrument-manager "
            + "name=\"" + makeSafeAttribute( manager.getName() ) + "\" "
            + "description=\"" + makeSafeAttribute( manager.getDescription() ) + "\" "
            + "state-version=\"" + manager.getStateVersion() + "\">" );
        
        String childIndent = indent + INDENT;
        
        InstrumentableDescriptor[] instrumentables = manager.getInstrumentableDescriptors();
        for ( int i = 0; i < instrumentables.length; i++ )
        {
            InstrumentableDescriptor instrumentable = instrumentables[i];
            if ( recurse )
            {
                outputInstrumentable( out, instrumentable, childIndent, recurse, packed );
            }
            else
            {
                outputInstrumentableBrief( out, instrumentable, childIndent, packed );
            }
        }
        
        outputLine( out, indent, packed, "</instrument-manager>" );
    }
    
    protected void outputInstrumentableBrief( PrintStream out,
                                              InstrumentableDescriptor instrumentable,
                                              String indent,
                                              boolean packed )
        throws IOException
    {
        outputLine( out, indent, packed, "<instrumentable "
            + "name=\"" + makeSafeAttribute( instrumentable.getName() ) + "\" "
            + "state-version=\"" + instrumentable.getStateVersion() + "\"/>" );
    }
    
    protected void outputInstrumentable( PrintStream out,
                                         InstrumentableDescriptor instrumentable,
                                         String indent,
                                         boolean recurse,
                                         boolean packed )
        throws IOException
    {
        InstrumentableDescriptor[] instrumentables =
            instrumentable.getChildInstrumentableDescriptors();
        InstrumentDescriptor[] instruments = instrumentable.getInstrumentDescriptors();
        
        String terminator;
        if ( ( instrumentables.length > 0 ) || ( instruments.length > 0 ) )
        {
            terminator = ">";
        }
        else
        {
            terminator = "/>";
        }
                                            
        outputLine( out, indent, packed, "<instrumentable "
            + "name=\"" + makeSafeAttribute( instrumentable.getName() ) + "\" "
            + "description=\"" + makeSafeAttribute( instrumentable.getDescription() ) + "\" "
            + "state-version=\"" + instrumentable.getStateVersion() + "\" "
            + "registered=\"" + instrumentable.isRegistered() + "\" "
            + "configured=\"" + instrumentable.isConfigured() + "\"" + terminator );
        
        if ( ( instrumentables.length > 0 ) || ( instruments.length > 0 ) )
        {
            String childIndent = indent + INDENT;
            
            for ( int i = 0; i < instrumentables.length; i++ )
            {
                InstrumentableDescriptor child = instrumentables[i];
                if ( recurse )
                {
                    outputInstrumentable( out, child, childIndent, recurse, packed );
                }
                else
                {
                    outputInstrumentableBrief( out, child, childIndent, packed );
                }
            }
            
            for ( int i = 0; i < instruments.length; i++ )
            {
                InstrumentDescriptor instrument = instruments[i];
                if ( recurse )
                {
                    outputInstrument( out, instrument, childIndent, recurse, packed );
                }
                else
                {
                    outputInstrumentBrief( out, instrument, childIndent, packed );
                }
            }
            
            outputLine( out, indent, packed, "</instrumentable>" );
        }
    }
    
    protected void outputInstrumentBrief( PrintStream out,
                                          InstrumentDescriptor instrument,
                                          String indent,
                                          boolean packed )
        throws IOException
    {
        outputLine( out, indent, packed, "<instrument "
            + "name=\"" + makeSafeAttribute( instrument.getName() ) + "\" "
            + "state-version=\"" + instrument.getStateVersion() + "\"/>" );
    }
    
    protected void outputInstrument( PrintStream out,
                                     InstrumentDescriptor instrument,
                                     String indent,
                                     boolean recurse,
                                     boolean packed )
        throws IOException
    {
        InstrumentSampleDescriptor[] samples = instrument.getInstrumentSampleDescriptors();
        
        String terminator;
        if ( samples.length > 0 )
        {
            terminator = ">";
        }
        else
        {
            terminator = "/>";
        }
        
        outputLine( out, indent, packed, "<instrument "
            + "name=\"" + makeSafeAttribute( instrument.getName() ) + "\" "
            + "description=\"" + makeSafeAttribute( instrument.getDescription() ) + "\" "
            + "type=\"" + instrument.getType() + "\" "
            + "state-version=\"" + instrument.getStateVersion() + "\" "
            + "registered=\"" + instrument.isRegistered() + "\" "
            + "configured=\"" + instrument.isConfigured() + "\"" + terminator );
        
        if ( samples.length > 0 )
        {
            String childIndent = indent + INDENT;
            
            for ( int i = 0; i < samples.length; i++ )
            {
                InstrumentSampleDescriptor sample = samples[i];
                if ( recurse )
                {
                    outputSample( out, sample, childIndent, packed );
                }
                else
                {
                    outputSampleBrief( out, sample, childIndent, packed );
                }
            }
            
            outputLine( out, indent, packed, "</instrument>" );
        }
    }
    
    protected void outputSampleBrief( PrintStream out,
                                      InstrumentSampleDescriptor sample,
                                      String indent,
                                      boolean packed )
        throws IOException
    {
        outputLine( out, indent, packed, "<sample "
            + "name=\"" + makeSafeAttribute( sample.getName() ) + "\" "
            + "state-version=\"" + sample.getStateVersion() + "\"/>" );
    }
    
    protected void outputSample( PrintStream out,
                                 InstrumentSampleDescriptor sample,
                                 String indent,
                                 boolean packed )
        throws IOException
    {
        outputLine( out, indent, packed, "<sample "
            + "name=\"" + makeSafeAttribute( sample.getName() ) + "\" "
            + "description=\"" + makeSafeAttribute( sample.getDescription() ) + "\" "
            + "type=\"" + sample.getType() + "\" "
            + "interval=\"" + sample.getInterval() + "\" "
            + "size=\"" + sample.getSize() + "\" "
            + "value=\"" + sample.getValue() + "\" "
            + "time=\"" + sample.getTime() + "\" "
            + "expiration-time=\"" + sample.getLeaseExpirationTime() + "\" "
            + "state-version=\"" + sample.getStateVersion() + "\" "
            + "configured=\"" + sample.isConfigured() + "\"/>" );
    }
    
    protected void outputSampleHistory( PrintStream out,
                                        InstrumentSampleDescriptor sample,
                                        String indent,
                                        long baseTime,
                                        boolean packed,
                                        boolean compact )
        throws IOException
    {
        InstrumentSampleSnapshot snapshot = sample.getSnapshot();
        int[] values = snapshot.getSamples();
        
        // Given the base time, decide on the first value index and this time which
        //  will be included.
        long firstTime = snapshot.getTime() - ( snapshot.getSize() - 1 ) * snapshot.getInterval();
        int firstIndex;
        if ( baseTime <= firstTime )
        {
            firstIndex = 0;
        }
        else if ( baseTime >= snapshot.getTime() )
        {
            firstTime = snapshot.getTime();
            firstIndex = values.length - 1;
        }
        else
        {
            int count = (int)Math.ceil(
                ( (double)snapshot.getTime() - baseTime ) / snapshot.getInterval() ) + 1;
            firstTime = snapshot.getTime() - ( count - 1 ) * snapshot.getInterval();
            firstIndex = values.length - count;
        }
        
        // Where possible, display values from the snapshot rather than the sample
        //  to avoid any synchronization issues.
        outputLine( out, indent, packed, "<sample "
            + "name=\"" + makeSafeAttribute( sample.getName() ) + "\" "
            + "description=\"" + makeSafeAttribute( sample.getDescription() ) + "\" "
            + "type=\"" + sample.getType() + "\" "
            + "interval=\"" + snapshot.getInterval() + "\" "
            + "size=\"" + snapshot.getSize() + "\" "
            + "value=\"" + values[values.length - 1] + "\" "
            + "time=\"" + snapshot.getTime() + "\" "
            + "first-time=\"" + firstTime + "\" "
            + "count=\"" + ( values.length - firstIndex ) + "\" "
            + "expiration-time=\"" + sample.getLeaseExpirationTime() + "\" "
            + "state-version=\"" + snapshot.getStateVersion() + "\" "
            + "configured=\"" + sample.isConfigured() + "\">" );
        
        String childIndent = indent + INDENT;
        
        if ( compact )
        {
            // Output the values as a comma separated list.
            StringBuffer sb = new StringBuffer();
            sb.append( "<values>" );
            for ( int i = firstIndex; i < values.length; i++ )
            {
                if ( i > firstIndex )
                {
                    sb.append( "," );
                }
                sb.append( values[i] );
            }
            sb.append( "</values>" );
            
            outputLine( out, childIndent, packed, sb.toString() );
        }
        else
        {
            // Output an element for each value.
            long interval = snapshot.getInterval();
            long time = firstTime;
            for ( int i = firstIndex; i < values.length; i++ )
            {
                outputLine( out, childIndent, packed,
                    "<value time=\"" + time + "\" value=\"" + values[i] + "\"/>" );
                time += interval;
            }
        }
        
        outputLine( out, indent, packed, "</sample>" );
    }
}

