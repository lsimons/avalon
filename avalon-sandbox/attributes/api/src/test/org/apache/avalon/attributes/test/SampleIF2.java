package org.apache.avalon.attributes.test;

/**
 * @Dependency ( SampleService.class, "sample-if-2-c" )
 */
public interface SampleIF2 {
    
    /**
     * @Dependency ( SampleService.class, "sample-if-2" )
     * @ThreadSafe ()
     */
    public void someMethod (int parameter);
}