/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.activation.appliance.impl;

import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.runtime.Commissionable;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.activity.Disposable;

/**
 * Runnable deployment thread that handles the deployment of an 
 * arbitary number of commissionable instances.  The deployer maintains a 
 * list of deployment requests which are queued on a first come first 
 * serve basis.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.9 $ $Date: 2004/02/06 15:27:13 $
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
     * Deploys the given Commissonable, and allows a maximum time
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
        if( null != m_deploymentThread )
        {
            if( m_logger.isDebugEnabled() )
            {
                if( deployable instanceof ContainmentModel )
                {
                    m_logger.debug( 
                      "initiating container deployment [" 
                      + deployable.getName() 
                      + "]" );
                }
                else
                {
                    m_logger.debug( 
                      "initiating component deployment [" 
                      + deployable.getName() 
                      + "]" );
                }
            }

            DeploymentRequest req = 
              new DeploymentRequest( deployable, m_deploymentThread );
            m_deploymentFIFO.put( req );
            long t = req.waitForCompletion();
            if( m_logger.isDebugEnabled() )
            {
                m_logger.debug( 
                  "deployment of [" 
                  + deployable.getName() 
                  + "] completed in " 
                  + t + " milliseconds" );
            }
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
                if( !( deployable.getHandler() instanceof Commissionable ) )
                {
                    final String error =
                      "Deployment handler assigned to model: " + deployable
                      + " does not implement the Commissionable contract";
                    throw new IllegalStateException( error );
                }

                Commissionable target = (Commissionable) deployable.getHandler();

                try
                {
                    target.commission();
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
