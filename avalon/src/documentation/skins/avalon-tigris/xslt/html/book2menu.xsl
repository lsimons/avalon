<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:param name="resource"/>

  <xsl:template match="book">
     <menu>
       <xsl:apply-templates/>
     </menu>
  </xsl:template>

  <xsl:template match="project">
  </xsl:template>

<!--  <xsl:templaatch="menu[position()=1]">
    <xsl:apply-templates/>
  </xsl:template>te match="menu[position()=1]">
    <xsl:apply-templates/>
  </xsl:template>-->

  <xsl:template match="menu">
     <div>
      <strong><xsl:value-of select="@label"/></strong>
       <xsl:apply-templates/>
     </div>
  </xsl:template>

  <xsl:template match="menu-item">
    <xsl:if test="not(@type) or @type!='hidden'">
     <div><!--<small>-->
       <xsl:choose>
         <xsl:when test="@href=$resource">
          <xsl:value-of select="@label"/>
         </xsl:when>
         <xsl:otherwise>
          <a href="{@href}"><xsl:value-of select="@label"/></a>
        </xsl:otherwise>
       </xsl:choose>
       <!--</small>--></div>
     </xsl:if>
  </xsl:template>

  <xsl:template match="external">
     <xsl:if test="not(@type) or @type!='hidden'">
     <div><small>
      <a href="{@href}"><xsl:value-of select="@label"/></a>
      </small></div>
    </xsl:if>
  </xsl:template>

  <xsl:template match="node()|@*" priority="-1"/>
</xsl:stylesheet>

