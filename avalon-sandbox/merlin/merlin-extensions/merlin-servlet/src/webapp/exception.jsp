<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<!--
The exception.jsp page handles cascading exception reporting.
-->

<%@ page isErrorPage="true" %>
<%@ page import="javax.servlet.jsp.JspException" %>
<%@ page import="javax.servlet.ServletException" %>
<%@ page import="org.apache.avalon.assembly.util.ExceptionHelper" %>
<%! String m_path; %>
<%! String m_message; %>
<%

    String message = ExceptionHelper.packException( exception.getClass().getName(), exception );
    m_message = message.replaceAll("\n","</br>");
    request.setAttribute( "urn:merlin:page.title", "Merlin Exception Report" );
    String query = request.getQueryString();
    String url = "" + request.getRequestURL();
    if( query != null )
    {
        m_path = url + "?" + query;
    }
    else
    {
        m_path = url;
    }
    pageContext.include("/header.jsp");
%>
  <table border="0" cellPadding="0" cellSpacing="0" width="100%"> 
   <tr bgcolor="lightsteelblue">
     <td valign="top" colspan="2"><p class="banner">Description</p></td>
   </tr>
  </table>
  
  <p>
An error occured while attempting to handle the request</br>
<a href="<%= m_path %>"><%= m_path %></a>
  </p>

  <table border="0" cellPadding="3" cellSpacing="0" width="100%"> 

    <tr bgcolor="lightsteelblue">
      <td valign="top"><p class="banner">Exception</p></td>
      <td><p class="banner">Message</p></td>
    </tr>
    <tr valign="top">
      <td>
        <p><%= m_message %></p>
      </td>
    </tr>

  </table>
  
<%
    pageContext.include("footer.jsp");
%>
