package org.apache.avalon.attributes;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * API for accessing attributes.
 */
public class Attributes {
    
    /**
     * A cache of attribute repositories. The map used is a WeakHashMap keyed on the
     * Class owning the attribute repository. This works because Class objects use
     * the identity function to compare for equality - i.e. if the two classes
     * have the same name, and are loaded from the same two ClassLoaders, then
     * <code>class1 == class2</code>. Also, <code>(class1.equals(class2)) == (class1 ==
     * class2)</code>. This means that a once a Class reference has been garbage-collected,
     * it can't be re-created. Thus we can treat the cache map as a normal map - the only
     * entries that will ever disappear are those we can't look up anyway because we
     * can't ever create the key for them!
     *
     * <p>Also, this will keep the cache from growing too big in environments where
     * classes are loaded and unloaded all the time (i.e. application servers).
     */
    private final static Map classRepositories = new WeakHashMap ();
    
    private static List initList = new ArrayList ();
    
    private synchronized static CachedRepository getCachedRepository (Class clazz) throws RepositoryError, CircularDependencyError {
        if (classRepositories.containsKey (clazz)) {
            CachedRepository cr = (CachedRepository) classRepositories.get (clazz);
            if (cr == null) {
                // Circular references.
                List dependencyList = new ArrayList ();
                dependencyList.addAll (initList);
                throw new CircularDependencyError (clazz.getName (), dependencyList);
            } else {
                return cr;
            }
        } else {
            // Indicate that we're loading it.
            initList.add (clazz.getName ());
            classRepositories.put (clazz, null);
            
            Class attributeRepo;
            CachedRepository cached;
            try {
                attributeRepo = Class.forName (clazz.getName () + "$__org_apache_avalon_Attributes", true, clazz.getClassLoader ());
                AttributeRepositoryClass repo = (AttributeRepositoryClass) attributeRepo.newInstance ();
                
                cached = new DefaultCachedRepository (clazz, repo);
            } catch (ClassNotFoundException cnfe) {
                cached = CachedRepository.EMPTY;
            } catch (InstantiationException ie) {
                throw new RepositoryError (ie);
            } catch (IllegalAccessException iae) {
                throw new RepositoryError (iae);
            }
            
            classRepositories.put (clazz, cached);
            
            initList.remove (initList.size () - 1);
            
            return cached;
        }
    }
    
    /**
     * Gets all attributes for a class.
     */
    public static Collection getAttributes (Class clazz) {
        return Collections.unmodifiableCollection (getCachedRepository (clazz).getAttributes ());
    }
    
    /**
     * Gets all attributes for a method.
     */
    public static Collection getAttributes (Method method) {
        return Collections.unmodifiableCollection (getCachedRepository (method.getDeclaringClass()).getAttributes (method));
    }
    
    /**
     * Gets all attributes for a field.
     */
    public static Collection getAttributes (Field field) {
        return Collections.unmodifiableCollection (getCachedRepository (field.getDeclaringClass()).getAttributes (field));
    }
    
    /**
     * Gets all attributes for a constructor.
     */
    public static Collection getAttributes (Constructor cons) {
        return Collections.unmodifiableCollection (getCachedRepository (cons.getDeclaringClass()).getAttributes (cons));
    }
    
    /**
     * Selects from a collection of attributes only those with a given class.
     */
    private static Collection getAttributes (Collection attrs, Class attributeClass) {
        HashSet result = new HashSet ();
        Iterator iter = attrs.iterator ();
        while (iter.hasNext ()) {
            Object attr = iter.next ();
            if (attr.getClass () == attributeClass) {
                result.add (attr);
            }
        }
        
        return Collections.unmodifiableCollection (result);
    }
    
    /**
     * Get all attributes of a given type from a class. For all objects o in the returned 
     * collection, <code>o.getClass() == attributeClass</code>.
     */
    public static Collection getAttributes (Class clazz, Class attributeClass) {
        return getAttributes (getAttributes (clazz), attributeClass);
    }
    
    /**
     * Get all attributes of a given type from a field. For all objects o in the returned 
     * collection, <code>o.getClass() == attributeClass</code>.
     */
    public static Collection getAttributes (Field field, Class attributeClass) {
        return getAttributes (getAttributes (field), attributeClass);
    }
    
    /**
     * Get all attributes of a given type from a constructor. For all objects o in the returned 
     * collection, <code>o.getClass() == attributeClass</code>.
     */
    public static Collection getAttributes (Constructor ctor, Class attributeClass) {
        return getAttributes (getAttributes (ctor), attributeClass);
    }
    
