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

import junit.framework.TestCase;
import org.apache.avalon.fortress.impl.role.Role2MetaInfoManager;
import org.apache.avalon.fortress.impl.role.FortressRoleManager;
import org.apache.avalon.framework.logger.ConsoleLogger;

/**
 * Role2MetaInfoManagerTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS Revision: 1.1 $
 */
public class Role2MetaInfoManagerTestCase extends AbstractMetaInfoManagerTestCase
{
    public Role2MetaInfoManagerTestCase( String name )
    {
        super( name );
    }

    public void setUp()
    {
        FortressRoleManager roles = new FortressRoleManager( null, this.getClass().getClassLoader() );
        roles.enableLogging( new ConsoleLogger( ConsoleLogger.LEVEL_INFO ) );
        roles.initialize();
        m_manager = new Role2MetaInfoManager(roles);
    }

    public void testRole2MetaInfoManager() throws Exception
    {
        String[] roles = new String[] {"org.apache.avalon.excalibur.datasource.DataSourceComponent"};

        checkRole( "jdbc-datasource", roles,
            "org.apache.avalon.excalibur.datasource.JdbcDataSource",
            "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );
        checkRole( "j2ee-datasource", roles,
            "org.apache.avalon.excalibur.datasource.J2eeDataSource",
            "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );
        if ( isInformixClassExists() )
        {
            checkRole( "informix-datasource", roles,
                "org.apache.avalon.excalibur.datasource.InformixDataSource",
                "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );
        }

        roles[0] = "org.apache.avalon.excalibur.monitor.Monitor";

        checkRole( "monitor", roles,
            "org.apache.avalon.excalibur.monitor.ActiveMonitor",
            "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );
        checkRole( "passive-monitor", roles,
            "org.apache.avalon.excalibur.monitor.PassiveMonitor",
            "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );

        roles[0] = "org.apache.excalibur.xml.xpath.XPathProcessor";

        checkRole( "xalan-xpath", roles,
            "org.apache.excalibur.xml.xpath.XPathProcessorImpl",
            "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );
        checkRole( "jaxpath", roles,
            "org.apache.excalibur.xml.xpath.JaxenProcessorImpl",
            "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );

        roles[0] = "org.apache.excalibur.source.SourceResolver";

        checkRole( "resolver", roles,
            "org.apache.excalibur.source.impl.SourceResolverImpl",
            "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );

        roles[0] = "org.apache.excalibur.xml.dom.DOMParser";

        checkRole( "parser", roles,
            "org.apache.excalibur.xml.impl.JaxpParser",
            "org.apache.avalon.fortress.impl.handler.PerThreadComponentHandler" );
        checkRole( "xerces-parser", roles,
            "org.apache.excalibur.xml.impl.XercesParser",
            "org.apache.avalon.fortress.impl.handler.FactoryComponentHandler" );
    }
}