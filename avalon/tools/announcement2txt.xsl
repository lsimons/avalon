<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="text" indent="no"/>

  <xsl:template match="changes">
    <xsl:variable name="version" select="@version"/>
    <xsl:apply-templates select="document(@file,/)/changes/release[attribute::version=string($version)]"/>
  </xsl:template>

  <xsl:template name="print-title">
    <xsl:param name="title"/>
<xsl:variable name="text" select="normalize-space($title)"/>
<xsl:value-of select="$text"/><xsl:text>
</xsl:text>
    <xsl:call-template name="line">
      <xsl:with-param name="len" select="string-length($text)"/>
    </xsl:call-template>
    <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="announcement">
    <text>
      <xsl:call-template name="print-title">
        <xsl:with-param name="title"><xsl:value-of select="title"/> Released</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="word-wrap">
        <xsl:with-param name="text" select="normalize-space(abstract)"/>
        <xsl:with-param name="count" select="0"/>
      </xsl:call-template>
    <xsl:text>

</xsl:text>
      <xsl:call-template name="print-title">
        <xsl:with-param name="title">About Avalon</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="word-wrap">
        <xsl:with-param name="text" select="normalize-space(description)"/>
        <xsl:with-param name="count" select="0"/>
      </xsl:call-template>
    <xsl:text>

</xsl:text>
      <xsl:for-each select="subproject">
        <xsl:call-template name="print-title">
          <xsl:with-param name="title">About <xsl:value-of select="title"/></xsl:with-param>
        </xsl:call-template>
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </text>
  </xsl:template>

  <xsl:template match="subproject">
    <xsl:call-template name="word-wrap">
      <xsl:with-param name="text" select="normalize-space(description)"/>
      <xsl:with-param name="count" select="0"/>
    </xsl:call-template>
    <xsl:text>

</xsl:text>
    <xsl:text>
For more information about </xsl:text>
    <xsl:value-of select="title"/>
    <xsl:text>, please go to
</xsl:text>
    <xsl:value-of select="@site"/>
    <xsl:text>

ChangeLog for </xsl:text>
    <xsl:value-of select="title"/>
    <xsl:text>

</xsl:text>
    <xsl:apply-templates select="changes"/>
    <xsl:text>
Downloads for </xsl:text><xsl:value-of select="title"/> available at 

<xsl:value-of select="downloads/@base"/>/latest
  </xsl:template>

  <xsl:template match="p">
    <xsl:apply-templates/>
    <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="link">
    <xsl:value-of select="."/>
    <xsl:text> (</xsl:text>
    <xsl:value-of select="@href"/>
    <xsl:text>)</xsl:text>
  </xsl:template>

  <xsl:template match="release">
    <xsl:for-each select="action">
      <xsl:text>*) </xsl:text>
      <xsl:call-template name="word-wrap">
        <xsl:with-param name="text" select="normalize-space(.)"/>
        <xsl:with-param name="count" select="0"/>
      </xsl:call-template><xsl:text> </xsl:text>
      <xsl:if test="@dev">
        <xsl:text>[</xsl:text><xsl:value-of select="@dev"/><xsl:text>]</xsl:text>
      </xsl:if>
      <xsl:text>

</xsl:text>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="line">
    <xsl:param name="len"/>
    <xsl:if test="number($len) > 0">
      <xsl:text>-</xsl:text>
      <xsl:call-template name="line">
        <xsl:with-param name="len" select="number($len)-1"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="word-wrap">
    <xsl:param name="text"/>
    <xsl:param name="count"/>
    <xsl:choose>
      <xsl:when test="$count > 40">
        <xsl:text>
</xsl:text>
        <xsl:call-template name="word-wrap">
          <xsl:with-param name="text" select="$text"/>
          <xsl:with-param name="count" select="0"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="not(contains($text,' '))">
        <xsl:text> </xsl:text>
        <xsl:value-of select="$text"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="word" select="substring-before($text,' ')"/>
        <xsl:variable name="remainder" select="substring-after($text,' ')"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="$word"/>
        <xsl:if test="string-length($word) > 0">
          <xsl:call-template name="word-wrap">
            <xsl:with-param name="text" select="$remainder"/>
            <xsl:with-param name="count" select="$count + string-length($word)"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