    /**
     * Get all attributes of a given type from a method. For all objects o in the returned 
     * collection, <code>o.getClass() == attributeClass</code>.
     */
    public static Collection getAttributes (Method method, Class attributeClass) {
        return getAttributes (getAttributes (method), attributeClass);
    }
    
    /**
     * Filters a collection of <code>Class</code> objects. The returned collection
     * only contains those classes that have an attribute of the specified type.
     */
    public static Collection getClassesWithAttributeType (Collection classes, Class attributeClass) {
        ArrayList result = new ArrayList ();
        Iterator iter = classes.iterator ();
        while (iter.hasNext ()) {
            Class clazz = (Class) iter.next ();
            if (hasAttributeType (clazz, attributeClass)) {
                result.add (clazz);
            }
        }
        
        return result;
    }
    
    /**
     * Filters a collection objects. The returned collection
     * only contains those objects that have an attribute of the specified type.
     */
    public static Collection getObjectsWithAttributeType (Collection objects, Class attributeClass) {
        ArrayList result = new ArrayList ();
        Iterator iter = objects.iterator ();
        while (iter.hasNext ()) {
            Class clazz = (Class) iter.next ().getClass ();
            if (hasAttributeType (clazz, attributeClass)) {
                result.add (clazz);
            }
        }
        
        return result;
    }
    
    /**
     * Convenience function to test whether a collection of attributes contain
     * an attribute of a given class.
     */
    private static boolean hasAttributeType (Collection attrs, Class attributeClass) {
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
     * Tests if a class has an attribute of a given type. That is, is there any attribute
     * <code>attr</code> such that <code>attr.getClass() == attributeClass</code>?
     */
    public static boolean hasAttributeType (Class clazz, Class attributeClass) {
        return hasAttributeType (getAttributes (clazz), attributeClass);
    }

    /**
     * Tests if a field has an attribute of a given type. That is, is there any attribute
     * <code>attr</code> such that <code>attr.getClass() == attributeClass</code>?
     */
    public static boolean hasAttributeType (Field field, Class attributeClass) {
        return hasAttributeType (getAttributes (field), attributeClass);
    }
    
    /**
     * Tests if a constructor has an attribute of a given type. That is, is there any attribute
     * <code>attr</code> such that <code>attr.getClass() == attributeClass</code>?
     */
    public static boolean hasAttributeType (Constructor ctor, Class attributeClass) {
        return hasAttributeType (getAttributes (ctor), attributeClass);
    }
    
    /**
     * Tests if a method has an attribute of a given type. That is, is there any attribute
     * <code>attr</code> such that <code>attr.getClass() == attributeClass</code>?
     */
    public static boolean hasAttributeType (Method method, Class attributeClass) {
        return hasAttributeType (getAttributes (method), attributeClass);
    }
    
    /**
     * Convenience function to test whether a collection of attributes contain
     * an attribute.
     */
    private static boolean hasAttribute (Collection attrs, Object attribute) {
        return attrs.contains (attribute);
    }
    
    /**
     * Tests if a class has an attribute. That is, is there any attribute
     * <code>attr</code> such that <code>attr.equals(attribute)</code>?
     */
    public static boolean hasAttribute (Class clazz, Object attribute) {
        return hasAttribute (getAttributes (clazz), attribute);
    }
    
    /**
     * Tests if a field has an attribute. That is, is there any attribute
     * <code>attr</code> such that <code>attr.equals(attribute)</code>?
     */
    public static boolean hasAttribute (Field field, Object attribute) {
        return hasAttribute (getAttributes (field), attribute);
    }
    
    /**
     * Tests if a constructor has an attribute. That is, is there any attribute
     * <code>attr</code> such that <code>attr.equals(attribute)</code>?
     */
    public static boolean hasAttribute (Constructor ctor, Object attribute) {
        return hasAttribute (getAttributes (ctor), attribute);
    }
    
    /**
     * Tests if a method has an attribute. That is, is there any attribute
     * <code>attr</code> such that <code>attr.equals(attribute)</code>?
     */
    public static boolean hasAttribute (Method method, Object attribute) {
        return hasAttribute (getAttributes (method), attribute);
    }
    
    /**
     * Constructs an <code>AttributeIndex</code> for the given <code>ClassLoader</code>.
     * An AttributeIndex allows oyu to quickly find all classes that have a given
     * attribute.
     */
    public static AttributeIndex getAttributeIndex (ClassLoader cl) throws Exception {
        return new AttributeIndex (cl);
    }
}