<?xml version="1.0"?>
<!DOCTYPE blockinfo PUBLIC "-//PHOENIX/Block Info DTD Version 1.0//EN" 
                  "http://jakarta.apache.org/phoenix/blockinfo_1_0.dtd">

<blockinfo>

  <!-- section to describe block -->
  <block>
    <version><XDtClass:classTagValue tagName="phoenix:version" default="1.0"/></version>
  </block>

  <!-- services that are offered by this block -->
  <services>
    <XDtClass:forAllClassTags tagName="phoenix:service">
      <service name="<XDtClass:classTagValue tagName="phoenix:service" paramName="name"/>"
               version="<XDtClass:classTagValue tagName="phoenix:service" paramName="version" default="1.0"/>" />
    </XDtClass:forAllClassTags>
  </services>

  <!-- services that are required by this block -->
  <dependencies>
    <XDtMethod:ifHasMethod name="compose" 
                           parameters="org.apache.avalon.framework.component.ComponentManager">
      <XDtMethod:setCurrentMethod name="compose"
                                  parameters="org.apache.avalon.framework.component.ComponentManager">
      <dependency>
        <service name="<XDtMethod:methodTagValue tagName="phoenix:dependency" paramName="name"/>" 
                 version="<XDtMethod:methodTagValue tagName="phoenix:dependency" paramName="version" default="1.0"/>"/>
      </dependency>
      </XDtMethod:setCurrentMethod>
    </XDtMethod:ifHasMethod>
  </dependencies>

</blockinfo>
