/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.launcher;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.HashMap;
import java.util.Map;

/**
 * PhoenixLoader is the class that bootstraps and sets up engine ClassLoader.
 * It also sets up a default policy that gives full permissions to engine code.
 *
 * @author Peter Donald
 */
public final class Main
{
    private static final String MAIN_CLASS =
        "org.apache.avalon.phoenix.frontends.CLIMain";

    private static Object c_frontend;

    /**
     * Main entry point for Phoenix.
     *
     * @param args the command line arguments
     * @throws Exception if an error occurs
     */
    public static final void main( final String[] args )
        throws Exception
    {
        final int exitCode =
            startup( args, new HashMap(), true );
        System.exit( exitCode );
    }

    /**
     * Method to call to startup Phoenix from an
     * external (calling) application. Protected to allow
     * access from DaemonLauncher.
     *
     * @param args the command line arg array
     * @param data a set of extra parameters to pass to embeddor
     * @param blocking false if the current thread is expected to return.
     *
     * @return the exit code which should be used to exit the JVM
     */
    protected static final int startup( final String[] args,
                                        final Map data,
                                        final boolean blocking )
    {
        int exitCode;
        try
        {
            //setup new Policy manager
            Policy.setPolicy( new FreeNEasyPolicy() );

            //Create engine ClassLoader
            final URL[] urls = LauncherUtils.getEngineClassPath();
            final URLClassLoader classLoader = new URLClassLoader( urls );

            data.put( "common.classloader", ClassLoader.getSystemClassLoader() );
            data.put( "container.classloader", classLoader );
            data.put( "phoenix.home", new File( LauncherUtils.findPhoenixHome() ) );

            //Setup context classloader
            Thread.currentThread().setContextClassLoader( classLoader );

            //Create main launcher
            final Class clazz = classLoader.loadClass( MAIN_CLASS );
            final Class[] paramTypes =
                new Class[]{args.getClass(), Map.class, Boolean.TYPE};
            final Method method = clazz.getMethod( "main", paramTypes );
            c_frontend = clazz.newInstance();

            //kick the tires and light the fires....
            final Integer integer = (Integer)method.invoke(
                c_frontend, new Object[]{args, data, new Boolean( blocking )} );
            exitCode = integer.intValue();
        }
        catch( final Exception e )
        {
            e.printStackTrace();
            exitCode = 1;
        }
        return exitCode;
    }

    /**
     * Method to call to shutdown Phoenix from an
     * external (calling) application. Protected to allow
     * access from DaemonLauncher.
     */
    protected static final void shutdown()
    {
        if( null == c_frontend )
        {
            return;
        }

        try
        {
            final Class clazz = c_frontend.getClass();
            final Method method = clazz.getMethod( "shutdown", new Class[ 0 ] );

            //Lets put this sucker to sleep
            method.invoke( c_frontend, new Object[ 0 ] );
        }
        catch( final Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            c_frontend = null;
        }
    }
}
