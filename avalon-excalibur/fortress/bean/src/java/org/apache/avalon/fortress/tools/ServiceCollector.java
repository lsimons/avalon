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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * Look at all the classes in a JAR file and determine if they
 * implement a specified service.  When we are done, serialize the
 * list to the appropriate services file.
 * 
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 */
public class ServiceCollector extends Task
{
    private File m_serviceList;
    private Map m_serviceMap;
    private final Set m_services = new HashSet();
    private File m_inJar = null;
    private File m_outJar = null;
    
    /**
     * Add elements of the form:
     * 
     * <service name="org.apache.package.MyInterface/>
     * 
     * @param service  The ServiceElement to add
     */
    public void addConfiguredService(ServiceElement service)
    {
        String serviceName = service.getName();
        
        if (null == serviceName || "".equals(serviceName.trim()))
        {
            String message = "Must supply a Service Name";
            throw new BuildException(message, getLocation());
        }
        
        m_services.add(serviceName);
    }
    
    /**
     * Support the attribute "inputjar".
     * 
     * @param input  The File representing a JAR file.
     */
    public void setInputJar(File input)
    {
        if ( null == input || input.isDirectory() )
        {
            String message = "Jar attribute is not a file";
            throw new BuildException(message, getLocation());
        }
        
        m_inJar = input;
    }
    
    /**
     * Support the attribute "outputjar".
     * 
     * @param input  The File representing a JAR file.
     */
    public void setOutputJar(File output)
    {
        if ( null == output || output.isDirectory() )
        {
            String message = "Jar attribute is not a file";
            throw new BuildException(message, getLocation());
        }
        
        m_outJar = output;
    }
    
    public void setServiceList(File serviceList)
    {
        if ( null == serviceList || serviceList.isDirectory() )
        {
            String message = "ServiceList attribute is not a file";
            throw new BuildException(message, getLocation());
        }
        
        m_serviceList = serviceList;
    }
    
    /**
     * Perform the actual checks to extract services.
     */
    public void execute()
    {
        if ( m_inJar == null )
        {
            throw new BuildException("Input Jar Attribute must be set", getLocation());
        }
        
        if ( m_outJar == null )
        {
            throw new BuildException("Output Jar Attribute must be set", getLocation());
        }
        
        if ( m_inJar.getAbsolutePath().equals(m_outJar.getAbsolutePath()))
        {
            throw new BuildException("The two jars cannot be the same.", getLocation());
        }
        
        log("Collecting Services", Project.MSG_INFO);
        
        try
        {
            URLClassLoader loader = new URLClassLoader(new URL[]{m_inJar.toURL()}, getClass().getClassLoader());
            FileOutputStream out = new FileOutputStream(m_outJar);
            
            try
            {
                Enumeration enum = loader.getResources("services.list");
                while (enum.hasMoreElements())
                {
                    URL resource = (URL)enum.nextElement();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream()));
                    readServiceList(reader);
                    reader.close();
                }
            }
            catch (IOException ioe)
            {
                throw new BuildException(ioe);
            }
        
            if ( m_serviceList != null )
            {
                BufferedReader reader;
                try
                {
                    reader = new BufferedReader(new FileReader(m_serviceList));
                    readServiceList(reader);
                    reader.close();
                }
                catch (FileNotFoundException fnfe)
                {
                    throw new BuildException(fnfe);
                }
                catch (IOException ioe)
                {
                    throw new BuildException(ioe);
                }
            }
        
            if ( m_services.isEmpty() )
            {
                log("No services selected.", Project.MSG_WARN);
            }

            JarFile jarFile = new JarFile(m_inJar);
            JarOutputStream outJar = new JarOutputStream(out, jarFile.getManifest());
            
            m_serviceMap = new HashMap();
            Iterator it = m_services.iterator();
            while(it.hasNext())
            {
                String name = it.next().toString();

                try
                {
                    Class klass = loader.loadClass(name);
                    
                    if ( klass.isInterface() )
                    {
                        m_serviceMap.put(klass, new LinkedList());
                    }
                }
                catch(Exception e)
                {
                    log(name + " is not an interface", Project.MSG_WARN);
                }
            }
            
