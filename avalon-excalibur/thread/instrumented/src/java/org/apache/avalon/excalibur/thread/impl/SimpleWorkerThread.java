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

package org.apache.avalon.excalibur.thread.impl;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.thread.impl.AbstractThreadPool;
import org.apache.excalibur.thread.impl.WorkerThread;

/**
 * This class extends the Thread class to add recyclable functionalities.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author Peter Donald
 * @deprecated Only Used by deprecated code and will be removed in future
 */
class SimpleWorkerThread
    extends WorkerThread
    implements Poolable, LogEnabled
{
    /** Log major events like uncaught exceptions and worker creation
     *   and deletion.  Stuff that is useful to be able to see over long
     *   periods of time. */
    private Logger m_logger;
    
    /**
     * Log minor detail events like 
     */
    private Logger m_detailLogger;

    /**
     * Allocates a new <code>Worker</code> object.
     */
    protected SimpleWorkerThread( final AbstractThreadPool pool,
                                  final ThreadGroup group,
                                  final String name )
    {
        super( pool, group, name );
    }

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
        m_detailLogger = logger.getChildLogger( "detail" );
        
        // Log a created message here rather as we can't in the constructor
        //  due to the lack of a logger.
        debug( "created." );
    }

    /**
     * Used to log major events against the worker.  Creation, deletion,
     *  uncaught exceptions etc.
     *
     * @param message Message to log.
     */
    protected void debug( final String message )
    {
        if ( m_logger.isDebugEnabled() )
        {
            // As we are dealing with threads where more than one thread is
            //  always involved, log both the name of the thread that triggered
            //  event along with the name of the worker involved.  This
            //  increases the likely hood of walking away sane after a
            //  debugging session.
            m_logger.debug( "\"" + getName() + "\" "
                + "(in " + Thread.currentThread().getName() + ") : " + message );
        }
    }

    /**
     * Used to log major events against the worker.  Creation, deletion,
     *  uncaught exceptions etc.
     *
     * @param message Message to log.
     * @param throwable Throwable to log with the message.
     */
    protected void debug( final String message, final Throwable throwable )
    {
        if ( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "\"" + getName() + "\" "
                + "(in " + Thread.currentThread().getName() + ") : " + message, throwable );
        }
    }

    /**
     * Used to log minor events against the worker.  Start and stop of
     *  individual pieces of work etc.  Separated from the major events
     *  so that they are not lost in a sea of minor events.
     *
     * @param message Message to log.
     */
    protected void detailDebug( final String message )
    {
        if ( m_detailLogger.isDebugEnabled() )
        {
            m_detailLogger.debug( "\"" + getName() + "\" "
                + "(in " + Thread.currentThread().getName() + ") : " + message );
        }
    }

    /**
     * Used to log minor events against the worker.  Start and stop of
     *  individual pieces of work etc.  Separated from the major events
     *  so that they are not lost in a sea of minor events.
     *
     * @param message Message to log.
     * @param throwable Throwable to log with the message.
     */
    protected void detailDebug( final String message, final Throwable throwable )
    {
        if ( m_detailLogger.isDebugEnabled() )
        {
            m_detailLogger.debug( "\"" + getName() + "\" "
                + "(in " + Thread.currentThread().getName() + ") : " + message, throwable );
        }
    }
}

