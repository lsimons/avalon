/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.avalon.excalibur.logger.factory;

import org.apache.avalon.excalibur.logger.LogTargetFactory;
import org.apache.avalon.excalibur.logger.LogTargetFactoryManageable;
import org.apache.avalon.excalibur.logger.LogTargetFactoryManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.log.LogTarget;
import org.apache.log.output.AsyncLogTarget;

/**
 * AsyncLogTargetFactory class.
 *
 * This factory creates LogTargets with a wrapped AsyncLogTarget around it:
 *
 * <pre>
 *
 * &lt;async-target id="target-id" queue-size=".." priority="MIN|NORM|MAX|n"&gt;
 *  &lt;any-target-definition/&gt;
 * &lt;/async-target&gt;
 *
 * </pre>
 * <p>
 *  This factory creates a AsyncLogTarget object with a specified queue-size
 *  attribute (which defaults to what the AsyncLogTarget uses if absent).
 *  The LogTarget to wrap is described in a child element of the configuration (in
 *  the sample above named as &lt;any-target-definition/&gt;).
 *  The Thread of the created AsyncLogTarget will have a priority specified by the
 *  priotity attribute (which defaults to Thread.MIN_PRIORITY). The priority values
 *  corresponds to those defined in the Thread class which are:
 * </p>
 * <p>
 * <blockquote>
 * MIN=Thread.MIN_PRIORITY<br>
 * NORM=Thread.NORM_PRIORITY<br>
 * MAX=Thread.MAX_PRIORITY<br>
 * number=priority number (see class java.lang.Thread)<br>
 * </blockquote>
 * </p>
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/07 13:37:00 $
 * @since 4.0
 */
public final class AsyncLogTargetFactory
    extends AbstractTargetFactory
    implements LogTargetFactoryManageable
{
    /** The LogTargetFactoryManager */
    protected LogTargetFactoryManager m_logTargetFactoryManager;

    /**
     * create a LogTarget based on a Configuration
     */
    public final LogTarget createTarget( final Configuration configuration )
        throws ConfigurationException
    {
        final int queuesize = configuration.getAttributeAsInteger( "queue-size", -1 );
        final Configuration config = configuration.getChildren()[ 0 ];
        final LogTargetFactory factory = m_logTargetFactoryManager.getLogTargetFactory( config.getName() );
        final LogTarget target = factory.createTarget( config );
        final AsyncLogTarget asyncTarget;
        if( queuesize == -1 )
        {
            asyncTarget = new AsyncLogTarget( target );
        }
        else
        {
            asyncTarget = new AsyncLogTarget( target, queuesize );
        }

        final String priority = configuration.getAttribute( "priority", null );
        final int threadPriority;
        if( "MIN".equalsIgnoreCase( priority ) )
        {
            threadPriority = Thread.MIN_PRIORITY;
        }
        else if( "NORM".equalsIgnoreCase( priority ) )
        {
            threadPriority = Thread.NORM_PRIORITY;
        }
        else if( "NORM".equalsIgnoreCase( priority ) )
        {
            threadPriority = Thread.NORM_PRIORITY;
        }
        else
        {
            threadPriority = configuration.getAttributeAsInteger( "priority", 1 );
        }
        final Thread thread = new Thread( asyncTarget );
        thread.setPriority( threadPriority );
        thread.setDaemon( true );
        thread.start();
        return asyncTarget;
    }

    /**
     * get the LogTargetFactoryManager
     */
    public final void setLogTargetFactoryManager( LogTargetFactoryManager logTargetFactoryManager )
    {
        m_logTargetFactoryManager = logTargetFactoryManager;
    }

}

