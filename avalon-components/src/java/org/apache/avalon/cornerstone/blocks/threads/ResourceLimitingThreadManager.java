/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Cornerstone", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
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

package org.apache.avalon.cornerstone.blocks.threads;

import java.util.Map;
import org.apache.avalon.excalibur.thread.impl.ResourceLimitingThreadPool;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Default implementation of ThreadManager.
 *
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.cornerstone.services.threads.ThreadManager"
 *
 * @author <a href="mailto:leif at apache.org">Leif Mortenson</a>
 */
public class ResourceLimitingThreadManager
    extends AbstractThreadManager
{
    protected void configureThreadPool( final Map threadPools,
                                        final Configuration configuration )
        throws ConfigurationException
    {
        final String name = configuration.getChild( "name" ).getValue();
        final boolean isDaemon = configuration.getChild( "is-daemon" ).getValueAsBoolean( false );
        
        final int max = configuration.getChild( "max-threads" ).getValueAsInteger( 10 );
        final boolean maxStrict = configuration.getChild( "max-strict" ).getValueAsBoolean( true );
        final boolean blocking = configuration.getChild( "blocking" ).getValueAsBoolean( true );
        final long blockTimeout = configuration.getChild( "block-timeout" ).getValueAsLong( 0 );
        final long trimInterval = configuration.getChild( "trim-interval" ).getValueAsLong( 10000 );

        try
        {
            final ResourceLimitingThreadPool threadPool = new ResourceLimitingThreadPool(
                name, max, maxStrict, blocking, blockTimeout, trimInterval );
            threadPool.setDaemon( isDaemon );
            threadPool.enableLogging( getLogger() );
            threadPools.put( name, threadPool );
        }
        catch( final Exception e )
        {
            final String message = "Error creating ThreadPool named " + name;
            throw new ConfigurationException( message, e );
        }
    }
}
