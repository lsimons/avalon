package org.apache.avalon.attributes.test;

/**
 * @Dependency ( SampleService.class, "super-sample" )
 */
public class SuperSample {
    
    /**
     * @Dependency ( SampleService.class, "super-some-method-sample" )
     * @ThreadSafe ()
     */
    public void someMethod (int parameter) {
        
    }
    
}