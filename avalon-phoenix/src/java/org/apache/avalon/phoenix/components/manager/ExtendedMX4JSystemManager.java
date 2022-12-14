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

package org.apache.avalon.phoenix.components.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * This component is responsible for managing phoenix instance.
 * Support Flexible jmx helper mbean configuration.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author Peter Donald
 * @author <a href="mailto:Huw@mmlive.com">Huw Roberts</a>
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class ExtendedMX4JSystemManager
    extends AbstractJMXManager
    implements Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ExtendedMX4JSystemManager.class );

    private static final String DEFAULT_NAMING_FACTORY =
        "com.sun.jndi.rmi.profile.RegistryContextFactory";

    private Configuration m_configuration;
    private Map m_mBeanScripters;

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final String namingFactory =
            configuration.getChild( "rmi-naming-factory" ).getValue( null );
        if( null != namingFactory )
        {
            getLogger().warn( "Deprecated." );
            System.setProperty( "java.naming.factory.initial", namingFactory );
        }
        else if( null == System.getProperty( "java.naming.factory.initial" ) )
        {
            System.setProperty( "java.naming.factory.initial", DEFAULT_NAMING_FACTORY );
        }

        m_configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        super.initialize();

        m_mBeanScripters = new HashMap();
        final Configuration[] scripters =
            m_configuration.getChildren( "mbean" );
        for( int i = 0; i < scripters.length; i++ )
        {
            createMBeanScripter( scripters[ i ] );
        }
    }

    public void dispose()
    {
        final Iterator scripterNames = m_mBeanScripters.keySet().iterator();
        while( scripterNames.hasNext() )
        {
            destroyMBeanScripter( (String)scripterNames.next() );
        }
        m_mBeanScripters.clear();
        m_mBeanScripters = null;

        super.dispose();
    }

    private void createMBeanScripter( final Configuration scripterConf )
        throws Exception
    {
        final MBeanScripter scripter =
            new MBeanScripter( getMBeanServer(), scripterConf );
        try
        {
            scripter.startup();

            m_mBeanScripters.put( scripter.getName(), scripter );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.jmxmbean.initialize", scripter.getName() );
            getLogger().error( message, e );
            throw e;
        }
    }

    private void destroyMBeanScripter( final String name )
    {
        final MBeanScripter scripter =
            (MBeanScripter)m_mBeanScripters.get( name );
        try
        {
            scripter.shutdown();
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.jmxmbean.dispose", scripter.getName() );
            getLogger().error( message, e );
        }
    }

    protected MBeanServer createMBeanServer()
        throws Exception
    {
        MX4JLoggerAdapter.setLogger( getLogger() );
        mx4j.log.Log.redirectTo( new MX4JLoggerAdapter() );
        return MBeanServerFactory.createMBeanServer( "Phoenix" );
    }
}
