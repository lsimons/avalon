<?xml version="1.0"?>
<xsl:stylesheet 
    version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml" 
    xml:lang="en" 
>
  <xsl:template match="document">
    <html>
    <head>
      <title>
        <xsl:value-of select="properties/title" />
      </title>
      <link rel="stylesheet" href="print.css" type="text/css" media="print"></link>
      <link rel="stylesheet" type="text/css">
        <xsl:attribute name="href"><xsl:value-of select="document('navigation.xml', / )/project/body/menu//level" />resources/style.css</xsl:attribute>
      </link>
    </head>
      <xsl:apply-templates select="body" />
    </html>    
  </xsl:template>
  
  <xsl:template match="author">
    <meta name="author"><xsl:attribute name="content"><xsl:value-of select="." /></xsl:attribute></meta>
    <meta name="email"><xsl:attribute name="content"><xsl:value-of select="@email" /></xsl:attribute></meta>
  </xsl:template>
  
  <xsl:template match="body">
    <body>
      <table class="logobar" >
        <tr>
          <td class="feather" width="167px"  >
            <img>
              <xsl:attribute name="src"><xsl:value-of select="document('navigation.xml', / )/project/body/menu//level" />resources/feather.jpg</xsl:attribute>
            </img>
          </td>
          <td class="panel">
            <div class="project">Apache Avalon</div>
            <div class="title"><xsl:value-of select="../properties/title" /></div>
          </td>
        </tr>
      </table>
      
      <div class="icons">
        <img class="pdf" >
          <xsl:attribute name="src"><xsl:value-of select="document('navigation.xml', / )/project/body/menu//level" />resource/pdf.png</xsl:attribute>
        </img>
        <img class="printer" >
          <xsl:attribute name="src"><xsl:value-of select="document('navigation.xml', / )/project/body/menu//level" />resource/printer.png</xsl:attribute>
        </img>
      </div>
      
      <div class="categorybar">
        <a class="homecategory">
          <xsl:attribute name="href"><xsl:value-of select="document('navigation.xml', / )/project/body/menu//level" />index.html</xsl:attribute>
          Home
        </a>
        
        <xsl:apply-templates select="document('navigation.xml', / )/project/body//category/item" >
          <xsl:with-param name="dir" select="''" />
          <xsl:with-param name="class" select="'category'" />
        </xsl:apply-templates>
      </div>
      
      <div class="menubar" >
        <xsl:apply-templates select="document('navigation.xml', / )/project/body/menu" >
          <xsl:with-param name="dir" select="''" />
        </xsl:apply-templates>
      </div>
      
      <div class="content" >
        <xsl:apply-templates />
      </div>
    </body>
  </xsl:template>
  
  <xsl:template match="section">
    <div class="section" >
      <span class="section-header">
        <xsl:value-of select="@name" />
      </span>
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="subsection">
    <div class="subsection" >
      <span class="subsection-header">
        <xsl:value-of select="@name" />
      </span>
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="source">
    <pre class="source">
      <xsl:apply-templates />
    </pre>
    <div class="source-title">
      <span class="source-title" >
        <xsl:value-of select="name" />
      </span>
    </div>
  </xsl:template>
  
  <xsl:template match="p">
    <p>
      <xsl:apply-templates />
    </p>
  </xsl:template>
  
  <xsl:template match="menu">
    <xsl:param name="dir" />
    <xsl:choose>
      <xsl:when test="count( menu ) = 0" >
          <xsl:apply-templates select="menu" >
            <xsl:with-param name="dir" select="concat( $dir, '../')" />
          </xsl:apply-templates>
        <div class="menu">
          <xsl:apply-templates select="item" >
            <xsl:with-param name="dir" select="$dir" />
          </xsl:apply-templates>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <div class="menu">
          <xsl:apply-templates select="menu" >
            <xsl:with-param name="dir" select="concat( $dir, '../')" />
          </xsl:apply-templates>
          <xsl:apply-templates select="item" >
            <xsl:with-param name="dir" select="$dir" />
          </xsl:apply-templates>
        </div>
      </xsl:otherwise>
    </xsl:choose>
    
  </xsl:template>

  <xsl:template match="item">
    <xsl:param name="dir" />
    <xsl:param name="class" select="'menuitem'" />
    <a>
      <xsl:attribute name="class"><xsl:value-of select="$class" /></xsl:attribute>
      <xsl:attribute name="href">
        <xsl:value-of select="concat( $dir, @href )" />
      </xsl:attribute>
      <xsl:value-of select="@name" />
    </a>
  </xsl:template>

  <xsl:template match="footer" >
    <div class="footer" >
      <xsl:apply-templates />
    </div>
  </xsl:template>
  
  <xsl:template match="legal" >
    <span class="legal"><xsl:value-of select="." /></span>
  </xsl:template>
  
  <!-- Table generation  -->
  <xsl:template match="table" >
    <table>
      <xsl:apply-templates />
    </table>
  </xsl:template>
  
  <xsl:template match="tr" >
    <tr>
      <xsl:attribute name="row" >
        <xsl:choose>
          <xsl:when test="count(preceding-sibling::tr) mod 2 = 0" >odd</xsl:when>
          <xsl:otherwise>even</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      
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



