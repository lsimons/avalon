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

import java.lang.reflect.InvocationTargetException;

import org.apache.avalon.composition.runtime.Commissionable;
import org.apache.avalon.activation.appliance.DeploymentException;
import org.apache.avalon.activation.appliance.FatalDeploymentException;
import org.apache.avalon.composition.model.DeploymentModel;

/**
 * A deployment request handler.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.7 $ $Date: 2004/02/06 15:27:13 $
 */
class DeploymentRequest
{
    //------------------------------------------------------------
    // immutable state
    //------------------------------------------------------------

    private final DeploymentModel m_deployable;
    private final Thread m_deploymentThread;
    private long m_timeout;
    
    //------------------------------------------------------------
    // mutable state
    //------------------------------------------------------------

    private boolean   m_completed;
    private boolean   m_interrupted;
    private Throwable m_exception;

    //------------------------------------------------------------
    // constructor
    //------------------------------------------------------------

    DeploymentRequest( DeploymentModel deployable, Thread deploymentThread )
    {
        m_deployable = deployable;
        m_completed = false;
        m_interrupted = false;
        m_exception = null;
        m_deploymentThread = deploymentThread;
    }

    //------------------------------------------------------------
    // implementation
    //------------------------------------------------------------

    DeploymentModel getDeployable()
    {
        return m_deployable;
    }

    long waitForCompletion()
        throws Exception
    {
        long t1 = System.currentTimeMillis();
        synchronized( this )
        {
            long timeout = getDeployable().getDeploymentTimeout();
            wait( timeout ); // wait for startup
            processException();
            if( m_completed )
            {
                long t2 = System.currentTimeMillis();
                return t2-t1;
            }
            m_deploymentThread.interrupt();
            wait( timeout ); // wait for shutdown
            processException();
            if( m_interrupted || m_completed )
            {
                final String error = 
                  "deployment target: [" 
                  + m_deployable 
                  + "] did not respond within the timeout period: [" 
                  + timeout
                  + "] and was successfully interrupted.";
                throw new DeploymentException( error );
            }
            else
            {
                final String error = 
                  "deployment target: [" 
                  + m_deployable 
                  + "] did not respond within the timeout period: [" 
                  + timeout
                  + "] and failed to respond to an interrupt.";
                throw new FatalDeploymentException( error );
            }
        }
    }

    private void processException()
        throws Exception
    {
        if( m_exception != null )
        {
            if( m_exception instanceof Exception )
            {
                throw (Exception) m_exception;
            }
            else if( m_exception instanceof Error )
            {
                throw (Error) m_exception;
            }
            else
            {
                final String error = 
                  "Unexpected deployment error.";
                throw new InvocationTargetException( m_exception, error );
            }
        }
    }

    void done()
    {
        synchronized( this )
        {
            m_completed = true;
            notifyAll();
        }
    }

    void interrupted()
    {
        m_interrupted = true;
        synchronized( this )
        {
            notify();
        }
    }

    void exception( Throwable e )
    {
        m_exception = e;
        synchronized( this )
        {
            notify();
        }
    }
}

