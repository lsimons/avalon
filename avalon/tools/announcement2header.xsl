<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" indent="yes"/>

  <xsl:template match="changes">
    <xsl:variable name="version" select="@version"/>
    <xsl:apply-templates select="document(@file,/)/changes/release[attribute::version=string($version)]"/>
  </xsl:template>

  <xsl:template match="announcement">
    <h1 align="center"><xsl:value-of select="title"/><xsl:text> Released</xsl:text></h1>
    <xsl:apply-templates select="abstract"/>

    <xsl:for-each select="project">
      <h2>About <xsl:value-of select="title"/></h2>
      <xsl:apply-templates select="."/>
    </xsl:for-each>

  </xsl:template>

  <xsl:template match="project">
    <p><xsl:apply-templates select="description"/></p>

    <p>For more information about <xsl:value-of select="title"/>, please go to
    <a><xsl:attribute name="href"><xsl:value-of select="@site"/></xsl:attribute>
    <xsl:value-of select="@site"/></a>.</p>

    <!-- print out ChangeLog if present --> 
    <xsl:if test="changes">
      <h3>ChangeLog for <xsl:value-of select="title"/></h3>
      <xsl:apply-templates select="changes"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="abstract">
    <div align="center">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="link">
    <xsl:value-of select="."/>
    <xsl:text> (</xsl:text>
    <xsl:value-of select="@href"/>
    <xsl:text>)</xsl:text>
  </xsl:template>

  <xsl:template match="release">
    <ul>
    <xsl:for-each select="action">
      <li> 
        <xsl:value-of select="normalize-space(.)"/>
        <xsl:if test="@dev">
          <xsl:text>[</xsl:text><xsl:value-of select="@dev"/><xsl:text>]</xsl:text>
        </xsl:if>
      </li>
    </xsl:for-each>
    </ul>
  </xsl:template>

</xsl:stylesheet>
