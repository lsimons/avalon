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
package org.apache.avalon.fortress.examples.servlet;

import org.apache.avalon.fortress.ContainerManager;
import org.apache.avalon.fortress.impl.DefaultContainerManager;
import org.apache.avalon.framework.container.ContainerUtil;

import java.io.IOException;
import java.io.File;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

/**
 * Servlet based Fortress container example.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version $Id: servlet.java,v 1.7 2003/05/30 20:55:10 bloritsch Exp $
 */
public final class servlet extends HttpServlet
{
    private ServletContainer m_container;
    private ContainerManager m_containerManager;

    /**
     * Initializes Servlet and creates a <code>ServletContainer</code> instance
     *
     * @exception ServletException if an error occurs
     */
    public void init()
        throws ServletException
    {
        super.init();

        try
        {
            final org.apache.avalon.fortress.util.FortressConfig config = new org.apache.avalon.fortress.util.FortressConfig();
            config.setContainerClass( ServletContainer.class );
            config.setContextDirectory( getServletContext().getRealPath("/") );
            config.setWorkDirectory( (File) getServletContext().getAttribute( "javax.servlet.context.tempdir" ) );
            config.setContainerConfiguration( "resource://org/apache/avalon/fortress/examples/servlet/ServletContainer.xconf" );
            config.setLoggerManagerConfiguration( "resource://org/avalon/excalibur/fortress/examples/servlet/ServletContainer.xlog" );

            m_containerManager = new DefaultContainerManager( config.getContext() );
            ContainerUtil.initialize( m_containerManager );

            m_container = (ServletContainer)m_containerManager.getContainer();
        }
        catch( Exception e )
        {
            throw new ServletException( "Error during initialization", e );
        }
    }

    /**
     * Pass all servlet requests through to container to be handled. In a more
     * complex system, there could be multiple containers that handle different
     * requests, or a main controlling container with subcontainers for different
     * requests.
     *
     * @param request a <code>ServletRequest</code> instance
     * @param response a <code>ServletResponse</code> instance
     * @exception IOException if an IO error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void service( ServletRequest request, ServletResponse response )
        throws IOException, ServletException
    {
        m_container.handleRequest( request, response );
    }

    /**
     * Disposes of container manager and container instance.
     */
    public void destroy()
    {
        ContainerUtil.dispose( m_containerManager );
    }
}
