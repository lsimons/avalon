/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.jmx.mx4j;

import java.io.File;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;

import org.apache.avalon.jmx.spi.AbstractJMXComponentRegistrationManager;

import org.apache.avalon.util.i18n.ResourceManager;
import org.apache.avalon.util.i18n.Resources;

import mx4j.adaptor.rmi.jrmp.JRMPAdaptorMBean;
import mx4j.log.Log;
import mx4j.util.StandardMBeanProxy;

/**
 * A component manager using the MX4J implementation of JMX.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $
 *
 * @avalon.component name="jmx-mx4j-manager" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.jmx.ComponentRegistrationManager"
 */
public class MX4JComponentRegistrationManager extends AbstractJMXComponentRegistrationManager 
    implements Contextualizable, Configurable
{
    private static final Resources REZ = 
      ResourceManager.getPackageResources( MX4JComponentRegistrationManager.class );
    private static final String DEFAULT_NAMING_FACTORY =
        "com.sun.jndi.rmi.registry.RegistryContextFactory";
    private static final String DEFAULT_HTTPADAPTER_HOST = "localhost";
    private static final int DEFAULT_HTTPADAPTER_PORT = 
      Integer.getInteger( "merlin.adapter.http", 8082 ).intValue();

    private String m_host;
    private int m_port;
    private boolean m_rmi;
    private File m_homeDir;
    private String m_stylesheetDir;
    private String m_namingFactory;
    private String m_password;
    private String m_username;

   /*
    public MX4JComponentRegistrationManager(
      final Context context, final Configuration configuration, ServiceManager manager )
    {
    }
    */

    /**
     * @avalon.entry key="urn:avalon:home" type="java.io.File"
     */
    public void contextualize( Context context ) throws ContextException
    {
        m_homeDir = ( File ) context.get( "urn:avalon:home" );
    }

    public void configure( final Configuration configuration ) throws ConfigurationException
    {
        m_host = configuration.getChild( "manager-adaptor-host" ).getValue(
            DEFAULT_HTTPADAPTER_HOST );

        m_port = configuration.getChild( "manager-adaptor-port" ).getValueAsInteger(
            DEFAULT_HTTPADAPTER_PORT );

        //This is for backwards compatability with old-style
        //RI JMX implementation
        m_port = configuration.getChild( "port" ).getValueAsInteger( m_port );

        getLogger().debug( "MX4J HTTP listener port: " + m_port );

        m_rmi = configuration.getChild( "enable-rmi-adaptor" ).getValueAsBoolean( false );

        m_namingFactory = configuration.getChild( "rmi-naming-factory" ).getValue(
            DEFAULT_NAMING_FACTORY );

        final String stylesheets = 
          configuration.getChild( "stylesheets-dir" ).getValue( null );
        if ( null != stylesheets )
        {
            m_stylesheetDir = new File( m_homeDir, stylesheets ).getAbsolutePath();
        }

        /*
         * <user>
         *  <name>user</name>
         *  <password>passwd</password>
         * </user>
         */
        final Configuration userConfig = configuration.getChild( "user" );
        m_username = userConfig.getChild( "name" ).getValue( null );
        m_password = userConfig.getChild( "password" ).getValue( null );
    }

    public void initialize() throws Exception
    {
        super.initialize();

        final MBeanServer mBeanServer = getMBeanServer();

        startHttpAdaptor( mBeanServer );

        if ( m_rmi )
        {
            startRMIAdaptor( mBeanServer );
        }
    }

    public void dispose()
    {
        final MBeanServer mBeanServer = getMBeanServer();

        stopHttpAdaptor( mBeanServer );

        if ( m_rmi )
        {
            stopRMIAdaptor( mBeanServer );
        }

        super.dispose();
    }

    private void startHttpAdaptor( final MBeanServer mBeanServer ) throws Exception
    {
        final ObjectName adaptorName = new ObjectName( "Http:name=HttpAdaptor" );
        mBeanServer.createMBean( "mx4j.adaptor.http.HttpAdaptor", adaptorName, null );
        mBeanServer.setAttribute( adaptorName, new Attribute( "Host", m_host ) );
        mBeanServer.setAttribute( adaptorName, new Attribute( "Port", new Integer( m_port ) ) );

        if ( null != m_username )
        {
            configureAuthentication( mBeanServer, adaptorName );
        }

        configureProcessor( mBeanServer, adaptorName );

        // starts the server
        mBeanServer.invoke( adaptorName, "start", null, null );
    }

    private void configureProcessor( final MBeanServer mBeanServer, final ObjectName adaptorName ) throws
        Exception
    {
        final ObjectName processorName = new ObjectName( "Http:name=XSLTProcessor" );
        mBeanServer.createMBean( "mx4j.adaptor.http.XSLTProcessor", processorName, null );
        mBeanServer.setAttribute( adaptorName, new Attribute( "ProcessorName", processorName ) );

        if ( null != m_stylesheetDir )
        {
            final Attribute stylesheetDir = new Attribute( "File", m_stylesheetDir );
            mBeanServer.setAttribute( processorName, stylesheetDir );
        }

        final Attribute useCache = new Attribute( "UseCache", Boolean.FALSE );
        mBeanServer.setAttribute( processorName, useCache );
    }

    private void configureAuthentication( final MBeanServer mBeanServer,
                                          final ObjectName adaptorName ) throws
        InstanceNotFoundException, MBeanException, ReflectionException, AttributeNotFoundException,
        InvalidAttributeValueException
    {
        // add user names
        mBeanServer.invoke( adaptorName, "addAuthorization", new Object[]
                            {m_username, m_password}
                            , new String[]
                            {"java.lang.String", "java.lang.String"} );

        // use basic authentication
        mBeanServer.setAttribute( adaptorName, new Attribute( "AuthenticationMethod", "basic" ) );
    }

    private void startRMIAdaptor( final MBeanServer server ) throws Exception
    {
        // Create and start the naming service
        final ObjectName naming = new ObjectName( "Naming:type=rmiregistry" );
        server.createMBean( "mx4j.tools.naming.NamingService", naming, null );
        server.invoke( naming, "start", null, null );

        // Create the JRMP adaptor
        final ObjectName adaptor = new ObjectName( "Adaptor:protocol=JRMP" );
        server.createMBean( "mx4j.adaptor.rmi.jrmp.JRMPAdaptor", adaptor, null );
        JRMPAdaptorMBean mbean = ( JRMPAdaptorMBean ) StandardMBeanProxy.create( JRMPAdaptorMBean.class,
            server, adaptor );
        // Set the JNDI name with which will be registered
        mbean.setJNDIName( "jrmp" );
        mbean.putJNDIProperty( javax.naming.Context.INITIAL_CONTEXT_FACTORY, m_namingFactory );
        // Register the JRMP adaptor in JNDI and start it
        mbean.start();
    }

    private void stopHttpAdaptor( final MBeanServer server )
    {
        stopJMXMBean( server, "Http:name=HttpAdaptor" );
    }

    private void stopRMIAdaptor( final MBeanServer server )
    {
        // stop the JRMP adaptor
        stopJMXMBean( server, "Adaptor:protocol=JRMP" );
        // stop the naming service
        stopJMXMBean( server, "Naming:type=rmiregistry" );
    }

    protected MBeanServer createMBeanServer() throws Exception
    {
        MX4JLoggerAdapter.setLogger( getLogger() );
        Log.redirectTo( new MX4JLoggerAdapter() );
        return MBeanServerFactory.createMBeanServer( "Merlin" );
    }

    private void stopJMXMBean( final MBeanServer mBeanServer, final String name )
    {
        try
        {
            final ObjectName objectName = new ObjectName( name );
            mBeanServer.invoke( objectName, "stop", null, null );
        }
        catch ( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.jmxmbean.dispose", name );
            getLogger().error( message, e );
        }
    }
}
