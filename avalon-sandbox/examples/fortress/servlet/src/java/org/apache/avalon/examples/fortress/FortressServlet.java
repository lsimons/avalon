/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.examples.fortress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import org.apache.avalon.examples.simple.Simple;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.*;



public class FortressServlet extends HttpServlet {
  private static final String CONTENT_TYPE = "text/html";
  //Initialize global variables
  public void init() throws ServletException {
  }
  //Process the HTTP Get request
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType(CONTENT_TYPE);

    ServletContext context = getServletContext();
    ServiceManager manager = (ServiceManager) context.getAttribute(ServiceManager.class.getName());

    String message = "Default Message";

    try {
      Simple simple = (Simple) manager.lookup(Simple.class.getName());
      message = simple.getName();
      manager.release(simple);
    }
    catch (ServiceException ex) {
      message = ex.getMessage();
    }

    PrintWriter out = response.getWriter();
    out.println("<html>");
    out.println("<head><title>FortressServlet</title></head>");
    out.println("<body bgcolor=\"#ffffff\">");
    out.println("<h1>FortressServlet</h1><hr/>");
    out.println("<p>Looked up the 'simple' service: ");
    out.println(message);
    out.println("</p></body></html>");

  }
  //Clean up resources
  public void destroy() {
  }
}