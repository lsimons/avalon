/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.framework.tools.infobuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;
import org.apache.avalon.phoenix.framework.info.DependencyDescriptor;
import org.apache.avalon.phoenix.framework.info.FeatureDescriptor;
import org.apache.avalon.phoenix.framework.info.SchemaDescriptor;
import org.apache.avalon.phoenix.framework.info.ServiceDescriptor;

/**
 * Write {@link ComponentInfo} objects to a stream as xml
 * documents in legacy BlockInfo format.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/22 12:07:13 $
 */
public class LegacyBlockInfoWriter
    implements InfoWriter
{
    /**
     * Write out info representation to xml.
     *
     * @param info the info object
     * @param outputStream the stream to write to
     * @throws IOException if unable to write xml
     */
    public void writeComponentInfo( final ComponentInfo info,
                                    final OutputStream outputStream )
        throws Exception
    {
        final Writer writer = new OutputStreamWriter( outputStream );
        writeHeader( writer );
        writeDoctype( writer );
        writer.write( "<blockinfo>" );
        writeBlock( writer, info );
        writeServices( writer, info.getServices() );
        writeMxServices( writer, info.getServices() );
        writeDependencies( writer, info.getDependencies() );
        writer.write( "</blockinfo>" );
        writer.flush();
    }

    private void writeHeader( final Writer writer )
        throws IOException
    {
        writer.write( "<?xml version=\"1.0\" ?>" );
    }

    /**
     * Write out DOCType delcaration.
     *
     * @param writer the writer
     * @throws IOException if unable to write xml
     */
    private void writeDoctype( final Writer writer )
        throws IOException
    {
        final String doctype =
            "<!DOCTYPE blockinfo " +
            "PUBLIC \"-//PHOENIX/Block Info DTD Version 1.0//EN\" " +
            "\"http://jakarta.apache.org/avalon/dtds/phoenix/blockinfo_1_0.dtd\">";
        writer.write( doctype );
    }

    /**
     * Write out xml representation of a component.
     *
     * @param writer the writer
     * @param info the component info
     * @throws IOException if unable to write xml
     */
    private void writeBlock( final Writer writer,
                             final ComponentInfo info )
        throws IOException
    {
        writer.write( "<block>\n" );
        writer.write( "  <version>1.0</version>" );

        final SchemaDescriptor schema = info.getConfigurationSchema();
        if( null != schema )
        {
            final String output =
                "  <schema-type>" + schema.getType() + "</schema-type>";
            writer.write( output );
        }

        writer.write( "</block>" );
    }

    /**
     * Write out xml representation of a set of services.
     *
     * @param writer the writer
     * @param services the services
     * @throws IOException if unable to write xml
     */
    private void writeServices( final Writer writer,
                                final ServiceDescriptor[] services )
        throws IOException
    {
        if( 0 == services.length )
        {
            return;
        }

        writer.write( "<services>" );
        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDescriptor service = services[ i ];
            if( !LegacyUtil.isMxService( service ) )
            {
                writeService( writer, service.getType(), service );
            }
        }
        writer.write( "</services>" );
    }


    /**
     * Write out xml representation of a set of services.
     *
     * @param writer the writer
     * @param services the services
     * @throws IOException if unable to write xml
     */
    private void writeMxServices( final Writer writer,
                                final ServiceDescriptor[] services )
        throws IOException
    {
        if( 0 == services.length )
        {
            return;
        }

        writer.write( "<management-access-points>" );
        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDescriptor service = services[ i ];
            if( LegacyUtil.isMxService( service ) )
            {
                writeService( writer, service.getType(), service );
            }
        }
        writer.write( "</management-access-points>" );
    }

    /**
     * Write out xml representation of a set of dependencies.
     *
     * @param writer the writer
     * @param dependencies the dependencies
     * @throws IOException if unable to write xml
     */
    private void writeDependencies( final Writer writer,
                                    final DependencyDescriptor[] dependencies )
        throws IOException
    {
        if( 0 == dependencies.length )
        {
            return;
        }

        writer.write( "<dependencies>" );
        for( int i = 0; i < dependencies.length; i++ )
        {
            final DependencyDescriptor dependency = dependencies[ i ];
            if( dependency.isOptional() )
            {
                continue;
            }

            writer.write( "<dependency>" );
            final String key = dependency.getKey();
            final String type = dependency.getType();
            if( !key.equals( type ) )
            {
                writer.write( "<role>" );
                writer.write( key );
                writer.write( "</role>" );
            }
            writeService( writer, type, dependency );
            writer.write( "</dependency>" );
        }
        writer.write( "</dependencies>" );
    }

    /**
     * Write out xml representation of a service.
     *
     * @param writer the writer
     * @param type the type of the service
     * @param feature the feature describing service
     * @throws IOException if unable to write xml
     */
    private void writeService( final Writer writer,
                               final String type,
                               final FeatureDescriptor feature )
        throws IOException
    {
        writer.write( "<service name=\"" );
        writer.write( type );

        final String version = LegacyUtil.getVersionString( feature );
        if( null != version )
        {
            writer.write( "\" version=\"" );
            writer.write( version );
        }

        writer.write( "\"/>" );
    }

}
