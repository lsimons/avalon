/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger.factory;

import javax.servlet.ServletContext;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.ContextException;
import org.apache.log.LogTarget;
import org.apache.log.format.Formatter;
import org.apache.log.output.ServletOutputLogTarget;

/**
 * ServletTargetFactory class.
 *
 * This factory creates a ServletOutputLogTargets. It uses the
 * context-key attribute to locate the required ServletContext from
 * the Context object passed to this factory.  The default context-key
 * is <code>servlet-context</code>.
 *
 * <pre>
 *
 * &lt;servlet id="target-id" context-key="context-key-to-servlet-context-object"&gt;
 *  &lt;format type="raw|pattern|extended"&gt;pattern to be used if needed&lt;/format&gt;
 * &lt;/servlet&gt;
 *
 * </pre>
 * <dl>
 *  <dt>&lt;format&gt;</dt>
 *  <dd>
 *   The type attribute of the pattern element denotes the type of
 *   Formatter to be used and according to it the pattern to use for.
 *   This elements defaults to:
 *   <p>
 *    %7.7{priority} %5.5{time}   [%8.8{category}] (%{context}): %{message}\\n%{throwable}
 *   </p>
 *  </dd>
 * </dl>
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 02:34:15 $
 * @since 4.0
 */
public final class ServletTargetFactory
    extends AbstractTargetFactory
{

    /**
     * create a LogTarget based on a Configuration
     */
    public final LogTarget createTarget( final Configuration configuration )
        throws ConfigurationException
    {
        if( m_context == null )
        {
            throw new ConfigurationException( "Context not available." );
        }
        final String contextkey =
            m_configuration.getAttribute( "context-key", "servlet-context" );
        final ServletContext sctx;

        final Configuration confFormat = configuration.getChild( "format" );
        final Formatter formatter = getFormatter( confFormat );

        try
        {
            sctx = (ServletContext)m_context.get( contextkey );
        }
        catch( final ContextException ce )
        {
            throw new ConfigurationException( "Cannot find ServletContext object in " +
                                              "application context", ce );
        }

        return new ServletOutputLogTarget( sctx );
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
