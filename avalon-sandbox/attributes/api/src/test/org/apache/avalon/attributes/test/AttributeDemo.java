package org.apache.avalon.attributes.test;

import org.apache.avalon.attributes.Attributes;

public class AttributeDemo {
    
    public static void main (String[] args) throws Exception {
        Class sample = Class.forName ("org.apache.avalon.attributes.test.Sample");
        
        System.out.println ("Getting attributes for class Sample...");
        
        System.out.println ("Sample has the following class attributes:\n" + Attributes.getAttributes (sample));
        
        System.out.println ("Getting attributes for the method Sample.someMethod(int)...");
        System.out.println ("Sample.someMethod(int) has the following attributes:\n" + Attributes.getAttributes (sample.getMethod ("someMethod", new Class[]{ Integer.TYPE })));
        
        System.out.println ("Getting attributes for the field Sample.field...");
        System.out.println ("Sample.field has the following attributes:\n" + Attributes.getAttributes (sample.getField ("field")));
        
        System.out.println ("Getting attributes for the constructor Sample()...");
        System.out.println ("Sample() has the following attributes:\n" + Attributes.getAttributes (sample.getConstructor (new Class[0])));
    }
    
}