
<%@ taglib uri="/WEB-INF/merlin.tld" prefix="m" %>
<%@ page errorPage="exception.jsp" %>
<%! Object m_adapter; %>
<%! String m_base; %>
<%! String m_info; %>
<% 
    request.setAttribute("urn:merlin:page.title", "Merlin Index" );
    pageContext.include("/header.jsp");
    m_base = request.getContextPath();
    if( request.getPathInfo() == null )
    {
       m_info = "";
    }
    else
    {
        m_info = request.getPathInfo();
    }
%>

  <m:target url="<%=m_info%>">
    <p>INFO: <%=m_info%></p>
    <p>TARGET: <m:target feature="this"/></p>
    <p>CLASS: <m:target feature="class"/></p>
    <p>URL: <a href="<%=m_base%>/<%=m_info%>"><%=m_base%>/<%=m_info%></a></p>
  </m:target>

<% 
    pageContext.include("/footer.jsp");
%>

