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

package org.apache.avalon.magic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.net.URL;

import java.util.StringTokenizer;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Interpreter;

public class ScriptFacade extends AbstractLogEnabled    
    implements PluginFacade
{
    static public final String SCRIPT_FILE_NAME = "magic.bsh";
    
    private PluginContext m_Context;
    private String m_Script;
    private String m_Classname;
    private Plugin m_Plugin;
    
    ScriptFacade( PluginContext context )
        throws IOException
    {
        m_Context = context;
        m_Script = readScript( m_Context.getPluginDir() );
        m_Classname = findName();
        m_Context.setPluginClassname( m_Classname );
    }
    
    String getScript()
    {
        return m_Script;
    }

    public PluginContext getPluginContext()
    {
        return m_Context;
    }

    public String getPluginClassname()
    {
        return m_Classname;
    }
    
    /** Returns the Plugin instance of that this PluginDelegate refers to.
     */
    public Plugin resolve()
        throws EvalError, IOException, ArtifactException
    {
        if( m_Plugin != null )
            return m_Plugin;
            
        Interpreter.DEBUG = false;
        Interpreter bsh = new Interpreter();
        
        bsh.setStrictJava( true );
        bsh.setOut( System.out );
        bsh.setErr( System.err );
        
        BshClassManager classman = bsh.getClassManager();
        String pluginname = m_Context.getPluginName();
        Artifact thisArtifact = Artifact.resolve( m_Context, pluginname );
        System.out.println( thisArtifact );
        Artifact[] deps = thisArtifact.getDependencies();
        System.out.println( "Deps: " + deps.length  );
        
        URL[] urls = Util.getURLs( deps );
        for( int i = 0 ; i < urls.length ; i++ )
        {
            if( getLogger().isDebugEnabled() )
                getLogger().debug( "Adding to BeanShell classpath:" + urls[i] );
            classman.addClassPath( urls[i] );
        }
                    
        if( ! classman.classExists( m_Classname ) )
        {
            bsh.eval( m_Script );
        }
        String expr1 = "import org.apache.avalon.magic.Plugin;  Plugin plugin = new " + m_Classname + "();";
        bsh.eval( expr1 );
        m_Plugin = (Plugin) bsh.get( "plugin" );
        return m_Plugin;
    }

    public void invalidate()
    {
        m_Plugin = null;
    }
    
    String readScript( File dir )
        throws IOException
    {
        File scriptFile = new File( dir, SCRIPT_FILE_NAME );
        FileReader in = null;
        BufferedReader br = null;
        try
        {
            in = new FileReader( scriptFile );
            br = new BufferedReader( in );
            StringBuffer buf = new StringBuffer(1000);
            String line = br.readLine();
            while( line != null )
            {
                buf.append( line );
                buf.append( "\n" );
                line = br.readLine();
            }
            return buf.toString();
        } finally
        {
            if( in != null )
                in.close();
            if( br != null )
                br.close();
        }                
    }
    
    String findName()
        throws IllegalArgumentException
    {
        // TODO: Performance improvement possible by parsing a char[] instead.
        
        StringTokenizer st = new StringTokenizer( m_Script, " \n\r\t", false );
        while( st.hasMoreTokens() )
        {
            String token = st.nextToken();
            if( token.equals( "class" ) )
            {
                String classname = st.nextToken();
                if( classname.endsWith( "Plugin" ) )
                    return classname;
                else
                    throw new IllegalArgumentException( "Plugins must have 'Plugin' at the end of the name:" + classname );
            }
        }
        throw new IllegalArgumentException( "The plugin script does not contain a class.\n" + m_Script );
    }
} 
 
