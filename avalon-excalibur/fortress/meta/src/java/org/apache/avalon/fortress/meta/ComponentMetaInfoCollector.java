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
import com.thoughtworks.qdox.model.JavaMethod;
import org.apache.avalon.fortress.MetaInfoEntry;
import org.apache.avalon.fortress.impl.handler.FactoryComponentHandler;
import org.apache.avalon.fortress.impl.handler.PerThreadComponentHandler;
import org.apache.avalon.fortress.impl.handler.PoolableComponentHandler;
import org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler;
import org.apache.avalon.fortress.util.dag.*;
import org.apache.avalon.framework.thread.SingleThreaded;
import org.apache.avalon.framework.thread.ThreadSafe;
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
 * @version CVS $Revision: 1.20 $ $Date: 2003/05/23 16:54:57 $
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
    private static final String POOLABLE = "org.apache.avalon.excalibur.pool.Poolable";
    private static final String RECYCLABLE = "org.apache.avalon.excalibur.pool.Recyclable";
    private static final String SERVICE_MANAGER = "org.apache.avalon.framework.service.ServiceManager";
    private static final String TAG_DEPENDENCY = "avalon.dependency";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_NAME = "name";
    private static final String TAG_LIFESTYLE = "x-avalon.lifestyle";
    private static final String TAG_HANDLER = "fortress.handler";
    private static final String TAG_INFO = "x-avalon.info";
    private static final String TAG_NAME = "fortress.name";
    private static final String TAG_COMPONENT = "avalon.component";
    private static final String TAG_SERVICE = "avalon.service";
    private static final String META_NAME = "x-avalon.name";
    private static final String METH_SERVICE = "service";

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
        final List dagVerifyList = new ArrayList(Component.m_repository.size());
        final Iterator it = Component.m_repository.iterator();
        while ( it.hasNext() )
        {
            final Component comp = (Component) it.next();
            comp.serialize( m_destDir );
            dagVerifyList.add(comp.getVertex());
        }

        DirectedAcyclicGraphVerifier.verify(dagVerifyList);
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
                final Component comp = new Component( javaClass.getFullyQualifiedName() );

                final DocletTag[] tags = javaClass.getTagsByName( TAG_SERVICE );
                for ( int t = 0; t < tags.length; t++ )
                {
                    final String serviceName = resolveClassName( javaClass, tags[t].getNamedParameter( ATTR_TYPE ) );
                    final Service service = getService( serviceName );
                    service.addComponent( comp );
                }

                final DocletTag avalonLifecycle = javaClass.getTagByName( TAG_LIFESTYLE );
                final DocletTag fortressHandler = javaClass.getTagByName( TAG_HANDLER );
                String lifecycle = null;
                String handler = null;

                if ( avalonLifecycle == null && fortressHandler == null )
                {
                    final Type[] interfaces = javaClass.getImplements();
                    for ( int i = 0; i < interfaces.length && handler != null; i++ )
                    {
                        if ( interfaces[i].getClass().equals( ThreadSafe.class ) )
                        {
                            handler = ThreadSafeComponentHandler.class.getName();
                        }
                        else if ( interfaces[i].getClass().getName().equals( POOLABLE ) ||
                            interfaces[i].getClass().getName().equals( RECYCLABLE ) )
                        {
                            handler = PoolableComponentHandler.class.getName();
                        }
                        else if ( interfaces[i].getClass().equals( SingleThreaded.class ) )
                        {
                            handler = FactoryComponentHandler.class.getName();
                        }
                    }
                }

                if ( null != avalonLifecycle )
                {
                    lifecycle = stripQuotes(avalonLifecycle.getNamedParameter( ATTR_TYPE ));
                }
                else if ( handler != null )
                {
                    handler = ( null == fortressHandler ) ? PerThreadComponentHandler.class.getName() : stripQuotes(fortressHandler.getNamedParameter( ATTR_TYPE ));
                }

                if ( null != lifecycle ) comp.setAttribute( TAG_LIFESTYLE, lifecycle );
                if ( null != handler ) comp.setAttribute( TAG_HANDLER, handler );

                DocletTag avalonConfigName = javaClass.getTagByName( TAG_INFO );
                if ( null == avalonConfigName ) avalonConfigName = javaClass.getTagByName( TAG_NAME );

                comp.setAttribute( META_NAME, ( avalonConfigName == null ) ? MetaInfoEntry.createShortName( javaClass.getName() ) : avalonConfigName.getNamedParameter( ATTR_NAME ) );

                JavaMethod[] methods = javaClass.getMethods();
                for (int i = 0; i < methods.length; i++)
                {
                    if (methods[i].getName().equals(METH_SERVICE))
                    {
                        if (methods[i].getParameters().length == 1 && methods[i].getParameters()[0].getType().getValue().equals(SERVICE_MANAGER))
                        {
                            DocletTag[] dependencies = methods[i].getTagsByName(TAG_DEPENDENCY);
                            for(int d = 0; d < dependencies.length; d++)
                            {
                                String type = stripQuotes(dependencies[d].getNamedParameter(ATTR_TYPE));
                                //String optional = dependencies[d].getNamedParameter("optional");

                                Service service = getService(type);
                                comp.addDependency(service);
                            }
                        }
                    }
                }
            }
        }
    }

    private String stripQuotes(final String value)
    {
        if ( null == value ) return null;
        if ( value.length() < 2 ) return value;

        String retVal = value.trim();

        if ( retVal.startsWith("\"") && retVal.endsWith("\"") )
        {
            retVal = retVal.substring(1, retVal.length() - 1);
        }

        return retVal;
    }

    /**
     * Resolve the classname from the "@avalon.service" javadoc tags.
     *
     * @param javaClass    The supplied JavaClass file
     * @param serviceName  The service type name
     * @return  The fully qualified class name
     */
    private String resolveClassName( final JavaClass javaClass, final String serviceName )
    {
        if ( null == javaClass ) throw new NullPointerException( "javaClass" );
        if ( null == serviceName ) throw new BuildException( "(" + javaClass.getFullyQualifiedName() + ") You must specify the service name with the \"type\" parameter" );

        String className = stripQuotes(serviceName);
        if ( className != null || className.length() > 0 )
        {
            if ( className.indexOf( '.' ) < 0 )
            {
                final Type[] types = javaClass.getImplements();
                for ( int t = 0; t < types.length; t++ )
                {
                    final String type = types[t].getValue();
                    final int index = type.lastIndexOf( '.' ) + 1;

                    if ( type.substring( index ).equals( className ) )
                    {
                        className = type;
                    }
                }
            }
        }

        return className;
    }

    /**
     * Get the unique Service object for the specified type.
     *
     * @param type  The service type name
     * @return the Service object
     */
    private Service getService( final String type )
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
