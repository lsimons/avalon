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

package org.apache.avalon.merlin.jndi;

import javax.naming.*;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * This is an object factory that when given a reference for a service
 * object, will create an instance of the corresponding service.
 */
public class ServiceFactory implements ObjectFactory 
{
    private static final String MERLIN_KERNEL_LOADER_CLASSNAME = 
      "org.apache.avalon.merlin.kernel.impl.DefaultKernelLoader";

    private Object m_kernel;

   /**
    * Return a service instance if the supplied object is a reference
    * with the classname corresponding to the Service class, else null.
    *
    */
    public Object getObjectInstance(
     Object object, final Name name, Context context, final Hashtable environment ) throws Exception 
    {
        System.out.println("object: " + object );
        System.out.println("name: " + name );
        System.out.println("context: " + context );
        System.out.println("environment: " + environment );

        if( m_kernel == null )
        {
            m_kernel = bootstrapKernel( object );
        }

        return m_kernel;
    }

    private Object bootstrapKernel( Object object ) throws Exception
    {
        try
        {
            if( object == null )
            {
                throw new Exception( "object");
            }
            if( !( object instanceof Reference ) )
            {
                throw new Exception( "Cannot resolve JNDI bootstrap properties.");
            }

            Reference reference = (Reference) object;

            File home = getFile( "home", reference );
            File system = getFile( "system", reference );
            File kernel = getFile( "kernel", reference );
            URL block = getURL( "block", reference );

            System.out.println("HOME:   " + home );
            System.out.println("SYSTEM: " + system );
            System.out.println("BLOCK:  " + block );
            System.out.println("KERNEL: " + kernel );

            File config = null;
            try
            {
                config = getFile( "config", reference );
                System.out.println("CONFIG: " + config );
            }
            catch( Throwable e )
            {
                // optional
            }

            String debug = "INFO";
            try
            {
                debug = getString( "debug", reference );
                System.out.println("DEBUG: " + debug );
            }
            catch( Throwable e )
            {
                // optional
            }


            File lib = new File( system, "lib" );
            File shared = new File( lib, "shared" );
            File sys = new File( lib, "system" );
            URL[] targets = getJarFiles( shared );
            URL[] libs = getJarFiles( sys, targets );
            ClassLoader current = Thread.currentThread().getContextClassLoader();
            ClassLoader common = new URLClassLoader( targets, current );
            ClassLoader internal =  new URLClassLoader( libs, current );

            Map map = new Hashtable();

            //
            // build the kernel loader context
            //

            map.put( "urn:merlin:classloader.common", common );
            map.put( "urn:merlin:classloader.system", internal);
            map.put( "urn:merlin:home", home );
            map.put( "urn:merlin:system", system );
            map.put( "urn:merlin:kernel.profile", kernel );
            map.put( "urn:merlin:block.url", block );
            map.put( "urn:merlin:debug", debug );
            if( config != null )
            {
                map.put( "urn:merlin:block.config", config.toURL() );
            }

            return loadKernel( internal, map );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unexpected error while bootstrapping the kernel.";
            final String message = ExceptionHelper.packException( error, e );
            throw new Exception( message ); 
        }
    }

    private Object loadKernel( ClassLoader loader, Map map )
    {
        Thread.currentThread().setContextClassLoader( loader );
        Object kernelLoader = null;
        Class clazz;

        //
        // load the kernel loader class from the supplied classloader
        //

        try
        {
            clazz = loader.loadClass( MERLIN_KERNEL_LOADER_CLASSNAME );
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error during loader class creation.";
            throw new RuntimeException( error, e );
        }

        //
        // instantiate the loader
        //

        try
        {
            Constructor constructor = clazz.getConstructor( new Class[0] );
            kernelLoader = constructor.newInstance( new Object[0] );
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error during loader instantiation.";
            throw new RuntimeException( error, e );
        }

        //
        // create the kernel instance
        //

        try
        {
            Method method = kernelLoader.getClass().getMethod( "build", new Class[]{ Map.class } );
            return method.invoke( kernelLoader, new Object[]{ map } );
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error during kernel resolution.";
            throw new RuntimeException( error, e );         
        }
    }

    private static URL[] getJarFiles( File base )
    {
        if( base == null )
        {
            throw new NullPointerException( "base" );
        }
        return getJarFiles( base, new URL[0] );
    }

    private static URL[] getJarFiles( File base, URL[] urls )
    {
        if( base == null )
        {
            throw new NullPointerException( "base" );
        }
        List list = new ArrayList();
        for( int i=0; i<urls.length; i++ )
        {
            list.add( urls[i] );
        }
        populateJars( list, base );
        return (URL[]) list.toArray( new URL[0] );
    }

    private static void populateJars( List list, File base )
    {
        if( list == null )
        {
            throw new NullPointerException( "list" );
        }
        if( base == null )
        {
            throw new NullPointerException( "base" );
        }
        try
        {
            File[] files = base.listFiles();
            for( int i=0; i<files.length; i++ )
            {
                File file = files[i];
                if( file.getName().endsWith( ".jar" ) )
                {
                    list.add( file.toURL() );
                }
            }
        }
        catch( Throwable e )
        {
            final String error =
              "Unexpected error while scanning files in shared directory: " + base;
            throw new RuntimeException( error, e );
        }
    }


    private File getFile( String key, Reference reference ) throws Exception
    {
        RefAddr ref = reference.get( key );
        if( ref == null )
        {
            throw new Exception( "Missing resolve " + key + " ref.");
        }
        String value = (String) ref.getContent();
        if( value == null )
        {
            throw new Exception( "Null " + key + " ref value.");
        }
        return new File( value );
    }
   
    private URL getURL( String key, Reference reference ) throws Exception
    {
        RefAddr ref = reference.get( key );
        if( ref == null )
        {
            throw new Exception( "Missing resolve " + key + " ref.");
        }
        String value = (String) ref.getContent();
        if( value == null )
        {
            throw new Exception( "Null " + key + " ref value.");
        }
        return new URL( value );
    }    

    private String getString( String key, Reference reference ) throws Exception
    {
        RefAddr ref = reference.get( key );
        if( ref == null )
        {
            throw new Exception( "Missing resolve " + key + " ref.");
        }
        String value = (String) ref.getContent();
        if( value == null )
        {
            throw new Exception( "Null " + key + " ref value.");
        }
        return value;
    }    

}
