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
import org.apache.tools.ant.BuildException;
import org.apache.avalon.tools.model.Plugin.ListenerDef;
import org.apache.avalon.tools.model.Plugin.TaskDef;
import org.apache.avalon.tools.model.Plugin;
import org.apache.avalon.tools.model.Policy;

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
public class BlockTask extends DeclareTask
{
    private static final String BLOCK = "block";
    private static final String MAIN = "main";
    private static final String TEST = "test";

    public static class Identifiable
    {
        private String m_name;

        public void setName( final String name )
        {
            m_name = name;
        }

        public String getName()
        {
            return m_name;
        }
    }

    public static class Component extends Identifiable
    {
        private String m_classname;
        private String m_profile;
        private boolean m_context = false;
        private ArrayList m_children = new ArrayList();

        public void setClass( final String classname )
        {
            m_classname = classname;
        }

        public void setProfile( final String profile )
        {
            m_profile = profile;
        }

        public String getClassname()
        {
            return m_classname;
        }

        public String getProfile()
        {
            return m_profile;
        }

        public Context createContext()
        {
            if( m_context )
            {
                Context context = new Context();
                m_children.add( context );
                m_context = true;
                return context;
            }
            else
            {
                final String error = 
                  "Context entry already set!";
                throw new BuildException( error );
            }
        }

        public Object[] getChildren()
        {
            return m_children.toArray();
        }

        public Dependency createDependency()
        {
            final Dependency dep = new Dependency();
            m_children.add( dep );
            return dep;
        }
    }

    public static class Include extends Identifiable
    {
        private String m_artifact;

        public void setArtifact( final String spec )
        {
            m_artifact = spec;
        }

        public String getArtifact()
        {
            return m_artifact;
        }
    }

    public static class Service
    {
        private String m_type;
        private String m_source;

        public void setType( final String type )
        {
            m_type = type;
        }

        public String getType()
        {
            return m_type;
        }

        public void setSource( final String source )
        {
            m_source = source;
        }

        public String getSource()
        {
            return m_source;
        }
    }

    public static class Context
    {
        private String m_class;
        private List m_entries = new ArrayList();

        public void setClass( final String classname )
        {
            m_class = classname ;
        }

        public String getClassname()
        {
            return m_class;
        }

        public Entry createEntry()
        {
            final Entry entry = new Entry();
            m_entries.add( entry );
            return entry;
        }

        public Entry[] getEntries()
        {
            return (Entry[]) m_entries.toArray( new Entry[0] );
        }
    }

    public static class Entry extends Param
    {
        private String m_key;

        public void setKey( final String key )
        {
            m_key = key ;
        }

        public String getKey()
        {
            return m_key;
        }
    }

    public static class Param
    {
        private String m_classname;
        private String m_value;
        private List m_params = new ArrayList();

        public void setClass( final String classname )
        {
            m_classname = classname;
        }

        public String getClassname()
        {
            return m_classname;
        }

        public void setValue( final String value )
        {
            m_value = value;
        }

        public String getValue()
        {
            return m_value;
        }

        public Param createParam()
        {
            final Param param = new Param();
            m_params.add( param );
            return param;
        }

        public Param[] getParams()
        {
            return (Param[]) m_params.toArray( new Param[0] );
        }
    }

    public static class Dependency
    {
        private String m_key;
        private String m_source;

        public void setKey( final String key )
        {
            m_key = key;
        }

        public String getKey()
        {
            return m_key;
        }

        public void setSource( final String source )
        {
            m_source = source;
        }

        public String getSource()
        {
            return m_source;
        }
    }

    private String m_target;
    private String m_container;
    private List m_content = new ArrayList();
    private boolean m_standalone = true;
    private Service m_service;

    public void setName( final String name )
    {
        m_container = name;
    }

   /**
    * Optional attribute indicating that the block is to be generated
    * as an embedded BLOCK-INF/block.xml into either the MAIN or TEST
    * classes directory as indicated by the target parameter.
    */
    public void setEmbed( final String target )
    {
        if( MAIN.equalsIgnoreCase( target ) )
        {
            m_target = MAIN;
        }
        else if( TEST.equalsIgnoreCase( target ) )
        {
            m_target = TEST;
        }
        else
        {
            final String error = 
              "Embed policy not recognized (use MAIN or TEST)";
            throw new BuildException( error );
        }
    }

   /**
    * Optional attribute indicating that the block is to be generated
    * as a standalone block.
    */
    public void setStandalone( final boolean flag )
    {
        m_standalone = flag;
    }


    private String getName( Definition def )
    {
        if( null == m_container )
        {
            return def.getInfo().getName();
        }
        else
        {
            return m_container;
        }
    }

    protected File getPluginFile()
    {
        if( null == m_target )
        {
            return super.getPluginFile();
        }
        else
        {
            File root = getEmbeddedRoot( m_target );
            File blockinf = new File( root, "BLOCK-INF" );
            return new File( blockinf, "block.xml" );
        }
    }

    private File getEmbeddedRoot( String target )
    {
        if( MAIN.equals( m_target ) )
        {
            return getContext().getClassesDirectory();
        }
        else
        {
            return getContext().getTestClassesDirectory();
        }
    }

    public Component createComponent()
    {
        final Component component = new Component();
        m_content.add( component );
        return component;
    }

