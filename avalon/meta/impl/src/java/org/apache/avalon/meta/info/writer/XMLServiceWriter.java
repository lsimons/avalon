/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.meta.info.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.avalon.meta.info.Descriptor;
import org.apache.avalon.meta.info.Service;

/**
 * Write {@link Service} objects to a stream as xml documents.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:15:50 $
 */
public class XMLServiceWriter
    implements ServiceWriter
{

    /**
     * Write out type representation to xml.
     *
     * @param service the service descriptor instance
     * @param stream the stream to write to
     * @throws Exception if unable to write xml
     */
    public void writeService( final Service service, final OutputStream stream )
        throws Exception
    {
        final Writer writer = new OutputStreamWriter( stream );
        writeHeader( writer );
        writeDoctype( writer, "service" );
        writer.write( "\n\n<service>" );

        writer.write( "\n  <version>");
        writer.write( service.getVersion().toString() );
        writer.write( "</version>");

        writeAttributes( writer, service );
        writer.write( "\n</service>" );
        writer.flush();
    }

   /**
    * Write the XML header.
    * @param writer the writer
     * @throws IOException if unable to write xml
    */
    private void writeHeader( final Writer writer )
        throws IOException
    {
        writer.write( "<?xml version=\"1.0\" ?>" );
    }

    /**
     * Write out DOCType delcaration.
     *
     * @param writer the writer
     * @param root the root name of document
     * @throws IOException if unable to write xml
     */
    private void writeDoctype( final Writer writer,
                               final String root )
        throws IOException
    {
        final String doctype =
            "\n<!DOCTYPE " + root
            + " PUBLIC \"-//AVALON/Service DTD Version 1.0//EN\" "
            + "\"http://avalon.apache.org/dtds/meta/service_1_0.dtd\" >";
        writer.write( doctype );
    }

    /**
     * Write out xml representation of a set of attributes.
     *
     * @param writer the writer
     * @param descriptor a meta info descriptor holding attributes
     * @throws IOException if unable to write xml
     */
    private void writeAttributes( final Writer writer, final Descriptor descriptor )
        throws IOException
    {
        String[] names = descriptor.getAttributeNames();
        if( names.length > 0 )
        {
            writer.write( "\n  <attributes>" );
            for( int i = 0; i < names.length; i++ )
            {
                writeAttribute( writer, names[i], descriptor.getAttribute( names[i] ) );
            }
            writer.write( "\n  </attributes>" );
        }
    }

    /**
     * Write out xml representation of a single attribute entry.
     *
     * @param writer the writer
     * @param key the attribute key
     * @param value the attribute value
     * @throws IOException if unable to write xml
     */
    private void writeAttribute( final Writer writer,
                                 final String key, final String value )
        throws IOException
    {
        writer.write( "\n    <attribute key=\"" );
        writer.write( key );
        writer.write( "\" value=\"" );
        writer.write( value );
        writer.write( "\"/>" );
    }
}
