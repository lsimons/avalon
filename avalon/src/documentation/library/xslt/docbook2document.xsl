<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

 
  <xsl:template match="book">
  <document>
   <header>
    <title><xsl:value-of select="title"/></title>
    <subtitle><xsl:value-of select="subtitle"/></subtitle>
   </header>     
   <body>
    <section>
      <xsl:apply-templates select="dedication"/>
      <xsl:apply-templates select="bookinfo"/>
    </section>
    </body>
  </document>        
  </xsl:template>

  <xsl:template match="chapter|article|appendix">
  <document>
   <header>
    <title><xsl:value-of select="title"/></title>
    <subtitle><xsl:value-of select="subtitle"/></subtitle>
    <authors>       
      <xsl:for-each select="//author">
       <xsl:element name = "person">
         <xsl:attribute  name = "id" ><xsl:value-of select="id" /></xsl:attribute>
         <xsl:attribute  name = "name" >
           <xsl:if test="honorific"><xsl:value-of select="honorific"/>. </xsl:if>
           <xsl:if test="firstname"><xsl:value-of select="firstname"/> </xsl:if>
           <xsl:value-of select="surname"/>
         </xsl:attribute>
         <xsl:attribute  name = "email" ><xsl:value-of select="address/email" /></xsl:attribute>
       </xsl:element>
      </xsl:for-each>      
    </authors>   
   </header>     

   <body>
      <xsl:apply-templates/>
   </body>
  </document>        
  </xsl:template>

  <xsl:template match="title|subtitle"/>

  <xsl:template match="/author">
  <document>
   <header>
    <title>
        <xsl:value-of select="honorific"/><xsl:text>. </xsl:text>
        <xsl:value-of select="firstname"/><xsl:text> </xsl:text>
        <xsl:value-of select="surname"/>
     </title>
   </header>     
   <body>
    <section title="Affiliations">
        <table width="100%">
          <tr>
            <td>
                <ul>
                  <xsl:apply-templates select="affiliation"/>
                </ul>
            </td>
          </tr>
        </table>
    </section>
    <xsl:apply-templates select="authorblurb"/>
   </body>
  </document>   
  </xsl:template>

  <xsl:template match="affiliation">
    <li>
      <xsl:text>[</xsl:text><xsl:value-of select="shortaffil"/><xsl:text>] </xsl:text>
      <b><xsl:value-of select="jobtitle"/></b>
      <i><xsl:value-of select="orgname"/><xsl:if test="orgdiv"><xsl:text>/</xsl:text><xsl:value-of select="orgdiv"/></xsl:if></i>
    </li>
  </xsl:template>

  <xsl:template match="authorblurb">
    <section title="Bio">
      <table width="100%">
        <tr>
          <td>
            <ul>
              <xsl:apply-templates/>
            </ul>
          </td>
        </tr>
      </table>
   </section>
  </xsl:template>

  <xsl:template match="honorific|firstname|surname|orgdiv|orgname|shortaffil|jobtitle"/>

  <xsl:template match="revhistory">
    <document>
     <header>
      <title>Revision History</title>
     </header>
     <body>
      <section>
        <table width="100%">
          <xsl:variable name="unique-revisions" 
            select="revision[not(revnumber=preceding-sibling::revision/revnumber)]/revnumber"/>
          <xsl:variable name="base" select="."/>
          <xsl:for-each select="$unique-revisions">
          <tr>
            <td>
                <b>Revision <xsl:value-of select="."/> 
                   (<xsl:value-of select="$base/revision[revnumber=current()]/date"/>)
                </b>
            </td>
          </tr>
          <tr>
            <td>
              <font color="#000000" face="arial,helvetica,sanserif"><br/>
                <ul>
                  <xsl:apply-templates select="$base/revision[revnumber=current()]"/>
                </ul>
              </font>
            </td>
          </tr>
          </xsl:for-each>
        </table>
    </section>
    </body>
    </document>        
  </xsl:template>

  <xsl:template match="para">
    <p><xsl:apply-templates/></p>
  </xsl:template>

  <xsl:template match="emphasis"><em><xsl:apply-templates/></em></xsl:template>

  <xsl:template match="revision">
    <li>
      <xsl:choose>
        <xsl:when test="@revisionflag='added'">
          <img align="absmiddle" alt="added" src="images/add.jpg"/>
        </xsl:when>
        <xsl:when test="@revisionflag='changed'">
          <img align="absmiddle" alt="changed" src="images/update.jpg"/>
        </xsl:when>
        <xsl:when test="@revisionflag='deleted'">
          <img align="absmiddle" alt="deleted" src="images/remove.jpg"/>
        </xsl:when>
        <xsl:when test="@revisionflag='off'">
          <img align="absmiddle" alt="off" src="images/fix.jpg"/>
        </xsl:when>
        <xsl:otherwise>
          <img align="absmiddle" alt="changed" src="images/update.jpg"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="revremark"/>
      <xsl:text> (</xsl:text><xsl:value-of select="authorinitials"/><xsl:text>)</xsl:text>
    </li>
  </xsl:template>

  <xsl:template match="revnumber|revremark|authorinitials|date"/>

  <xsl:template match="section">
    <section>
     <xsl:if test = "title">
       <xsl:attribute  name = "title" ><xsl:value-of select="title"/></xsl:attribute>
     </xsl:if>
     <xsl:apply-templates/>
    </section>

  </xsl:template>

  <xsl:template match="bookinfo">
      <table border="0" cellpadding="2" cellspacing="0" width="100%">
        <tr>
          <td bgcolor="#525D76">
            <font color="#ffffff" face="arial,helvetica,sanserif" size="+1">
              <b><xsl:value-of select="edition"/></b><xsl:text> </xsl:text>
              <i><font size="0">pub. <xsl:value-of select="pubdate"/></font></i>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            <font color="#000000" face="arial,helvetica,sanserif">
              <br/>
              <xsl:apply-templates/>
            </font>
          </td>
        </tr>
      </table>
  </xsl:template>

  <xsl:template match="dedication">
      <table border="0" cellpadding="2" cellspacing="0" width="100%">
        <tr>
          <td bgcolor="#525D76">
            <font color="#ffffff" face="arial,helvetica,sanserif" size="+1">
              <b>Dedication</b>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            <font color="#000000" face="arial,helvetica,sanserif">
              <br/>
              <xsl:apply-templates/>
            </font>
          </td>
        </tr>
      </table>
  </xsl:template>

  <xsl:template match="edition|pubdate|year|holder"/>

  <xsl:template match="copyright">
    <p>Copyright &#x00A9;<xsl:value-of select="year"/> by <xsl:value-of select="holder"/>.<br/>
      <i>All rights reserved.</i>
    </p>
  </xsl:template>

  <xsl:template match="legalnotice">
      <table border="1" cellpadding="2" cellspacing="2">
        <tr>
          <td><xsl:apply-templates/></td>
        </tr>
      </table>
  </xsl:template>

  <xsl:template match="programlisting">
      <table border="1" cellpadding="2" cellspacing="2">
        <tr>
          <td>
            <pre>
              <xsl:apply-templates/>
            </pre>
          </td>
        </tr>
      </table>
  </xsl:template>

  <xsl:template match="orderedlist"><ol><xsl:apply-templates/></ol></xsl:template>

  <xsl:template match="listitem"><li><xsl:apply-templates/></li></xsl:template>

  <xsl:template match="itemizedlist"><ul><xsl:apply-templates/></ul></xsl:template>

  <xsl:template match="classname|function|parameter"><code><xsl:apply-templates/><xsl:if test="name(.)='function'"><xsl:text>()</xsl:text></xsl:if></code></xsl:template>

  <xsl:template match="blockquote">
      <table border="1" cellpadding="2" cellspacing="2">
        <xsl:if test="title">
          <tr>
            <td bgcolor="#525D76">
              <font color="#ffffff"><xsl:value-of select="title"/></font>
            </td>
          </tr>
        </xsl:if>
        <tr>
          <td bgcolor="#c0c0c0">
            <font color="#023264" size="-1"><xsl:apply-templates/></font>
          </td>
        </tr>
      </table>
  </xsl:template>

  <xsl:template match="warning">
      <table border="1" cellpadding="2" cellspacing="2">
        <xsl:if test="title">
          <tr>
            <td bgcolor="#800000">
              <font color="#ffffff"><xsl:value-of select="title"/></font>
            </td>
          </tr>
        </xsl:if>
        <tr>
          <td bgcolor="#c0c0c0">
            <font color="#023264" size="-1"><xsl:apply-templates/></font>
          </td>
        </tr>
      </table>
  </xsl:template>

  <xsl:template match="ulink"><link href="{@uri}"><xsl:apply-templates/></link></xsl:template>

  <xsl:template match="footnote"><sup><a href="#{generate-id(.)}"><xsl:value-of select="generate-id(.)"/></a></sup></xsl:template>

  <xsl:template match="footnote" mode="base">
      <a name="{generate-id(.)}"/><xsl:value-of select="generate-id(.)"/><xsl:text>) </xsl:text><i><xsl:value-of select="."/></i>
  </xsl:template>

  <xsl:template match="figure">
      <table>
        <tr>
          <td><xsl:value-of select="title"/></td>
        </tr>
        <xsl:apply-templates/>
      </table>
  </xsl:template>

  <xsl:template match="graphic">
    <tr>
      <td><img alt="{@srccredit}" src="{@fileref}"/></td>
    </tr>
    <xsl:if test="@srccredit">
      <tr>
        <td><ul><li><xsl:value-of select="@srccredit"/></li></ul></td>
      </tr>
    </xsl:if>
  </xsl:template>

  <xsl:template match="table">
    <table width="100%">
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="tgroup">
    <xsl:apply-templates select="thead|tbody|tfoot"/>
  </xsl:template>

  <xsl:template match="thead">
    <xsl:apply-templates select="row" mode="head"/>
  </xsl:template>

  <xsl:template match="row" mode="head">
    <th><xsl:apply-templates/></th>
  </xsl:template>

  <xsl:template match="row">
    <tr><xsl:apply-templates/></tr>
  </xsl:template>

  <xsl:template match="tbody|tfoot">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="entry">
    <td align="left" bgcolor="#a0ddf0" valign="top">
      <font color="#000000" face="arial,helvetica,sanserif" size="-1"><xsl:apply-templates/></font>
    </td>
  </xsl:template>

  <xsl:template match="trademark"><xsl:apply-templates/><sup>TM</sup></xsl:template>

  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

