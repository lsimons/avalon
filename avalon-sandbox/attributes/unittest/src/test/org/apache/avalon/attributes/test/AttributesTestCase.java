package org.apache.avalon.attributes.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.avalon.attributes.Attributes;
import org.apache.avalon.attributes.AttributeIndex;
import junit.framework.TestCase;

public class AttributesTestCase extends TestCase {
    
    public void testClassAttributes () throws Exception {
        
        /**
         * @Dependency ( SampleService.class, "super-sample" )
         */
        Class c = SuperSample.class;
        assertEquals (1, Attributes.getAttributes (c).size ());
        assertEquals (1, Attributes.getAttributes (c, Dependency.class).size ());
        assertTrue (Attributes.hasAttributeType (c, Dependency.class));
        assertTrue (Attributes.hasAttribute (c, new Dependency ( SampleService.class, "super-sample" )));
    }
        
    public void testMethodAttributes () throws Exception {
        /**
         * @Dependency ( SampleService.class, "super-some-method-sample" )
         * @ThreadSafe ()
         */
        Method m = SuperSample.class.getMethod ("someMethod", new Class[]{ Integer.TYPE });
        assertEquals (2, Attributes.getAttributes (m).size ());
        assertEquals (1, Attributes.getAttributes (m, Dependency.class).size ());
        assertEquals (1, Attributes.getAttributes (m, ThreadSafe.class).size ());
        assertTrue (Attributes.hasAttributeType (m, Dependency.class));
        assertTrue (Attributes.hasAttributeType (m, ThreadSafe.class));
        assertTrue (Attributes.hasAttribute (m, new Dependency ( SampleService.class, "super-some-method-sample" )));
        assertTrue (Attributes.hasAttribute (m, new ThreadSafe ()));
    }
        
    public void testFieldAttributes () throws Exception {
        /**
         * @ThreadSafe ()
         * @Dependency ( SampleService.class, "super-field" )
         */
        Field f = SuperSample.class.getField ("field");
        assertEquals (2, Attributes.getAttributes (f).size ());
        assertEquals (1, Attributes.getAttributes (f, ThreadSafe.class).size ());
        assertEquals (1, Attributes.getAttributes (f, Dependency.class).size ());
        assertTrue (Attributes.hasAttribute (f, new ThreadSafe ()));
        assertTrue (Attributes.hasAttribute (f, new Dependency ( SampleService.class, "super-field" ) ));
        assertTrue (Attributes.hasAttributeType (f, ThreadSafe.class));
        assertTrue (Attributes.hasAttributeType (f, Dependency.class));
    }
    
    public void testDefaultConstructorAttributes () throws Exception {
        /**
         * @Dependency ( SampleService.class, "sample-ctor1" )
         */
        Constructor c = SuperSample.class.getDeclaredConstructor (new Class[0]);
        assertEquals (1, Attributes.getAttributes (c).size ());
        assertEquals (1, Attributes.getAttributes (c, Dependency.class).size ());
        assertTrue (Attributes.hasAttributeType (c, Dependency.class));
        assertTrue (Attributes.hasAttribute (c, new Dependency ( SampleService.class, "sample-ctor1" )));
    }
    
    public void testConstructorAttributes () throws Exception {
        /**
         * @Dependency ( SampleService.class, "sample-ctor2" )
         */
        Constructor c = SuperSample.class.getDeclaredConstructor (new Class[]{ String.class, (new String[0][0]).getClass () } );
        assertEquals (1, Attributes.getAttributes (c).size ());
        assertEquals (1, Attributes.getAttributes (c, Dependency.class).size ());
        assertTrue (Attributes.hasAttributeType (c, Dependency.class));
        assertTrue (Attributes.hasAttribute (c, new Dependency ( SampleService.class, "sample-ctor2" )));
    }
    
    public void testClassInheritance () throws Exception {
        Class c = Sample.class;
        assertEquals (5, Attributes.getAttributes (c).size ());
        assertEquals (4, Attributes.getAttributes (c, Dependency.class).size ());
        assertTrue (Attributes.hasAttributeType (c, Dependency.class));
        assertTrue (Attributes.hasAttributeType (c, ThreadSafe.class));
        assertTrue (Attributes.hasAttribute (c, new Dependency ( SampleService.class, "sample" )));
        assertTrue (Attributes.hasAttribute (c, new Dependency ( SampleService.class, "super-sample" )));
        assertTrue (Attributes.hasAttribute (c, new Dependency ( SampleService.class, "sample-if-1-c" )));
        assertTrue (Attributes.hasAttribute (c, new Dependency ( SampleService.class, "sample-if-2-c" )));
        assertTrue (Attributes.hasAttribute (c, new ThreadSafe ()));
    }
    
