<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="javax.servlet.jsp.JspException" %>
<%! String m_title; %>
<%! String[] m_options; %>
<%! String[] m_actions; %>
<%! String m_banner; %>
<%! String m_home; %>
<%
    m_title = (String) request.getAttribute("urn:merlin:page.title");
    m_options = (String[]) request.getAttribute("urn:merlin:page.options");
    m_actions = (String[]) request.getAttribute("urn:merlin:page.actions");
    m_banner = (String) request.getAttribute("urn:merlin:page.banner");

    if( m_title == null ) m_title = "Untitled Page";
    if( m_options == null ) m_options = new String[0];
    if( m_actions == null ) m_actions = new String[0];
    if( m_banner == null ) m_banner = "";

    m_home = (String) request.getAttribute("urn:merlin:page.home");
    if( m_home == null ) m_home = "/index.jsp";
%>

<html>

  <head>
    <link rel="stylesheet" type="text/css" 
      href="<%= request.getContextPath() %>/css/merlin.css" title="index" />
    <title><%= m_title %></title>
    <meta HTTP-EQUIV="expires" content="0"/>
    <meta name="description" content="<%= m_title %>"/>
    <meta name="keywords" content="merlin"/>
  </head>

  <body background="<%= request.getContextPath() %>/images/Paper.gif">

    <table width="100%" border="0" >
      <tr bgcolor="7171A5"> 
        <td width="56%" > 
          <div class="page-title-text" align="left">
            <%= m_title %> 
          </div>
        </td>
      </tr>
      <tr>
        <td>
          <p class="caption"><a href="<%= m_home %>">home</a></p>
        </td>
      </tr>
      <tr height="2" bgcolor="lightsteelblue">
        <td colspan="3" height="2"></td>
      </tr>
      <tr>
        <td>
          <p class="caption">
          <%
              for( int i=0; i<m_options.length; i++ )
              {
                  String option = m_options[i];
                  %>
                  <%= option %>
                  <%
                  if( i < ( m_options.length -1 ) )
                  {
                      %>
                      <%= " | " %>
                      <%
                  }
                  else
                  {
                      %>
                      <%= " &nbsp; " %>
                      <%
                  }
              }
          %>
          </p>
        </td>
     </tr>
     <tr height="2" bgcolor="lightsteelblue">
       <td colspan="3" height="2"></td>
     </tr>
     <tr>
        <td align="left">
          <p class="caption">
          <%
              for( int i=0; i<m_actions.length; i++ )
              {
                  String action = m_actions[i];
                  %>
                  <%= action %>
                  <%
                  if( i < ( m_actions.length -1 ) )
                  {
                      %>
                      <%= " | " %>
                      <%
                  }
                  else
                  {
                      %>
                      <%= " &nbsp; " %>
                      <%
                  }
              }
          %>
          </p>
        </td>
      </tr>
    </table>

    <table cellPadding="0" cellSpacing="0" width="100%">
       <tr>
         <td bgcolor="lightsteelblue">
           <p class="banner"> <%= m_banner %> </p>
         </td>
       </tr>
    </table>
