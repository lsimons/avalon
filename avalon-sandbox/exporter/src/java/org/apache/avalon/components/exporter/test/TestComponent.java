package org.apache.avalon.components.exporter.test;


/**
 * @avalon.component name="test"
 * @avalon.service type="org.apache.avalon.components.exporter.test.TestService"
 * @avalon.stage id="urn:exporter:altrmi"
 */
public class TestComponent
    implements TestService
{
  public TestComponent() {
  }

  public String getValue() {
    return "Hello World!";
  }
}