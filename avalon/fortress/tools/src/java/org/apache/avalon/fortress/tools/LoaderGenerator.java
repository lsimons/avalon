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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.types.ZipFileSet;

/**
 * Creates a Loader archive.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class LoaderGenerator
    extends Jar
{
    private File m_splashScreen;
    private File m_buildNumber;
    private String m_name;
    private String m_version;
    private String m_lookAndFeel;
    private String m_proxy;

    public LoaderGenerator()
    {
        archiveType = "jar";
        emptyBehavior = "fail";
    }

    public void setDest( final File file )
    {
        setDestFile( file );
    }

    public void setSplashScreen( final File screen )
    {
        m_splashScreen = screen;

        if( !m_splashScreen.exists() )
        {
            final String message =
                "Splash Screen: " + m_splashScreen + " does not exist.";
            throw new BuildException( message, getLocation() );
        }

        if( !m_splashScreen.isFile() )
        {
            final String message =
                "Splash Screen: " + m_splashScreen + " is not a file.";
            throw new BuildException( message, getLocation() );
        }
    }

    public void setBuildNumberFile( final File buildNumber )
    {
        m_buildNumber = buildNumber;

        if( !m_buildNumber.exists() )
        {
            final String message =
                "Build Number File: " + m_buildNumber + " does not exist.";
            throw new BuildException( message, getLocation() );
        }

        if( !m_buildNumber.isFile() )
        {
            final String message =
                "Build Number File: " + m_buildNumber + " is not a file.";
            throw new BuildException( message, getLocation() );
        }
    }

    public void setAppName( final String appName )
    {
        m_name = appName;

        if( null == m_name || m_name.length() == 0 )
        {
            final String message =
                "Application Name must be set.";
            throw new BuildException( message, getLocation() );
        }
    }

    public void setVersion( final String version )
    {
        m_version = version;

        if( null == m_version || m_version.length() == 0 )
        {
            final String message = "Application Version must be supplied.";
            throw new BuildException( message, getLocation() );
        }
    }
    
    public void setLookAndFeel( final String laf )
    {
        m_lookAndFeel = laf;

        if( null == m_lookAndFeel || m_lookAndFeel.length() == 0 )
        {
            final String message = "Look and Feel must be supplied.";
            throw new BuildException( message, getLocation() );
        }
    }
    
    public void setProxy( final String proxyClassName )
    {
        m_proxy = proxyClassName;
        
        if( null == m_proxy || m_proxy.length() == 0 )
        {
            final String message = "Application Proxy class name must be supplied.";
            throw new BuildException( message, getLocation() );
        }
        
        log("Warning, you are overriding the default application proxy class.");
    }

    public void execute()
        throws BuildException
    {
        if( null == m_splashScreen )
        {
            final String message = "splashscreen attribute is required";
            throw new BuildException( message, getLocation() );
        }
        if( null == m_name )
        {
            final String message = "name attribute is required";
            throw new BuildException( message, getLocation() );
        }
        if( null == m_version )
        {
            final String message = "version attribute is required";
            throw new BuildException( message, getLocation() );
        }
        if( null == m_proxy )
        {
            m_proxy = "org.d_haven.guiapp.container.FortressRunner";
        }
        if ( null == m_lookAndFeel )
        {
            m_lookAndFeel = "system";
        }
        
        File propFile = null;
        try
        {
            Properties props = new Properties();
            
            String build = "0";
            if (null != m_buildNumber)
            {
                FileInputStream bfis = new FileInputStream(m_buildNumber);
                Properties buildProps = new Properties();
                buildProps.load(bfis);
                bfis.close();
                build = buildProps.getProperty("build.number");
            }
            
            props.put("app.name", m_name);
            props.put("app.version", m_version);
            props.put("app.build", build);
            props.put("app.laf", m_lookAndFeel);
            props.put("app.proxy", m_proxy);
            
            propFile = File.createTempFile("app", ".properties");
            propFile.deleteOnExit();
            FileOutputStream pos = new FileOutputStream(propFile);
            props.store(pos, "#Application constants");
            pos.close();

            Manifest manifest = Manifest.getDefaultManifest();
            Manifest.Attribute attr = new Manifest.Attribute();
            attr.setName("Main-Class");
            attr.setValue("org.d_haven.guiapp.Main");
            manifest.addConfiguredAttribute(attr);
            
            addConfiguredManifest(manifest);
            
            mergeArchive();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BuildException("Could not create properties", getLocation());
        }

        pushFile( "META-INF/app.properties", propFile );
        pushFile( "org/d_haven/guiapp/splashscreen.jpg", m_splashScreen);

        super.execute();
    }
    
    private void mergeArchive()
        throws IOException
    {
        File loaderJar = File.createTempFile("loader", ".jar");
        loaderJar.deleteOnExit();
        InputStream resourceJar = getClass().getClassLoader().getResourceAsStream("loader.jar");
        FileOutputStream fos = new FileOutputStream(loaderJar);
        int length = -1;
        byte[] buffer = new byte[1024];
        
        while ( (length = resourceJar.read(buffer)) > 0 )
        {
            fos.write(buffer,0,length);
        }
        
        resourceJar.close();
        fos.close();
        
        final ZipFileSet zipFileSet = new ZipFileSet();
        zipFileSet.setSrc(loaderJar);
        zipFileSet.setIncludes("org/**");
        
        addFileset(zipFileSet);
    }

    private void pushFile( final String path, final File file )
    {
        final ZipFileSet zipFileSet = new ZipFileSet();
        zipFileSet.setDir( new File( file.getParent() ) );
        zipFileSet.setIncludes( file.getName() );
        zipFileSet.setFullpath( path );
        super.addFileset( zipFileSet );
    }

    protected void cleanUp()
    {
        super.cleanUp();

        m_splashScreen = null;
        m_name = null;
        m_version = null;
        m_lookAndFeel = null;
        m_proxy = null;
    }
}
