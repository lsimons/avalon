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

import java.lang.reflect.InvocationTargetException;

import org.apache.avalon.activation.appliance.Deployable;
import org.apache.avalon.activation.appliance.DeploymentException;
import org.apache.avalon.activation.appliance.FatalDeploymentException;
import org.apache.avalon.composition.model.DeploymentModel;

/**
 * A deployment request handler.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2.2.2 $ $Date: 2004/01/08 12:51:16 $
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
        m_timeout = deployable.getDeploymentTimeout();
    }

    //------------------------------------------------------------
    // implementation
    //------------------------------------------------------------

    DeploymentModel getDeployable()
    {
        return m_deployable;
    }

    void waitForCompletion()
        throws Exception
    {
        synchronized( this )
        {
            wait( m_timeout ); // wait for startup
            processException();
            if( m_completed )
            {
                return;
            }
            m_deploymentThread.interrupt();
            wait( m_timeout ); // wait for shutdown
            processException();
            if( m_interrupted || m_completed )
            {
                final String error = 
                  "deployment target: [" 
                  + m_deployable 
                  + "] did not respond within the timeout period: [" 
                  + m_timeout
                  + "] and was successfully interrupted.";
                throw new DeploymentException( error );
            }
            else
            {
                final String error = 
                  "deployment target: [" 
                  + m_deployable 
                  + "] did not respond within the timeout period: [" 
                  + m_timeout
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
    }

    void exception( Throwable e )
    {
        m_exception = e;
    }
}