    public Include createInclude()
    {
        final Include include = new Include();
        m_content.add( include );
        return include;
    }

    public Service createService()
    {
        if( null == m_service )
        {
            m_service = new Service();
            return m_service;
        }
        else
        {
            final String error = 
              "Multiple service export not supported nor recomended.";
            throw new BuildException( error );
        }
    }

    public void init()
    {
        super.init();
        super.setType( BLOCK );
    }

    public void execute()
    {
        if( null != m_target )
        {
            super.execute(); // generate the embedded block
            m_target = null;
        }
        if( m_standalone )
        {
            super.execute(); // generate the standalone block
        }
    }

    protected void writePlugin( final Writer writer, final Definition def )
        throws IOException
    {
        final Info info = def.getInfo();

        writer.write( "\n\n<container name=\"" + getName( def ) + "\">" );

        if( null != m_service )
        {
            writeService( writer, m_service );
        }

        writer.write( "\n\n  <classloader>" );
        boolean standalone = (null == m_target);
        writeClasspath( writer, def, "    ", standalone );
        writer.write( "\n  </classloader>" );

        Identifiable[] components = 
          (Identifiable[]) m_content.toArray( new Identifiable[0] );
        for( int i=0; i<components.length; i++ )
        {
            Identifiable identifiable = components[i];
            if( identifiable instanceof Component )
            {
                writeComponent( writer, (Component) identifiable );
            }
            else if( identifiable instanceof Include )
            {
                writeInclude( writer, (Include) identifiable );
            }
        }

        writer.write( "\n\n</container>\n" );
    }

    private void writeComponent( final Writer writer, final Component component )
        throws IOException
    {
        writer.write( "\n\n  <component name=\"" + component.getName() 
          + "\" class=\"" + component.getClassname() + "\"" );

        if( null != component.getProfile() )
        {
            writer.write( " profile=\"" + component.getProfile() + "\"" );
        }

        Object[] children = component.getChildren();
        if( children.length == 0 )
        {
            writer.write( "/>" );
        }
        else
        {
            writer.write( ">" );
            for( int i=0; i<children.length; i++ )
            {
                Object child = children[i];
                if( child instanceof Context )
                {
                    writeContext( writer, (Context) child );
                }
                else if( child instanceof Dependency )
                {
                    writeDependency( writer, (Dependency) child );
                }
            }
            writer.write( "\n  </component>" );
        }
    }

    private void writeContext( final Writer writer, final Context context )
        throws IOException
    {
        writer.write( "\n    <context" ); 
        if( null != context.getClassname() )
        {
            writer.write( " class=\"" + context.getClassname() + "\"" );
        }
        Entry[] entries = context.getEntries();
        if( entries.length == 0 )
        {
            writer.write( "/>" );
        }
        else
        {
            writer.write( ">" );
            for( int i=0; i<entries.length; i++ )
            {
                writeEntry( writer, entries[i] );
            }  
            writer.write( "\n    </context>" ); 
        }
    }

    private void writeEntry( final Writer writer, final Entry entry )
        throws IOException
    {
        writer.write( 
          "\n      <entry key=\"" + entry.getKey() + "\"" );

        if( null != entry.getClassname() )
        {
            writer.write( " class=\"" + entry.getClassname() + "\"" );
        }

        if( null != entry.getValue() )
        {
            writer.write( ">" + entry.getValue() + "</entry>" );
        }
        else
        {
            Param[] params = entry.getParams();
            if( params.length == 0 )
            {
                writer.write( "/>" );  // is this legal?
            }
            else
            {
                writer.write( ">" );
                for( int i=0; i<params.length; i++ )
                {
                    writeParam( writer, params[i] );
                } 
                writer.write( "\n      </entry>" ); 
            }
        }
    }

    private void writeInclude( final Writer writer, final Include include )
        throws IOException
    {
        writer.write( 
          "\n  <include name=\"" 
          + include.getName() + "\" artifact=\"" 
          + include.getArtifact() + "\"/>\n" );
    }

    private void writeService( final Writer writer, final Service service )
        throws IOException
    {
        writer.write( "\n" );
        writer.write( "\n  <services>" );
        writer.write( "\n    <service type=\"" + service.getType() + "\">" );
        writer.write( "\n      <source>" + service.getSource() + "</source>" );
        writer.write( "\n    </service>" );
        writer.write( "\n  </services>" );
    }

    private void writeParam( final Writer writer, final Param param )
        throws IOException
    {
        if( null == param.getClassname() )
        {
            writer.write( "\n        <param>" );
        }
        else
        {
            writer.write( "\n        <param class=\"" + param.getClassname() + "\">" );
        }

        String value = param.getValue();
        if( null != value )
        {
            writer.write( value + "</param>" );
        }
        else
        {
            Param[] parameters = param.getParams();
            if( parameters.length == 0 )
            {
                writer.write( "/>" );  // is this legal?
            }
            else
            {
                writer.write( ">" );
                for( int i=0; i<parameters.length; i++ )
                {
                    writeParam( writer, parameters[i] );
                } 
                writer.write( "\n        </param>" ); 
            }
        }
    }

    private void writeDependency( final Writer writer, final Dependency dep )
        throws IOException
    {
        writer.write( 
          "\n    <dependency key=\"" 
          + dep.getKey() + "\" source=\"" 
          + dep.getSource() + "\"/>" );
    }
}
