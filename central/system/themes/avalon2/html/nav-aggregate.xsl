<?xml version="1.0"?>
<xsl:stylesheet 
    version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xml:lang="en" 
    lang="en"
>
  <xsl:template match="project" >
    <project>
      <xsl:apply-templates />  
    </project>
  </xsl:template>
  
  <xsl:template match="title" >
    <title>
      <xsl:value-of select="." />
    </title>
  </xsl:template>
  
  <xsl:template match="body" >
    <body>
      <xsl:apply-templates />  
    </body>
  </xsl:template>
  
  <xsl:template match="menu" >
    <xsl:param name="level" select="''" />
    <menu>
      <level><xsl:value-of select="$level" /></level>
      <xsl:choose >
        <xsl:when test="count( ../links ) = 0" >
          <xsl:apply-templates select="document('../navigation.xml', / )/project/body/menu" >
            <xsl:with-param name="level" select="concat( $level, '../' )" />
          </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
          <category>
            <xsl:apply-templates select="../links/item" />
          </category>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates />  
    </menu>
  </xsl:template>

  <xsl:template match="item" >
      <xsl:copy-of select="." />
  </xsl:template>
  
</xsl:stylesheet> 



