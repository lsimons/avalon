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

package org.apache.avalon.phoenix.tools.punit;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.WrapperComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.phoenix.containerkit.lifecycle.ResourceProvider;
import org.apache.excalibur.instrument.manager.NoopInstrumentManager;
import org.apache.excalibur.instrument.InstrumentManager;

/**
 * PUnitResourceProvider
 *
 * @author Paul Hammant
 */
public class PUnitResourceProvider
    implements ResourceProvider
{
    private final ServiceManager m_serviceManager;
    private final Configuration m_configuration;
    private final Logger m_logger;

    /**
     * PUnitResourceProvider
     * @param serviceManager The service manager
     * @param configuration The configuration
     */
    public PUnitResourceProvider( final ServiceManager serviceManager,
                                  final Configuration configuration,
                                  final Logger logger )
    {
        m_serviceManager = serviceManager;
        m_configuration = configuration;
        m_logger = logger;
    }

    /**
     * Create an object
     *
     * @param object The object
     * @return The returned object
     * @throws Exception If a problm
     */
    public Object createObject( final Object object )
        throws Exception
    {
        return object;
    }

    /**
     * Create a Logger
     * @param object The object to make a logger for
     * @return The Logger
     * @throws Exception If a problem
     */
    public Logger createLogger( final Object object ) throws Exception
    {
        return m_logger;
    }

    /**
     * Create a new InstrumentMaanger object for component.
     *
     * @param entry the entry
     * @return a new InstrumentManager object for component
     * @throws Exception if unable to create resource
     */
    public InstrumentManager createInstrumentManager( Object entry )
        throws Exception
    {
        return new NoopInstrumentManager();
    }

    /**
     * Create a name for this components instrumentables.
     *
     * @param entry the entry
     * @return the String to use as the instrumentable name
     * @throws Exception if unable to create resource
     */
    public String createInstrumentableName( Object entry )
        throws Exception
    {
        return "punit";
    }

    /**
     * Create some Context
     * @param object For this object
     * @return the context
     * @throws Exception If a problem
     */
    public Context createContext( final Object object )
        throws Exception
    {
        return new PUnitBlockContext();
    }

    /**
     * Create a Comp Mgr
     * @param object For this object
     * @return The comp mgr
     * @throws Exception If a problem
     */
    public ComponentManager createComponentManager( final Object object ) throws Exception
    {
        return new WrapperComponentManager( m_serviceManager );
    }

    /**
     * Create a Service Manager
     * @param object For this object
     * @return The service manager
     * @throws Exception If a problem
     */
    public ServiceManager createServiceManager( final Object object )
        throws Exception
    {
        return m_serviceManager;
    }

    /**
     * Create some Configuration
     *
     * @param object For this object
     * @return The configuration
     * @throws Exception If a problem
     */
    public Configuration createConfiguration( final Object object )
        throws Exception
    {
        return m_configuration;
    }

    /**
     * Create Some parameters
     * @param object For this object
     * @return The parameters
     * @throws Exception If a problem
     */
    public Parameters createParameters( final Object object )
        throws Exception
    {
        final Configuration configuration = createConfiguration( object );
        return Parameters.fromConfiguration( configuration );
    }
}
