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

import java.io.BufferedReader ;
import java.io.IOException;
import java.io.InputStreamReader ;

/**
 * Encapsulates operating system specific access to environment variables.
 * 
 * @todo Add more methods that allow access to path and library parameters in a
 * platform neutral fashion.
 * 
 * @see List of operating system specific System property values 
 * <a href="http://www.tolstoy.com/samizdat/sysprops.html">here</a>.
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.1 $
 */
public class Env
{
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
             -1 != l_osName.indexOf( "Solaris" )        ||
             -1 != l_osName.indexOf( "MPE/iX" )         ||
             -1 != l_osName.indexOf( "AIX" )            ||
             -1 != l_osName.indexOf( "FreeBSD" )        ||
             -1 != l_osName.indexOf( "Irix" )           ||
             -1 != l_osName.indexOf( "Digital Unix" )   ||
             -1 != l_osName.indexOf( "HP-UX" ) )
        {
            return getUnixVariable( a_name ) ;
        }
        else if ( -1 != l_osName.indexOf( "Windows" ) ) 
        {
            return getWindowsVariable( a_name ) ;
        }
        
        throw new EnvAccessException( a_name, "Unrecognized operating system: " 
                + l_osName ) ;
    }
    
    
    /**
     * Gets a UNIX shell environment parameter by forking a call to echo.  This
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
        BufferedReader l_in = null ;
        StringBuffer l_cmd = new StringBuffer() ;
        String l_osName = System.getProperty( "os.name" ) ;
        
        l_cmd.append( "echo $" ) ;
        l_cmd.append( a_name ) ;

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
            if ( l_value.trim().equals( "" ) )
            {
                return null ;
            }
            
            return l_value ;
        }
        
        throw new EnvAccessException( a_name, "Environment process failed with "
                + "non-zero exit code of " + l_proc.exitValue() ) ;
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

        // build the the command based on the shell used: cmd.exe or command.exe 
        if ( -1 != l_osName.indexOf( "98" ) || -1 != l_osName.indexOf( "95" ) )
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
        
        throw new EnvAccessException( a_name, "Environment process failed with "
                + "non-zero exit code of " + l_proc.exitValue() ) ;
    }
}


