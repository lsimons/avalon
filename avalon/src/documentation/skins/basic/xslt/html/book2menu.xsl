<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:param name="resource"/>

  <xsl:template match="project">
    <menu>
      <xsl:apply-templates/>
    </menu>
  </xsl:template>

  <xsl:template match="body">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="menu">
     <br/><b><xsl:value-of select="@name"/></b>&#160;;
     <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="item">-&#160;
       <xsl:choose>
         <xsl:when test="@href=$resource">
          <xsl:value-of select="@name"/>
         </xsl:when>
         <xsl:otherwise>
          <a href="{@href}"><xsl:value-of select="@name"/></a>
        </xsl:otherwise>
       </xsl:choose>
  </xsl:template>

  <xsl:template match="node()|@*" priority="-1"/>
</xsl:stylesheet>

