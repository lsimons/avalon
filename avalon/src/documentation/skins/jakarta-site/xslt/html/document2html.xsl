<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:template match="document">
   <body>
    <xsl:if test="normalize-space(header/title)!=''">
      <title><xsl:value-of select="header/title"/></title>
            <table class="centered" align="center" width="100%">
              <tbody>
                <tr>
                  <td align="center">
                    <table class="title" cellspacing="0" cellpadding="1" border="0">
                      <tbody>
                        <tr>
                          <td bgcolor="#525d76">
                            <table class="centered" cellspacing="0" cellpadding="2" border="0" width="100%">
                              <tbody>
                                <tr>
                                  <td bgcolor="#f3dd61">
                                    <span class="title"><xsl:value-of select="header/title"/>
                                     <xsl:if test="header/subtitle">
                                      &#160;-&#160;<i><xsl:value-of select="header/subtitle"/></i>
                                     </xsl:if>
                                    </span>
                                  </td>
                                </tr>
                              </tbody>
                            </table>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </td>
                </tr>
              </tbody>
            </table>
      </xsl:if>

      <font color="#000000" size="-2">
        <p>
          <xsl:for-each select="header/person">
            <xsl:choose>
              <xsl:when test="position()=1">by</xsl:when>
              <xsl:otherwise>, </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
          <a href="mailto:{@email}">
            <xsl:value-of select="@name"/>
          </a>
        </p>
      </font>

      <xsl:apply-templates select="body"/>

    </body>
  </xsl:template>

  <xsl:template match="changes"/>

  <xsl:template match="action"/>

  <xsl:template match="body">
    <xsl:apply-templates/>
  </xsl:template>


  <xsl:template match="section">
    <xsl:call-template name="dosection">
       <xsl:with-param name="level"><xsl:value-of select="count(ancestor::section)+1"/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="dosection">
    <xsl:param name="level"/>

    <div align="right">
      <table border="0" cellpadding="2" cellspacing="0">
        <xsl:attribute name="width"><xsl:value-of select="number(100)-(1*(number($level)-1))"/>%</xsl:attribute>
        <tr>
          <td bgcolor="#525D76">
            <font color="#ffffff">
              <xsl:attribute name="size">
                <xsl:choose>
                  <xsl:when test="number($level)=1">+1</xsl:when>
                  <xsl:when test="number($level)=2">+0</xsl:when>
                  <xsl:otherwise>-<xsl:value-of select="number($level)-2"/></xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
              <font face="Arial,sans-serif"><b><xsl:value-of select="@title"/></b></font>
            </font>
          </td>
        </tr>
        <tr>
          <td>
              <br/>
              <xsl:apply-templates>
                <xsl:with-param name="level" select="number($level)+1"/>
              </xsl:apply-templates>
          </td>
        </tr>
      </table>
    </div><br/>

  </xsl:template>

  <xsl:template match="s1">
    <xsl:call-template name="dosection">
       <xsl:with-param name="level">1</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="s2">
    <xsl:call-template name="dosection">
       <xsl:with-param name="level">2</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="s3">
    <xsl:call-template name="dosection">
       <xsl:with-param name="level">3</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="s4">
    <xsl:call-template name="dosection">
       <xsl:with-param name="level">4</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="br">
    <br/>
  </xsl:template>

  <xsl:template match="strong|em">
    <em><xsl:apply-templates/></em>
  </xsl:template>

  <xsl:template match="ul">
    <ul><xsl:apply-templates/></ul>
  </xsl:template>

  <xsl:template match="li">
    <li><xsl:apply-templates/></li>
  </xsl:template>

  <xsl:template match="ol">
    <ol><xsl:apply-templates/></ol>
  </xsl:template>

  <xsl:template match="link">
    <a href="{@href}"><xsl:apply-templates/></a>
  </xsl:template>

  <xsl:template match="figure">
    <xsl:choose>
      <xsl:when test="@src">
    <div align="center">
      <table border="0" cellpadding="2" cellspacing="2">
        <tr>
          <td bgcolor="#525D76"><font color="#ffffff" size="0"><xsl:value-of select="@alt"/></font></td>
        </tr>
    <tr>
      <td><img border="0" alt="{@alt}" src="{@src}"/></td>
    </tr>
    <xsl:if test="@alt">
      <tr>
        <td><font size="-1"><ul><li><xsl:value-of select="@alt"/></li></ul></font></td>
      </tr>
    </xsl:if>

      </table>
    </div>

      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="fixme">
   <div align="center">
      <table border="1" cellpadding="2" cellspacing="2">
        <xsl:if test="title">
          <tr>
            <td bgcolor="#800000">
              <font color="#ffffff"><xsl:value-of select="title"/></font>
            </td>
          </tr>
        </xsl:if>
        <tr>
          <td bgcolor="#c0c0c0">
            <font color="#023264" size="-1"><xsl:apply-templates/></font>
          </td>
        </tr>
      </table>
    </div>
  </xsl:template>

  <xsl:template match="note">
    <note><xsl:apply-templates/></note>
  </xsl:template>

  <xsl:template match="warn">
    <div align="center">
      <table border="1" cellpadding="2" cellspacing="2">
        <xsl:if test="title">
          <tr>
            <td bgcolor="#800000">
              <font color="#ffffff"><xsl:value-of select="title"/></font>
            </td>
          </tr>
        </xsl:if>
        <tr>
          <td bgcolor="#c0c0c0">
            <font color="#023264" size="-1"><xsl:apply-templates/></font>
          </td>
        </tr>
      </table>
    </div>
  </xsl:template>

  <xsl:template match="code">
    <code><xsl:apply-templates/><xsl:if test="name(.)='function'"><xsl:text>()</xsl:text></xsl:if></code>
  </xsl:template>

  <xsl:template match="source">
    <div align="center">
      <table border="1" cellpadding="2" cellspacing="2">
        <tr>
          <td>
            <pre>
              <xsl:apply-templates/>
            </pre>
          </td>
        </tr>
      </table>
    </div>
  </xsl:template>

  <xsl:template match="table">
    <table border="0" cellpadding="2" cellspacing="2" width="100%">
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="th">
    <th><xsl:apply-templates/></th>
  </xsl:template>

  <xsl:template match="tr">
    <tr><xsl:apply-templates/></tr>
  </xsl:template>

  <xsl:template match="td">
    <td><xsl:apply-templates/></td>
  </xsl:template>

  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

