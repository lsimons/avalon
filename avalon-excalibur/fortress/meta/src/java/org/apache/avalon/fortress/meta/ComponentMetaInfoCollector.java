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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

        final String message =
            "Writing Info descriptors as property files (.meta).";
        log( message );

        super.execute();

        try
        {
            writeInfoMetaData();
            
            PrintWriter writer = new PrintWriter( new FileWriter( m_serviceFile ) );
            
            Iterator it = m_services.iterator();
            while (it.hasNext())
            {
                writer.println(it.next());
            }
            
            writer.close();
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
            DocletTag tag = javaClass.getTagByName( "avalon.service" );
            if( null != tag )
            {
                m_services.add(javaClass.getFullyQualifiedName());
            }
            else
            {
                tag = javaClass.getTagByName( "avalon.component" );
                if( null != tag )
                {
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
        DocletTag avalonScope = javaClass.getTagByName("avalon.scope");
        DocletTag fortressHandler = javaClass.getTagByName("fortress.handler");
        String scope = null;
        String handler = null;
        
        if ( avalonScope == null && fortressHandler == null )
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
        
        if (null != avalonScope)
        {
            scope = avalonScope.getValue();
        }
        else if (handler != null)
        {
            handler = (null == fortressHandler) ? PerThreadComponentHandler.class.getName() : fortressHandler.getValue();
        }
        
        if ( null != scope ) meta.setProperty("avalon.scope", scope);
        if ( null != handler ) meta.setProperty("fortress.handler", handler);
        
        DocletTag avalonConfigName = javaClass.getTagByName("avalon.configname");
        if ( null == avalonConfigName ) avalonConfigName = javaClass.getTagByName("fortress.configname");

        meta.setProperty("avalon.configname", (avalonConfigName == null) ? ServiceRoleManager.createShortName(javaClass.getName()) : avalonConfigName.getValue() );
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
}
