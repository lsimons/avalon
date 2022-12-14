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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.merlin.kernel.Kernel;

/**
 * Servlet implementing the Merlin Kernel interface.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 */
public class TestServlet extends MerlinServlet
{

    URL m_root;

    public void init()
      throws ServletException
    {
        super.init();
        m_root = (URL) getServletContext().getAttribute( Kernel.BASE_URL_KEY );
        if( m_root == null )
        {
            final String error = 
              "Unable to locate the Merlin Kernel base URL for the supplied context.";
            throw new ServletException( error );
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

	writer.println("<html>");
	writer.println("<head>");
	writer.println("<title>Merlin Test Servlet Page</title>");
	writer.println("</head>");
	writer.println("<body bgcolor=white>");

	writer.println("<table border=\"0\">");
	writer.println("<tr>");
	writer.println("<td>");
	writer.println("<h1>Merlin Test Servlet Page</h1>");
	writer.println("<p>This servlet extends the MerlinServlet.</p>");
	writer.println("</td>");
	writer.println("</tr>");
	writer.println("</table>");

	writer.println("<hr/>");

	writer.println("<table border=\"0\">");
	writer.println("<tr>");
	writer.println("<td>");
	writer.println( "URL:" );
	writer.println("</td>");
	writer.println("<td>");
	writer.println( m_root.toString() );
	writer.println("</td>");
	writer.println("</tr>");
	writer.println("</table>");
	writer.println("<hr/>");

	writer.println("<h1>Request Deconstructor</h1>");
	writer.println("<table border=\"0\">");
	writer.println("<tr><td>Path info:</td><td>" + request.getPathInfo() + "</td></tr>");
	writer.println("<tr><td>Translated:</td><td>" + request.getPathTranslated() + "</td></tr>");
	Enumeration names = request.getHeaderNames();
	while (names.hasMoreElements()) {
	    String name = (String) names.nextElement();
	    writer.println("<tr>");
	    writer.println("  <td>" + name + ":</td>");
	    writer.println("  <td>" + request.getHeader(name) + "</td>");
	    writer.println("</tr>");
	}
	writer.println("</table>");
	writer.println("<hr/>");

	writer.println("<h1>Context Deconstructor</h1>");
	writer.println("<table border=\"0\" width=\"100%\">");
	names = getServletContext().getAttributeNames();
	while (names.hasMoreElements())
      {
	    String name = (String) names.nextElement();
	    writer.println("<tr>");
	    writer.println("  <td>" + name + ":</td>");
	    writer.println("  <td>" + request.getHeader(name) + "</td>");
	    writer.println("</tr>");
	}
	writer.println("</table>");

	writer.println("</body>");
	writer.println("</html>");

    }
}
