/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.avalon.fortress.util.test;

import junit.framework.TestCase;
import org.apache.avalon.fortress.ContainerManagerConstants;
import org.apache.avalon.fortress.util.ContextManager;
import org.apache.avalon.fortress.util.FortressConfig;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.manager.DefaultInstrumentManager;

/**
 * ContextManagerTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public class ContextManagerTestCase extends TestCase implements ContainerManagerConstants
{
    private ContextManager m_manager;
    private InstrumentManager m_instrManager;

    public ContextManagerTestCase( String name )
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        FortressConfig config = new FortressConfig( FortressConfig.createDefaultConfig() );
        config.setContainerConfiguration( "resource://org/apache/avalon/fortress/test/data/test1.xconf" );
        config.setLoggerManagerConfiguration( "resource://org/apache/avalon/fortress/test/data/test1.xlog" );

        m_instrManager = new DefaultInstrumentManager();
        ContainerUtil.enableLogging(m_instrManager, new ConsoleLogger());
        ContainerUtil.initialize(m_instrManager);
        config.setInstrumentManager(m_instrManager);

        m_manager = new ContextManager( config.getContext(), new ConsoleLogger() );
        m_manager.initialize();
    }

    public void testContextManager() throws Exception
    {
        final Context managerContext = m_manager.getContainerManagerContext();
        assertNotNull( managerContext );

        final ServiceManager serviceManager = (ServiceManager) managerContext.get( SERVICE_MANAGER );
        assertNotNull( serviceManager );

        final InstrumentManager instrumentManager =
                (InstrumentManager) serviceManager.lookup( InstrumentManager.ROLE );
        assertNotNull( instrumentManager );
        assertSame( m_instrManager, instrumentManager );
    }

    public void tearDown()
    {
        m_manager.dispose();
    }
}
