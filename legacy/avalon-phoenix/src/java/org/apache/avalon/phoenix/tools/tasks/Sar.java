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

package org.apache.avalon.phoenix.tools.tasks;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.ZipFileSet;

/**
 * Creates a Sar archive.
 *
 * @author Peter Donald
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 */
public class Sar
    extends Jar
{
    private File m_config;
    private File m_assembly;
    private File m_environment;

    public Sar()
    {
        archiveType = "sar";
        emptyBehavior = "fail";
    }

    public void setSarfile( final File file )
    {
        setDestFile( file );
    }

    public void setConfig( final File config )
    {
        m_config = config;

        if( !m_config.exists() )
        {
            final String message =
                "Config descriptor: " + m_config + " does not exist.";
            throw new BuildException( message, getLocation() );
        }

        if( !m_config.isFile() )
        {
            final String message =
                "Config descriptor: " + m_config + " is not a file.";
            throw new BuildException( message, getLocation() );
        }
    }

    public void setAssembly( final File assembly )
    {
        m_assembly = assembly;

        if( !m_assembly.exists() )
        {
            final String message =
                "Assembly descriptor: " + m_assembly + " does not exist.";
            throw new BuildException( message, getLocation() );
        }

        if( !m_assembly.isFile() )
        {
            final String message =
                "Assembly descriptor: " + m_assembly + " is not a file.";
            throw new BuildException( message, getLocation() );
        }
    }

    public void setServer( final File server )
    {
        System.err.println( "DEPRECATED: Server attribute of sar task is deprecated" );
        setEnvironment( server );
    }

    public void setEnvironment( final File environment )
    {
        m_environment = environment;

        if( !m_environment.exists() )
        {
            final String message = "Environment descriptor: " +
                m_environment + " does not exist.";
            throw new BuildException( message, getLocation() );
        }

        if( !m_environment.isFile() )
        {
            final String message = "Environment descriptor: " +
                m_environment + " is not a file.";
            throw new BuildException( message, getLocation() );
        }
    }

    public void addLib( final ZipFileSet zipFileSet )
    {
        zipFileSet.setPrefix( "SAR-INF/lib" );
        super.addFileset( zipFileSet );
    }

    public void addClasses( final ZipFileSet zipFileSet )
    {
        zipFileSet.setPrefix( "SAR-INF/classes" );
        super.addFileset( zipFileSet );
    }

    public void execute()
        throws BuildException
    {
        if( null == m_config )
        {
            final String message = "config attribute is required";
            throw new BuildException( message, getLocation() );
        }
        if( null == m_assembly )
        {
            final String message = "assembly attribute is required";
            throw new BuildException( message, getLocation() );
        }
        if( null == m_environment )
        {
            final String message = "environment attribute is required";
            throw new BuildException( message, getLocation() );
        }

        pushFile( "SAR-INF/config.xml", m_config );
        pushFile( "SAR-INF/assembly.xml", m_assembly );
        pushFile( "SAR-INF/environment.xml", m_environment );

        super.execute();
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

        m_config = null;
        m_assembly = null;
        m_environment = null;
    }
}
