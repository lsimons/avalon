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

import org.apache.tools.ant.BuildException;

import org.apache.avalon.tools.model.Definition;

import java.io.IOException;
import java.io.File;
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
        private boolean m_configured = false;
        private boolean m_parameters = false;
        private boolean m_dependencies = false;
        private boolean m_activation = true;
        private ArrayList m_children = new ArrayList();

        public void setClass( final String classname )
        {
            m_classname = classname;
        }

        public void setActivation( final boolean flag )
        {
            m_activation = flag ;
        }

        public void setProfile( final String profile )
        {
            m_profile = profile;
        }

        public boolean getActivation()
        {
            return m_activation;
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
            if( !m_context )
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

        public Configuration createConfiguration()
        {
            if( !m_configured )
            {
                Configuration config = new Configuration();
                m_children.add( config );
                m_configured = true;
                return config;
            }
            else
            {
                final String error = 
                  "Configuration entry already set!";
                throw new BuildException( error );
            }
        }

        public Parameters createParameters()
        {
            if( !m_parameters )
            {
                Parameters parameters = new Parameters();
                m_children.add( parameters );
                m_parameters = true;
                return parameters;
            }
            else
            {
                final String error = 
                  "Parameters definition already set!";
                throw new BuildException( error );
            }
        }

        public Dependencies createDependencies()
        {
            if( !m_dependencies )
            {
                Dependencies deps = new Dependencies();
                m_children.add( deps );
                m_dependencies = true;
                return deps;
            }
            else
            {
                final String error = 
                  "Dependencies definition already set!";
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
        private String m_source;

        private String m_key;

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

    public static class Dependencies
    {
        private List m_dependencies = new ArrayList();

        public Dependency createDependency()
        {
            final Dependency dep = new Dependency();
            m_dependencies.add( dep );
            return dep;
        }

        public Dependency[] getDependencies()
        {
            return (Dependency[]) m_dependencies.toArray( new Dependency[0] );
        }
    }

    public static class Parameter extends Identifiable
    {
        private String m_value;

        public void setValue( final String value )
        {
            m_value = value;
        }

        public String getValue()
        {
            return m_value;
        }
    }

    public static class Parameters
    {
        private List m_parameters = new ArrayList();

        public Parameter createParameter()
        {
            final Parameter parameter = new Parameter();
            m_parameters.add( parameter );
            return parameter;
        }

        public Parameter[] getParameters()
        {
            return (Parameter[]) m_parameters.toArray( new Parameter[0] );
        }
    }

    public static class Configuration
    {
        private File m_file;

        public void setFile( final File file )
        {
            m_file = file;
        }

        public File getFile()
        {
            return m_file;
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
        if( MAIN.equals( target ) )
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
        writeContainer( "", writer, def );
    }

    protected void writeContainer( final String pad, final Writer writer, final Definition def )
        throws IOException
    {
        writer.write( "\n\n" + pad + "<container name=\"" + getName( def ) + "\">" );

        if( null != m_service )
        {
            writeService( pad + "  ", writer, m_service );
        }

        final String indent = pad + "  ";
        writer.write( "\n\n" + indent + "<classloader>" );
        boolean standalone = (null == m_target);
        writeClasspath( writer, def, indent + "  ", standalone );
        writer.write( "\n" + indent + "</classloader>" );

        Identifiable[] components = 
          (Identifiable[]) m_content.toArray( new Identifiable[0] );
        for( int i=0; i<components.length; i++ )
        {
            Identifiable identifiable = components[i];
            if( identifiable instanceof Component )
            {
                writeComponent( indent, writer, (Component) identifiable );
            }
            else if( identifiable instanceof Include )
            {
                writeInclude( indent, writer, (Include) identifiable );
            }
        }

        writer.write( "\n\n" + pad + "</container>\n" );
    }

    private void writeComponent( final String pad, final Writer writer, final Component component )
        throws IOException
    {
        writer.write( "\n\n" + pad + "<component name=\"" + component.getName() 
          + "\" class=\"" + component.getClassname() + "\"" );

        if( null != component.getProfile() )
        {
            writer.write( " profile=\"" + component.getProfile() + "\"" );
        }

        if( !component.getActivation() )
        {
            writer.write( " activation=\"false\" " );
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
                    writeContext( pad + "  ", writer, (Context) child );
                }
                else if( child instanceof Dependencies )
                {
                    writeDependencies( pad + "  ", writer, (Dependencies) child );
                }
                else if( child instanceof Parameters )
                {
                    writeParameters( pad + "  ", writer, (Parameters) child );
                }
                else if( child instanceof Configuration )
                {
                    writeConfiguration( pad + "  ", writer, (Configuration) child );
                }
                else
                {
                    final String error = 
                      "Component declaration contains an unrecognized class: "
                      + child.getClass().getName();
                    throw new IllegalStateException( error );
                }
            }
            writer.write( "\n" + pad + "</component>" );
        }
    }

    private void writeConfiguration( final String pad, final Writer writer, final Configuration config )
        throws IOException
    {
        File file = config.getFile();
        if( null == file )
        {
            final String error = 
              "Missing file attribute in configuration declaration.";
            throw new BuildException( error );
        }
        if( !file.exists() )
        {
            final String error = 
              "Missing configuration file [" 
              + file
              + "] does not exist.";
            throw new BuildException( error );
        }
        if( file.isDirectory() )
        {
            final String error = 
              "Configuration file [" 
              + file
              + "] referes to a directory.";
            throw new BuildException( error );
        }
        writer.write( "\n" + pad + "<configuration/>" ); 
    }


    private void writeContext( final String pad, final Writer writer, final Context context )
        throws IOException
    {
        writer.write( "\n" + pad + "<context" ); 
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
                writeEntry( pad + "  ", writer, entries[i] );
            }  
            writer.write( "\n    </context>" ); 
        }
    }

    private void writeEntry( final String pad, final Writer writer, final Entry entry )
        throws IOException
    {
        writer.write( 
          "\n" + pad + "<entry key=\"" + entry.getKey() + "\"" );

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
                    writeParam( pad + "  ", writer, params[i] );
                } 
                writer.write( "\n" + pad + "</entry>" ); 
            }
        }
    }

    private void writeInclude( final String pad, final Writer writer, final Include include )
        throws IOException
    {
        writer.write( 
          "\n\n" + pad + "<include name=\"" 
          + include.getName() + "\" artifact=\"" 
          + include.getArtifact() + "\"/>" );
    }

    private void writeService( final String pad, final Writer writer, final Service service )
        throws IOException
    {
        final String lead = "\n" + pad;
        writer.write( "\n" );
        writer.write( lead + "<services>" );
        writer.write( lead + "  <service type=\"" + service.getType() + "\">" );
        writer.write( lead + "    <source>" + service.getSource() + "</source>" );
        writer.write( lead + "  </service>" );
        writer.write( lead + "</services>" );
    }

    private void writeParam( final String pad, final Writer writer, final Param param )
        throws IOException
    {
        if( null == param.getClassname() )
        {
            writer.write( "\n" + pad + "<param>" );
        }
        else
        {
            writer.write( "\n" + pad + "<param class=\"" + param.getClassname() + "\">" );
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
                    writeParam( pad + "  ", writer, parameters[i] );
                } 
                writer.write( "\n" + pad + "</param>" ); 
            }
        }
    }

    private void writeDependencies( final String pad, final Writer writer, final Dependencies deps )
        throws IOException
    {
        writer.write( "\n" + pad + "<dependencies>" );
        Dependency[] dependencies = deps.getDependencies();
        for( int i=0; i<dependencies.length; i++ )
        {
            writeDependency( pad + "  ", writer, dependencies[i] );
        } 
        writer.write( "\n" + pad + "</dependencies>" );
    }

    private void writeDependency( final String pad, final Writer writer, final Dependency dep )
        throws IOException
    {
        writer.write( 
          "\n" + pad + "<dependency key=\"" 
          + dep.getKey() + "\" source=\"" 
          + dep.getSource() + "\"/>" );
    }

    private void writeParameters( final String pad, final Writer writer, final Parameters parameters )
        throws IOException
    {
        writer.write( "\n" + pad + "<parameters>" );
        Parameter[] params = parameters.getParameters();
        for( int i=0; i<params.length; i++ )
        {
            writeParameter( pad + "  ", writer, params[i] );
        } 
        writer.write( "\n" + pad + "</parameters>" );
    }

    private void writeParameter( final String pad, final Writer writer, final Parameter param )
        throws IOException
    {
        writer.write( 
          "\n" + pad + "<parameter name=\"" 
          + param.getName() + "\" value=\"" 
          + param.getValue() + "\"/>" );
    }

}