    public void testMethodInheritance () throws Exception {
        Method m = Sample.class.getMethod ("someMethod", new Class[]{ Integer.TYPE });
        assertEquals (4, Attributes.getAttributes (m).size ());
        assertEquals (4, Attributes.getAttributes (m, Dependency.class).size ());
        assertTrue (Attributes.hasAttributeType (m, Dependency.class));
        assertTrue (Attributes.hasAttribute (m, new Dependency ( SampleService.class, "super-some-method-sample" )));
        assertTrue (Attributes.hasAttribute (m, new Dependency ( SampleService.class, "sample-some-method2" ) ));
        assertTrue (Attributes.hasAttribute (m, new Dependency ( SampleService.class, "sample-if-1" ) ));
        assertTrue (Attributes.hasAttribute (m, new Dependency ( SampleService.class, "sample-if-2" ) ));
    }
    
    public void testFieldNonInheritance () throws Exception {
        Field f = SuperSample.class.getField ("field");
        assertEquals (2, Attributes.getAttributes (f).size ());
        assertEquals (1, Attributes.getAttributes (f, ThreadSafe.class).size ());
        assertEquals (1, Attributes.getAttributes (f, Dependency.class).size ());
        assertTrue (Attributes.hasAttributeType (f, ThreadSafe.class));
        assertTrue (Attributes.hasAttributeType (f, Dependency.class));
        assertTrue (Attributes.hasAttribute (f, new ThreadSafe ()));
        assertTrue (Attributes.hasAttribute (f, new Dependency ( SampleService.class, "super-field" )));
        
        f = Sample.class.getField ("field");
        assertEquals (1, Attributes.getAttributes (f).size ());
        assertEquals (1, Attributes.getAttributes (f, ThreadSafe.class).size ());
        assertTrue (Attributes.hasAttributeType (f, ThreadSafe.class));
        assertTrue (Attributes.hasAttribute (f, new ThreadSafe ()));
                
        f = SuperSample.class.getField ("noAttributesInSubClass");
        assertEquals (1, Attributes.getAttributes (f).size ());
        assertEquals (1, Attributes.getAttributes (f, Dependency.class).size ());
        assertTrue (Attributes.hasAttribute (f, new Dependency ( SampleService.class, "super-noattrs" )));
        assertTrue (Attributes.hasAttributeType (f, Dependency.class));
        
        f = Sample.class.getField ("noAttributesInSubClass");
        assertEquals (0, Attributes.getAttributes (f).size ());
        assertEquals (0, Attributes.getAttributes (f, Dependency.class).size ());
        assertTrue (!Attributes.hasAttribute (f, new Dependency ( SampleService.class, "super-noattrs" )));
        assertTrue (!Attributes.hasAttributeType (f, Dependency.class));
    }
    
    public void testNoAttributes () throws Exception {
        Method m = Sample.class.getMethod ("methodWithNoAttributes", new Class[0]);
        assertEquals (0, Attributes.getAttributes (m).size ());
    }
    
    /**
     * Ensure that loading a class with the same name from two different class loaders
     * won't mess up the attribute cache.
     */
    public void testClassLoaderKeying () throws Exception {
        URLClassLoader cl1 = new URLClassLoader (new URL[]{new File ("unittest/target/cl1/").toURL ()}, getClass().getClassLoader ());
        URLClassLoader cl2 = new URLClassLoader (new URL[]{new File ("unittest/target/cl2/").toURL ()}, getClass().getClassLoader ());
        
        Class cl1Class = cl1.loadClass ("TestClass");
        Class cl2Class = cl2.loadClass ("TestClass");
        
        assertEquals ("[[TestAttribute 1]]", Attributes.getAttributes (cl1Class).toString ());
        assertEquals ("[[TestAttribute 2]]", Attributes.getAttributes (cl2Class).toString ());
    }
    
    public void testAttributeIndex () throws Exception {
        URLClassLoader cl2 = new URLClassLoader (new URL[]{new File ("unittest/target/cl2/cl2.jar").toURL ()}, getClass().getClassLoader ());
        AttributeIndex index = Attributes.getAttributeIndex (cl2);
        assertEquals ("[TestClass]", index.getClassesWithAttribute ("TestAttribute").toString ());
    }
}