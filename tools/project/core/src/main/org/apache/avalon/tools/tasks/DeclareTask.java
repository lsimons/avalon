/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.tools.tasks;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Mkdir;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.home.Context;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.Resource;
import org.apache.avalon.tools.project.ResourceRef;
import org.apache.avalon.tools.project.Info;
import org.apache.avalon.tools.project.Policy;
import org.apache.avalon.tools.project.Plugin;
import org.apache.avalon.tools.project.Plugin.TaskDef;

/**
 * Load a plugin.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class DeclareTask extends Task
{
    private static final String TYPE = "plugin";

    private boolean m_init = false;
    private Context m_context;
    private Home m_home;

   /**
    * Set the home ref id.
    * @param id a home id
    */
    public void setRefid( String id )
    {
        Object object = getProject().getReference( id );
        if( null == object )
        {
            final String error = 
              "Unknown ref id '" + id + "'.";
            throw new BuildException( error );
        }
        if( object instanceof Home )
        {
            m_home = (Home) object;
        }
        else
        {
            final String error = 
              "Supplied id '" + id + "' does not refer to a Home.";
            throw new BuildException( error );
        }
    }

    public void init() throws BuildException 
    {
        if( !m_init )
        {
            Project project = getProject();
            m_context = Context.getContext( project );
            m_init = true;
        }
    }

    public void execute() throws BuildException 
    {
        if( null == m_home ) 
        {
            final String error = 
              "Required system home 'refid' attribute not set in the task definition ["
              + getTaskName() + "].";
            throw new BuildException( error );
        }

        log( "creating plugin declaration" );
        final Definition def = m_home.getDefinition();

        try
        {
            final File file = getPluginFile();
            file.createNewFile();
            final OutputStream output = new FileOutputStream( file );

            try
            {
                writePluginDef( output, def );
            }
            finally
            {
                closeStream( output );
            }
        }
        catch( Throwable e )
        {
            throw new BuildException( e );
        }
    }

    private File getPluginFile()
    {
        File dir = m_context.getDeliverablesDirectory();
        File ants = new File( dir, TYPE + "s" );
        mkDir( ants );

        Definition def = m_home.getDefinition();
        Info info = def.getInfo();
        String filename = getFilename( info );
        return new File( ants, filename );
    }

    private String getFilename( Info info )
    {
        String version = info.getVersion();
        if( null == version )
        {
            return info.getName() + "." + TYPE;
        }
        else
        {
            return info.getName() + "-" + version + "." + TYPE;
        }
    }

    public void writePluginDef( final OutputStream output, final Definition def )
        throws IOException
    {
        final Writer writer = new OutputStreamWriter( output );
        writeHeader( writer );
        writePlugin( writer, def );
        writer.flush();
    }

   /**
    * Write the XML header.
    * @param writer the writer
     * @throws IOException if unable to write xml
    */
    private void writeHeader( final Writer writer )
        throws IOException
    {
        writer.write( "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" );
    }

    private void writePlugin( final Writer writer, final Definition def )
        throws IOException
    {
        final Info info = def.getInfo();

        writer.write( "\n\n<plugin>" );
        writeInfo( writer, info );
        if( def instanceof Plugin )
        {
            Plugin plugin = (Plugin) def;
            writeTaskDefs( writer, plugin );
        }
        writeClasspath( writer, def );
        writer.write( "\n</plugin>\n" );
    }

    private void writeTaskDefs( final Writer writer, final Plugin plugin )
        throws IOException
    {
        writer.write( "\n  " );
        writer.write( "<tasks>" );
        TaskDef[] defs = plugin.getTaskDefs();
        for( int i=0; i<defs.length; i++ )
        {
            TaskDef def = defs[i];
            writer.write( 
              "\n    <taskdef name=\"" 
              + def.getName() 
              + "\" class=\""
              + def.getClassname() 
              + "\"/>" );
        }
        writer.write( "\n  </tasks>" );
    }

    private void writeInfo( final Writer writer, final Info info )
        throws IOException
    {
        final String name = info.getName();
        final String group = info.getGroup();
        final String version = info.getVersion();
        final String type = info.getType();

        writer.write( "\n  <info>" );
        writer.write( "\n    <name>" + name + "</name>" );
        writer.write( "\n    <group>" + group + "</group>" );
        if( null != version )
        {
            writer.write( "\n    <version>" + version + "</version>" );
        }
        writer.write( "\n    <type>" + TYPE + "</type>" );
        writer.write( "\n  </info>" );
    }

    private void writeClasspath( final Writer writer, final Definition def )
        throws IOException
    {
        writer.write( "\n  <classpath>" );
        final String pad = "    ";
        ResourceRef[] resources = m_home.getRepository().getResourceRefs( def );
        writeResourceRefs( writer, pad, resources );
        writeResource( writer, pad, def );
        writer.write( "\n  </classpath>" );
    }

    private void writeResourceRefs( final Writer writer, String pad, final ResourceRef[] resources )
        throws IOException
    {
        for( int i=0; i<resources.length; i++ )
        {
            ResourceRef ref = resources[i];
            Policy policy = ref.getPolicy();
            if( policy.isRuntimeEnabled() )
            {
                Resource resource = m_home.getResource( ref );
                writeResource( writer, pad, resource );
            }
        }
    }

    private void writeResource( final Writer writer, String pad, final Resource resource )
        throws IOException
    {
        Info info = resource.getInfo();
        String name = info.getName();
        String group = info.getGroup();
        String version = info.getVersion();
        String type = info.getType();

        writer.write( "\n" );
        writer.write( pad );
        writer.write( "<" + type + ">" );
        writer.write( group );
        writer.write( "/" );
        writer.write( name );

        if( null != version )
        {
            writer.write( "#" );
            writer.write( version );
        }
        writer.write( "</" + type + ">" );
    }

    private void closeStream( final OutputStream output )
    {
        if( null != output )
        {
            try
            {
                output.close();
            }
            catch( IOException e )
            {
                // ignore
            }
        }
    }

    private void mkDir( File dir )
    {
        Mkdir mkdir = (Mkdir) getProject().createTask( "mkdir" );
        mkdir.setDir( dir );
        mkdir.init();
        mkdir.execute();
    }

}
