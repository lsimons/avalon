<?xml version="1.0"?>

<!--
This stylesheet filters all references to the javadocs, the samples, and any
other links that shouldn't be traversed.  It is referenced from the customized
sitemap.
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:template match="@src|@href|@background">
        <xsl:if test="not(contains(., '/api/')) and
                      not(starts-with(., 'api/')) and not(. = '..')">
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
