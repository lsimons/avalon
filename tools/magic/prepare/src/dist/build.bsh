/*
Copyright 2004 The Apache Software Foundation
Licensed  under the  Apache License,  Version 2.0  (the "License");
you may not use  this file  except in  compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed  under the  License is distributed on an "AS IS" BASIS,
WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
implied.

See the License for the specific language governing permissions and
limitations under the License.
*/

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.avalon.magic.AbstractPlugin;
import org.apache.avalon.magic.Plugin;
import org.apache.avalon.magic.PluginContext;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Javac;

public class PreparePlugin extends AbstractPlugin
{
    private Project m_Project;
    
    private boolean m_Initialized = false;
        
    public void init()
    {   
        if( m_Initialized )
            return;
        notifyPreMethod( "init" );
        copySources();
        notifyPostMethod( "init" );
        m_Initialized = true;
    }
    
    private void copySources()
    {
        String destdirname = m_Context.getProperty( "prepare.build.src.dir" );
        File toDir = new File( destdirname ); 
        String srcdirname = m_Context.getProperty( "prepare.src.dir" );
        File fromDir = new File( srcdirname ); 
        toDir.mkdirs();  /* ensure that the directory exists. */
        String textFiles = m_Context.getProperty( "prepare.filtered.files" );
        
        copyWithFilter( fromDir, toDir, textFiles );
        copyWithOutFilter( fromDir, toDir, textFiles );
        
    }
    
    private void copyWithFilter( File fromDir, File toDir, String textFiles )
    {
        FileSet from = new FileSet();
        from.setDir( fromDir );
        from.setIncludes( textFiles );

        /* Copy with filtering */    
        Copy copy = (Copy) m_Project.createTask( "copy" );
        FilterSet fs = copy.createFilterSet();
        Iterator list = m_Context.getPropertyKeys();
        while( list.hasNext() )
        {
            String key = (String) list.next();
            String value = m_Context.getProperty( key );
            fs.addFilter( key.toUpperCase(), value );
        }
        copy.setTodir( toDir );
        copy.setFiltering( true );
        copy.addFileset( from );
        copy.init();
        copy.execute();
    }
    
    private void copyWithOutFilter( File fromDir, File toDir, String textFiles )
    {
        FileSet from = new FileSet();
        from.setDir( fromDir );
        from.setIncludes( "**/*" );
        from.setExcludes( textFiles );
        
        /* Copy without filtering */    
        Copy copy = (Copy) m_Project.createTask( "copy" );
        copy.setTodir( toDir );
        copy.addFileset( from );
        copy.setFiltering( false );
        copy.init();
        copy.execute();
    }
}   
