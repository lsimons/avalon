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

package org.apache.avalon.cornerstone.blocks.sockets;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * Contains the code common for both TLS socket factories. They both
 * need to use an SSLFactoryBuilder which is configured using
 * configuration and context given by the container. Then, they both
 * set timeouts on the manufactured sockets.
 *
 * @author <a href="mailto:greg-avalon-apps at nest.cx">Greg Steuck</a>
 */
public abstract class AbstractTLSSocketFactory
    extends AbstractLogEnabled
    implements Contextualizable, Configurable, Initializable
{
    private final static int WAIT_FOREVER = 0;
    protected int m_socketTimeOut;

    private Context m_context;
    private Configuration m_childConfig;

    public void contextualize( final Context context )
    {
        m_context = context;
    }

    /**
     * Configures the factory.
     *
     * @param configuration the Configuration
     * @exception ConfigurationException if an error occurs
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_socketTimeOut = configuration.getChild( "timeout" ).getValueAsInteger( WAIT_FOREVER );
        m_childConfig = configuration.getChild( "ssl-factory", false );
        if( m_childConfig == null )
        {
            final String message = "ssl-factory child not found, please" +
                " update your configuration according to" +
                " the documentation. Reverting to the" +
                " old configuration format.";
            getLogger().warn( message );
            // not completely compatible though
            m_childConfig = configuration;
        }
    }

    /**
     * Creates an SSL factory using the confuration values.
     */
    public void initialize() throws Exception
    {
        final SSLFactoryBuilder builder = new SSLFactoryBuilder();
        setupLogger( builder );
        ContainerUtil.contextualize( builder, m_context );
        ContainerUtil.configure( builder, m_childConfig );
        ContainerUtil.initialize( builder );

        visitBuilder( builder );

        ContainerUtil.shutdown( builder );
        m_context = null;
        m_childConfig = null;
    }

    /**
     * The child factories have to use an instance of
     * <tt>SSLFactoryBuilder</tt> to obtain their factories.  So they
     * are given an instance when it's ready. Another alternative was
     * to have the SSLFactoryBuilder export buildContext method, but
     * that would mean SSLContext which is deep in Sun guts will be
     * aired in 3-4 classes instead of 1.
     */
    protected abstract void visitBuilder( SSLFactoryBuilder builder );
}
