<?xml version="1.0"?>
<!DOCTYPE blockinfo PUBLIC "-//PHOENIX/Block Info DTD Version 1.0//EN"
                  "http://jakarta.apache.org/phoenix/blockinfo_1_0.dtd">

<blockinfo>

  <!-- section to describe block -->
  <block>
    <XDtClass:ifHasClassTag tagName="phoenix:block" paramName="name"><name><XDtClass:classTagValue tagName="phoenix:block" paramName="name"/></name></XDtClass:ifHasClassTag>
    <version><XDtClass:classTagValue tagName="phoenix:block" paramName="version" default="1.0"/></version>
  </block>

  <!-- services that are offered by this block -->
  <services>
    <XDtClass:forAllClassTags tagName="phoenix:service">
    <service name="<XDtClass:classTagValue tagName="phoenix:service" paramName="name"/>"<XDtClass:ifHasClassTag tagName="phoenix:service" paramName="version"> version="<XDtClass:classTagValue tagName="phoenix:service" paramName="version"/>"</XDtClass:ifHasClassTag>/>
    </XDtClass:forAllClassTags>
  </services>

  <!-- services that are required by this block -->
  <dependencies>
    <XDtMethod:ifHasMethod name="compose"
                           parameters="org.apache.avalon.framework.component.ComponentManager">
      <XDtMethod:setCurrentMethod name="compose"
                                  parameters="org.apache.avalon.framework.component.ComponentManager">
        <XDtMethod:forAllMethodTags tagName="phoenix:dependency">
    <dependency>
      <service name="<XDtMethod:methodTagValue tagName="phoenix:dependency" paramName="name"/>"<XDtMethod:ifHasMethodTag tagName="phoenix:dependency" paramName="version"> version="<XDtMethod:methodTagValue tagName="phoenix:dependency" paramName="version"/>"</XDtMethod:ifHasMethodTag>/>
    </dependency>
        </XDtMethod:forAllMethodTags>
      </XDtMethod:setCurrentMethod>
    </XDtMethod:ifHasMethod>
    <XDtMethod:ifHasMethod name="service"
                           parameters="org.apache.avalon.framework.service.ServiceManager">
      <XDtMethod:setCurrentMethod name="service"
                                  parameters="org.apache.avalon.framework.service.ServiceManager">
        <XDtMethod:forAllMethodTags tagName="phoenix:dependency">
    <dependency>
      <service name="<XDtMethod:methodTagValue tagName="phoenix:dependency" paramName="name"/>"<XDtMethod:ifHasMethodTag tagName="phoenix:dependency" paramName="version"> version="<XDtMethod:methodTagValue tagName="phoenix:dependency" paramName="version"/>"</XDtMethod:ifHasMethodTag>/>
    </dependency>
        </XDtMethod:forAllMethodTags>
      </XDtMethod:setCurrentMethod>
    </XDtMethod:ifHasMethod>
  </dependencies>

</blockinfo>
