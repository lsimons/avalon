/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.frontends;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.ExceptionUtil;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.phoenix.components.embeddor.SingleAppEmbeddor;
import org.apache.avalon.phoenix.interfaces.Embeddor;

/**
 * Servlet frontends for SingleAppEmbeddor.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class PhoenixServlet
    extends HttpServlet
    implements Runnable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( PhoenixServlet.class );

    private Parameters              m_parameters;
    private SingleAppEmbeddor       m_embeddor;

    private String getInitParameter( final String name,
                                     final String defaultValue )
    {
        final String value = getInitParameter( name );
        if ( null == value )
        {
            return defaultValue;
        }
        else
        {
            return value;
        }
    }
    
    public void init()
        throws ServletException
    {
        super.init();

        //TODO: configuring with more parameters.
        final ServletContext context = getServletContext();
        final String logDestination = context.getRealPath( getInitParameter( "log-destination", "/WEB-INF/logs/phoenix.log" ) );
        final String logPriority = getInitParameter( "log-priority", "INFO" );
        final String appName = getInitParameter( "application-name", "default" );
        final String appLoc = context.getRealPath( getInitParameter( "application-location", "/WEB-INF/" + appName ) );

        m_parameters = new Parameters();
        m_parameters.setParameter( "log-destination", logDestination );
        m_parameters.setParameter( "log-priority", logPriority );
        m_parameters.setParameter( "application-name", appName );
        m_parameters.setParameter( "application-location", appLoc );

        try
        {
            m_embeddor = new SingleAppEmbeddor();
            if ( m_embeddor instanceof Parameterizable )
            {
                ((Parameterizable)m_embeddor).parameterize( m_parameters );
            }
            m_embeddor.initialize();

            new Thread( this ).start();
        }
        catch ( final Throwable throwable )
        {
            log( REZ.getString( "main.exception.header" ) );
            log( "---------------------------------------------------------" );
            log( ExceptionUtil.printStackTrace( throwable ) );
            log( "---------------------------------------------------------" );
            log( REZ.getString( "main.exception.footer" ) );
            throw new ServletException( throwable );
        }

        getServletContext().setAttribute( Embeddor.ROLE, m_embeddor );
    }

    public void run()
    {
        try
        {
            m_embeddor.execute();
        }
        catch ( final Throwable throwable )
        {
            log( REZ.getString( "main.exception.header" ) );
            log( "---------------------------------------------------------" );
            log( ExceptionUtil.printStackTrace( throwable ) );
            log( "---------------------------------------------------------" );
            log( REZ.getString( "main.exception.footer" ) );

            final String message = REZ.getString( "servlet.error.execute" );
            throw new CascadingRuntimeException( message, throwable );
        }
    }

    public void destroy()
    {
        getServletContext().removeAttribute( Embeddor.ROLE );

        try
        {
            m_embeddor.dispose();
            m_embeddor = null;
            m_parameters = null;
        }
        catch ( final Throwable throwable )
        {
            log( REZ.getString( "main.exception.header" ) );
            log( "---------------------------------------------------------" );
            log( ExceptionUtil.printStackTrace( throwable ) );
            log( "---------------------------------------------------------" );
            log( REZ.getString( "main.exception.footer" ) );
        }
    }
}
