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

package org.apache.avalon.util.env ;


import java.io.File ;
import java.io.FileReader ;
import java.io.IOException ;
import java.io.PrintWriter ;
import java.io.BufferedReader ;
import java.io.InputStreamReader ;

import java.util.Properties ;
import java.util.Enumeration ;


/**
 * Encapsulates operating system and shell specific access to environment 
 * variables.
 * 
 * TODO cleanup exception handling which is now in an odd state (constructors)
 * need to be added and or changed.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.2 $
 */
public class Env extends Properties
{
    /** os.name System property */
    public static final String OSNAME = System.getProperty( "os.name" ) ;
    /** user.name System property */
    public static final String USERNAME = System.getProperty( "user.name" ) ;

    /** the user's platform specific shell executable */
    private static String s_shell = null ;
    

    /**
     * Creates a snapshot of the current shell environment variables for a user.
     * 
     * @throws EnvAccessException if there is an error accessing the environment
     */
    public Env() throws EnvAccessException
    {
        Properties l_props = getEnvVariables() ;
        Enumeration l_list = l_props.propertyNames() ;
        
        while ( l_list.hasMoreElements() )
        {
            String l_key = ( String ) l_list.nextElement() ;
            setProperty( l_key, l_props.getProperty( l_key ) ) ;
        }
    }


    /**
     * Gets the value of a shell environment variable.
     * 
     * @param a_name the name of variable 
     * @return the String representation of an environment variable value
     * @throws Exception if there is a problem accessing the environment 
     */
    public static String getEnvVariable( String a_name )
        throws EnvAccessException
    {
        String l_osName = System.getProperty( "os.name" ) ;
        
        if ( isUnix() )
        {
            return getUnixShellVariable( a_name ) ;
        }
        else if ( -1 != l_osName.indexOf( "Windows" ) ) 
        {
              if ( null == s_shell )
              {
                if ( -1 != l_osName.indexOf( "98" ) || 
                  -1 != l_osName.indexOf( "95" ) )
                {
                    s_shell = "command.exe" ;
                }
                else
                {
                    s_shell = "cmd.exe" ;
                }
              }
            return getWindowsShellVariable( a_name ) ;
        }
        
        throw new EnvAccessException( a_name, 
            "Unrecognized operating system: " + l_osName ) ;
    }

    
    /**
     * Checks to see if the operating system is a UNIX variant.
     * 
     * @return true of the OS is a UNIX variant, false otherwise 
     */
    public static boolean isUnix()
    {
        if ( -1 != OSNAME.indexOf( "Linux" )          || 
             -1 != OSNAME.indexOf( "SunOS" )          ||
             -1 != OSNAME.indexOf( "Solaris" )        ||
             -1 != OSNAME.indexOf( "MPE/iX" )         ||
             -1 != OSNAME.indexOf( "AIX" )            ||
             -1 != OSNAME.indexOf( "FreeBSD" )        ||
             -1 != OSNAME.indexOf( "Irix" )           ||
             -1 != OSNAME.indexOf( "Digital Unix" )   ||
             -1 != OSNAME.indexOf( "HP-UX" )          ||
             -1 != OSNAME.indexOf( "Mac OS X" ) )
        {
            return true ;
        }
        
        return false ;
    }
    
    
    /**
     * Checks to see if the operating system is a Windows variant.
     * 
     * @return true of the OS is a Windows variant, false otherwise 
     */
    public static boolean isWindows()
    {
        if ( -1 != OSNAME.indexOf( "Windows" ) ) 
        {
            return true ;
        }
        
        return false ;
    }
    
    
    /**
     * Gets all environment variables within a Properties instance where the 
     * key is the environment variable name and value is the value of the 
     * property.
     * 
     * @return the environment variables and values as Properties
     */
    public static Properties getEnvVariables() throws EnvAccessException
    {
        if ( isUnix() )
        {
            return getUnixShellVariables() ;
        }
        
        if ( isWindows() )
        {
            return getWindowsShellVariables() ;
        }
        
        throw new EnvAccessException(  
            new UnsupportedOperationException( "Environment operations not "
            + "supported on unrecognized operatings system" ) ) ;
    }

    
    /**
     * Gets the user's shell executable.
     * 
     * @return the shell executable for the user
     * @throws EnvAccessException the there is a problem accessing shell 
     * information
     */
    public static String getUserShell() throws EnvAccessException
    {
        if ( -1 != OSNAME.indexOf( "Mac OS X" ) )
        {
            return getMacUserShell() ;
        }
        
        if ( isUnix() )
        {
            return getUnixUserShell() ;
        }
        
        if ( isWindows() )
        {
            return getWindowsUserShell() ;
        }
        
        throw new EnvAccessException( 
            new UnsupportedOperationException( "Environment operations not "
                + "supported on unrecognized operatings system" ) ) ;
    }


