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

import java.util.ArrayList;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.phoenix.containerkit.lifecycle.LifecycleException;
import org.apache.avalon.phoenix.containerkit.lifecycle.LifecycleHelper;

/**
 * PUnit helper
 * @author Paul Hammant
 */
public final class PUnitHelper
    implements PUnit, Initializable
{
    private LifecycleHelper m_lifecycleHelper;
    private ArrayList m_blocks;
    private DefaultServiceManager m_serviceManager;
    private PUnitLogger m_logger;

    /**
     * Query the log
     * @param startsWith For an expression that starts with this
     * @return The logged entry.
     */
    public final String lookupInLog( final String startsWith )
    {
        return m_logger.get( startsWith );
    }

    /**
     * Query the log
     * @param startsWith For an expression that starts with this
     * @return true or not
     */
    public boolean logHasEntry( final String startsWith )
    {
        return m_logger.contains( startsWith );
    }

    /**
     * Initialize
     * @throws Exception If a problem
     */
    public void initialize() throws Exception
    {
        m_logger = new PUnitLogger();
        m_lifecycleHelper = new LifecycleHelper();
        m_lifecycleHelper.enableLogging( new ConsoleLogger() );
        m_serviceManager = new DefaultServiceManager();
        m_blocks = new ArrayList();
    }

    /**
     * Add a block
     * @param blockName The block name
     * @param block The block
     * @param serviceName The service name (for lookup)
     * @param configuration The configuration
     */
    public void addBlock( final String blockName,
                          final String serviceName,
                          final Object block,
                          final Configuration configuration )
    {
        final PUnitResourceProvider resourceProvider =
            new PUnitResourceProvider( m_serviceManager, configuration, m_logger );
        final PUnitBlockEntry pBlock = new PUnitBlockEntry( blockName, block, resourceProvider );
        m_blocks.add( pBlock );
        if( serviceName != null )
        {
            m_serviceManager.put( serviceName, block );
        }
    }

    /**
     * Run blocks thru startup.
     * @throws LifecycleException If a problem
     */
    public void startup() throws LifecycleException
    {

        for( int i = 0; i < m_blocks.size(); i++ )
        {
            final PUnitBlockEntry block = (PUnitBlockEntry)m_blocks.get( i );
            m_lifecycleHelper.startup( block.getBlockName(),
                                       block.getBlock(),
                                       block.getResourceProvider() );
        }
    }

    /**
     * Run blocks thru shutdown
     * @throws LifecycleException If a problem
     */
    public void shutdown() throws LifecycleException
    {
        final int size = m_blocks.size();
        for( int i = 0; i < size; i++ )
        {
            final PUnitBlockEntry block = (PUnitBlockEntry)m_blocks.get( i );
            m_lifecycleHelper.shutdown( block.getBlockName(), block.getBlock() );
        }
    }
}
