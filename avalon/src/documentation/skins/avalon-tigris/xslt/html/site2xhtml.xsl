<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
<xsl:template match="/">
<html>
  <head><!-- This is a generated file.  Do not edit. -->

  <link type="text/css" href="skin/tigris.css" rel="stylesheet"/>
  <link type="text/css" href="skin/site.css"   rel="stylesheet"/>
  <link type="text/css" href="skin/print.css"  rel="stylesheet" media="print" />
			
  <meta name="author" value="Avalon Documentation Team"/>
  <meta name="email" value="avalon-dev@jakarta.apache.org"/>
      
  <title><xsl:value-of select="/site/document/title"/> - <xsl:value-of select="/site/document/subtitle"/></title>

  </head>
  
  <body marginwidth="0" marginheight="0" class="composite">
    
    <div id="banner">
      <table border="0" cellspacing="0" cellpadding="8" width="100%">
          <!-- TOP IMAGE -->
          <tbody><tr>
            <td></td>
            <td align="left">
              <a href="@group-logo.href@"><img src="@group-logo.src@" border="0"/></a>
            </td>
           <td></td>
           <td align="right">
              <a href="@project-logo.href@"><img src="@project-logo.src@" border="0"/></a>
           </td>
         </tr>
       </tbody>
      </table>
     </div>
      	<!-- end header -->
		
			<!-- breadcrumb trail (javascript-generated) -->
			<div id="breadcrumbs">
              <a href="@link1.href@" class="menu">@link1@ &gt;</a>
              <a href="@link2.href@" class="menu">@link2@ &gt;</a>
              <a href="@link3.href@" class="menu">@link3@</a>   
				<!-- -->
   				<script language="JavaScript1.2" type="text/javascript">
   			     <![CDATA[ 
					function sentenceCase(str) {
						var lower = str.toLowerCase();
						return lower.substr(0,1).toUpperCase() + lower.substr(1);
					}
					function getDirsAsArray() {
						var trail = document.location.pathname.split("/");
						var lastdir = (trail[trail.length-1].indexOf(".html") != -1)? trail.length-2 : trail.length-1;
						var urlprefix = "/avalon/";
						var postfix = " &gt"; 
						for(var i = 1; i <= lastdir; i++) {
							document.writeln('<a href=' + urlprefix + trail[i] + ' class="menu">' + sentenceCase(trail[i]) + '</a>'+postfix);
							urlprefix += trail[i] + "/";
							if(i == lastdir-1) postfix = ":";
						}
					}
					getDirsAsArray();
				]]>
				</script> 	
				<!-- -->
						
                <!--<script type="text/javascript" language="JavaScript" src="skin/breadcrumbs.js"></script>-->
			
			</div>
			<!-- end breadcrumb trail -->
			
   <!-- BODY -->
  
      <table border="0" cellspacing="0" cellpadding="8" width="100%" id="main">
        <tbody><tr valign="top">
        
             <!-- LEFT SIDE NAVIGATION -->
              <td id="leftcol" width="20%">
              <div id="navcolumn">
                 <xsl:copy-of select="/site/menu/node()|@*"/>
                </div></td>   
                 
                 
            <td><div id="bodycol"><div class="app">
               <h1><div class="h1"><xsl:value-of select="/site/document/title"/></div></h1>
               <h2><div class="h2"><xsl:value-of select="/site/document/subtitle"/></div></h2>
               <div class="h3">
               <xsl:copy-of select="/site/document/body/node()|@*"/>
            </div></div>
            
         
           </div>
          </td>  
        </tr>
      </tbody></table>

      <!-- FOOTER -->

     
      <div id="footer">
        <table border="0" cellspacing="0" cellpadding="4">
          <tbody><tr>
            <td align="left">Copyright &#x00A9; @year@ @vendor@. All Rights Reserved.</td>
            <td></td>
            <td><script language="JavaScript">
<![CDATA[<!-- 
document.write("last modified: " + document.lastModified); 
//  -->]]>
</script></td>
          </tr>
        </tbody></table>
      </div>

 </body>
</html>
</xsl:template>
</xsl:stylesheet>
