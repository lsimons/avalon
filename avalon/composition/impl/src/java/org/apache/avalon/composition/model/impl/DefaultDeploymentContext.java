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

package org.apache.avalon.composition.model.impl;

import org.apache.avalon.composition.model.DependencyGraph;
import org.apache.avalon.composition.model.DeploymentContext;
import org.apache.avalon.composition.model.SystemContext;

import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.composition.data.Mode;

/**
 * Default implementation of a deployment context.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2004/01/13 11:41:26 $
 */
public class DefaultDeploymentContext extends DefaultContext 
  implements DeploymentContext
{
    //---------------------------------------------------------
    // immutable state
    //---------------------------------------------------------

    private final String m_name;
    private final String m_partition;
    private final Logger m_logger;
    private final Mode m_mode;
    private final DependencyGraph m_graph;
    private final SystemContext m_system;

    //---------------------------------------------------------
    // constructor
    //---------------------------------------------------------

   /**
    * Creation of a new deployment context.
    *
    * @param logger the logging channel to assign
    * @param partition the assigned partition name
    * @param name the profile name
    * @param mode the deployment mode
    * @param graph the parent deployment assembly graph
    */
    public DefaultDeploymentContext( 
      Logger logger, SystemContext system, String partition, String name, 
      Mode mode, DependencyGraph graph )
    {
        if( logger == null )
        {
            throw new NullPointerException( "logger" );
        }
        if( name == null )
        {
            throw new NullPointerException( "name" );
        }
        if( mode == null )
        {
            throw new NullPointerException( "mode" );
        }
        if( system == null )
        {
            throw new NullPointerException( "system" );
        }

        m_graph = new DependencyGraph( graph );
        if( graph != null )
        {
            graph.addChild( m_graph );
        }

        m_logger = logger;
        m_system = system;
        m_partition = partition;
        m_name = name;
        m_mode = mode;

    }

    //---------------------------------------------------------
    // DeploymentContext
    //---------------------------------------------------------

   /**
    * Return the system context.
    *
    * @return the system context
    */
    public SystemContext getSystemContext()
    {
        return m_system;
    }

   /**
    * Return the profile name.
    * @return the name
    */
    public String getName()
    {
        return m_name;
    }

   /**
    * Return the assigned partition name.
    *
    * @return the partition name
    */
    public String getPartitionName()
    {
        return m_partition;
    }

   /**
    * Return the model fully qualified name.
    * @return the fully qualified name
    */
    public String getQualifiedName()
    {
        if( null == getPartitionName() )
        {
            return SEPARATOR;
        }
        else
        {
            return getPartitionName() + getName();
        }
    }

   /**
    * Return the mode of establishment.
    * @return the mode
    */
    public Mode getMode()
    {
        return m_mode;
    }

   /**
    * Return the assigned logger.
    * @return the logging channel
    */
    public Logger getLogger()
    {
        return m_logger;
    }

   /**
    * Return the dependency graph used to construct 
    * deployment and decommissioning sequences.
    *
    * @return the dependency graph
    */
    public DependencyGraph getDependencyGraph()
    {
        return m_graph;
    }
}
