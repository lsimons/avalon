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
package org.apache.avalon.fortress.impl.role.test;

import org.apache.avalon.fortress.impl.handler.FactoryComponentHandler;
import org.apache.avalon.fortress.impl.handler.PerThreadComponentHandler;
import org.apache.avalon.fortress.impl.handler.PoolableComponentHandler;
import org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler;
import org.apache.avalon.fortress.impl.role.ServiceMetaManager;
import org.apache.avalon.fortress.test.data.*;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.NullLogger;

/**
 * ServiceMetaManagerTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public class ServiceMetaManagerTestCase extends AbstractMetaInfoManagerTestCase
{
    public ServiceMetaManagerTestCase( String name )
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        m_manager = new ServiceMetaManager();
        ContainerUtil.enableLogging( m_manager, new NullLogger() );
        ContainerUtil.initialize( m_manager );
    }

    public void testTestRoles() throws Exception
    {
        String[] roles = new String[]{Role1.class.getName()};
        checkRole( "component1", roles, Component1.class.getName(), ThreadSafeComponentHandler.class.getName() );

        roles[0] = Role2.class.getName();
        checkRole( "component2", roles, Component2.class.getName(), PoolableComponentHandler.class.getName() );

        roles[0] = Role4.class.getName();
        checkRole( "component4", roles, Component4.class.getName(), FactoryComponentHandler.class.getName() );

        roles = new String[]
        {
            Role3.class.getName(),
            BaseRole.class.getName()
        };

        checkRole( "component3", roles, Component3.class.getName(), PerThreadComponentHandler.class.getName() );
    }
}
