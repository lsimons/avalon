<%@ page import="org.apache.avalon.framework.service.ServiceManager"%>
<%@ page import="org.apache.avalon.examples.simple.Simple"%>
<!--

  Copyright 2004 Apache Software Foundation
  Licensed  under the  Apache License,  Version 2.0  (the "License");
  you may not use  this file  except in  compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed  under the  License is distributed on an "AS IS" BASIS,
  WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
  implied.

  See the License for the specific language governing permissions and
  limitations under the License.

-->

<html>
 <head>
  <title>Fortress Servlet Example</title>
 </head>
 <body bgcolor="#ffffff">
 <h1>Fortress Servlet Example</h1>
 <hr/>
 <p>
   Attempting to lookup service simple in a JSP
 </p>
 <%
   ServiceManager manager = (ServiceManager) getServletContext().getAttribute(ServiceManager.class.getName());
   Simple simple = (Simple) manager.lookup(Simple.class.getName());
   String message = simple.getName();
  %>
  <p>Simple Message: <%= message %></p>
  <p>
    Now click <a href="fortress">here</a> to see a servlet version.
  </p>
 </body>
</html>
