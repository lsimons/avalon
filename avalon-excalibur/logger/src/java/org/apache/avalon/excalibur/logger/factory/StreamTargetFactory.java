/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
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
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 02:34:15 $
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

