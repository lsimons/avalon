package org.apache.merlin.magic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Interpreter;


public class ScriptFacade extends AbstractLogEnabled    
    implements PluginFacade
{
    static public final String SCRIPT_FILE_NAME = "build.bsh";
    
    private PluginContext m_Context;
    private String m_Script;
    private Plugin m_Plugin;
    
    ScriptFacade( PluginContext context )
    {
        m_Context = context;
    }
    
    String getScript()
    {
        return m_Script;
    }

    public PluginContext getPluginContext()
    {
        return m_Context;
    }

    /** Returns the Plugin instance of that this PluginDelegate refers to.
     */
    public Plugin resolve()
        throws EvalError, IOException
    {
        if( m_Plugin != null )
            return m_Plugin;
            
        m_Script = readScript( m_Context.getPluginDir() );
        String classname = findName( m_Script );
        m_Context.setPluginClassname( classname );
        classname = classname + "Plugin";
        
        Interpreter.DEBUG = false;
        Interpreter bsh = new Interpreter();
        
        bsh.setStrictJava( true );
        bsh.setOut( System.out );
        bsh.setErr( System.err );
        
        BshClassManager classman = bsh.getClassManager();
        
        getLogger().info( "Class: " + classname + "   --> " + classman.classExists( classname ) );
        if( ! classman.classExists( classname ) )
        {
            bsh.eval( m_Script );
        }
        String expr1 = "import org.apache.merlin.magic.Plugin;  Plugin plugin = new " + classname + "();";
        System.out.println( expr1 );
        bsh.eval( expr1 );
        m_Plugin = (Plugin) bsh.get( "plugin" );
        return m_Plugin;
    }

    public void invalidate()
    {
        m_Plugin = null;
    }
    
    private String readScript( File dir )
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
    
    private String findName( String script )
        throws IllegalArgumentException
    {
        // TODO: Performance improvement possible by parsing a char[] instead.
        
        StringTokenizer st = new StringTokenizer( script, " \n\r\t", false );
        while( st.hasMoreTokens() )
        {
            String token = st.nextToken();
            if( token.equals( "class" ) )
            {
                String classname = st.nextToken();
                if( classname.endsWith( "Plugin" ) )
                    return classname.substring( 0, classname.length() - 6);
                else
                    throw new IllegalArgumentException( "Plugins must have 'Plugin' at the end of the name:" + classname );
            }
        }
        throw new IllegalArgumentException( "The plugin script does not contain a class.\n" + script );
    }
} 
 
