/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.tasks;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.ZipFileSet;

/**
 * Creates a Sar archive.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
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
        setZipfile( file );
    }

    public void setConfig( final File config )
    {
        m_config = config;

        if( !m_config.exists() )
        {
            throw new BuildException( "Config descriptor: " + m_config + " does not exist." );
        }

        if( !m_config.isFile() )
        {
            throw new BuildException( "Config descriptor: " + m_config + " is not a file." );
        }
    }

    public void setAssembly( final File assembly )
    {
        m_assembly = assembly;

        if( !m_assembly.exists() )
        {
            throw new BuildException( "Assembly descriptor: " + m_assembly + " does not exist." );
        }

        if( !m_assembly.isFile() )
        {
            throw new BuildException( "Assembly descriptor: " + m_assembly + " is not a file." );
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
            final String message = "Environment descriptor: "
                + m_environment + " does not exist.";
            throw new BuildException( message );
        }

        if( !m_environment.isFile() )
        {
            final String message = "Environment descriptor: "
                + m_environment + " is not a file.";
            throw new BuildException( message );
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
            throw new BuildException( "config attribute is required", location );
        }
        if( null == m_assembly )
        {
            throw new BuildException( "assembly attribute is required", location );
        }
        if( null == m_environment )
        {
            throw new BuildException( "environment attribute is required", location );
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
