/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "D-Haven" and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.avalon.fortress.tools;

import org.apache.avalon.fortress.util.dag.Vertex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Represents a component, and output the meta information.
 *
 * @author <a href="mailto:dev@avalon.apache.org">The Avalon Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2003/05/23 17:04:30 $
 */
final class Component
{
    /** The repository of components. */
    static final Set m_repository = new HashSet();

    private final String m_type;
    private final Properties m_attributes;
    private final List m_dependencies;
    private final Vertex m_vertex;

    /**
     * Initialize a service with the type name.
     *
     * @param type
     */
    public Component( final String type )
    {
        if ( type == null ) throw new NullPointerException( "type" );

        m_type = type;
        m_attributes = new Properties();
        m_dependencies = new ArrayList( 10 );
        m_vertex = new Vertex( this );

        m_repository.add( this );
    }

    /**
     * Get the type name.
     *
     * @return String
     */
    public String getType()
    {
        return m_type;
    }

    /**
     * Add a dependency to this type.
     *
     * @param service  The name of the service that depends on this.
     */
    public void addDependency( Service service )
    {
        if ( !m_dependencies.contains( service ) )
        {
            m_dependencies.add( service );
        }
    }

    public Vertex getVertex()
    {
        if ( m_vertex.getDependencies().size() != m_dependencies.size() )
        {
            Iterator it = m_dependencies.iterator();
            while ( it.hasNext() )
            {
                Service service = (Service) it.next();

                Iterator cit = service.getComponents();
                while ( cit.hasNext() )
                {
                    Component component = (Component)cit.next();
                    m_vertex.addDependency( component.getVertex() );
                }
            }
        }
        return m_vertex;
    }

    /**
     * Set the component attribute.
     *
     * @param name   The name of the attribute
     * @param value  The attribute value
     */
    public void setAttribute( final String name, final String value )
    {
        m_attributes.setProperty( name, value );
    }

    /**
     * Output the meta information.
     *
     * @param rootDir
     * @throws IOException
     */
    public void serialize( final File rootDir ) throws IOException
    {
        final String fileName = getType().replace( '.', '/' ).concat( ".meta" );
        final String depsName = getType().replace( '.', '/' ).concat( ".deps" );
        File output = new File( rootDir, fileName );
        FileOutputStream writer = null;

        try
        {
            writer = new FileOutputStream( output );
            m_attributes.store( writer, "Meta information for " + getType() );

            if ( m_dependencies.size() > 0 )
            {
                writer.close();
                output = new File( rootDir, depsName );
                writer = new FileOutputStream( output );

                Iterator it = m_dependencies.iterator();
                while ( it.hasNext() )
                {
                    Service service = (Service) it.next();
                    writer.write( service.getType().getBytes() );
                }
            }
        }
        finally
        {
            if ( null != writer )
            {
                writer.close();
            }
        }
    }
}
