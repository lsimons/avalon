<?xml version="1.0"?>
<xsl:stylesheet 
    version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml" 
    xml:lang="en" 
    lang="en"
>
  <xsl:template match="document">
    <html>
      <xsl:apply-templates />
    </html>    
  </xsl:template>
  
  <xsl:template match="properties">
    <head>
      <link rel="stylesheet" href="print.css" type="text/css" media="print"></link>
      <link rel="stylesheet" href="resources/style.css" type="text/css"></link>
      <xsl:apply-templates />
    </head>
  </xsl:template>
  
  <xsl:template match="author">
    <meta name="author"><xsl:attribute name="content"><xsl:value-of select="."/></xsl:attribute></meta>
    <meta name="email"><xsl:attribute name="content"><xsl:value-of select="@email"/></xsl:attribute></meta>
  </xsl:template>
  
  <xsl:template match="title">
    <title>
      <xsl:value-of select="." />
    </title>
  </xsl:template>
  
  <xsl:template match="body">
    <body>
<!-- NH: I want to use XSL parameters, but it doesn's seem to work. 
-->
      <span class="projecttitle">Apache Avalon</span>
      <div class="sword" >
        <img class="sword-tip" src="resources/sword-tip.png" />
      
        <img class="sword-blade" width="590px" src="resources/sword-blade.png" />
        
        <img class="sword-cross" src="resources/sword-cross.png" />
      
        <img class="sword-handle" src="resources/sword-handle.png" />
        
      </div>
      
      <div class="icons">
        <img class="pdf" src="/images/pdf.png" />
        <img class="printer" src="/images/printer.png" />
      </div>
      
      <span class="doctitle"><xsl:value-of select="/document/properties/title"/></span>
      
      <img class="shield" src="resources/shield.png" />
      <div class="menubar" >
        <xsl:apply-templates select="document('navigation.xml', / )/project/body/menu" />
        <div class="links" >
          <span class="link-title">Resources</span>
          <xsl:apply-templates select="document('navigation.xml', / )/project/body/links/*" />
        </div>
      </div>
      
      <div class="content" >
        <xsl:apply-templates />
      </div>
    </body>
  </xsl:template>
  
  <xsl:template match="section">
    <div class="section" >
      <img class="section-prefix" src="resources/sword-small.png" />
      <span class="section-header">
        <xsl:value-of select="@name" />
      </span>
    </div>
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="subsection">
    <div class="subsection" >
      <span class="subsection-header">
        <img class="subsection-prefix" src="resources/subsection-prefix.png" />
        <xsl:value-of select="@name" />
        <img class="subsection-postfix" src="resources/subsection-postfix.png" />
      </span>
    </div>
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="source">
    <div class="source" >
      <xsl:apply-templates />
    </div>
  </xsl:template>
  
  <xsl:template match="p">
    <p>
      <xsl:apply-templates />
    </p>
  </xsl:template>
  
  <xsl:template match="menu">
    <div class="menu">
      <span class="menu-title"><xsl:value-of select="@name" /></span>
      <xsl:apply-templates select="./item" />
    </div>
  </xsl:template>

  <xsl:template match="menu/item">
    <div class="menuitem">
      <a><xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute><xsl:value-of select="@name" /></a>
    </div>
  </xsl:template>
  
  <xsl:template match="links">
    <div class="links" >
      <xsl:apply-templates />
    </div>
  </xsl:template>
  
  <xsl:template match="links/item">
    <div class="linkitem">
      <a><xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute><xsl:value-of select="@name" /></a>
    </div>
  </xsl:template>

  <!-- Table generation  -->
  <xsl:template match="table" >
    <table>
      <xsl:apply-templates />
    </table>
  </xsl:template>
  
  <xsl:template match="tr" >
    <tr>
      <xsl:apply-templates />
    </tr>
  </xsl:template>
  
  <xsl:template match="th" >
    <th>
      <xsl:apply-templates />
    </th>
  </xsl:template>
  
  <xsl:template match="td" >
    <td>
      <xsl:apply-templates />
    </td>
  </xsl:template>
  
  <!-- Inlines -->
  <xsl:template match="strong" >
    <strong>
      <xsl:apply-templates />
    </strong>
  </xsl:template>
  
  <xsl:template match="em" >
    <em>
      <xsl:apply-templates />
    </em>
  </xsl:template>
  
  <!-- Create a box to be used for searching the site on Google. -->
  <xsl:template match="search" >
      <div class="search" >
      </div>
  </xsl:template>
  
</xsl:stylesheet> 
