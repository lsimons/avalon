<?xml version="1.0"?>
<!--
 Copyright (C) MX4J.
 All rights reserved.

 This software is distributed under the terms of the MX4J License version 1.0.
 See the terms of the MX4J License in the documentation provided with this software.

 Author: Carlos Quiroz (tibu@users.sourceforge.net)
 Revision: $Revision: 1.1 $
			
			
 ** Phoenix Note **
 * This sheet was forked from that in MX4J (version 1.3 in their CVS) by Paul Hammant on 10th Aug 2002
 * All that is different:
 * 
 * 1) <a href="http://jakarta.apache.org/avalon/phoenix">Avalon-Phoenix homepage</a>&nbsp; <a href="http://mx4j.sourceforge.net">MX4J homepage</a>
 * 2) <big><big><big>Avalon-Phoenix Management application</big></big></big>
 * 3) Management via MX4J's HTTP Adaptor
 * 
 * It may be necessary to fork this sheet again and reapply the same changes.
 *
 **
			
			
			
			-->
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:import href="xalan-ext.xsl"/>
	<!-- Common head template -->
	<xsl:template name="head">
		<xsl:if test="$head.title">
			<title><xsl:value-of select="$head.title"/></title>
		</xsl:if>

		<xsl:if test="$html.stylesheet">
			<link rel="stylesheet"
						href="{$html.stylesheet}"
						type="{$html.stylesheet.type}"/>
		</xsl:if>

		<meta name="generator" content="MX4J HttpAdaptor, JMX, JMX implementation"/>
	</xsl:template>

	<!-- Common bottom template -->
	<xsl:template name="bottom">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td width="100%" class="fronttab">&nbsp;</td>
			</tr>
			<tr>
				<td class="darker"/>
			</tr>
			<tr>
				<td><div align="center" class="bottom"><a href="http://jakarta.apache.org/avalon/phoenix">Avalon-Phoenix homepage</a>&nbsp; <a href="http://mx4j.sourceforge.net">MX4J homepage</a></div></td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="serverview">
		<tr>
			<td class="darkline" align="right">
				<a href="/">Return to server view</a>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="mbeanview">
		<xsl:param name="objectname"/>
		<xsl:param name="text">Return to MBean view</xsl:param>
		<tr>
			<td class="darkline" align="right">
				<xsl:variable name="objectname-encode">
					<xsl:call-template name="uri-encode">
						<xsl:with-param name="uri" select="$objectname"/>
					</xsl:call-template>
				</xsl:variable>
				<a href="/mbean?objectname={$objectname-encode}"><xsl:value-of select="$text"/></a>
			</td>
		</tr>
	</xsl:template>

	<!-- Common tabs template -->
	<xsl:template name="tabs">
		<xsl:param name="selection" select="."/>
		<xsl:variable name="server.class">
			<xsl:choose>
				<xsl:when test="$selection='server'">fronttab</xsl:when>
				<xsl:otherwise>backtab</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="timer.class">
			<xsl:choose>
				<xsl:when test="$selection='timer'">fronttab</xsl:when>
				<xsl:otherwise>backtab</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="mbean.class">
			<xsl:choose>
				<xsl:when test="$selection='mbean'">fronttab</xsl:when>
				<xsl:otherwise>backtab</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="monitor.class">
			<xsl:choose>
				<xsl:when test="$selection='monitor'">fronttab</xsl:when>
				<xsl:otherwise>backtab</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="relation.class">
			<xsl:choose>
				<xsl:when test="$selection='relation'">fronttab</xsl:when>
				<xsl:otherwise>backtab</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="mlet.class">
			<xsl:choose>
				<xsl:when test="$selection='mlet'">fronttab</xsl:when>
				<xsl:otherwise>backtab</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<table cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td class="{$server.class}">
				<xsl:if test="not ($selection='server')"><a href="/serverbydomain" class="tabs">Server view</a></xsl:if>
				<xsl:if test="$selection='server'">Server view</xsl:if>
				</td>
				<td width="2"></td>
				<td class="{$mbean.class}"><a href="/mbean.html" class="tabs">MBean View</a></td>
				<td width="2"></td>
				<td class="{$timer.class}"><a href="/serverbydomain?instanceof=javax.management.timer.Timer&amp;template=timer" class="tabs">Timers</a></td>
				<td width="2"></td>
				<td class="{$monitor.class}"><a href="/serverbydomain?instanceof=javax.management.monitor.Monitor&amp;template=monitor" class="tabs">Monitors</a></td>
				<td width="2"></td>
				<td class="{$relation.class}"><a href="/relation?instanceof=javax.management.relation.Relation&amp;template=relation" class="tabs">Relations</a></td>
				<td width="2"></td>
				<td class="{$mlet.class}"><a href="/serverbydomain?instanceof=javax.management.loading.MLetMBean&amp;template=mlet" class="tabs">MLet</a></td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="toprow">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr><td colspan="2" class="darker"/></tr>
			<tr>
				<td colspan="2" class="topheading">
					<div align="left">
						<big><big><big>Avalon-Phoenix Management application</big></big></big>
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="topheading">
					<div align="left" class="sectionheading">
						Management via MX4J's HTTP Adaptor
					</div>
				</td>
			</tr>
			<tr><td colspan="2" class="darker"/></tr>
		</table>
		<br/>
	</xsl:template>
</xsl:stylesheet>
