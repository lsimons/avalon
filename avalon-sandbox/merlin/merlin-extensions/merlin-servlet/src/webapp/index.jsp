
<%@ taglib uri="/WEB-INF/merlin.tld" prefix="merlin" %>
<%@ page errorPage="exception.jsp" %>
<%! Object m_adapter; %>
<% 
    request.setAttribute("urn:merlin:page.title", "Merlin Index" );
    pageContext.include("header.jsp");
%>

  <merlin:target url="/">
    <p>BLOCK: <merlin:target feature="this"/></p>
    <p>Activation: <merlin:target feature="activationPolicy"/></p>
    <p>Name: <merlin:target feature="name"/></p>
    <p>Partition: <merlin:target feature="partitionName"/></p>
    <p>Path: <merlin:target feature="path"/></p>
    <p>URL: <merlin:target feature="URL"/></p>
  </merlin:target>

<% 
    pageContext.include("footer.jsp");
%>

