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

import com.thoughtworks.qdox.ant.AbstractQdoxTask;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Type;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.fortress.impl.handler.FactoryComponentHandler;
import org.apache.avalon.fortress.impl.handler.PerThreadComponentHandler;
import org.apache.avalon.fortress.impl.handler.PoolableComponentHandler;
import org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler;
import org.apache.avalon.fortress.impl.role.ServiceRoleManager;
import org.apache.avalon.framework.thread.SingleThreaded;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * @author bloritsch
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ComponentMetaInfoCollector extends AbstractQdoxTask
{
    /**
     * The services to write the meta info for.
     */
    private Map m_services = new HashMap();

    /**
     * The destination directory for metadata files.
     */
    private File m_destDir;

    /**
     * The service list destination.
     */
    private File m_serviceFile;

    public void setDestDir( final File destDir )
    {
        m_destDir = destDir;
    }

    /**
     * Execute generator task.
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
        catch( final Exception e )
        {
            throw new BuildException( e.toString(), e );
        }
    }

    /**
     *
     */
    private void writeComponents() throws IOException
    {
        Iterator it = Component.m_repository.iterator();
        while( it.hasNext() )
        {
            Component comp = (Component)it.next();
            comp.serialize( m_destDir );
        }
    }

    public void writeServiceList( Iterator it ) throws IOException
    {
        PrintWriter writer = new PrintWriter( new FileWriter( m_serviceFile, true ) );
        int numServices = 0;

        while( it.hasNext() )
        {
            writer.println( ( (Service)it.next() ).getType() );
            numServices++;
        }

        writer.close();

        if( numServices == 0 )
        {
            m_serviceFile.delete();
        }
    }

    /**
     * Validate that the parameters are valid.
     */
    private void validate()
    {
        if( null == m_destDir )
        {
            final String message =
                "DestDir (" + m_destDir + ") not specified";
            throw new BuildException( message );
        }

        if( !m_destDir.isDirectory() )
        {
            final String message =
                "DestDir (" + m_destDir + ") is not a directory.";
            throw new BuildException( message );
        }

        if( !m_destDir.exists() && !m_destDir.mkdirs() )
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
    private void collectInfoMetaData() throws ClassNotFoundException
    {
        final Iterator it = allClasses.iterator();
        while( it.hasNext() )
        {
            final JavaClass javaClass = (JavaClass)it.next();
            DocletTag tag = javaClass.getTagByName( "avalon.component" );

            if( null != tag )
            {
                Component comp = new Component( javaClass.getFullyQualifiedName() );

                DocletTag[] tags = javaClass.getTagsByName( "avalon.service" );
                for( int t = 0; t < tags.length; t++ )
                {
                    String serviceName = resolveClassName( javaClass, tags[ t ].getNamedParameter( "type" ) );
                    Service service = getService( serviceName );
                    service.addComponent( comp );
                }

                DocletTag avalonLifecycle = javaClass.getTagByName( "x-avalon.lifestyle" );
                DocletTag fortressHandler = javaClass.getTagByName( "fortress.handler" );
                String lifecycle = null;
                String handler = null;

                if( avalonLifecycle == null && fortressHandler == null )
                {
                    Type[] interfaces = javaClass.getImplements();
                    for( int i = 0; i < interfaces.length && handler != null; i++ )
                    {
                        if( interfaces[ i ].getClass().equals( ThreadSafe.class ) )
                        {
                            handler = ThreadSafeComponentHandler.class.getName();
                        }
                        else if( interfaces[ i ].getClass().getName().equals( "org.apache.avalon.excalibur.pool.Poolable" ) ||
                            interfaces[ i ].getClass().getName().equals( "org.apache.avalon.excalibur.pool.Recyclable" ) )
                        {
                            handler = PoolableComponentHandler.class.getName();
                        }
                        else if( interfaces[ i ].getClass().equals( SingleThreaded.class ) )
                        {
                            handler = FactoryComponentHandler.class.getName();
                        }
                    }
                }

                if( null != avalonLifecycle )
                {
                    lifecycle = avalonLifecycle.getNamedParameter( "type" );
                }
                else if( handler != null )
                {
                    handler = ( null == fortressHandler ) ? PerThreadComponentHandler.class.getName() : fortressHandler.getNamedParameter( "type" );
                }

                if( null != lifecycle ) comp.setAttribute( "x-avalon.lifestyle", lifecycle );
                if( null != handler ) comp.setAttribute( "fortress.handler", handler );

                DocletTag avalonConfigName = javaClass.getTagByName( "x-avalon.info" );
                if( null == avalonConfigName ) avalonConfigName = javaClass.getTagByName( "fortress.name" );

                comp.setAttribute( "x-avalon.name", ( avalonConfigName == null ) ? ServiceRoleManager.createShortName( javaClass.getName() ) : avalonConfigName.getNamedParameter( "name" ) );
            }
        }
    }

    private String resolveClassName( final JavaClass javaClass, final String serviceName )
    {
        if( null == javaClass ) throw new NullPointerException( "javaClass" );
        if( null == serviceName ) throw new BuildException( "(" + javaClass.getFullyQualifiedName() + ") You must specify the service name with the \"type\" parameter" );

        String className = serviceName.trim();
        if( className != null || className.length() > 0 )
        {
            if( className.indexOf( '.' ) < 0 )
            {
                int classLen = className.length();
                Type[] types = javaClass.getImplements();
                for( int t = 0; t < types.length; t++ )
                {
                    String type = types[ t ].getValue();
                    int index = type.lastIndexOf('.') + 1;

                    if( type.substring( index ).equals( className ) )
                    {
                        className = type;
                    }
                }
            }
        }

        return className;
    }

    private Service getService( final String type ) throws ClassNotFoundException
    {
        Service service = (Service)m_services.get( type );

        if( null == service )
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

        while( services.hasNext() )
        {
            Service service = (Service)services.next();
            log( "Processing service " + service.getType(), Project.MSG_VERBOSE );
            try
            {
                service.serialize( m_destDir );
            }
            catch( Exception e )
            {
                log( "Could not save information for service " + service.getType(), Project.MSG_WARN );
            }
        }
    }
}
