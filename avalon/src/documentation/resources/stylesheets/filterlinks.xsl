<?xml version="1.0"?>

<!--
This stylesheet is used by Forrest to filter out all references to the
javadocs, and in general, any links we don't want Forrest to 'crawl'.

In CVS Forrest, nothing further should be needed.  In 0.4, Forrest didn't allow
filterlinks.xsl to be overridden like this, so this file should be copied to
$FORREST_HOME/context/library/xslt/, overwriting the file already there.
-->
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">

  <xsl:template match="@src|@href|@background">

    <!-- Add other URLs to filter out here -->
    <xsl:if test="not(contains(.,'api/'))">
      <xsl:copy>
        <xsl:apply-templates select="."/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
