<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="no"/>

    <!-- book to project -->
    <xsl:template match="book">
        <project name="{@software}"
            href="http://jakarta.apache.org/avalon/FIXME">
            <title>
                <xsl:value-of select="@software"/>
            </title>
            <body>
                <xsl:apply-templates/>
            </body>
        </project>
    </xsl:template>

    <xsl:template match="menu">
        <menu name="{@label}">
            <xsl:apply-templates/>
        </menu>
    </xsl:template>

    <xsl:template match="menu-item">
        <xsl:if test="@type='external'">
            <item name="{@label}" href="{@href}"/>
        </xsl:if>
        <xsl:if test="not(@type='external')">
            <item name="{@label}" href="/{@href}"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="project">
        <xsl:if test="@type='external'">
            <item name="{@label}" href="{@href}"/>
        </xsl:if>
        <xsl:if test="not(@type='external')">
            <item name="{@label}" href="/{@href}"/>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
