/*
 * Copyright 1997-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.console;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.Socket;

import java.util.TreeMap;
import java.util.Map;
import java.util.StringTokenizer;

class CommandInterpreter extends Thread
{
    static private final String WELCOME_MESSAGE =
    "Type 'help' for a list of available commands.\n\nlogin\n";

    private Socket m_Socket;
    private boolean m_Login;
    private BufferedWriter m_Output;
    private BufferedReader m_Input;
    static final private Map m_Commands;

    static
    {
        m_Commands = new TreeMap();
        m_Commands.put( "echo", new EchoCmd() );
        m_Commands.put( "exit", new ExitCmd() );
        m_Commands.put( "help", new HelpCmd() );
        m_Commands.put( "login", new LoginCmd() );
        m_Commands.put( "threads", new ThreadCmd() );
        m_Commands.put( "", new DummyCmd() );
    }

    static public Map getCommands()
    {
        return m_Commands;
    }

    CommandInterpreter( Socket socket )
        throws IOException
    {
        m_Socket = socket;
    }

    public void run()
    {
        boolean running = true;
        welcomeMessage();
        while( running )
        {
            try
            {
                if( ! m_Login )
                    execute( "login" );
                m_Login = true; //LoginCmd throws an exception if login fails.
                execute( waitForCommandLine() );
            } catch( InterruptedException e )
            {
                running = false;
            } catch( Exception e )
            {
                errorMessage( e.getMessage() );
            }
        }
        try
        {
            m_Socket.close();
        } catch( IOException e )
        {
        }
    }


    private void execute( String cmdline )
        throws Exception
    {
        String command = parseCommand( cmdline );
        ConsoleCommand cmd = (ConsoleCommand) m_Commands.get( command );
        if( cmd != null )
        {
            String[] args = parseArguments( cmdline );
            cmd.execute( getInput(), getOutput(), args );
        }
        else
            errorMessage( "Unknown command. 'help' for available commands." );

    }


    private String waitForCommandLine()
        throws IOException
    {
        String cmdline = m_Input.readLine().trim();
        return cmdline;
    }

    private String parseCommand( String cmdline )
    {
        StringTokenizer st = new StringTokenizer( cmdline, " ", false );
        if( st.hasMoreTokens() )
            return st.nextToken();
        else
            return "";
    }

    private String[] parseArguments( String cmdline )
    {
        StringTokenizer st = new StringTokenizer( cmdline, " ", false );
        if( st.hasMoreTokens() )
            st.nextToken();  // remove Command portion;
        String[] args = new String[ st.countTokens() ];
        for( int i=0 ; st.hasMoreTokens() ; i++ )
            args[i] = st.nextToken();
        return args;
    }

    private void welcomeMessage()
    {
        try
        {
            getOutput().write( WELCOME_MESSAGE );
            getOutput().newLine();
            getOutput().flush();
        } catch( IOException e )
        {
            e.printStackTrace( System.out );
        }
    }

    private void errorMessage( String message )
    {
        try
        {
            getOutput().write( message );
            getOutput().newLine();
            getOutput().flush();
        } catch( IOException e )
        {
            e.printStackTrace( System.out );
        }
    }

    private BufferedWriter getOutput()
        throws IOException
    {
        if( m_Output == null )
            openOut();
        return m_Output;
    }

    private BufferedReader getInput()
        throws IOException
    {
        if( m_Input == null )
            openIn();
        return m_Input;
    }

    private void openOut()
        throws IOException
    {
        m_Output = new BufferedWriter( new OutputStreamWriter( m_Socket.getOutputStream() ) );
    }

    private void openIn()
        throws IOException
    {
        m_Input = new BufferedReader( new InputStreamReader( m_Socket.getInputStream() ) );
    }

}

