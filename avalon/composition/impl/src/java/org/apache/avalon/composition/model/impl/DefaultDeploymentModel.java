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

import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.DeploymentContext;
import org.apache.avalon.composition.model.DependencyGraph;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.data.Mode;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;


/**
 * Abstract model base class.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.12 $ $Date: 2004/01/19 21:46:10 $
 */
public abstract class DefaultDeploymentModel
  implements DeploymentModel
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultDeploymentModel.class );

    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final DeploymentContext m_context;

    //--------------------------------------------------------------
    // muttable state
    //--------------------------------------------------------------

    private Object m_handler = null;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of an abstract model.  The model associated a 
    * name and a partition.
    *
    * @param context the deployment context
    */
    public DefaultDeploymentModel( DeploymentContext context )
    { 
        if( null == context )
        {
            throw new NullPointerException( "context" );
        }
        m_context = context;
    }

    //--------------------------------------------------------------
    // DeploymentModel
    //--------------------------------------------------------------

   /**
    * Return the profile name.
    * @return the name
    */
    public String getName()
    {
        return m_context.getName();
    }

   /**
    * Return the profile path.
    * @return the path
    */
    public String getPath()
    {
        if( null == m_context.getPartitionName() )
        {
            return SEPARATOR;
        }
        return m_context.getPartitionName();
    }

   /**
    * Return the model fully qualified name.
    * @return the fully qualified name
    */
    public String getQualifiedName()
    {
        return getPath() + getName();
    }

   /**
    * Return the mode of establishment.
    * @return the mode
    */
    public Mode getMode()
    {
        return m_context.getMode();
    }

   /**
    * Return the set of models consuming this model.
    * @return the consumers
    */
    public DeploymentModel[] getConsumerGraph()
    {
        return m_context.getDependencyGraph().getConsumerGraph( this );
    }

   /**
    * Return the set of models supplying this model.
    * @return the providers
    */
    public DeploymentModel[] getProviderGraph()
    {
        return m_context.getDependencyGraph().getProviderGraph( this );
    }

   /**
    * Set the runtime handler for the model.
    * @param handler the runtime handler
    */
    public void setHandler( Object handler )
    {
        m_handler = handler;
    }

   /**
    * Get the assigned runtime handler for the model.
    * @return the runtime handler
    */
    public Object getHandler()
    {
        return m_handler;
    }

   /**
    * Return the assigned logging channel.
    * @return the logging channel
    */
    public Logger getLogger()
    {
        return m_context.getLogger();
    }

    //--------------------------------------------------------------
    // implementation
    //--------------------------------------------------------------

    public String toString()
    {
        return "[" + getQualifiedName() + "]";
    }

    public boolean equals( Object other )
    {
        boolean equal = super.equals( other ); 
        return equal;
    }

   /** 
    * Return the default deployment timeout value declared in the 
    * kernel configuration.  The implementation looks for a value
    * assigned under the property key "urn:composition:deployment.timeout"
    * and defaults to 1000 msec if undefined.
    *
    * @return the default deployment timeout value
    */
    public long getDeploymentTimeout()
    {
        SystemContext sc = m_context.getSystemContext();
        Parameters params = sc.getSystemParameters();
        return params.getParameterAsLong( 
          DEPLOYMENT_TIMEOUT_KEY, 
          1000
        );
    }

}
