
<%@ taglib uri="/WEB-INF/merlin.tld" prefix="m" %>
<%@ page errorPage="exception.jsp" %>
<%! Object m_adapter; %>
<%! String m_base; %>
<%! String m_info; %>
<% 
    request.setAttribute("urn:merlin:page.title", "Merlin Index" );
    pageContext.include("header.jsp");
    m_base = request.getContextPath();
    if( request.getPathInfo() == null )
    {
       m_info = "/";
    }
    else
    {
        m_info = request.getPathInfo();
    }
%>

  <m:target url="<%=m_info%>">
    <p>INFO: <%=m_info%></p>
    <p>BLOCK: <m:target feature="this"/></p>
    <p>Activation: <m:target feature="activationPolicy"/></p>
    <p>Name: <m:target feature="name"/></p>
    <p>Partition: <m:target feature="partitionName"/></p>
    <p>Path: <m:target feature="path"/></p>
    <m:target resolve="contextProvider">
      <p>Name: <m:target feature="name"/></p>
      <p>Context Provider: <a href="<%=m_base%>/navigator/<m:target feature="path"/>"><m:target feature="name"/></a></p>
    </m:target>
    <p>Base: <%=m_base%></p>
    <p>URL: <a href="<%=m_base%>/navigator/<m:target feature="path"/>"><m:target feature="URL"/></a></p>
  </m:target>

<% 
    pageContext.include("footer.jsp");
%>

