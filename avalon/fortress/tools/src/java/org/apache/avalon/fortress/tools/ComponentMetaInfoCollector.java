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
 * 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
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

import com.thoughtworks.qdox.ant.AbstractQdoxTask;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import org.apache.avalon.fortress.util.dag.CyclicDependencyException;
import org.apache.avalon.fortress.util.dag.DirectedAcyclicGraphVerifier;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * ANT task to collect all the meta information for the components.
 *
 * @author <a href="mailto:dev@avalon.apache.org">The Avalon Team</a>
 * @version CVS $Revision: 1.25 $ $Date: 2003/12/01 18:01:30 $
 */
public final class ComponentMetaInfoCollector extends AbstractQdoxTask
{
    /**
     * The services to write the meta info for.
     */
    private final Map m_services = new HashMap();

    /**
     * The destination directory for metadata files.
     */
    private File m_destDir;

    /**
     * The service list destination.
     */
    private File m_serviceFile;

    private static final String TAG_COMPONENT = "avalon.component";

    /**
     * Set the destination directory for the meta information.
     *
     * @param destDir  The destination directory
     */
    public void setDestDir( final File destDir )
    {
        m_destDir = destDir;
    }

    /**
     * Execute generator task.
     *
     * @throws BuildException if there was a problem collecting the info
     */
    public void execute()
            throws BuildException
    {
        validate();

        log( "Writing Info descriptors as property files (.meta)." );

        super.execute();

        try
        {
            collectInfoMetaData();
            writeComponents();

            writeServiceList( m_services.values().iterator() );

            log( "Collecting service information." );
            writeServices();
        }
        catch ( final Exception e )
        {
            throw new BuildException( e.toString(), e );
        }
        finally
        {
            Component.m_repository.clear();
        }
    }

    /**
     * Write the component meta information to the associated files.
     *
     * @throws IOException if there is a problem.
     */
    private void writeComponents() throws IOException, CyclicDependencyException
    {
        final List dagVerifyList = new ArrayList( Component.m_repository.size() );
        final Iterator it = Component.m_repository.iterator();
        while ( it.hasNext() )
        {
            final Component comp = (Component) it.next();
            comp.serialize( m_destDir );
            dagVerifyList.add( comp.getVertex() );
        }

        DirectedAcyclicGraphVerifier.verify( dagVerifyList );
    }

    /**
     * Write the service list to the "/service.list" file.
     *
     * @param it  The iterator for the services
     * @throws IOException if there is a problem writing the file
     */
    public void writeServiceList( final Iterator it ) throws IOException
    {
        final PrintWriter writer = new PrintWriter( new FileWriter( m_serviceFile ) );
        int numServices = 0;

        while ( it.hasNext() )
        {
            writer.println( ( (Service) it.next() ).getType() );
            numServices++;
        }

        writer.close();

        if ( numServices == 0 )
        {
            m_serviceFile.delete();
        }
    }

    /**
     * Validate that the parameters are valid.
     */
    private void validate()
    {
        if ( null == m_destDir )
        {
            final String message =
                    "DestDir (" + m_destDir + ") not specified";
            throw new BuildException( message );
        }

        if ( !m_destDir.isDirectory() )
        {
            final String message =
                    "DestDir (" + m_destDir + ") is not a directory.";
            throw new BuildException( message );
        }

        if ( !m_destDir.exists() && !m_destDir.mkdirs() )
        {
            final String message =
                    "DestDir (" + m_destDir + ") could not be created.";
            throw new BuildException( message );
        }

        m_serviceFile = new File( m_destDir, "services.list" );
    }

    /**
     * Output the metadata files.
     */
    private void collectInfoMetaData()
    {
        final Iterator it = allClasses.iterator();
        while ( it.hasNext() )
        {
            final JavaClass javaClass = (JavaClass) it.next();
            final DocletTag tag = javaClass.getTagByName( TAG_COMPONENT );

            if ( null != tag )
            {
                final Component comp = new Component( javaClass );

                Iterator sit = comp.getServiceNames();
                while ( sit.hasNext() )
                {
                    String servName = (String) sit.next();
                    Service service = getService( servName );
                    service.addComponent( comp );
                }

                Iterator dit = comp.getDependencyNames();
                while ( dit.hasNext() )
                {
                    String depName = (String) dit.next();
                    Service service = getService( depName );
                    comp.addDependency( service );
                }
            }
        }
    }

    /**
     * Get the unique Service object for the specified type.
     *
     * @param type  The service type name
     * @return the Service object
     */
    protected Service getService( final String type )
    {
        Service service = (Service) m_services.get( type );

        if ( null == service )
        {
            service = new Service( type );
            m_services.put( service.getType(), service );
        }

        return service;
    }

    /**
     * Collect all the services and write out the implementations.
     */
    private void writeServices()
    {
        final File baseDir = new File( m_destDir, "META-INF/services/" );
        baseDir.mkdirs();

        final Iterator services = m_services.values().iterator();

        while ( services.hasNext() )
        {
            final Service service = (Service) services.next();
            log( "Processing service " + service.getType(), Project.MSG_VERBOSE );
            try
            {
                service.serialize( m_destDir );
            }
            catch ( Exception e )
            {
                log( "Could not save information for service " + service.getType(), Project.MSG_WARN );
            }
        }
    }
}
