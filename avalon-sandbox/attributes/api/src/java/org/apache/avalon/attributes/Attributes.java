package org.apache.avalon.attributes;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * API for accessing attributes.
 */
public class Attributes {
    
    private final static HashMap classRepositories = new HashMap ();
    
    protected synchronized static CachedRepository getCachedRepository (Class clazz) throws Exception {
        if (classRepositories.containsKey (clazz)) {
            CachedRepository cr = (CachedRepository) classRepositories.get (clazz);
            if (cr == null) {
                // Circular references.
                throw new ClassCircularityError (clazz.getName ());
            } else {
                return cr;
            }
        } else {
            // Indicates we're loading it.
            classRepositories.put (clazz, null);
            
            Class attributeRepo;
            CachedRepository cached;
            try {
                attributeRepo = Class.forName (clazz.getName () + "$Attributes", true, clazz.getClassLoader ());
                AttributeRepositoryClass repo = (AttributeRepositoryClass) attributeRepo.newInstance ();
                
                cached = new DefaultCachedRepository (clazz, repo);
            } catch (ClassNotFoundException cnfe) {
                cached = CachedRepository.EMPTY;
            }
            
            classRepositories.put (clazz, cached); // Should be keyed on ClassLoader as well.
            
            return cached;
        }
    }
    
    /**
     * Gets all attributes for a class.
     */
    public static Collection getAttributes (Class clazz) throws Exception {
        return getCachedRepository (clazz).getAttributes ();
    }
    
    /**
     * Gets all attributes for a method.
     */
    public static Collection getAttributes (Method method) throws Exception {
        return getCachedRepository (method.getDeclaringClass()).getAttributes (method);
    }
    
    /**
     * Gets all attributes for a field.
     */
    public static Collection getAttributes (Field field) throws Exception {
        return getCachedRepository (field.getDeclaringClass()).getAttributes (field);
    }
    
    /**
     * Gets all attributes for a constructor.
     */
    public static Collection getAttributes (Constructor cons) throws Exception {
        return getCachedRepository (cons.getDeclaringClass()).getAttributes (cons);
    }
    
    /**
     * Selects from a collection of attributes only those with a given class.
     */
    private static Collection getAttributes (Collection attrs, Class attributeClass) throws Exception {
        HashSet result = new HashSet ();
        Iterator iter = attrs.iterator ();
        while (iter.hasNext ()) {
            Object attr = iter.next ();
            if (attr.getClass () == attributeClass) {
                result.add (attr);
            }
        }
        
        return result;
    }
    
    /**
     * Get all attributes of a given type from a class.
     */
    public static Collection getAttributes (Class clazz, Class attributeClass) throws Exception {
        return getAttributes (getAttributes (clazz), attributeClass);
    }
    
    /**
     * Get all attributes of a given type from a field.
     */
    public static Collection getAttributes (Field field, Class attributeClass) throws Exception {
        return getAttributes (getAttributes (field), attributeClass);
    }
    
    /**
     * Get all attributes of a given type from a constructor.
     */
    public static Collection getAttributes (Constructor ctor, Class attributeClass) throws Exception {
        return getAttributes (getAttributes (ctor), attributeClass);
    }
    
    /**
     * Get all attributes of a given type from a method.
     */
    public static Collection getAttributes (Method method, Class attributeClass) throws Exception {
        return getAttributes (getAttributes (method), attributeClass);
    }
    
    /**
     * Convenience function to test whether a collection of attributes contain
     * an attribute of a given class.
     */
    private static boolean hasAttribute (Collection attrs, Class attributeClass) throws Exception {
        Iterator iter = attrs.iterator ();
        while (iter.hasNext ()) {
            Object attr = iter.next ();
            if (attr.getClass () == attributeClass) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Tests if a class has an attribute of a given type.
     */
    public static boolean hasAttribute (Class clazz, Class attributeClass) throws Exception {
        return hasAttribute (getAttributes (clazz), attributeClass);
    }

    /**
     * Tests if a class has an attribute of a given type.
     */
    public static boolean hasAttribute (Field field, Class attributeClass) throws Exception {
        return hasAttribute (getAttributes (field), attributeClass);
    }
    
    /**
     * Tests if a class has an attribute of a given type.
     */
    public static boolean hasAttribute (Constructor ctor, Class attributeClass) throws Exception {
        return hasAttribute (getAttributes (ctor), attributeClass);
    }
    
    /**
     * Tests if a class has an attribute of a given type.
     */
    public static boolean hasAttribute (Method method, Class attributeClass) throws Exception {
        return hasAttribute (getAttributes (method), attributeClass);
    }
    
}