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

package org.apache.avalon.activation.appliance.impl;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.activation.appliance.ApplianceRepository;
import org.apache.avalon.activation.appliance.DependencyGraph;
import org.apache.avalon.activation.appliance.ServiceContext;
import org.apache.avalon.activation.appliance.Engine;
import org.apache.avalon.activation.appliance.BlockContext;

/**
 * Context object applied to a new block.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/11/03 06:11:30 $
 */
public class DefaultBlockContext implements BlockContext
{
    private final Logger m_logger;

    private final ContainmentModel m_model;

    private final DependencyGraph m_graph;

    private final ServiceContext m_context;

    private final Engine m_engine;

    private final ApplianceRepository m_repository;

   /**
    * Creation of a new block context.
    * @param logger the logging channel to assign
    * @param model the containment model describing the block
    * @param graph the dependency graph
    * @param context the service context
    * @param engine the engine from which dependent applicance 
    *      instances may be resolved
    * @param repository the parent appliance repository
    */
    public DefaultBlockContext( 
      Logger logger, ContainmentModel model, DependencyGraph graph, 
      ServiceContext context, Engine engine, ApplianceRepository repository )
    {
        if( graph == null ) throw new NullPointerException( "graph" );
        if( context == null ) throw new NullPointerException( "context" );
        if( repository == null ) throw new NullPointerException( "repository" );

        m_logger = logger;
        m_repository = repository;
        m_model = model;
        m_context = context;
        m_engine = engine;
        m_graph = graph;
    }

   /**
    * Returns the logging channel to assign to the block.
    * @return the logging channel
    */
    public Logger getLogger()
    {
        return m_logger;
    }

   /**
    * Returns the containment model assigned to the block.
    * @return the containment model
    */
    public ContainmentModel getContainmentModel()
    {
        return m_model;
    }

   /**
    * Returns the dependency graph assigned to the block.
    * @return the dependency graph
    */
    public DependencyGraph getDependencyGraph()
    {
        return m_graph;
    }

   /**
    * Returns the service context assigned to the block.
    * @return the service context
    */
    public ServiceContext getServiceContext()
    {
        return m_context;
    }

   /**
    * Returns the assigned engine.
    * @return the engine
    */
    public Engine getEngine()
    {
        return m_engine;
    }

   /**
    * Returns the parent appliance repository.
    * @return the appliance repository
    */
    public ApplianceRepository getApplianceRepository()
    {
        return m_repository;
    }
}
