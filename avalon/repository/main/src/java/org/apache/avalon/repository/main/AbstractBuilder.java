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

package org.apache.avalon.repository.main ;

import java.io.File;
import java.text.ParseException ;
import java.util.Map ;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException ;
import java.lang.reflect.Method ;

import javax.naming.directory.Attributes;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.repository.RepositoryRuntimeException;
import org.apache.avalon.repository.provider.Factory;
import org.apache.avalon.repository.provider.InitialContext;


/**
 * Application and component bootstrapper used to instantiate, and or invoke
 * Classes and their methods within newly constructed Repository ClassLoaders.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.4 $
 */
public abstract class AbstractBuilder
{
   /**
    * Load a factory class using a supplied classloader and factory classname.
    * @param classloader the classloader to load the class from
    * @param factory the factory classname
    * @return the factory class
    * @exception RepositoryException if a factory class loading error occurs
    */
    protected Class loadFactoryClass( ClassLoader classloader, String factory )
        throws RepositoryException
    {
        try
        {
            return classloader.loadClass( factory );
        }
        catch( ClassNotFoundException e )
        {
            final String error = 
              "Could not find the factory class: " + factory;
            throw new RepositoryException( error, e );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to load factory class: [" 
              + factory 
              + "].";
            throw new RepositoryException( error, e );
        }
    }

   /**
    * <p>Create a factory delegate using a supplied class and command line arguemnts.
    * The implementation will conduct an ordered search for a constructor matching
    * one of the four standard constructor patterns.</p>
    * <ul>
    * <li>[FactoryClass]( InitialContext context, ClassLoader loader )</li>
    * <li>[FactoryClass]( InitialContext context )</li>
    * <li>[FactoryClass]( ClassLoader loader )</li>
    * <li>[FactoryClass]( )</li>
    * </ul>
    * 
    * @param classloader the classloader
    * @param factory the the factory classname
    * @param context the inital repository context
    * @return the instantiated factory
    * @exception RepositoryException if a factory creation error occurs
    */
    protected Factory createDelegate( 
      ClassLoader classloader, String factory, InitialContext context ) 
      throws RepositoryException
    {

        if( null == classloader ) throw new NullPointerException( "classloader" );
        if( null == factory ) throw new NullPointerException( "factory" );
        if( null == context ) throw new NullPointerException( "context" );

        Class clazz = loadFactoryClass( classloader, factory );

        try
        {
            Constructor constructor = 
              clazz.getConstructor( 
                new Class[]{ InitialContext.class, ClassLoader.class } );
            return createFactory( 
              constructor, new Object[]{ context, classloader } );
        }
        catch( NoSuchMethodException e )
        {
            try
            {
                Constructor constructor = 
                  clazz.getConstructor( 
                    new Class[]{ InitialContext.class } );
                return createFactory( 
                  constructor, new Object[]{ context } );
            }
            catch( NoSuchMethodException ee )
            {
                try
                {
                    Constructor constructor = 
                      clazz.getConstructor( 
                        new Class[]{ ClassLoader.class } );
                    return createFactory( 
                      constructor, new Object[]{ classloader } );
                }
                catch( NoSuchMethodException eee )
                {
                    try
                    {
                        Constructor constructor = 
                          clazz.getConstructor( 
                            new Class[0] );
                        return createFactory( 
                          constructor, new Object[0] );
                    }
                    catch( NoSuchMethodException eeee )
                    {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append( "Supplied factory class [" );
                        buffer.append( clazz.getName() );
                        buffer.append( 
                          " ] does not implement a recognized constructor." );
                        throw new RepositoryException( buffer.toString() );
                    }
                }
            }
        }
    }

   /**
    * Instantiation of a factory instance using a supplied constructor 
    * and arguments.
    * 
    * @param constructor the factory constructor
    * @param args the constructor arguments
    * @return the factory instance
    * @exception RepositoryException if an instantiation error occurs
    */
    private Factory createFactory( 
      Constructor constructor, Object[] args ) 
      throws RepositoryException
    {
        Class clazz = constructor.getDeclaringClass();
        try
        {
            return (Factory) constructor.newInstance( args );
        }
        catch( Throwable e )
        {
            final String error = 
              "Error while attempting to instantiate the factory: [" 
              + clazz.getName() 
              + "]."; 
            throw new RepositoryException( error, e );
        }
    }
}
