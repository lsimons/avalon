package org.apache.avalon.attributes;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

class DefaultCachedRepository implements CachedRepository {
    
    private final Set classAttributes = new HashSet ();
    private final Map fields = new HashMap ();
    private final Map methods = new HashMap ();
    private final Map constructors = new HashMap ();
    
    public DefaultCachedRepository (Class clazz, AttributeRepositoryClass repo) throws Exception {
        // ---- Fix up class attributes
        this.classAttributes.addAll (repo.getClassAttributes ());
        
        Class c = clazz.getSuperclass ();
        while (c != null) {
            this.classAttributes.addAll (getInheritableAttributes (Attributes.getAttributes (c)));
            c = c.getSuperclass ();
        }
        
        // ---- Fix up method attributes
        Method[] methods = clazz.getDeclaredMethods ();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            String key = Util.getSignature (m);
            Set attributes = new HashSet ();
            attributes.addAll ((Collection) repo.getMethodAttributes ().get (key));
            
            c = clazz.getSuperclass ();
            while (c != null) {
                
                try {
                    // Get equivalent method in superclass
                    Method m2 = c.getMethod (m.getName (), m.getParameterTypes ());
                    if (m2.getDeclaringClass () == c) {
                        attributes.addAll (getInheritableAttributes (Attributes.getAttributes (m2)));
                    }
                } catch (NoSuchMethodException nsme) {
                }
                
                c = c.getSuperclass ();
            }
            
            this.methods.put (m, attributes);
        }
    }
    
    private static Collection getInheritableAttributes (Collection attrs) throws Exception {
        HashSet result = new HashSet ();
        
        Iterator iter = attrs.iterator ();
        while (iter.hasNext ()) {
            Object attr = iter.next ();
            if (Attributes.hasAttribute (attr.getClass (), Inheritable.class)) {
                result.add (attr);
            }
        }
        return result;
    }
    
    public Collection getAttributes () throws Exception {
        return classAttributes;
    }
    
    public Collection getAttributes (Field f) throws Exception {
        return (Collection) fields.get (f);
    }
    
    public Collection getAttributes (Method m) throws Exception {
        return (Collection) methods.get (m);
    }
    
    public Collection getAttributes (Constructor c) throws Exception {
        return (Collection) constructors.get (c);
    }
}