    // ------------------------------------------------------------------------
    // Private UNIX Shell Operations
    // ------------------------------------------------------------------------

    
    /**
     * Gets the default login shell used by a mac user.
     *
     * @return the Mac user's default shell as referenced by cmd: 
     *      'nidump passwd /'
     */
    private static String getMacUserShell() throws EnvAccessException
    {
        Process l_proc = null ;
        BufferedReader l_in = null ;
        
        if ( null != s_shell )
        {
            return s_shell ;
        }

        try
        {
            String l_entry = null ;
            String [] l_args = { "nidump", "passwd", "/" } ;
            l_proc = Runtime.getRuntime().exec( l_args ) ;
            l_in = new BufferedReader( 
                    new InputStreamReader( l_proc.getInputStream() ) ) ;
            
            while ( null != ( l_entry = l_in.readLine() ) )
            {
                // Skip entries other than the one for this username
                if ( ! l_entry.startsWith( USERNAME ) ) 
                {
                    continue ;
                }
        
                // Get the shell part of the passwd entry
                int l_index = l_entry.lastIndexOf( ':' ) ;

                if ( l_index == -1 )
                {
                    throw new EnvAccessException( 
                        "passwd database contains malformed user entry for " 
                        + USERNAME ) ;
                }
        
                s_shell = l_entry.substring( l_index + 1 ) ;
                return s_shell ;
            }
            
            l_proc.waitFor() ;
            l_in.close() ;
        }
        catch( Throwable t )
        {
            t.printStackTrace() ;
            throw new EnvAccessException( t ) ;
        }
        finally
        {
            if ( l_proc != null )
            {    
                l_proc.destroy() ;
            }
            
            try
            {
                if ( null != l_in )
                {    
                    l_in.close() ;
                }
            }
            catch( IOException e )
            {
                // do nothing
            }
        }
        
        throw new EnvAccessException( "User " + USERNAME 
            + " does not seem to exist in the passwd database" ) ;
    }
    
    
    /**
     * Gets the default login shell used by a unix user.
     *
     * @return the UNIX user's default shell as referenced in /etc/passwd
     */
    private static String getUnixUserShell()
        throws EnvAccessException
    {
        if ( null != s_shell )
        {
            return s_shell ;
        }
        
        File l_etcpasswd = new File( "/etc/passwd" ) ;
        if ( l_etcpasswd.exists() && l_etcpasswd.canRead() )
        {
            BufferedReader l_in = null ;
    
            try 
            {
                String l_entry = null ;
                l_in = new BufferedReader( new FileReader( l_etcpasswd ) ) ;
        
                while ( null != ( l_entry = l_in.readLine() ) )
                {
                    // Skip entries other than the one for this username
                    if ( ! l_entry.startsWith( USERNAME ) ) 
                    {
                        continue ;
                    }
        
                    // Get the shell part of the passwd entry
                    int l_index = l_entry.lastIndexOf( ':' ) ;

                    if ( l_index == -1 )
                    {
                        throw new EnvAccessException( 
                            "/etc/passwd contains malformed user entry for " 
                            + USERNAME ) ;
                    }
        
                    s_shell = l_entry.substring( l_index + 1 ) ;
                    return s_shell ;
                }
            } 
            catch ( IOException e )
            {
                throw new EnvAccessException( e ) ;
            }
    
            throw new EnvAccessException( "User " + USERNAME 
                + " does not seem to exist in /etc/passwd" ) ;
        }
    
        throw new EnvAccessException( "Don't know what to do with"
            + " a UNIX system without a readable /etc/passwd file" ) ;
    }


    /**
     * Gets a UNIX shell environment parameter by forking the user's default 
     * shell based on /etc/passwd and running echo on the environment variable
     * within the shell.  This should work on all UNIX shells like sh, ksh, csh,
     * zsh and bash accross all the supported platforms.
     * 
     * @param a_name the name of the variable accessed
     * @return the value of the variable 
     * @throws EnvAccessException if there is an error accessing the value
     */
    private static String getUnixShellVariable( String a_name )
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
            String [] l_args = { getUnixUserShell(), "-t" } ;
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
        
