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

import java.lang.reflect.InvocationTargetException;

import org.apache.avalon.activation.appliance.Deployable;
import org.apache.avalon.activation.appliance.DeploymentException;
import org.apache.avalon.activation.appliance.FatalDeploymentException;

class DeploymentRequest
{
    private Deployable m_Deployable;
    private boolean   m_Completed;
    private boolean   m_Interrupted;
    private Thread    m_DeploymentThread;
    private Throwable m_Exception;
    
    DeploymentRequest( Deployable deployable, Thread deploymentThread )
    {
        m_Deployable = deployable;
        m_Completed = false;
        m_Interrupted = false;
        m_Exception = null;
        m_DeploymentThread = deploymentThread;
    }

    Deployable getDeployable()
    {
        return m_Deployable;
    }

    void waitForCompletion( long timeout )
        throws Exception
    {
        synchronized( this )
        {
            wait( timeout );
            processException();
            if( m_Completed )
                return;
            m_DeploymentThread.interrupt();
            wait( timeout );
            processException();
            if( m_Interrupted || m_Completed )
                throw new DeploymentException( "Deployable '" + m_Deployable + "' hanged during deployment and was interrupted." );
            throw new FatalDeploymentException( "Deployable '" + m_Deployable + "' hanged during deployment and could not be interrupted." );
        }
    }

    private void processException()
        throws Exception
    {
        if( m_Exception != null )
        {
            if( m_Exception instanceof Exception )
                throw (Exception) m_Exception;
            else if( m_Exception instanceof Error )
                throw (Error) m_Exception;
            else
                throw new InvocationTargetException( m_Exception, "Unknown Throwable type, neither Exception nor Error." );
        }
    }

    void done()
    {
        synchronized( this )
        {
            m_Completed = true;
            notifyAll();
        }
    }

    void interrupted()
    {
        m_Interrupted = true;
    }

    void exception( Throwable e )
    {
        m_Exception = e;
    }
}

