/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.frontends;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.phoenix.interfaces.Embeddor;
import org.apache.avalon.phoenix.components.embeddor.SingleAppEmbeddor;

/**
 * Composable servlet for easy life with <code>PhoenixServlet</code>.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public abstract class ComposableServlet
    extends HttpServlet
    implements Composable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ComposableServlet.class );

    private SingleAppEmbeddor m_embeddor;

    public void init()
        throws ServletException
    {
        super.init();

        m_embeddor = (SingleAppEmbeddor)getServletContext().getAttribute( Embeddor.ROLE );
        if ( null == m_embeddor )
        {
            final String message = REZ.getString( "servlet.error.load" );
            throw new ServletException( message );
        }

        try
        {
            compose( m_embeddor );
        }
        catch ( final ComponentException ce )
        {
            throw new ServletException( ce );
        }
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
    }
}