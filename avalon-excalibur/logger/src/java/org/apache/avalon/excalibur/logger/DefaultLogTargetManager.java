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
package org.apache.avalon.excalibur.logger;

import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.log.LogTarget;

/**
 * Default LogTargetManager implementation.  It populates the LogTargetManager
 * from a configuration file.
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.7 $ $Date: 2002/11/26 08:05:45 $
 * @since 4.0
 */
public class DefaultLogTargetManager
    extends AbstractLogEnabled
    implements LogTargetManager, LogTargetFactoryManageable, Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources(DefaultLogTargetManager.class);

    /** Map for ID to LogTarget mapping */
    final private Map m_targets = new HashMap();

    /** The LogTargetFactoryManager object */
    private LogTargetFactoryManager m_factoryManager;

    /**
     * Retrieves a LogTarget for an ID. If this LogTargetManager
     * does not have the match a null will be returned.
     *
     * @param id The LogTarget ID
     * @return the LogTarget or null if none is found.
     */
    public final LogTarget getLogTarget( final String id )
    {
        return (LogTarget)m_targets.get( id );
    }

    /**
     * Gets the LogTargetFactoryManager.
     */
    public final void setLogTargetFactoryManager( final LogTargetFactoryManager logTargetFactoryManager )
    {
        m_factoryManager = logTargetFactoryManager;
    }

    /**
     * Reads a configuration object and creates the log targets.
     *
     * @param configuration  The configuration object.
     * @throws ConfigurationException if the configuration is malformed
     */
    public final void configure( final Configuration configuration )
        throws ConfigurationException
    {
        if( null == m_factoryManager )
        {
            final String message = REZ.getString("target.error.null-target-factory");
            throw new ConfigurationException( message );
        }

        final Configuration[] confs = configuration.getChildren();
        for( int i = 0; i < confs.length; i++ )
        {
            final String targetName = confs[ i ].getName();
            final LogTargetFactory logTargetFactory = m_factoryManager.getLogTargetFactory( targetName );
            if( logTargetFactory == null )
            {
                final String message = REZ.getString("target.error.missing", targetName);
                throw new ConfigurationException( message );
            }
            final LogTarget logTarget = logTargetFactory.createTarget( confs[ i ] );
            final String targetId = confs[ i ].getAttribute( "id" );
            if( getLogger().isDebugEnabled() )
            {
                final String message = REZ.getString("target.notice.add", targetId);
                getLogger().debug( message );
            }
            m_targets.put( targetId, logTarget );
        }
    }
}
