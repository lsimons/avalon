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

    public static class Component
    {
        private String m_name;
        private String m_classname;

        public Component()
        {
        }

        public void setName( final String name )
        {
            m_name = name;
        }

        public void setClass( final String classname )
        {
            m_classname = classname;
        }

        public String getName()
        {
            return m_name;
        }

        public String getClassname()
        {
            return m_classname;
        }
    }

    private String m_target;
    private String m_container;
    private List m_components = new ArrayList();

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
        m_components.add( component );
        return component;
    }

    public void init()
    {
        super.init();
        super.setType( BLOCK );
    }

    protected void writePlugin( final Writer writer, final Definition def )
        throws IOException
    {
        final Info info = def.getInfo();

        writer.write( "\n\n<container name=\"" + getName( def ) + "\">" );
        writer.write( "\n\n  <classloader>" );
        boolean standalone = (null == m_target);
        writeClasspath( writer, def, "    ", standalone );
        writer.write( "\n  </classloader>" );
        writer.write( "\n" );

        Component[] components = 
          (Component[]) m_components.toArray( new Component[0] );
        for( int i=0; i<components.length; i++ )
        {
            Component component = components[i];
            writeComponent( writer, component );
        }

        writer.write( "\n</container>\n" );
    }

    private void writeComponent( final Writer writer, final Component component )
        throws IOException
    {
        writer.write( 
          "\n  <component name=\"" + component.getName() + "\" class=\""
          + component.getClassname() + "\"/>\n" );
    }
}
