
<%@ taglib uri="/WEB-INF/merlin.tld" prefix="merlin" %>
<%@ page errorPage="exception.jsp" %>
<%! Object m_adapter; %>
<% 
    request.setAttribute("urn:merlin:page.title", "Merlin Index" );
    pageContext.include("header.jsp");
%>
  <merlin:target url="/">
    <p>BLOCK: <merlin:target feature="this"/></p>
  </merlin:target>
<% 
    pageContext.include("footer.jsp");
%>

