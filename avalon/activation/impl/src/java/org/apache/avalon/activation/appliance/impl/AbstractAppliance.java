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

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * An Appliance is the basic tool merlin wraps around a component to
 * provide support for lifecycle and lifestyle management. Different
 * implementations of Appliance can be plugged into the merlin system
 * to allow merlin to manage a variety of components.
 *
 * The name appliance is used to call up an association with a kitchen
 * utility like a microwave. Merlin acts as a chef in his kitchen, and uses
 * various appliances to "cook up" various components as the restaurant
 * customers (which can be other components or systems on the other end
 * on the planet) ask for them.
 *
 * An appliance manages the establishment of a component
 * type relative to a deployment criteria. Once established, an appliance
 * provides support for the deployment of component instances on request.
 * An appliance is responsible for component lifestyle and lifecycle
 * management during the deployment and decommission cycles.
 *
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/13 11:41:22 $
 */
public abstract class AbstractAppliance extends AbstractLogEnabled implements Appliance, Disposable
{
    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    private DeploymentModel m_model;

    private boolean m_enabled = true;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

    public AbstractAppliance( DeploymentModel model )
    {
        enableLogging( model.getLogger() );
        m_model = model;
        m_model.setHandler( this );
    }

    //-------------------------------------------------------------------
    // Appliance
    //-------------------------------------------------------------------

    /**
     * Return the model backing the appliance.
     * @return the type that the appliance is managing
     */
    public DeploymentModel getModel()
    {
        if( null == m_model ) 
        {
            throw new NullPointerException( "model" );
        }
        return m_model;
    }

    //-------------------------------------------------------------------
    // Disposable
    //-------------------------------------------------------------------

    public void dispose()
    {
        m_model.setHandler( null );
        m_model = null;
        getLogger().debug( "disposal complete" );
    }

    //-------------------------------------------------------------------
    // Object
    //-------------------------------------------------------------------

    public String toString()
    {
        return "appliance:" + getModel().getQualifiedName();
    }
}
