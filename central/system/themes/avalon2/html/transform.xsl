<?xml version="1.0"?>
<xsl:stylesheet 
    version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml" 
    xml:lang="en" 
>
  <xsl:param name="directory" />
  <xsl:param name="file" />
  <xsl:param name="fullpath" />
  <xsl:param name="svn-location" />
  <xsl:param name="copyright" />
  
  <xsl:template match="document">
    <html>
    <head>
      <title>
        <xsl:value-of select="properties/title" />
      </title>
      <link rel="stylesheet" href="print.css" type="text/css" media="print"></link>
      <xsl:variable name="x" select="document('navigation.xml', / )/project/body//menu/level" />
      <link rel="stylesheet" type="text/css">
        <xsl:attribute name="href"><xsl:value-of select="$x[position() = last()]" />resources/style.css</xsl:attribute>
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
    <xsl:variable name="x" select="document('navigation.xml', / )/project/body//menu/level" />
    <body>
      <table class="logobar" >
        <tr>
          <td class="feather" >
            <img>
              <xsl:attribute name="src"><xsl:value-of select="$x[position() = last()]" />resources/feather.jpg</xsl:attribute>
            </img>
          </td>
          <td class="panel">
            <div class="project">Apache Avalon</div>
            <div class="title"><xsl:value-of select="../properties/title" /></div>
          </td>
        </tr>
      </table>
      
      <xsl:choose>
        <xsl:when test="$directory = ''" >
          <xsl:call-template name="create-bar">
            <xsl:with-param name="homeclass" select="'homecategory-selected'" />
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="create-bar">
            <xsl:with-param name="homeclass" select="'homecategory'" />
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
      <div class="menubar" >
        <xsl:apply-templates select="document('navigation.xml', / )/project/body/menu" >
          <xsl:with-param name="dir" select="''" />
        </xsl:apply-templates>
      </div>
      
      <div class="content" >
        <xsl:apply-templates />
      
        <div class="footer" >
          <span class="copyright"><xsl:value-of select="$copyright" /></span>
          <div class="views" >
            <a  id="xmllink" class="viewlink" >
              <xsl:attribute name="href"><xsl:value-of select="$svn-location" /></xsl:attribute>
              <img>
                <xsl:attribute name="src"><xsl:value-of select="$x[position() = last()]" />resources/xml.gif</xsl:attribute>
              </img>
            </a>
          </div>
        </div>
      </div>
    </body>
  </xsl:template>

  <xsl:template name="create-bar" >
    <xsl:param name="homeclass" />
    <div class="categorybar">
      <xsl:attribute name="dir"><xsl:value-of select="$directory" /></xsl:attribute>
      <a>
        <xsl:attribute name="class"><xsl:value-of select="$homeclass" /></xsl:attribute>
        <xsl:variable name="x" select="document('navigation.xml', / )/project/body//menu/level" />
        <xsl:attribute name="href"><xsl:value-of select="$x[position() = last()]" />index.html</xsl:attribute>
        Home
      </a>
      <xsl:variable name="x" select="document('navigation.xml', / )/project/body//category" />
      <xsl:apply-templates select="$x/item" >
        <xsl:with-param name="dir" select="$x/../level" />
        <xsl:with-param name="class" select="'category'" />
      </xsl:apply-templates>
    </div>
  </xsl:template>
    
  <xsl:template match="a">
    <a>
      <xsl:attribute name="class">doclink</xsl:attribute>
      <xsl:attribute name="href"><xsl:value-of select="@href" /></xsl:attribute>
      <xsl:value-of select="." />
    </a>
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
  
  <xsl:template match="menu">
    <xsl:param name="dir" />
    <xsl:choose>
      
      <!-- Don't include the Root level menu choices in the menu bar
           unless it is the root directory 
      -->
      <xsl:when test="count( menu ) = 0 and not( $directory = '' )" >
      </xsl:when>
      
      <xsl:otherwise>
        <div class="menu">
          <xsl:apply-templates select="menu" >
            <xsl:with-param name="dir" select="concat( $dir, '../')" />
          </xsl:apply-templates>
          <xsl:if test="count( item ) = 0" >
            <xsl:attribute name="border">none</xsl:attribute>
            <span class="dummy" />
          </xsl:if>
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
      <xsl:choose>
        <xsl:when test="@selected = true() or ( contains( $file, @href ) and local-name( ../.. ) = 'body' )" >
          <xsl:attribute name="class"><xsl:value-of select="$class" />-selected</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="class"><xsl:value-of select="$class" /></xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:attribute name="href">      
        <xsl:choose>
          <xsl:when test="contains( @href, '.html' )" >
            <xsl:value-of select="concat( $dir, @href )" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="concat( concat( $dir, @href ), 'index.html')" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:value-of select="@name" />
    </a>
  </xsl:template>

  <xsl:template match="footer" >
  </xsl:template>
  
  <xsl:template match="legal" >
    <span class="legal"><xsl:value-of select="." /></span>
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
  
  <!-- Create a box to be used for searching the site on Google. -->
  <xsl:template match="search" >
      <div class="search" >
      </div>
  </xsl:template>

  <xsl:template match="*" >
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>
      
</xsl:stylesheet> 
