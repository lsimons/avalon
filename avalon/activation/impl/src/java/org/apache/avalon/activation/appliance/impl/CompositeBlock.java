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

package org.apache.avalon.activation.appliance.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.ApplianceException;
import org.apache.avalon.activation.appliance.BlockContext;
import org.apache.avalon.activation.appliance.Home;
import org.apache.avalon.composition.data.ServiceDirective;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.framework.logger.Logger;

/**
 * The DefaultBlock is responsible for the management 
 * of the assembly of the subsidiary appliances, the coordination
 * of the deployment, decommissioning and eventual dissassembly of 
 * contained appliances, and the overall management of a containment 
 * context.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2004/01/08 16:48:12 $
 */
public class CompositeBlock extends AbstractBlock implements Home
{
    //-------------------------------------------------------------------
    // immmutable state
    //-------------------------------------------------------------------

    private final BlockContext m_context;

    private final Object m_proxy;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

   /**
    * Creation of a new block.
    *
    * @param context the block context
    * @exception ApplianceException if a block creation error occurs
    */
    CompositeBlock( BlockContext context )
      throws ApplianceException
    {
        super( context );
        m_context = context;

        //
        // build the default proxy
        //

        try
        {
            final Logger log = context.getLogger().getChildLogger( "proxy" );
            final ContainmentModel model = context.getContainmentModel();
            final BlockInvocationHandler handler = 
              new BlockInvocationHandler( log, this );
            final Class[] classes = getInterfaceClasses();
            
            m_proxy = Proxy.newProxyInstance( 
              model.getClassLoaderModel().getClassLoader(),
              classes,
              handler );
        }
        catch( Throwable e )
        {
            final String error = 
              "Composite service establishment failure in block: " + this;
            throw new ApplianceException( error, e );
        }
    }

    //-------------------------------------------------------------------
    // Home
    //-------------------------------------------------------------------

    /**
     * Resolve a object to a value.
     *
     * @return the resolved object
     * @throws Exception if an error occurs
     */
    public Object resolve() throws Exception
    {
        return m_proxy;
    }

    /**
     * Release an object
     *
     * @param instance the object to be released
     */
    public void release( Object instance )
    {
        //
        // container proxy is a singleton reference
        //
    }

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

    private Class[] getInterfaceClasses() throws Exception
    {
        ContainmentModel model = m_context.getContainmentModel();
        ClassLoader loader = model.getClassLoaderModel().getClassLoader();
        ArrayList list = new ArrayList();
        ServiceDirective[] services = model.getExportDirectives();
        for( int i=0; i<services.length; i++ )
        {
            final ServiceDirective service = services[i];
            final String classname = service.getReference().getClassname();
            try
            {
                Class clazz = loader.loadClass( classname );
                list.add( clazz );
            }
            catch( ClassNotFoundException cnfe )
            {
                final String error = 
                   "Class not found: [" + classname
                   + "] in block [" + this
                   + "] with classloader content: \n";
                StringBuffer buffer = new StringBuffer( error );
                if( loader instanceof URLClassLoader ) 
                {
                    URL[] urls = ((URLClassLoader)loader).getURLs();
                    for( int j=0; j<urls.length; j++ )
                    {
                        buffer.append( "\n  " + urls[j].toString() );
                    }
                }
                String message = buffer.toString();
                throw new ApplianceException( message );
            }
        }
        return (Class[]) list.toArray( new Class[0] );
    }

   /**
    * This makes a dynamic proxy for an object.  The object can be represented
    * by one, some or all of it's interfaces.
    */
    final class BlockInvocationHandler
        implements InvocationHandler
    {
        private final CompositeBlock m_block;
        private final Logger m_logger;

       /**
        * Create a proxy invocation handler.
        *
        * @param block the underlying block implementation
        * @exception if an invocation handler establishment error occurs
        */
        protected BlockInvocationHandler( final Logger logger, final CompositeBlock block )
            throws Exception
        {
            if( block == null ) throw new NullPointerException( "block" );
            m_block = block;
            m_logger = logger;
        }

        /**
         * Invoke the specified method on underlying object.
         * This is called by the proxy object.
         *
         * @param proxy the proxy object
         * @param method the method invoked on proxy object
         * @param args the arguments supplied to method
         * @return the return value of method
         * @throws Throwable if an error occurs
         */
        public Object invoke( final Object proxy,
                final Method method,
                final Object[] args )
                throws Throwable
        {
            if( proxy == null ) throw new NullPointerException( "proxy" );
            if( method == null ) throw new NullPointerException( "method" );

            //
            // if the invocation is against java.lang.Object then
            // delegate the operation to the block
            //

            if( method.getDeclaringClass().equals( java.lang.Object.class ) )
            {
                m_logger.debug( "invocation: " +  method.getName() );
                try
                {
                    return method.invoke( m_block, args );
                }
                catch( InvocationTargetException e )
                {
                    final String error = 
                      "Unexpected delegation error on java.lang.Object";
                    throw new ApplianceException( error, e.getTargetException() );
                }
            }

            //
            // otherwise we are delegating to an implementation component
            //

            final ContainmentModel model = m_context.getContainmentModel();
            ServiceDirective service = 
              model.getExportDirective( method.getDeclaringClass() );

            String path = service.getPath();
            Appliance provider = (Appliance) m_block.locate( path );
            m_logger.debug( "delegating: " +  method.getName() );

            //
            // resolve the service object from the appliance
            // and delegate the invocation to that provider
            //

            try
            {
                Object object = provider.resolve();
                return method.invoke( object, args );
            }
            catch( UndeclaredThrowableException e )
            {
                Throwable cause = e.getUndeclaredThrowable();
                if( cause != null ) throw cause;
                final String error = 
                  "Delegation error raised by component: " + m_block;
                throw new ApplianceException( error, e );
            }
            catch( InvocationTargetException e )
            {
                Throwable cause = e.getTargetException();
                if( cause != null ) throw cause;
                final String error = 
                  "Delegation error raised by component: " + m_block;
                throw new ApplianceException( error, e );
            }
            catch( Throwable e )
            {
                final String error =
                  "Composite service resolution failure for the class: '" 
                  + method.getDeclaringClass()
                  + "' for operation: '" + method.getName()
                  + "' in appliance: " + m_block;
                throw new ApplianceException( error, e );
            }
        }
    }
}
