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

import org.apache.avalon.tools.model.Definition;
import org.apache.avalon.tools.model.ResourceRef;
import org.apache.avalon.tools.model.Info;
import org.apache.avalon.tools.model.Resource;
import org.apache.avalon.tools.model.Plugin.ListenerDef;
import org.apache.avalon.tools.model.Plugin.TaskDef;
import org.apache.avalon.tools.model.Plugin;
import org.apache.avalon.tools.model.Policy;
import org.apache.avalon.tools.model.Home;
import org.apache.avalon.tools.model.Context;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.ArrayList;

/**
 * Create meta-data for a block.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class GumpTask extends SystemTask
{
    public static class Href
    {
        private String m_href;

        public void setHref( String href )
        {
            m_href = href;
        }
   
        public String getHref()
        {
            return m_href;
        }
    }

    public static class FileHolder
    {
        private String m_file;

        public void setFile( String file )
        {
            m_file = file;
        }
   
        public String getFile()
        {
            return m_file;
        }
    }

    public static class License extends FileHolder
    {
    }

    public static class Template extends FileHolder
    {
        private String m_target;

        public void setTarget( String target )
        {
            m_target = target;
        }
   
        public String getTarget()
        {
            return m_target;
        }
    }

    public static class Url extends Href
    {
    }

    public static class Cvs extends Href
    {
    }

    public static class Svn extends Href
    {
    }

    private String m_name;
    private String m_filename;
    private String m_description;
    private Url m_url;
    private Cvs m_cvs;
    private Svn m_svn;
    private License m_license;
    private Template m_template = new Template();
    private boolean m_public = false;

    public void setName( String name )
    {
        m_name = name;
    }

    public void setFilename( String filename )
    {
        m_filename = filename;
    }

    public void setDescription( String description )
    {
        m_description = description;
    }
        
    public void setPublic( boolean flag )
    {
        m_public = flag;
    }

    public Url createUrl()
    {
        if( null == m_url )
        {
            m_url = new Url();
            return m_url;
        }
        else
        {
            throw new BuildException( "Multiple url entries not allowed." );
        }
    }

    public Svn createSvn()
    {
        if( null == m_svn )
        {
            if( null == m_cvs )
            {
                m_svn = new Svn();
                return m_svn;
            }
            else
            {
                throw new BuildException( "Cannot declare both cvs and svn." );
            }
        }
        else
        {
            throw new BuildException( "Multiple svn entries not allowed." );
        }
    }

    public Cvs createCvs()
    {
        if( null == m_cvs )
        {
            if( null == m_cvs )
            {
                m_cvs = new Cvs();
                return m_cvs;
            }
            else
            {
                throw new BuildException( "Cannot declare both cvs and svn." );
            }
        }
        else
        {
            throw new BuildException( "Multiple cvs entries not allowed." );
        }
    }

    public License createLicense()
    {
        if( null == m_license )
        {
            m_license = new License();
            return m_license;
        }
        else
        {
            throw new BuildException( "Multiple license entries not allowed." );
        }
    }

    public Template createTemplate()
    {
        return m_template;
    }


    private File getFile()
    {
        if( null != m_filename )
        {
            final Project project = getProject();
            return Context.getFile( project.getBaseDir(), m_filename );
        }
        else
        {
            return new File( getContext().getDeliverablesDirectory(), "gump.xml" );
        }
    }

    public void execute()
    {
        try
        {
            final File file = getFile();
            file.getParentFile().mkdirs();
            file.createNewFile();
            final OutputStream output = new FileOutputStream( file );
            final Writer writer = new OutputStreamWriter( output );

            try
            {
                writeHeader( writer );
                writeModule( writer );
                writer.flush();
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

    private String getName()
    {
        if( null != m_name )
        {
            return m_name;
        }
        else
        {
            return getProject().getName();
        }
    }

    protected void writeModule( final Writer writer )
        throws IOException
    {
        Home home = getHome();
        Project project = getProject();
        String name = getName();

        writer.write( "\n\n<module name=\"" + name + "\">" );
        writer.write( "\n\n  <url href=\"" + m_url.getHref() + "\"/>" );

        if( null != m_description )
        {
            writer.write( "\n  <description>" );
            writer.write( m_description );
            writer.write( "</description>" );
        }

        if( null != m_svn )
        {
            writer.write( "\n  <svn href=\"" + m_svn.getHref() + "\"/>" );
        }
        else if( null != m_svn )
        {
            writer.write( "\n  <cvs href=\"" + m_cvs.getHref() + "\"/>" );
        }

        Definition[] definitions = home.getDefinitions();
        for( int i=0; i<definitions.length; i++ )
        {
            Definition def = definitions[i];
            writeProject( writer, def );
        }

        writer.write( "\n\n</module>\n" );
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
        writer.write( "\n" );
        writer.write( "\n<!--" );
        writer.write( "\n#" );
        writer.write( "\n# Copyright 2004 Apache Software Foundation " );
        writer.write( "\n# Licensed  under the  Apache License,  Version 2.0  (the \"License\"); " );
        writer.write( "\n# you may not use  this file  except in  compliance with the License. " );
        writer.write( "\n# You may obtain a copy of the License at  " );
        writer.write( "\n#  " );
        writer.write( "\n# http://www.apache.org/licenses/LICENSE-2.0 " );
        writer.write( "\n#  " );
        writer.write( "\n# Unless required by applicable law or agreed to in writing, software " );
        writer.write( "\n# distributed  under the  License is distributed on an \"AS IS\" BASIS, " );
        writer.write( "\n#  WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or " );
        writer.write( "\n#  implied. " );
        writer.write( "\n#  " );
        writer.write( "\n# See the License for the specific language governing permissions and " );
        writer.write( "\n# limitations under the License. " );
        writer.write( "\n# " );
        writer.write( "\n# NOTE: The content of this file is automatically generated by Magic." );
        writer.write( "\n# http://avalon.apache.org/" );
        writer.write( "\n#" );
        writer.write( "\n-->" );
        writer.write( "\n" );
    }

   /**
    * Write the XML project definition.
    * @param writer the writer
    * @param definition the project definition
    * @throws IOException if unable to write xml
    */
    private void writeProject( final Writer writer, Definition definition )
        throws IOException
    {
        String path = definition.getBaseDir().getCanonicalPath();
        int j = getProject().getBaseDir().toString().length();
        String basedir = path.substring( j+1 ).replace( '\\', '/' );

        writer.write( 
          "\n\n  <project name=\"" + definition.getKey() + "\">" );

        if( null != m_license )
        {
            String license = m_license.getFile();
            writer.write( 
               "\n    <license name=\"" + license + "\"/>" );
        }

        final ResourceRef[] refs =
          definition.getResourceRefs( Policy.ANY, ResourceRef.ANY, true );

        String template = m_template.getFile();
        String target = m_template.getTarget();

        writer.write( 
          "\n    <ant basedir=\"" + basedir + "\"" );
        if( null != template )
        {
            writer.write( 
              "\n      buildfile=\"" + template + "\"" );
        }
        if( null != target )
        {
            writer.write( 
              " target=\"" + target + "\"" );
        }
        writer.write( ">" );

        writer.write( 
           "\n      <!-- for magic -->" );
        writer.write( 
           "\n      <property name=\"gump.signature\" value=\"@@DATE@@\"/>" );

        boolean flag = false;
        for( int i=0; i<refs.length; i++ )
        {
            Resource resource = getHome().getResource( refs[i] );
            if( !(resource instanceof Definition) )
            {
                if( !flag )
                {
                    flag = true;
                    writer.write( 
                      "\n      <!-- external references -->" );
                }
                String key = resource.getKey();
                String id = resource.getGump().getId();
                if( null == id )
                {
                    String alias = getKeyForResource( resource );
                    writer.write( 
                      "\n      <depend property=\"gump.resource." + key 
                      + "\" project=\"" + alias 
                      + "\"/>" );
                }
                else
                {
                    String alias = getKeyForResource( resource );
                    writer.write( 
                      "\n      <depend property=\"gump.resource." + key 
                      + "\" project=\"" + alias 
                      + "\" id=\"" + id + "\"/>" );
                }
            }
        }

        writer.write( "\n      <!-- end for -->" );

        flag = false;
        writer.write( "\n    </ant>" );       
        for( int i=0; i<refs.length; i++ )
        {
            Resource resource = getHome().getResource( refs[i] );
            if( resource instanceof Definition )
            {
                if( !flag )
                {
                    flag = true;
                    writer.write( "\n    <!-- for gump -->" );
                }
                String key = resource.getKey();
                writer.write( 
                  "\n    <depend project=\"" + key + "\"/>" );
            }
        }

        if( flag )
        {
            writer.write( "\n    <!-- end for -->" );
        }

        writer.write( 
          "\n    <home nested=\"" 
          + basedir + "/target/deliverables\"/>" );

        String name = definition.getInfo().getName();
        String type = definition.getInfo().getType();
        if( "jar".equals( type ) || "bar".equals( type ) )
        {
            writer.write( 
              "\n    <jar name=\"" + type + "s/" 
              + name + "-@@DATE@@.jar\"/>" );
        }
        if( "plugin".equals( type ) )
        {
            writer.write( 
              "\n    <jar name=\"jars/" 
              + name + "-@@DATE@@.jar\"/>" );
        }
        else if( "doc".equals( type ) )
        {
            writer.write( 
              "\n    <!-- doc output is relative to the module root -->" );
        }

        writer.write( "\n    <nag to=\"dev@avalon.apache.org\"" ); 
        writer.write( "\n       from=\"Magic Integration &lt;dev@avalon.apache.org&gt;\"/>" );

        writer.write( "\n  </project>" );
    }

    private String getKeyForResource( Resource resource )
    {
        final String alias = resource.getGump().getAlias();
        if( null != alias ) 
        {
            return alias;
        }
        else
        {
            return resource.getKey();
        }
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
}
