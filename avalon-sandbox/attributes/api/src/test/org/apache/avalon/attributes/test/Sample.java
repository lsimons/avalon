package org.apache.avalon.attributes.test;

/**
 * @ThreadSafe ()
 * @Dependency ( SampleService.class, "sample" )
 */
public class Sample extends SuperSample {
    
    /**
     * @ThreadSafe ()
     */
    public Object aaaa;
 
    
    /**
     * @Dependency ( SampleService.class, "sample-ctor1" )
     */
    public Sample () {
        
    }
    
    /**
     * @Dependency ( SampleService.class, "sample-ctor2" )
     */
    public Sample (String input, String[][] array) {
        
    }
    
    /**
     * @Dependency ( SampleService.class, "sample-some-method1" )
     */
    public void someMethod () {
        
    }
    
    /**
     * @Dependency ( SampleService.class, "sample-some-method2" )
     */
    public void someMethod (int parameter) {
        
    }
}