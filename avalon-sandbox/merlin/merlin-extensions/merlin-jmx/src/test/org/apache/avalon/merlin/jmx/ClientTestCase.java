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

package org.apache.avalon.merlin.jmx;

import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.management.ObjectName;
import javax.management.MBeanServer;

import junit.framework.TestCase;

import mx4j.connector.RemoteMBeanServer;
import mx4j.connector.rmi.jrmp.JRMPConnector;
import mx4j.util.StandardMBeanProxy;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.CascadingException;


/**
 *
 * @author <a href="mailto:biorn_steedom@users.sourceforge.net">Simone Bordet</a>
 * @version $Revision: 1.1 $
 */
public class ClientTestCase extends TestCase
{
    private static final String FACTORY = 
      "com.sun.jndi.rmi.registry.RegistryContextFactory";
    private static final String PACKAGES = 
      "com.sun.jndi.rmi.registry";
    private static final String CONNECTION_URL = "rmi://localhost:1099";
    private static final String CONTEXT_NAME = "jrmp";
    private static final String PRINCIPAL = "";
    private static final String CREDENTIALS = "";

    private Logger m_logger;

    private MBeanServer m_server;

    private RemoteMBeanServer m_remote;

    private JRMPConnector m_connector;

    private MyRemoteService m_service;

    public ClientTestCase( )
    {
        this( "jmx" );
    }

    public ClientTestCase( String name )
    {
        super( name );
    }

    protected void setUp() throws Exception
    {
        m_logger = new ConsoleLogger();
        getLogger().info( "setup" );
        m_server = createJMXServer();
        m_remote = createRemoteMBeanServer();
    }

    public void tearDown() throws Exception
    {
        getLogger().info( "stop" );
        m_connector.close();
    }

    private MBeanServer createJMXServer() throws Exception
    {
        DefaultJMXServer server = new DefaultJMXServer();
        ContainerUtil.enableLogging( server, getLogger().getChildLogger( "server" ) );
        ContainerUtil.initialize( server );
        return server;
    }

    private RemoteMBeanServer createRemoteMBeanServer() throws Exception
    {
        m_connector = new JRMPConnector();
        try
        {
            Hashtable properties = new Hashtable();
            properties.put("java.naming.factory.initial", FACTORY );
            properties.put("java.naming.factory.url.pkgs", PACKAGES );
            properties.put("java.naming.provider.url", CONNECTION_URL );
            properties.put("java.naming.security.principal", PRINCIPAL );
            properties.put("java.naming.security.credentials", CREDENTIALS );
            m_connector.connect( CONTEXT_NAME, properties );
            return m_connector.getRemoteMBeanServer();
        }
        catch( Throwable e )
        {
            final String error =
              "Unable to establish connection.";
            throw new CascadingException( error, e );
        }
    }

    public void testMBeanRegistration() throws Exception
    {
        getLogger().info( "registering remote service" );
        ObjectName name = new ObjectName("examples:type=remote");
        MyRemoteServiceObject remote = new MyRemoteServiceObject();
        m_server.registerMBean( remote, name );

        getLogger().info( "starting remote service" );

        MyRemoteServiceObjectMBean managed = 
         (MyRemoteServiceObjectMBean)StandardMBeanProxy.create(
           MyRemoteServiceObjectMBean.class, m_server, name );
        managed.start();
        assertTrue( true );
    }

    /*
    public void testClientConnectionUpdate() throws Exception
    {
        InitialContext ctx = new  InitialContext();
        MyRemoteService service = (MyRemoteService)ctx.lookup( MyRemoteService.JNDI_NAME );
        service.sayHello( "hello" );
        assertTrue( true );
    }
    */

    //public void testSetup() throws Exception
    //{
    //    assertTrue( true );
    //}

    private Logger getLogger()
    {
        return m_logger;
    }
}
