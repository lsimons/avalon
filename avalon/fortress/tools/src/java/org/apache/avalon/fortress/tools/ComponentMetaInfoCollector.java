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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.avalon.fortress.impl.handler.FactoryComponentHandler;
import org.apache.avalon.fortress.impl.handler.PerThreadComponentHandler;
import org.apache.avalon.fortress.impl.handler.PoolableComponentHandler;
import org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler;
import org.apache.avalon.fortress.impl.role.ServiceRoleManager;
import org.apache.avalon.framework.thread.SingleThreaded;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import com.thoughtworks.qdox.ant.AbstractQdoxTask;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Type;

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
    private Set m_services = new HashSet();
    
    /**
     * The components to write the meta info for.
     */
    private Set m_components = new HashSet();
    
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
            writeInfoMetaData();
            ClassLoader loader = getClassLoader();
            
            PrintWriter writer = new PrintWriter( new FileWriter( m_serviceFile ) );
            int numServices = 0;
            
            Iterator it = m_services.iterator();
            while (it.hasNext())
            {
                writer.println(it.next());
                numServices++;
            }
            
            writer.close();
            
            if (numServices == 0)
            {
                m_serviceFile.delete();
            }

            log( "Collecting service information." );
            collectClassLoaderServices( loader );
            collectServices( loader );
        }
        catch( final Exception e )
        {
            throw new BuildException( e.toString(), e );
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
        
        m_serviceFile = new File(m_destDir, "services.list");
    }

    /**
     * Output the metadata files.
     *
     * @throws IOException If a problem writing output
     */
    private void writeInfoMetaData() throws IOException
    {
        final Iterator it = allClasses.iterator();
        while( it.hasNext() )
        {
            final JavaClass javaClass = (JavaClass)it.next();
            DocletTag tag = javaClass.getTagByName( "x-avalon.role" );
            if( null != tag )
            {
                m_services.add(javaClass.getFullyQualifiedName());
            }
            else
            {
                tag = javaClass.getTagByName( "avalon.service" );
                if( null != tag )
                {
                    String className = tag.getNamedParameter("type").trim();
                    if (className != null || className.length() > 0)
                    {
                        if ( className.indexOf('.') < 0)
                        {
                            int classLen = className.length();
                            Type[] types = javaClass.getImplements();
                            for (int t = 0; t < types.length; t++)
                            {
                                String type = types[t].getValue();
                                int typeLen = type.length();
                                if (type.substring(typeLen - classLen).equals(className))
                                {
                                    className = type;
                                }
                            }
                        }

                        m_services.add( className );
                    }
                }

                tag = javaClass.getTagByName( "avalon.component" );
                if( null != tag )
                {
                    m_components.add(javaClass.getFullyQualifiedName());

                    Properties meta = new Properties();
                    prepareMetaInfo(meta, javaClass);
                    
                    File metaFile = getOutputFileForClass(javaClass.getFullyQualifiedName());
                    FileOutputStream fos = new FileOutputStream(metaFile);
                    meta.store(fos, "Meta-Information for " + javaClass.getFullyQualifiedName());
                }
            }
        }
    }

    /**
     * @param meta
     * @param javaClass
     */
    private void prepareMetaInfo(Properties meta, JavaClass javaClass)
    {
        DocletTag avalonLifecycle = javaClass.getTagByName("x-avalon.lifecycle");
        DocletTag fortressHandler = javaClass.getTagByName("fortress.handler");
        String lifecycle = null;
        String handler = null;
        
        if ( avalonLifecycle == null && fortressHandler == null )
        {
            Type[] interfaces = javaClass.getImplements();
            for (int i = 0; i < interfaces.length && handler != null; i++)
            {
                if(interfaces[i].getClass().equals(ThreadSafe.class))
                {
                    handler = ThreadSafeComponentHandler.class.getName();
                }
                else if (interfaces[i].getClass().getName().equals("org.apache.avalon.excalibur.pool.Poolable") ||
                         interfaces[i].getClass().getName().equals("org.apache.avalon.excalibur.pool.Recyclable"))
                {
                    handler = PoolableComponentHandler.class.getName();
                }
                else if (interfaces[i].getClass().equals(SingleThreaded.class))
                {
                    handler = FactoryComponentHandler.class.getName();
                }
            }
        }
        
        if (null != avalonLifecycle)
        {
            lifecycle = avalonLifecycle.getValue();
        }
        else if (handler != null)
        {
            handler = (null == fortressHandler) ? PerThreadComponentHandler.class.getName() : fortressHandler.getValue();
        }
        
        if ( null != lifecycle ) meta.setProperty("x-avalon.lifecycle", lifecycle);
        if ( null != handler ) meta.setProperty("fortress.handler", handler);
        
        DocletTag avalonConfigName = javaClass.getTagByName("x-avalon.info");
        if ( null == avalonConfigName ) avalonConfigName = javaClass.getTagByName("fortress.name");

        meta.setProperty("x-avalon.name", (avalonConfigName == null) ? ServiceRoleManager.createShortName(javaClass.getName()) : avalonConfigName.getNamedParameter("name") );
    }

    /**
     * Determine the file for specified {@link ComponentInfo}.
     *
     * @param classname the fully qualified name of file to generate
     * @return the file for info
     * @throws IOException if unable to determine base file
     */
    private File getOutputFileForClass( final String classname )
        throws IOException
    {
        String filename =
            classname.replace( '.', File.separatorChar );
        
        filename += ".meta";
        return new File( m_destDir, filename ).getCanonicalFile();
    }

    /**
     * Return the classloader used to determine the services info.
     * 
     * @return URLClassLoader
     */
    private ClassLoader getClassLoader() throws MalformedURLException {
        final URL[] urls = new URL[] {m_destDir.toURL()};
        return new URLClassLoader(urls, getClass().getClassLoader());
    }
    
    /**
     * Collect all the services and write out the implementations.
     */
    private void collectServices( final ClassLoader loader ) throws MalformedURLException
    {
        final File baseDir = new File(m_destDir, "META-INF/services/");
        final Iterator services = m_services.iterator();
        baseDir.mkdirs();
        
        while(services.hasNext())
        {
            String service = (String)services.next();
            log("Processing service " + service, Project.MSG_VERBOSE);
            try
            {
                Class role = loader.loadClass(service);
                    
                if ( role.isInterface() )
                {
                    File serviceFile = new File(baseDir, service);
                    collectComponents(serviceFile, role);
                }
                else
                {
                    log(service + " is not an interface", Project.MSG_WARN);
                }
            }
            catch(Exception e)
            {
                log(service + " could not be found", Project.MSG_WARN);
            }
        }
    }

    /**
	 * 
	 */
	private void collectClassLoaderServices(ClassLoader loader)
        throws IOException
    {
		Enumeration enum = loader.getResources("services.list");
        while (enum.hasMoreElements())
        {
            URL entry = (URL)enum.nextElement();
            BufferedReader reader = new BufferedReader(
                 new InputStreamReader( entry.openStream() ) );
            String line;
                 
            while ( (line = reader.readLine()) != null )
            {
                if (line.trim().length() > 0)
                {
                    m_services.add(line);
                }
            }
        }
	}

	/**
     * Output all the components that implement the service.
     * 
     * @param serviceFile
     * @param role
     * @throws IOException
     */
    private void collectComponents(final File serviceFile, Class role)
        throws IOException
    {
        final ClassLoader loader = role.getClassLoader();
        int numComponents = 0;
        log("Opening file: " + serviceFile.getAbsolutePath(), Project.MSG_DEBUG);
        PrintWriter writer = new PrintWriter( new FileWriter( serviceFile ) );
            
        final Iterator components = m_components.iterator();
        while( components.hasNext() )
        {
            String comp = (String)components.next();
            
            try
            {
                Class component = loader.loadClass(comp);
                if ( role.isAssignableFrom(component) )
                {
                    log(comp + " is a(n) " + role.getName(), Project.MSG_DEBUG);
                    writer.println(comp);
                    numComponents++;
                }
                else
                {
                    log(comp + " is not a(n) " + role.getName(), Project.MSG_DEBUG);
                }
            }
            catch (Exception e)
            {
                log(comp + " could not be found", Project.MSG_WARN);
            }
        }
        
        writer.close();
        log("Closing file: " + serviceFile.getAbsolutePath(), Project.MSG_DEBUG);
        log("Had " + numComponents + " components", Project.MSG_DEBUG);
        
        if ( numComponents == 0 )
        {
            log("No components for role " + role.getName() + ", deleting service entry.", Project.MSG_VERBOSE);
            serviceFile.delete();
        }
    }
}
