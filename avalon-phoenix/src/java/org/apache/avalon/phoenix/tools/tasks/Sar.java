/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.tasks;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.ZipFileSet;

/**
 * Creates a Sar archive.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 */
public class Sar 
    extends Jar 
{
    private File      m_config;
    private File      m_assembly;
    private File      m_server;

    public Sar() 
    {
        archiveType = "sar";
        emptyBehavior = "fail";
    }

    public void setFile( final File file )
    {
        super.setZipfile( file );
    }

    public void setConfig( final File config ) 
    {
        m_config = config;

        if( !m_config.exists() )
        {
            throw new BuildException("Config descriptor: " + m_config + " does not exist.");
        }

        if( !m_config.isFile() )
        {
            throw new BuildException("Config descriptor: " + m_config + " is not a file.");
        }
    }

    public void setAssembly( final File assembly ) 
    {
        m_assembly = assembly;

        if( !m_assembly.exists() )
        {
            throw new BuildException("Assembly descriptor: " + m_assembly + " does not exist.");
        }

        if( !m_assembly.isFile() )
        {
            throw new BuildException("Assembly descriptor: " + m_assembly + " is not a file.");
        }
    }

    public void setServer( final File server ) 
    {
        m_server = server;

        if( !m_server.exists() )
        {
            throw new BuildException("Server descriptor: " + m_server + " does not exist.");
        }

        if( !m_server.isFile() )
        {
            throw new BuildException("Server descriptor: " + m_server + " is not a file.");
        }
    }

    public void addBlocks( final ZipFileSet zipFileSet )
    {
        zipFileSet.setPrefix( "blocks");
        super.addFileset( zipFileSet );
    }

    public void addLib( final ZipFileSet zipFileSet )
    {
        zipFileSet.setPrefix( "lib");
        super.addFileset( zipFileSet );
    }

    public void execute() throws BuildException 
    {
        if( null == m_config )
        {
            throw new BuildException( "config attribute is required", location );
        }
        if( null == m_assembly )
        {
            throw new BuildException( "assembly attribute is required", location );
        }
        if( null == m_server )
        {
            throw new BuildException( "server attribute is required", location );
        }

        pushFile( "conf/config.xml", m_config );
        pushFile( "conf/assembly.xml", m_assembly );
        pushFile( "conf/server.xml", m_server );

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
        m_server = null;
    }
}
