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

import java.io.OutputStream;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.log.LogTarget;
import org.apache.log.format.Formatter;
import org.apache.log.output.io.StreamTarget;

/**
 * TargetFactory for {@link org.apache.log.output.io.StreamTarget}.
 *
 * This factory is able to create different StreamTargets according to the following
 * configuration syntax:
 * <pre>
 * &lt;stream id="foo"&gt;
 *  &lt;stream&gt;<i>stream-context-name</i>&lt;/stream&gt;
 *  &lt;format type="<i>raw|pattern|extended</i>"&gt;<i>pattern to be used if needed</i>&lt;/format&gt;
 * &lt;/stream&gt;
 * </pre>
 *
 * <p>The "stream-context-name" is the name of an <code>java.io.OutputStream</code> that
 * is fetched in the context. This context contains two predefined streams :
 * <li>"<code>System.out</code>" for the system output stream,</li>
 * <li>"<code>System.err</code>" for the system error stream.</li>
 * </p>
 *
 * <p>The syntax of "format" is the same as in <code>FileTargetFactory</code>.</p>
 *
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/07 13:37:00 $
 */
public class StreamTargetFactory
    extends AbstractTargetFactory
    implements Contextualizable
{

    /**
     * Create a LogTarget based on a Configuration
     */
    public LogTarget createTarget( final Configuration configuration )
        throws ConfigurationException
    {
        OutputStream stream;

        final Configuration streamConfig = configuration.getChild( "stream", false );
        if( null == streamConfig )
        {
            stream = System.err;
        }
        else
        {
            final String streamName = streamConfig.getValue();
            try
            {
                stream = (OutputStream)m_context.get( streamName );
            }
            catch( Exception e )
            {
                throw new ConfigurationException( "Error resolving stream '" +
                                                  streamName + "' at " +
                                                  streamConfig.getLocation(), e );
            }
        }

        final Configuration formatterConf = configuration.getChild( "format" );
        final Formatter formatter = getFormatter( formatterConf );

        return new StreamTarget( stream, formatter );
    }

    public void contextualize( final Context context )
        throws ContextException
    {
        // Add System output streams
        final DefaultContext newContext = new DefaultContext( context );

        newContext.put( "System.out", System.out );
        newContext.put( "System.err", System.err );

        super.contextualize( newContext );
    }

    protected Formatter getFormatter( final Configuration conf )
    {
        Formatter formatter = null;

        if( null != conf )
        {
            final FormatterFactory formatterFactory = new FormatterFactory();
            formatter = formatterFactory.createFormatter( conf );
        }

        return formatter;
    }
}

