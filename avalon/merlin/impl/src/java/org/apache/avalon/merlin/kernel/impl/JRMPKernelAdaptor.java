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


package org.apache.avalon.merlin.kernel.impl;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;

import mx4j.adaptor.rmi.jrmp.JRMPAdaptor;
import mx4j.tools.naming.NamingService;

import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * RMIAdaptor for the JMX Server established by the merlin kernel.
 *
 */
public class JRMPKernelAdaptor extends AbstractLogEnabled implements Startable 
{
    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final int m_port;
    private final MBeanServer m_server;
    private final NamingService m_naming;
    private final JRMPAdaptor m_adapter = new JRMPAdaptor();
    private final ObjectName m_namingName;
    private final ObjectName m_name;


    //--------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------

   /**
    * Creation of a new adapter.
    * @param logger the assigned logging channel
    * @param server the mbean server
    */
    public JRMPKernelAdaptor( Logger logger, MBeanServer server ) 
      throws Exception
    {
        this( logger, server, 1099 );
    }

   /**
    * Creation of a new adapter.
    * @param logger the assigned logging channel
    * @param server the mbean server
    * @param port the port on which the adapter is conected
    */
    public JRMPKernelAdaptor( Logger logger, MBeanServer server, int port ) 
      throws Exception
    {
        super.enableLogging( logger );

        m_server = server;
        m_port = port;

        m_namingName = 
          new ObjectName( "JMXServer:name=naming,type=rmiregistry" );
        m_name = 
          new ObjectName( "JMXServer:name=adaptor,protocol=JRMP" );

        m_naming = new NamingService( m_port );
        m_server.registerMBean( m_naming, m_namingName );

        m_adapter.setJNDIName("jrmp");
        m_adapter.setPort( m_port );
        m_adapter.putJNDIProperty( 
          Context.INITIAL_CONTEXT_FACTORY, 
          "com.sun.jndi.rmi.registry.RegistryContextFactory" );
        m_adapter.putJNDIProperty(
          Context.PROVIDER_URL, 
          "rmi://localhost:" + m_port );

        m_server.registerMBean( m_adapter, m_name );
    }

   /**
    * Start the adapter.
    * @exception Exception is an adapter startup error occurs
    */
    public void start() throws Exception 
    {
        //
        // Create and start the naming service
        //

        getLogger().info( "starting jmx" );
        getLogger().debug( "starting naming service" );
        m_naming.start();

        //
        // Optionally, you can specify the JNDI properties,
        // instead of having in the classpath a jndi.properties file
        //

        getLogger().debug( "starting jrmp adapter" );
        m_adapter.start();
    }

   /**
    * Stop the adapter.
    * @exception Exception is an adapter shutdown error occurs
    */
    public void stop() throws Exception 
    {
        getLogger().debug( "stopping jrmp adapter" );
        try
        {
            m_adapter.stop();
        }
        catch( Throwable e )
        {
            final String error = 
              "Ignoring error while attempting to stop adapter.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            getLogger().warn( message );
        }

        try
        {
            m_server.unregisterMBean( m_name );
        }
        catch( Throwable e )
        {
            final String error = 
              "Ignoring error while attempting to unregister adapter.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            getLogger().warn( message );
        }

        getLogger().debug( "stopping name service" );
        try
        {
            m_naming.stop();
        }
        catch( Throwable e )
        {
            final String error = 
              "Ignoring error while attempting to stop naming service.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            getLogger().warn( message );
        }

        try
        {
            m_server.unregisterMBean( m_namingName );
        }
        catch( Throwable e )
        {
            final String error = 
              "Ignoring error while attempting to unregister naming service.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            getLogger().warn( message );
        }

        getLogger().debug( "stopped" );

    }
}
