package org.apache.avalon.attributes.test;

/**
 * @Dependency ( SampleService.class, "super-sample" )
 */
public class SuperSample {
    
    /**
     * @ThreadSafe ()
     * @Dependency ( SampleService.class, "super-field" )
     */
    public Object field;
    
    /**
     * @Dependency ( SampleService.class, "super-noattrs" )
     */
    public Object noAttributesInSubClass;
    
    /**
     * @Dependency ( SampleService.class, "sample-ctor1" )
     */
    public SuperSample () {
        
    }
    
    /**
     * @Dependency ( SampleService.class, "sample-ctor2" )
     */
    public SuperSample (String input, String[][] array) {
        
    }
    
    /**
     * @Dependency ( SampleService.class, "super-some-method-sample" )
     * @ThreadSafe ()
     */
    public void someMethod (int parameter) {
        
    }
    
}