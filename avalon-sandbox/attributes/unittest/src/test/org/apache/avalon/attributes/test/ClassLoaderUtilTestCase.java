package org.apache.avalon.attributes.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import org.apache.avalon.attributes.Attributes;
import org.apache.avalon.attributes.ClassLoaderUtil;
import junit.framework.TestCase;

public class ClassLoaderUtilTestCase extends TestCase {
    
    public void testScanning () throws Exception {
        URLClassLoader cl1 = new URLClassLoader (new URL[]{new File ("unittest/target/cl1/").toURL ()}, getClass().getClassLoader ());
        URLClassLoader cl2 = new URLClassLoader (new URL[]{new File ("unittest/target/cl2/cl2.jar").toURL ()}, getClass().getClassLoader ());
        
        ClassLoaderUtil clUtil = ClassLoaderUtil.getClassLoaderUtil ();
        
        Collection cl1Classes = clUtil.getClasses (cl1);
        Collection cl2Classes = clUtil.getClasses (cl2);
        
        System.out.println (cl1Classes);
        System.out.println (cl2Classes);
    }
        
    
}