        // Check that we exited normally before returning an invalid output
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
     * Adds a set of Windows variables to a set of properties.
     */
    private static Properties getUnixShellVariables()
        throws EnvAccessException
    {
        Process l_proc = null ;
        Properties l_props = new Properties() ;

        // Read from process here
        BufferedReader l_in = null ;
    
        // Write to process here
        PrintWriter l_out = null ;

        // fire up the shell and get echo'd results on stdout
        try
        {
            String [] l_args = { getUnixUserShell(), "-t" } ;
            l_proc = Runtime.getRuntime().exec( l_args ) ;
            l_out = new PrintWriter( l_proc.getOutputStream() ) ;
            l_out.println( "env" ) ;
            l_out.flush() ;
            l_in = new BufferedReader( 
                    new InputStreamReader( l_proc.getInputStream() ) ) ;
            
            String l_line = null ;
            while ( null != ( l_line = l_in.readLine() ) ) 
            {
                int l_idx = l_line.indexOf( '=') ;
                
                if ( -1 == l_idx )
                {    
                    System.err.println( "Skipping line - could not find '=' in"
                            + " line: '" + l_line + "'" ) ;
                    continue ;
                }
                
                String l_name = l_line.substring( 0, l_idx ) ;
                String l_value = l_line.substring( l_idx + 1, l_line.length() );
                l_props.setProperty( l_name, l_value ) ;
            }
            
            l_proc.waitFor() ;
            l_in.close() ;
            l_out.close() ;
        }
        catch( Throwable t )
        {
            t.printStackTrace() ;
            throw new EnvAccessException( "NA", t ) ;
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
        
        // Check that we exited normally before returning an invalid output
        if ( 0 != l_proc.exitValue() )
        {
            throw new EnvAccessException( "Environment process failed "
                    + " with non-zero exit code of " + l_proc.exitValue() ) ;
        }
        
        return l_props ;
    }
    
    
    // ------------------------------------------------------------------------
    // Private Windows Shell Operations
    // ------------------------------------------------------------------------

    
    /**
     * Gets the shell used by the Windows user.
     * 
     * @return the shell: cmd.exe or command.exe.
     */
    private static String getWindowsUserShell()
    {
        if ( null != s_shell )
        {
            return s_shell ;
        }
        
        if ( -1 != OSNAME.indexOf( "98" ) || -1 != OSNAME.indexOf( "95" ) )
        {
            s_shell = "command.exe" ;
            return s_shell ;
        }

        s_shell = "cmd.exe" ;
        return s_shell ;
    }
    
    
    /**
     * Adds a set of Windows variables to a set of properties.
     */
    private static Properties getWindowsShellVariables()
        throws EnvAccessException
    {
        String l_line = null ;
        Process l_proc = null ;
        BufferedReader l_in = null ;
        Properties l_props = new Properties() ;
        StringBuffer l_cmd = new StringBuffer() ;

        // build the command based on the shell used: cmd.exe or command.exe 
        if ( -1 != OSNAME.indexOf( "98" ) || -1 != OSNAME.indexOf( "95" ) )
        {
            l_cmd.append( "command.exe /C SET" ) ;
        }
        else
        {
            l_cmd.append( "cmd.exe /C SET" ) ;
        }
        
        // fire up the shell and get echo'd results on stdout
        try
        {
            l_proc = Runtime.getRuntime().exec( l_cmd.toString() ) ;
            l_in = new BufferedReader( 
                    new InputStreamReader( l_proc.getInputStream() ) ) ;
            while ( null != ( l_line = l_in.readLine() ) ) 
            {
                int l_idx = l_line.indexOf( '=') ;
                
                if ( -1 == l_idx )
                {    
                    System.err.println( "Skipping line - could not find '=' in"
                            + " line: '" + l_line + "'" ) ;
                    continue ;
                }
                
                String l_name = l_line.substring( 0, l_idx ) ;
                String l_value = l_line.substring( l_idx + 1, l_line.length() );
                l_props.setProperty( l_name, l_value ) ;
            }
            
            l_proc.waitFor() ;
            l_in.close() ;
        }
        catch( Throwable t )
        {
            t.printStackTrace() ;
            throw new EnvAccessException( t ) ;
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
        
        if ( 0 != l_proc.exitValue() )
        {
            throw new EnvAccessException( "Environment process failed"
                    + " with non-zero exit code of " + l_proc.exitValue() ) ;
        }
        
        return l_props ;
    }


    /**
     * Gets the value for a windows command shell environment variable.
     * 
     * @param a_name the name of the variable
     * @return the value of the variable
     * @throws EnvAccessException if there is an error accessing the value
     */
    private static String getWindowsShellVariable( String a_name )
        throws EnvAccessException
    {
        String l_value = null ;
        Process l_proc = null ;
        BufferedReader l_in = null ;

        StringBuffer l_cmd = new StringBuffer( getWindowsUserShell() ) ;
        l_cmd.append( " /C echo %" ) ;
        l_cmd.append( a_name ) ;
        l_cmd.append( '%' ) ;

        // fire up the shell and get echo'd results on stdout
        try
        {
            l_proc = Runtime.getRuntime().exec( l_cmd.toString() ) ;
            l_in = new BufferedReader( 
                    new InputStreamReader( l_proc.getInputStream() ) ) ;
            l_value = l_in.readLine() ;
            l_proc.waitFor() ;
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
