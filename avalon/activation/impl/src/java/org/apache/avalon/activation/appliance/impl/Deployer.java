/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2004 The Apache Software Foundation. All rights reserved.

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

import org.apache.avalon.activation.appliance.Deployable;

import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ContainmentModel;

import org.apache.avalon.framework.logger.Logger;

/**
 * Runnable deployment thread that handles the deployment of an 
 * arbitary number of deployable instances.  The deployer maintains a 
 * list of deployment requests which are queued on a first come first 
 * serve basis.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2004/01/13 11:41:22 $
 * @see DeploymentRequest
 */
class Deployer
    implements Runnable
{
    //------------------------------------------------------------
    // static
    //------------------------------------------------------------

    static private int m_ThreadCounter = 0;
    
    //------------------------------------------------------------
    // immutable state
    //------------------------------------------------------------

    private final Logger m_logger;
    private final SimpleFIFO m_deploymentFIFO = new SimpleFIFO();

    //------------------------------------------------------------
    // mutable static
    //------------------------------------------------------------

    private Thread m_deploymentThread;
    
    //------------------------------------------------------------
    // constructor
    //------------------------------------------------------------

    Deployer( Logger logger )
    {
        m_logger = logger;
        m_deploymentThread = 
          new Thread( this, "Deployer " + m_ThreadCounter++ );
        m_deploymentThread.start();
    }

    //------------------------------------------------------------
    // implementation
    //------------------------------------------------------------

    /** 
     * Deploys the given Deployable, and allows a maximum time
     * for the deployment to complete.
     *
     * @param deployable the deployable model
     * @param timeout the maximum time to allow for deployment
     *
     * @throws DeploymentException if the deployment was not 
     *   completed within the timeout deadline and interuption
     *   of the deployment was successful
     * @throws FatalDeploymentException if the deployment was not 
     *   completed within the timeout deadline and interuption
     *   of the deployment was not successful
     * @throws Exception any Exception or Error thrown within the
     *   deployment of the component is forwarded to the caller.
     * @throws InvocationTargetException if the deployment throws a
     *   Throwable subclass that is NOT of type Exception or Error.
     **/
    void deploy( DeploymentModel deployable )
        throws Exception
    {
        if( null == deployable )
        {
            throw new NullPointerException( "deployable" );
        }
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "deploying: " + deployable );
        }
        if( null != m_deploymentThread )
        {
            DeploymentRequest req = 
              new DeploymentRequest( deployable, m_deploymentThread );
            m_deploymentFIFO.put( req );
            req.waitForCompletion();
        }
        else
        {
            final String warning = 
              "Ignoring attempt to deploy a component on a disposed deployer.";
            m_logger.warn( warning );
        }
    }

    /** 
     * Disposal of the Deployer.
     * The Deployer allocates a deployment thread, which needs to be
     * disposed of before releasing the Deployer reference.
     **/
    void dispose()
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "disposal" );
        }
        if( null != m_deploymentThread )
        { 
            m_deploymentThread.interrupt();
        }
    }
    
    public void run()
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "deployment thread started" );
        }
        try
        {
            while( true )
            {
                DeploymentRequest req = (DeploymentRequest) m_deploymentFIFO.get();
                DeploymentModel deployable = req.getDeployable();
                if( null == deployable.getHandler() )
                {
                    final String error =
                      "No handler assigned to model: " + deployable;
                    throw new IllegalStateException( error );
                }
                if( !( deployable.getHandler() instanceof Deployable ) )
                {
                    final String error =
                      "Deployment handler assigned to model: " + deployable
                      + " does not implement the deployable contract";
                    throw new IllegalStateException( error );
                }

                Deployable target = (Deployable) deployable.getHandler();

                try
                {
                    target.deploy();
                    req.done();
                } 
                catch( InterruptedException e )
                {
                    req.interrupted();
                }
                catch( Throwable e )
                {
                    req.exception( e );
                }
            }
        } 
        catch( InterruptedException e )
        { 
            // ignore, part of dispose();
        }
        m_deploymentThread = null;
    }
}
