/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.frontends;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.ExceptionUtil;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.interfaces.Embeddor;
import org.apache.avalon.phoenix.components.embeddor.SingleAppEmbeddor;

/**
 * Servlet frontends for SingleAppEmbeddor.
 *
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public class ServiceServlet
    extends HttpServlet
{
    private static final Resources REZ = ResourceManager.getPackageResources( ServiceServlet.class );

    private Parameters m_parameters;
    private SingleAppEmbeddor m_embeddor;

    private String getInitParameter( final String name, final String defaultValue )
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
            m_embeddor.execute();
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

    public void doGet( final HttpServletRequest request,
                       final HttpServletResponse response )
        throws IOException
    {
        doPost( request, response );
    }

    public void doPost( final HttpServletRequest request,
                        final HttpServletResponse response )
        throws IOException
    {
        final PrintWriter out = response.getWriter();

        out.println( "<html>" );
        out.println( "<body>" );

        out.println( "<h1>Parameters</h1>" );
        out.println( "<ul>" );
        final String[] paramNames = m_parameters.getNames();
        for ( int i = 0; i < paramNames.length; i++ )
        {
            final String name = paramNames[ i ];
            final String value = (String)m_parameters.getParameter( name, "" );
            out.println( "<li>" + name + ": " + value );
        }
        out.println( "</ul>" );

        out.println( "<h1>Loaded Blocks</h1>" );
        out.println( "<ul>" );
        final String[] blockNames = m_embeddor.list();
        for ( int i = 0; i < blockNames.length; i++ )
        {
            final String blockName = blockNames[ i ];
            out.println( "<li> " + blockName );
        }
        out.println( "</ul>" );

        out.println( "</body>" );
        out.println( "</html>" );
    }
}