            Enumeration entries = jarFile.entries();
            while (entries.hasMoreElements())
            {
                JarEntry entry = (JarEntry)entries.nextElement();
                String name = entry.getName();
                
                if (name.endsWith(".class"))
                {
                    try
                    {
                        String className = name.substring(0, name.length() - ".class".length()).replace('/', '.');
                        Class klass = loader.loadClass(className);
                        
                        checkClass(klass);
                    }
                    catch (Exception e)
                    {
                        log(e.getMessage(), Project.MSG_VERBOSE);
                    }
                }
                
                if ( ! "META-INF/MANIFEST.MF".equals(name) )
                {
                    if ("services.list".equals(name))
                    {
                        entry = new JarEntry("services.list");
                        outJar.putNextEntry(entry);
                        
                        Iterator services = m_services.iterator();
                        while (services.hasNext())
                        {
                            String service = (String)services.next();
                            service += "\n";
                            outJar.write(service.getBytes());
                        }
                    }
                    else
                    {
                        outJar.putNextEntry(entry);
                        
                        InputStream entryStream = jarFile.getInputStream(entry);
                        int length = -1;
                        byte[] buffer = new byte[1024];
                    
                        // Read the entry and write it to the temp jar.
                    
                        while ((length = entryStream.read(buffer)) != -1)
                        {
                             outJar.write(buffer, 0, length);
                        }
                    }
                }
            }
            
            it = m_serviceMap.keySet().iterator();
            while (it.hasNext())
            {
                Class iface = (Class)it.next();
                String name = iface.getName();
                JarEntry entry = new JarEntry("META-INF/services/" + name);
                outJar.putNextEntry(entry);
                
                List impls = (List)m_serviceMap.get(iface);
                Iterator imit = impls.iterator();
                while(imit.hasNext())
                {
                    String implementation = ((Class)imit.next()).getName();
                    implementation += "\n";
                    outJar.write(implementation.getBytes());
                }
            }

            outJar.close();
            jarFile.close();
            m_inJar.deleteOnExit();
            
            log("Deleting the input jar", Project.MSG_INFO);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BuildException("Could not process JAR: " + m_inJar.getName());
        }
    }

    public void readServiceList(BufferedReader reader) throws IOException
    {
        String line = reader.readLine();
        while ( line != null )
        {
            m_services.add(line);
            line = reader.readLine();
        }
    }

    /**
     * The core of the checkClass() logic.  It validates the
     * class passed in against the set of interfaces we have.
     * 
     * @param klass
     */
    protected void checkClass(Class klass)
    {
        if ( ! klass.isInterface() )
        {
            if ( ! Modifier.isAbstract(klass.getModifiers()) )
            {
                Iterator it = m_serviceMap.keySet().iterator();
                while (it.hasNext())
                {
                   Class iface = (Class) it.next();
                   
                   if ( iface.isAssignableFrom(klass) )
                   {
                       log(klass.getName() + " implements " + iface.getName(), Project.MSG_VERBOSE);
                       
                       List list = (List) m_serviceMap.get(iface);
                       list.add(klass);
                   }
                }
            }
        }
    }
    
    /**
     * Clean up after ourselves so we can reuse this.
     */
    protected void cleanUp()
    {
        m_services.clear();
        m_serviceMap.clear();
        m_inJar = null;
        m_outJar = null;
    }
    
    /**
     * The ServiceElement that is used to handle the
     * <service name="foo"/> nested elements.
     */
    public static class ServiceElement
    {
        private String _name = "";
        
        /**
         * Get the name of the service.
         * 
         * @return String
         */
        public String getName()
        {
            return _name;
        }
        
        /**
         * Sets the name of the service.
         * @param name
         */
        public void setName(String name)
        {
            _name = name;
        }
    }
}
