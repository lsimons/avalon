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
import java.io.IOException;
import java.util.Map;
import java.util.Hashtable;
import java.net.URL;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.merlin.Kernel;
import org.apache.avalon.merlin.KernelCriteria;
import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.provider.Builder;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Factory;
import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.repository.main.DefaultInitialContext;
import org.apache.avalon.repository.main.DefaultBuilder;
import org.apache.avalon.util.exception.ExceptionHelper;
import org.apache.avalon.util.env.Env;


/**
 * Servlet that handles the establishment of a Merlin Kernel
 * and registration of the kernel base URL under the servlet 
 * context using the key.
 * 
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 */
public class MerlinServlet extends HttpServlet
{
    //----------------------------------------------------------
    // static
    //----------------------------------------------------------

    private static final String MERLIN_PROPERTIES = "merlin.properties";

    private static final String IMPLEMENTATION_KEY = "merlin.implementation";

    //----------------------------------------------------------
    // state
    //----------------------------------------------------------

    private KernelCriteria m_criteria;

    private Block m_block;

    private Kernel m_kernel;

    //----------------------------------------------------------
    // Servlet
    //----------------------------------------------------------

    /**
     * Initializes Servlet by the web server container.
     *
     * @exception ServletException if an error occurs
     */
    public void init()
        throws ServletException
    {
        ClassLoader classloader = MerlinServlet.class.getClassLoader();

        try
        {
            String path = getServletContext().getRealPath( "." );
            File base = new File( path );
            File system = getMerlinSystemRepository();

            InitialContext context = 
                   new DefaultInitialContext( 
                     base, classloader, null, system, null );

            Artifact artifact = 
              DefaultBuilder.createImplementationArtifact( 
                classloader, 
                null,
                base, 
                MERLIN_PROPERTIES, 
                IMPLEMENTATION_KEY );

            Builder builder = 
              new DefaultBuilder( context, artifact );
            Factory factory = builder.getFactory();
            m_criteria = (KernelCriteria) factory.createDefaultCriteria();

            m_criteria.put( "merlin.server", "true" );
            m_criteria.put( "merlin.info", "true" );
            m_criteria.put( "merlin.debug", "true" );

            //
            // this is where we customize content based on web.xml
            // (currently not implemented - lets see what we can do with 
            // with merlin.properties first of all)
            //

            m_kernel = (Kernel) factory.create( m_criteria );
            
            System.out.println("kernel established");

            getServletContext().setAttribute( 
              "merlin-root-block", m_kernel.getBlock() );
        }
        catch( Throwable e )
        {
            final String error = ExceptionHelper.packException( e, true );
            System.out.println( error );
            throw new ServletException( error, e );
        }
    }

    /**
     * Disposes of container manager and container instance.
     */
    public void destroy()
    {
        if( m_kernel != null )
        {

            System.out.println("tearing down");
            
            try
            {
                m_kernel.shutdown();
            }
            catch( Throwable e )
            {
                final String error =
                  "Runnable kernel shutdown failure.";
                final String msg = ExceptionHelper.packException( error, e, true );
                throw new RuntimeException( msg, null );
            }
            finally
            {
                m_kernel = null;
            }
        }
    }

    /**
     * Respond to a GET request for the content produced by
     * this servlet.  This method should be overidden in a
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are producing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
      throws IOException, ServletException {

        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        String context = request.getContextPath();

        writer.println("<html>");

        writer.println("<head>");
        writer.println(
          "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + context + "/css/merlin.css\">" );
        writer.println("<title>Merlin Servlet Info</title>" );
        writer.println("</head>");
        writer.println("<body background=\"" + context + "/images/Paper.gif\">" );

        writer.println("<table border=\"1\" width=\"100%\" cellpadding=\"1\">");
        writer.println("<tr bgcolor=\"7171A5\">");
        writer.println("<td align=\"left\" colspan=\"2\">");
        writer.println("<div class=\"page-title-text\">Merlin Servlet Info</div>");
        writer.println("</td>");
        writer.println("</tr>");

        writer.println("<tr class=\"table-header-row\" bgcolor=\"EEEEFF\">" );
        writer.println("<td width=\"20%\">Criteria</td>");
        writer.println("<td>Value</td>");
        writer.println("</tr>");

        row( writer, "${merlin.anchor}", m_criteria.getAnchorDirectory() );
        row( writer, "${merlin.config}", m_criteria.getConfigDirectory() );
        row( writer, "${merlin.context}", m_criteria.getContextDirectory() );
        row( writer, "${merlin.home}", m_criteria.getHomeDirectory() );
        row( writer, "${merlin.kernel}", m_criteria.getKernelURL() );
        row( writer, "${merlin.lang}", m_criteria.getLanguageCode() );
        row( writer, "${merlin.override}", m_criteria.getOverridePath() );
        row( writer, "${merlin.repository}", m_criteria.getRepositoryDirectory() );
        row( writer, "${merlin.system}", m_criteria.getSystemDirectory() );
        row( writer, "${merlin.temp}", m_criteria.getTempDirectory() );
        row( writer, "${merlin.dir}", m_criteria.getWorkingDirectory() );
        row( writer, "${merlin.autostart}", m_criteria.isAutostartEnabled() );
        row( writer, "${merlin.debug}", m_criteria.isDebugEnabled() );
        row( writer, "${merlin.info}", m_criteria.isInfoEnabled() );
        row( writer, "${merlin.server}", m_criteria.isServerEnabled() );

        writer.println("</table>");

        writer.println("</body>");
        writer.println("</html>");
    }

   /**
    * Return the merlin system repository root directory.
    *
    * @param line the command line construct
    * @return the merlin system root repository directory
    */
    private static File getMerlinSystemRepository()
    {
        return new File( getMerlinHome( ), "system" );
    }

   /**
    * Return the merlin home directory.
    * @return the merlin install directory
    */
    private static File getMerlinHome()
    {
        return new File( getMerlinHomePath() );
    }

   /**
    * Return the merlin home directory path.
    * @return the merlin install directory path
    */
    private static String getMerlinHomePath()
    {
        try
        {
            String merlin = 
              System.getProperty( 
                "merlin.home", 
                Env.getEnvVariable( "MERLIN_HOME" ) );
            if( null != merlin ) return merlin;
            return System.getProperty( "user.home" ) 
              + File.separator + ".merlin";
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to access MERLIN_HOME environment.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }

    private void row( PrintWriter writer, String item, boolean val )
    {
        if( val )
        {
            row( writer, item, "true" );
        }
        else
        {
            row( writer, item, "false" );
        }
    }

    private void row( PrintWriter writer, String item, Object val )
    {
        writer.println("<tr class=\"table-row\">");
        writer.println("<td>");
        writer.println( item );
        writer.println("</td>");
        writer.println("<td>");
        if( null != val )
        {
            writer.println( val.toString() );
        }
        else
        {
            writer.println( "&nbsp;" );
        }
        writer.println("</td>");
        writer.println("</tr>");
    }
}
