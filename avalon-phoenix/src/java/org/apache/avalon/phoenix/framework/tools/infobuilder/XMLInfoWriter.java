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

import java.io.OutputStream;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;
import org.apache.avalon.phoenix.framework.info.ComponentDescriptor;
import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.phoenix.framework.info.LoggerDescriptor;
import org.apache.avalon.phoenix.framework.info.ContextDescriptor;
import org.apache.avalon.phoenix.framework.info.EntryDescriptor;
import org.apache.avalon.phoenix.framework.info.ServiceDescriptor;
import org.apache.avalon.phoenix.framework.info.DependencyDescriptor;
import org.apache.avalon.phoenix.framework.info.SchemaDescriptor;

/**
 * Write {@link ComponentInfo} objects to a stream as xml documents.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/22 12:07:13 $
 */
public class XMLInfoWriter
    implements InfoWriter
{
    private static final String CONTEXT_CLASS = "org.apache.avalon.framework.context.Context";

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
        writeDoctype( writer, "component-info" );
        writer.write( "<component-info>" );
        writeComponent( writer, info.getDescriptor() );
        writeLoggers( writer, info.getLoggers() );
        writeContext( writer, info.getContext() );
        writeServices( writer, info.getServices() );
        writeDependencies( writer, info.getDependencies() );
        writeSchema( writer, "configuration", info.getConfigurationSchema() );
        writeSchema( writer, "parameters", info.getParametersSchema() );
        writer.write( "</component-info>" );
        writer.flush();
    }

    /**
     * Write out Schema Descriptor.
     *
     * @param writer the writer
     * @param schema the descriptor
     * @throws IOException if unable to write xml
     */
    private void writeSchema( final Writer writer,
                              final String category,
                              final SchemaDescriptor schema )
        throws IOException
    {
        if( null == schema )
        {
            return;
        }

        writer.write( "<" + category + "-schema" );
        final String location = schema.getLocation();
        if( !"".equals( location ) )
        {
            writer.write( " location=\"" );
            writer.write( location );
            writer.write( "\"" );
        }
        final String type = schema.getType();
        if( !"".equals( type ) )
        {
            writer.write( " type=\"" );
            writer.write( type );
            writer.write( "\"" );
        }
        writer.write( "/>" );
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
     * @param root the root name of document
     * @throws IOException if unable to write xml
     */
    private void writeDoctype( final Writer writer,
                               final String root )
        throws IOException
    {
        final String doctype =
            "<!DOCTYPE " + root +
            " PUBLIC \"-//AVALON/Component Info DTD Version 1.0//EN\" " +
            "\"http://jakarta.apache.org/avalon/dtds/info/componentinfo_1_0.dtd\" >";
        writer.write( doctype );
    }

    /**
     * Write out xml representation of a component.
     *
     * @param writer the writer
     * @param component the component
     * @throws IOException if unable to write xml
     */
    private void writeComponent( final Writer writer,
                                 final ComponentDescriptor component )
        throws IOException
    {
        writer.write( "<component type=\"" );
        writer.write( component.getImplementationKey() );
        final Attribute[] attributes = component.getAttributes();
        if( 0 == attributes.length )
        {
            writer.write( "\"/>" );
        }
        else
        {
            writer.write( "\">" );
            writeAttributes( writer, attributes );
            writer.write( "</component>" );
        }
    }

    /**
     * Write out xml representation of a set of loggers.
     *
     * @param writer the writer
     * @param loggers the loggers
     * @throws IOException if unable to write xml
     */
    private void writeLoggers( final Writer writer,
                               final LoggerDescriptor[] loggers )
        throws IOException
    {
        if( 0 == loggers.length )
        {
            return;
        }
        writer.write( "<loggers>" );
        for( int i = 0; i < loggers.length; i++ )
        {
            writeLogger( writer, loggers[ i ] );
        }

        writer.write( "</loggers>" );
    }

    /**
     * Write out xml representation of a logger.
     *
     * @param writer the writer
     * @param logger the logger
     * @throws IOException if unable to write xml
     */
    private void writeLogger( final Writer writer,
                              final LoggerDescriptor logger )
        throws IOException
    {
        writer.write( "<logger name=\"" );
        writer.write( logger.getName() );
        final Attribute[] attributes = logger.getAttributes();
        if( 0 == attributes.length )
        {
            writer.write( "\"/>" );
        }
        else
        {
            writer.write( "\">" );
            writeAttributes( writer, attributes );
            writer.write( "</logger>" );
        }
    }

    /**
     * Write out xml representation of a context.
     *
     * @param writer the writer
     * @param context the context
     * @throws IOException if unable to write xml
     */
    private void writeContext( final Writer writer,
                               final ContextDescriptor context )
        throws IOException
    {
        final String type = context.getType();
        final Attribute[] attributes = context.getAttributes();
        final EntryDescriptor[] entrys = context.getEntrys();

        if( CONTEXT_CLASS.equals( type ) &&
            0 == attributes.length &&
            0 == entrys.length )
        {
            return;
        }

        writer.write( "<context" );
        if( !CONTEXT_CLASS.equals( type ) )
        {
            writer.write( " type=\"" );
            writer.write( type );
            writer.write( "\"" );
        }

        if( 0 == attributes.length && 0 == entrys.length )
        {
            writer.write( "/>" );
        }
        else
        {
            writer.write( ">" );
            for( int i = 0; i < entrys.length; i++ )
            {
                writeEntry( writer, entrys[ i ] );
            }
            writeAttributes( writer, attributes );
            writer.write( "</context>" );
        }
    }

    /**
     * Write out xml representation of an entry.
     *
     * @param writer the writer
     * @param entry the entry
     * @throws IOException if unable to write xml
     */
    private void writeEntry( final Writer writer,
                             final EntryDescriptor entry )
        throws IOException
    {
        writer.write( "<entry key=\"" );
        writer.write( entry.getKey() );
        writer.write( "\" type=\"" );
        writer.write( entry.getType() );

        if( entry.isOptional() )
        {
            writer.write( "\" optional=\"true" );
        }

        final Attribute[] attributes = entry.getAttributes();
        if( 0 == attributes.length )
        {
            writer.write( "\"/>" );
        }
        else
        {
            writer.write( "\">" );
            writeAttributes( writer, attributes );
            writer.write( "</entry>" );
        }
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
            writer.write( "<service type=\"" );
            writer.write( service.getType() );
            final Attribute[] attributes = service.getAttributes();
            if( 0 == attributes.length )
            {
                writer.write( "\"/>" );
            }
            else
            {
                writer.write( "\">" );
                writeAttributes( writer, attributes );
                writer.write( "</service>" );
            }
        }
        writer.write( "</services>" );
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
            writer.write( "<dependency " );

            if( !dependency.getKey().equals( dependency.getType() ) )
            {
                writer.write( "key=\"" );
                writer.write( dependency.getKey() );
                writer.write( "\" " );
            }

            writer.write( "type=\"" );
            writer.write( dependency.getType() );

            if( dependency.isOptional() )
            {
                writer.write( "\" optional=\"true" );
            }

            final Attribute[] attributes = dependency.getAttributes();
            if( 0 == attributes.length )
            {
                writer.write( "\"/>" );
            }
            else
            {
                writer.write( "\">" );
                writeAttributes( writer, attributes );
                writer.write( "</dependency>" );
            }
        }
        writer.write( "</dependencies>" );
    }

    /**
     * Write out xml representation of a set of attributes.
     *
     * @param writer the writer
     * @param attributes the attributes
     * @throws IOException if unable to write xml
     */
    private void writeAttributes( final Writer writer,
                                  final Attribute[] attributes )
        throws IOException
    {
        for( int i = 0; i < attributes.length; i++ )
        {
            writeAttribute( writer, attributes[ i ] );
        }
    }

    /**
     * Write out xml representation of an attribute.
     *
     * @param writer the writer
     * @param attribute the attribute
     * @throws IOException if unable to write xml
     */
    private void writeAttribute( final Writer writer,
                                 final Attribute attribute )
        throws IOException
    {
        writer.write( "<attribute name=\"" );
        writer.write( attribute.getName() );
        writer.write( "\">" );

        final String[] names = attribute.getParameterNames();
        for( int i = 0; i < names.length; i++ )
        {
            final String name = names[ i ];
            final String value = attribute.getParameter( name );
            writer.write( "<param name=\"" );
            writer.write( name );
            writer.write( "\" value=\"" );
            writer.write( value );
            writer.write( "\"/>" );
        }

        writer.write( "</attribute>" );
    }
}
