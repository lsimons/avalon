/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.merlin.env ;

import java.io.File ;
import java.io.FileReader ;
import java.io.IOException ;
import java.io.PrintWriter ;
import java.io.BufferedReader ;
import java.io.InputStreamReader ;

/**
 * Encapsulates operating system specific access to environment variables.
 * 
 * @todo Add more methods that allow access to path and library parameters in
 * a platform neutral fashion.
 * 
 * @see List of operating system specific System property values 
 * <a href="http://www.tolstoy.com/samizdat/sysprops.html">here</a>.
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.2 $
 */
public class Env
{
    /** The shell environment to run under */
    private static String m_shell = null ;

    /**
     * Gets the value of a shell environment variable.
     * 
     * @param a_name the name of variable 
     * @return the String representation of an environment variable value
     * @throws Exception if there is a problem accessing the environment 
     */
    public static String getVariable( String a_name )
        throws EnvAccessException
    {
        String l_osName = System.getProperty( "os.name" ) ;
        
        if ( -1 != l_osName.indexOf( "Linux" )          || 
             -1 != l_osName.indexOf( "SunOS" )          ||
             -1 != l_osName.indexOf( "Solaris" )        ||
             -1 != l_osName.indexOf( "MPE/iX" )         ||
             -1 != l_osName.indexOf( "AIX" )            ||
             -1 != l_osName.indexOf( "FreeBSD" )        ||
             -1 != l_osName.indexOf( "Irix" )           ||
             -1 != l_osName.indexOf( "Digital Unix" )   ||
             -1 != l_osName.indexOf( "HP-UX" ) )
        {
            if ( null == m_shell )
            {
                m_shell = getUnixUserShell( a_name ) ;
            }

            return getUnixVariable( a_name ) ;
        }
        else if ( -1 != l_osName.indexOf( "Windows" ) ) 
        {
              if ( null == m_shell )
              {
                if ( -1 != l_osName.indexOf( "98" ) || 
                  -1 != l_osName.indexOf( "95" ) )
                {
                    m_shell = "command.exe" ;
                }
                else
                {
                    m_shell = "cmd.exe" ;
                }
              }
            return getWindowsVariable( a_name ) ;
        }
        
        throw new EnvAccessException( a_name, 
            "Unrecognized operating system: " + l_osName ) ;
    }
    
    
    /**
     * Gets the default login shell used by a unix user.
     *
     * @param a_varName the var accessed used for exception constructor only
     * @return the UNIX user's default shell as referenced in /etc/passwd
     */
    private static String getUnixUserShell( String a_varName )
        throws EnvAccessException
    {
        File l_etcpasswd = new File( "/etc/passwd" ) ;
    
        if ( l_etcpasswd.exists() && l_etcpasswd.canRead() )
        {
            String l_username = System.getProperty( "user.name" ) ;
            BufferedReader l_in = null ;
    
            try 
            {
                String l_entry = null ;
                l_in = new BufferedReader( new FileReader( l_etcpasswd ) ) ;
        
                while( null != ( l_entry = l_in.readLine() ) )
                {
                    // Skip entries other than the one for this username
                    if ( ! l_entry.startsWith( l_username ) ) 
                    {
                        continue ;
                    }
        
                    // Get the shell part of the passwd entry
                    int l_index = l_entry.lastIndexOf( ':' ) ;
                    if ( l_index == -1 )
                    {
                        throw new EnvAccessException( a_varName,
                            "/etc/passwd contains malformed user entry for " 
                            + l_username ) ;
                    }
        
                    return l_entry.substring( l_index + 1 ) ;
                }
            } 
            catch ( IOException e )
            {
                throw new EnvAccessException( a_varName, e ) ;
            }
    
            throw new EnvAccessException( a_varName, "User " + l_username 
                    + " does not seem to exist in /etc/passwd" ) ;
        }
    
        throw new EnvAccessException( a_varName, "Don't know what to do with"
            + " a UNIX system without a readable /etc/passwd file" ) ;
    }

