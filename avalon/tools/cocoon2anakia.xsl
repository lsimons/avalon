<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="no"/>

    <!-- strip footer -->
    <xsl:template match="footer"/>

    <!-- header to properties -->
    <xsl:template match="header">
        <properties>
            <xsl:apply-templates/>
        </properties>
    </xsl:template>

    <!-- strip authors section -->
    <xsl:template match="warn"/>

    <!-- strip authors section -->
    <xsl:template match="authors">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- person to author -->
    <xsl:template match="person">
        <author email="{@email}">
            <xsl:value-of select="@name"/>
        </author>
    </xsl:template>

    <!-- update s1 section -->
    <xsl:template match="s1">
        <section name="{@title}">
            <xsl:apply-templates/>
        </section>
    </xsl:template>

    <!-- update s1 section -->
    <xsl:template match="s2">
        <subsection name="{@title}">
            <xsl:apply-templates/>
        </subsection>
    </xsl:template>

    <xsl:template match="node()|@*" priority="-1">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
