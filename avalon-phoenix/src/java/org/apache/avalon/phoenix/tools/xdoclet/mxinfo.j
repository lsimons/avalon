<?xml version="1.0"?>
<!DOCTYPE mxinfo PUBLIC "-//PHOENIX/Mx Info DTD Version 1.0//EN"
                  "http://jakarta.apache.org/phoenix/mxinfo_1_0.dtd">

<mxinfo>

    <XDtClass:ifHasClassTag tagName="phoenix:mx-topic" paramName="name">
    <topic name="<XDtClass:classTagValue tagName="phoenix:mx-topic" paramName="name"/>" >

      <!-- attributes -->
      <XDtMethod:forAllMethods> 
      <XDtMethod:ifHasMethodTag tagName="phoenix:mx-attribute" >
      <attribute
        name="<XDtMethod:propertyName/>"
        <XDtMethod:ifHasMethodTag tagName="phoenix:mx-description" >
        description="<XDtMethod:methodTagValue tagName="phoenix:mx-description" />"
        </XDtMethod:ifHasMethodTag>
        <XDtMethod:ifDoesntHaveMethodTag tagName="phoenix:mx-description" >
        description="<XDtMethod:methodComment no-comment-signs="true" />"
        </XDtMethod:ifDoesntHaveMethodTag>
        <XDtMethod:ifHasMethodTag tagName="phoenix:mx-isWriteable" >
        isWriteable="<XDtMethod:methodTagValue tagName="phoenix:mx-isWriteable" />"
        </XDtMethod:ifHasMethodTag>
        type="<XDtMethod:methodType/>"
      />
      </XDtMethod:ifHasMethodTag>
      </XDtMethod:forAllMethods>

      <!-- operations -->
      <XDtMethod:forAllMethods> 
      <XDtMethod:ifHasMethodTag tagName="phoenix:mx-operation" >
      <operation
        name="<XDtMethod:methodName/>"
        <XDtMethod:ifHasMethodTag tagName="phoenix:mx-description" >
        description="<XDtMethod:methodTagValue tagName="phoenix:mx-description" />"
        </XDtMethod:ifHasMethodTag>
        <XDtMethod:ifDoesntHaveMethodTag tagName="phoenix:mx-description" >
        description="<XDtMethod:methodComment no-comment-signs="true" />"
        </XDtMethod:ifDoesntHaveMethodTag>
        type="<XDtMethod:methodType/>"
      >
        <XDtParameter:forAllMethodParams>
        <param
          name="<XDtParameter:methodParamName/>"
          description="<XDtParameter:methodParamDescription/>"
          type="<XDtParameter:methodParamType/>"
        />
        </XDtParameter:forAllMethodParams>
      </operation>
      </XDtMethod:ifHasMethodTag>
      </XDtMethod:forAllMethods>

    </topic>
    </XDtClass:ifHasClassTag>

    <XDtClass:ifHasClassTag tagName="phoenix:mx-proxy" paramName="class">
    <proxy name="<XDtClass:classTagValue tagName="phoenix:mx-proxy" paramName="class"/>" />
    </XDtClass:ifHasClassTag>

</mxinfo>