    /**
     * Gets a UNIX shell environment parameter by forking a call to echo. This
     * should work on all UNIX shells like sh, ksh, csh, zsh and bash.
     * 
     * @param a_name the name of the variable accessed
     * @return the value of the variable 
     * @throws EnvAccessException if there is an error accessing the value
     */
    private static String getUnixVariable( String a_name )
        throws EnvAccessException
    {
        String l_value = null ;
        Process l_proc = null ;

        // Read from process here
        BufferedReader l_in = null ;
	
        // Write to process here
        PrintWriter l_out = null ;

        StringBuffer l_cmd = new StringBuffer() ;
        String l_osName = System.getProperty( "os.name" ) ;
        
        l_cmd.append( "echo $" ) ;
        l_cmd.append( a_name ) ;

        // fire up the shell and get echo'd results on stdout
        try
        {
            l_proc = Runtime.getRuntime().exec( l_cmd.toString() ) ;
            String [] l_args = { m_shell, "-t" } ;
            l_proc = Runtime.getRuntime().exec( l_args ) ;
            l_out = new PrintWriter( l_proc.getOutputStream() ) ;
            l_out.println( l_cmd.toString() ) ;
            l_out.flush() ;
            l_proc.waitFor() ;
            l_in = new BufferedReader( 
                    new InputStreamReader( l_proc.getInputStream() ) ) ;
            l_value = l_in.readLine() ;
            l_in.close() ;
            l_out.close() ;
        }
        catch( Throwable t )
        {
            t.printStackTrace() ;
            throw new EnvAccessException( a_name, t ) ;
        }
        finally
        {
            l_proc.destroy() ;
            
            try
            {
                if ( null != l_in )
                {    
                    l_in.close() ;
                }

                if ( null != l_out )
                {    
                    l_out.close() ;
                }
            }
            catch( IOException e )
            {
                
            }
        }
        
        if ( 0 == l_proc.exitValue() )
        {
            // Handle situations where the env property does not exist.
            if ( l_value.trim().equals( "" ) )
            {
                return null ;
            }
            
            return l_value ;
        }
        
        throw new EnvAccessException( a_name, "Environment process failed "
                + " with non-zero exit code of " + l_proc.exitValue() ) ;
    }
    
    
    /**
     * Gets the value for a windows command shell environment variable.
     * 
     * @param a_name the name of the variable
     * @return the value of the variable
     * @throws EnvAccessException if there is an error accessing the value
     */
    private static String getWindowsVariable( String a_name )
        throws EnvAccessException
    {
        String l_value = null ;
        Process l_proc = null ;
        BufferedReader l_in = null ;
        StringBuffer l_cmd = new StringBuffer() ;
        String l_osName = System.getProperty( "os.name" ) ;

        // build the command based on the shell used: cmd.exe or command.exe 
        if ( -1 != l_osName.indexOf( "98" ) || 
	     -1 != l_osName.indexOf( "95" ) )
        {
            l_cmd.append( "command.exe /C echo %" ) ;
        }
        else
        {
            l_cmd.append( "cmd.exe /C echo %" ) ;
        }
        
        l_cmd.append( a_name ) ;
        l_cmd.append( '%' ) ;

        // fire up the shell and get echo'd results on stdout
        try
        {
            l_proc = Runtime.getRuntime().exec( l_cmd.toString() ) ;
            l_proc.waitFor() ;
            l_in = new BufferedReader( 
                    new InputStreamReader( l_proc.getInputStream() ) ) ;
            l_value = l_in.readLine() ;
            l_in.close() ;
        }
        catch( Throwable t )
        {
            throw new EnvAccessException( a_name, t ) ;
        }
        finally
        {
            l_proc.destroy() ;
            
            try
            {
                if ( null != l_in )
                {    
                    l_in.close() ;
                }
            }
            catch( IOException e )
            {
                
            }
        }
        
        if ( 0 == l_proc.exitValue() )
        {
            // Handle situations where the env property does not exist.
            if ( l_value.startsWith( "%") && l_value.endsWith( "%" ) )
            {
                return null ;
            }
            
            return l_value ;
        }
        
        throw new EnvAccessException( a_name, "Environment process failed"
                + " with non-zero exit code of " + l_proc.exitValue() ) ;
    }
}


