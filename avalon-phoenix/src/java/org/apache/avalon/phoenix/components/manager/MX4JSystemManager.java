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

        final Configuration userConfig = configuration.getChild( "user" );
        m_username = userConfig.getChild( "name" ).getValue( null );
        m_password = userConfig.getChild( "password" ).getValue( null );
    }

    public void initialize()
        throws Exception
    {
        super.initialize();

        final MBeanServer mBeanServer = getMBeanServer();

        if( m_http )
        {
            startHttpAdaptor( mBeanServer );
        }

        if( m_rmi )
        {
            startRMIAdaptor( mBeanServer );
        }
    }

    public void dispose()
    {
        final MBeanServer mBeanServer = getMBeanServer();

        if( m_http )
        {
            stopHttpAdaptor( mBeanServer );
        }
        if( m_rmi )
        {
            stopRMIAdaptor( mBeanServer );
        }

        super.dispose();
    }

    private void startHttpAdaptor( final MBeanServer mBeanServer )
        throws Exception
    {
        final ObjectName adaptorName = new ObjectName( "Http:name=HttpAdaptor" );
        mBeanServer.createMBean( "mx4j.adaptor.http.HttpAdaptor", adaptorName, null );
        mBeanServer.setAttribute( adaptorName, new Attribute( "Host", m_host ) );
        mBeanServer.setAttribute( adaptorName, new Attribute( "Port", new Integer( m_port ) ) );

        if( null != m_username )
        {
            configureAuthentication( mBeanServer, adaptorName );
        }

        configureProcessor( mBeanServer, adaptorName );

        // starts the server
        mBeanServer.invoke( adaptorName, "start", null, null );
    }

    private void configureProcessor( final MBeanServer mBeanServer,
                                     final ObjectName adaptorName )
        throws Exception
    {
        final ObjectName processorName = new ObjectName( "Http:name=XSLTProcessor" );
        mBeanServer.createMBean( "mx4j.adaptor.http.XSLTProcessor", processorName, null );
        mBeanServer.setAttribute( adaptorName, new Attribute( "ProcessorName", processorName ) );

        if( null != m_stylesheetDir )
        {
            final Attribute stylesheetDir = new Attribute( "File", m_stylesheetDir );
            mBeanServer.setAttribute( processorName, stylesheetDir );
        }

        final Attribute useCache =
            new Attribute( "UseCache", Boolean.FALSE );
        mBeanServer.setAttribute( processorName, useCache );
    }

    private void configureAuthentication( final MBeanServer mBeanServer, final ObjectName adaptorName ) throws InstanceNotFoundException, MBeanException, ReflectionException, AttributeNotFoundException, InvalidAttributeValueException
    {
        // add user names
        mBeanServer.invoke( adaptorName,
                            "addAuthorization",
                            new Object[]{m_username, m_password},
                            new String[]{"java.lang.String", "java.lang.String"} );

        // use basic authentication
        mBeanServer.setAttribute( adaptorName,
                                  new Attribute( "AuthenticationMethod", "basic" ) );
    }

    private void stopHttpAdaptor( final MBeanServer server )
    {
        stopJMXMBean( server, "Http:name=HttpAdaptor" );
    }

    private void startRMIAdaptor( final MBeanServer server )
        throws Exception
    {
        // Create and start the naming service
        final ObjectName naming = new ObjectName( "Naming:type=rmiregistry" );
        server.createMBean( "mx4j.tools.naming.NamingService", naming, null );
        server.invoke( naming, "start", null, null );

        // Create the JRMP adaptor
        final ObjectName adaptor = new ObjectName( "Adaptor:protocol=JRMP" );
        server.createMBean( "mx4j.adaptor.rmi.jrmp.JRMPAdaptor", adaptor, null );
        final JRMPAdaptorMBean mbean =
            (JRMPAdaptorMBean)StandardMBeanProxy.create( JRMPAdaptorMBean.class,
                                                         server,
                                                         adaptor );
        // Set the JNDI name with which will be registered
        mbean.setJNDIName( "jrmp" );
        mbean.putJNDIProperty( javax.naming.Context.INITIAL_CONTEXT_FACTORY,
                               m_namingFactory );
        //mbean.putJNDIProperty( javax.naming.Context.PROVIDER_URL, "rmi://localhost:1099" );
        // Register the JRMP adaptor in JNDI and start it
        mbean.start();
    }

    private void stopRMIAdaptor( final MBeanServer server )
    {
        // stop the JRMP adaptor
        stopJMXMBean( server, "Adaptor:protocol=JRMP" );
        // stop the naming service
        stopJMXMBean( server, "Naming:type=rmiregistry" );
    }

    protected MBeanServer createMBeanServer()
        throws Exception
    {
        MX4JLoggerAdapter.setLogger( getLogger() );
        Log.redirectTo( new MX4JLoggerAdapter() );
        return MBeanServerFactory.createMBeanServer( "Phoenix" );
    }

    private void stopJMXMBean( final MBeanServer mBeanServer, final String name )
    {
        try
        {
            final ObjectName objectName = new ObjectName( name );
            mBeanServer.invoke( objectName, "stop", null, null );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "jmxmanager.error.jmxmbean.dispose", name );
            getLogger().error( message, e );
        }
    }
}
