/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 2003 The Apache Software Foundation. All rights reserved.

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
package org.apache.avalon.merlin.servlet;

import java.io.File;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.avalon.assembly.locator.impl.DefaultLocator;
import org.apache.avalon.merlin.block.Block;
import org.apache.avalon.merlin.kernel.Kernel;
import org.apache.avalon.merlin.kernel.impl.DefaultKernel;

/**
 * Servlet that handles the establishment of a Merlin Kernel
 * and registration of the kernel base URL under the servlet 
 * context using the key {@link Kernel.BASE_URL_KEY}.
 * 
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 */
public class MerlinServlet extends HttpServlet
{
    private DefaultKernel m_kernel;

    /**
     * Initializes Servlet by the web server container.
     *
     * @exception ServletException if an error occurs
     */
    public void init()
        throws ServletException
    {
        try
        {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            if( getServletContext() == null )
            {
                final String error =
                 "Cannot proceed. Container returned a null servlet context.";
                throw new ServletException( error );
            }

            String homePath = getServletContext().getRealPath( "." );
            File home = new File( homePath );

            String blockPath = getInitParameter( "block", "BLOCK-INF/block.xml" );
            URL block = new File( home, blockPath ).toURL();

            DefaultLocator context = new DefaultLocator();
            context.put( "urn:merlin:home", home );
            context.put( "urn:merlin:system", home );
            context.put( "urn:merlin:classloader.common", loader );
            context.put( "urn:merlin:classloader.system", loader );
            context.put( "urn:merlin:debug", "WARN" );
            context.put( "urn:merlin:logging.priority", "INFO" );
            context.put( "urn:merlin:block.url", block );
            context.makeReadOnly();

            m_kernel = new DefaultKernel();
            m_kernel.contextualize( context );
            m_kernel.initialize();

            getServletContext().setAttribute( Kernel.BASE_URL_KEY, m_kernel.getURL() );

            log( "kernel established" );
        }
        catch( Exception e )
        {
            throw new ServletException( "Initialization error.", e );
        }
    }

    /**
     * Disposes of container manager and container instance.
     */
    public void destroy()
    {
        if( m_kernel != null )
        {
            m_kernel.shutdown();
            m_kernel = null;
        }
    }

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
}
