package org.apache.avalon.attributes.test;

/**
 * @ThreadSafe ()
 * @Dependency ( SampleService.class, "sample" )
 */
public class Sample extends SuperSample implements SampleIFJoin {
    
    /**
     * @ThreadSafe ()
     */
    public Object field;
    
    public Object noAttributesInSubClass;
    
    /**
     * @Dependency ( SampleService.class, "sample-some-method1" )
     */
    public void someMethod () {
        
    }
    
    /**
     * @@Dependency ( SampleService.class, "sample-some-method2" )
     */
    public void someMethod (int parameter) {
        
    }
    
    public void methodWithNoAttributes () {
    }
    
    /**
     * @@Dependency ( SampleService.class, "inner-sample" )
     */
    public static class InnerSample {
    }
}