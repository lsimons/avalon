/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.avalon.fortress.impl.role;

import org.apache.avalon.framework.activity.Initializable;

/**
 * The Excalibur Role Manager is used for Excalibur Role Mappings.  All of
 * the information is hard-coded.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/02/07 16:08:12 $
 */
public class FortressRoleManager
    extends org.apache.avalon.fortress.impl.role.AbstractRoleManager
    implements Initializable
{
    /**
     * Default constructor--this RoleManager has no parent.
     */
    public FortressRoleManager()
    {
        this( null );
    }

    /**
     * Alternate constructor--this RoleManager has the specified
     * parent.
     *
     * @param parent  The parent <code>RoleManager</code>.
     */
    public FortressRoleManager( org.apache.avalon.fortress.RoleManager parent )
    {
        this( parent, null );
    }

    /**
     * Alternate constructor--this RoleManager has the specified
     * parent and a classloader.
     *
     * @param parent  The parent <code>RoleManager</code>.
     * @param loader  the classloader
     */
    public FortressRoleManager( org.apache.avalon.fortress.RoleManager parent, ClassLoader loader )
    {
        super( parent, loader );
    }

    /**
     * Initialize the role manager.
     */
    public void initialize()
    {
        /* Set up Cache relations */
        addRole( "cache",
                 "org.apache.excalibur.cache.Cache",
                 "org.apache.excalibur.cache.impl.DefaultCache",
                 "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );
        addRole( "lru-cache",
                 "org.apache.excalibur.cache.Cache",
                 "org.apache.excalibur.cache.impl.LRUCache",
                 "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );

        /* Set up DataSource relations */
        addRole( "jdbc-datasource",
                 "org.apache.avalon.excalibur.datasource.DataSourceComponent",
                 "org.apache.avalon.excalibur.datasource.JdbcDataSource",
                 "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );
        addRole( "j2ee-datasource",
                 "org.apache.avalon.excalibur.datasource.DataSourceComponent",
                 "org.apache.avalon.excalibur.datasource.J2eeDataSource",
                 "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );
        addRole( "informix-datasource",
                 "org.apache.avalon.excalibur.datasource.DataSourceComponent",
                 "org.apache.avalon.excalibur.datasource.InformixDataSource",
                 "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );

        /* Set up Monitor relations */
        addRole( "monitor",
                 "org.apache.avalon.excalibur.monitor.Monitor",
                 "org.apache.avalon.excalibur.monitor.ActiveMonitor",
                 "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );
        addRole( "passive-monitor",
                 "org.apache.avalon.excalibur.monitor.Monitor",
                 "org.apache.avalon.excalibur.monitor.PassiveMonitor",
                 "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );

        /* Set up XPath relations */
        addRole( "xalan-xpath",
                 "org.apache.excalibur.xml.xpath.XPathProcessor",
                 "org.apache.excalibur.xml.xpath.XPathProcessorImpl",
                 "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );
        addRole( "jaxpath",
                 "org.apache.excalibur.xml.xpath.XPathProcessor",
                 "org.apache.excalibur.xml.xpath.JaxenProcessorImpl",
                 "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );

        /* Set up SourceResolver relations */
        addRole( "resolver",
                 "org.apache.excalibur.source.SourceResolver",
                 "org.apache.excalibur.source.impl.SourceResolverImpl",
                 "org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler" );

        /* Set up XML parser relations */
        addRole( "parser",
                 "org.apache.excalibur.xml.dom.DOMParser",
                 "org.apache.excalibur.xml.impl.JaxpParser",
                 "org.apache.avalon.fortress.impl.handler.PerThreadComponentHandler" );
        addRole( "xerces-parser",
                 "org.apache.excalibur.xml.dom.DOMParser",
                 "org.apache.excalibur.xml.impl.XercesParser",
                 "org.apache.avalon.fortress.impl.handler.FactoryComponentHandler" );
    }
}

