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
import org.apache.log.Priority;
import org.apache.log.filter.PriorityFilter;

/**
 * PriorityFilterTargetFactory class.
 *
 * This factory creates LogTargets with a wrapped PriorityFilter around it:
 *
 * <pre>
 *
 * &lt;priority-filter id="target-id" log-level="ERROR"&gt;
 *  &lt;any-target-definition/&gt;
 *  ...
 *  &lt;any-target-definition/&gt;
 * &lt;/priority-filter&gt;
 *
 * </pre>
 * <p>
 *  This factory creates a PriorityFilter object with a logging Priority set
 *  to the value of the log-level attribute (which defaults to INFO if absent).
 *  The LogTarget to filter is described in child elements of the configuration (in
 *  the sample above named as &lt;any-target-definition/&gt;).
 * </p>
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/07 13:37:00 $
 * @since 4.0
 */
public final class PriorityFilterTargetFactory
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
        final String loglevel = configuration.getAttribute( "log-level", "INFO" );
        getLogger().debug( "loglevel is " + loglevel );
        final PriorityFilter filter = new PriorityFilter( Priority.getPriorityForName( loglevel ) );

        final Configuration[] configs = configuration.getChildren();
        for( int i = 0; i < configs.length; i++ )
        {
            final LogTargetFactory factory = m_logTargetFactoryManager.getLogTargetFactory( configs[ i ].getName() );
            getLogger().debug( "creating target " + configs[ i ].getName() + ": " + configs[ i ].toString() );
            final LogTarget logtarget = factory.createTarget( configs[ i ] );
            filter.addTarget( logtarget );
        }
        return filter;
    }

    /**
     * get the LogTargetFactoryManager
     */
    public final void setLogTargetFactoryManager( LogTargetFactoryManager logTargetFactoryManager )
    {
        m_logTargetFactoryManager = logTargetFactoryManager;
    }

}

