package org.apache.avalon.attributes.test;

/**
 * @Dependency ( SampleService.class, "sample-if-1-c" )
 */
public interface SampleIF1 {
    
    /**
     * @Dependency ( SampleService.class, "sample-if-1" )
     * @ThreadSafe ()
     */
    public void someMethod (int parameter);
}