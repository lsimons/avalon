package org.apache.avalon.attributes;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

class DefaultCachedRepository implements CachedRepository {
    
    private final static Collection EMPTY_COLLECTION = new ArrayList (0);
    
    private final Set classAttributes = new HashSet ();
    private final Map fields = new HashMap ();
    private final Map methods = new HashMap ();
    private final Map constructors = new HashMap ();
    
    public DefaultCachedRepository (Class clazz, AttributeRepositoryClass repo) {
        // ---- Fix up class attributes
        this.classAttributes.addAll (repo.getClassAttributes ());
        this.classAttributes.addAll (getInheritableClassAttributes (clazz.getSuperclass ()));
        Class[] ifs = clazz.getInterfaces ();
        for (int i = 0; i < ifs.length; i++) {
            this.classAttributes.addAll (getInheritableClassAttributes (ifs[i]));
        }
        
        // ---- Fix up method attributes
        Method[] methods = clazz.getDeclaredMethods ();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            String key = Util.getSignature (m);
            
            Set attributes = new HashSet ();
            
            if (repo.getMethodAttributes ().containsKey (key)) {
                attributes.addAll ((Collection) repo.getMethodAttributes ().get (key));
            }
            attributes.addAll (getInheritableMethodAttributes (clazz.getSuperclass (), m.getName (), m.getParameterTypes ()));
            for (int j = 0; j < ifs.length; j++) {
                attributes.addAll (getInheritableMethodAttributes (ifs[j], m.getName (), m.getParameterTypes ()));
            }
            
            if (attributes.size () > 0) {
                this.methods.put (m, attributes);
            }
        }
        
        // --- Just copy constructor attributes (they aren't inherited)
        Constructor[] constructors = clazz.getDeclaredConstructors ();
        for (int i = 0; i < constructors.length; i++) {
            Constructor ctor = constructors[i];
            String key = Util.getSignature (ctor);
            
            if (repo.getConstructorAttributes ().containsKey (key)) {
                this.constructors.put (ctor, repo.getConstructorAttributes ().get (key));
            }
        }
        
        // --- Just copy field attributes (they aren't inherited)
        Field[] fields = clazz.getDeclaredFields ();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            String key = f.getName ();
            if (repo.getFieldAttributes ().containsKey (key)) {
                this.fields.put (f, repo.getFieldAttributes ().get (key));
            }
        }
    }
    
    private static Collection getInheritableAttributes (Collection attrs) throws Exception {
        HashSet result = new HashSet ();
        
        Iterator iter = attrs.iterator ();
        while (iter.hasNext ()) {
            Object attr = iter.next ();
            if (Attributes.hasAttributeType (attr.getClass (), Inheritable.class)) {
                result.add (attr);
            }
        }
        return result;
    }
    
    private static Collection getInheritableClassAttributes (Class c) throws Exception {
        if (c == null) {
            return new ArrayList (0);
        }
        
        HashSet result = new HashSet ();
        result.addAll (getInheritableAttributes (Attributes.getAttributes (c)));
        
        // Traverse the class hierarchy
        result.addAll (getInheritableClassAttributes (c.getSuperclass ()));
        
        // Traverse the interface hierarchy
        Class[] ifs = c.getInterfaces ();
        for (int i = 0; i < ifs.length; i++) {
            result.addAll (getInheritableClassAttributes (ifs[i]));
        }
        
        return result;
    }
    
    private static Collection getInheritableMethodAttributes (Class c, String methodName, Class[] methodParams) throws Exception {
        if (c == null) {
            return new ArrayList (0);
        }
        
        HashSet result = new HashSet ();
        
        try {
            // Get equivalent method in c
            Method m = c.getMethod (methodName, methodParams);
            if (m.getDeclaringClass () == c) {
                result.addAll (getInheritableAttributes (Attributes.getAttributes (m)));
            }
        } catch (NoSuchMethodException nsme) {
        }
        
        // Traverse the class hierarchy
        result.addAll (getInheritableMethodAttributes (c.getSuperclass (), methodName, methodParams));
        
        // Traverse the interface hierarchy
        Class[] ifs = c.getInterfaces ();
        for (int i = 0; i < ifs.length; i++) {
            result.addAll (getInheritableMethodAttributes (ifs[i], methodName, methodParams));
        }
        
        return result;
    }
    
    public Collection getAttributes () {
        return classAttributes;
    }
    
    public Collection getAttributes (Field f) {
        if (fields.containsKey (f)) {
            return (Collection) fields.get (f);
        } else {
            return EMPTY_COLLECTION;
        }
        
    }
    
    public Collection getAttributes (Method m) {
        if (methods.containsKey (m)) {
            return (Collection) methods.get (m);
        } else {
            return EMPTY_COLLECTION;
        }
    }
    
    public Collection getAttributes (Constructor c) {
        if (constructors.containsKey (c)) {
            return (Collection) constructors.get (c);
        } else {
            return EMPTY_COLLECTION;
        }
    }   
}
