package org.apache.avalon.attributes;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * An attribute repository cache. Used internally to speed up operation.
 */
interface CachedRepository {
    
    public static final CachedRepository EMPTY = new EmptyCachedRepository ();
    
    public Collection getAttributes ();
    public Collection getAttributes (Field f);
    public Collection getAttributes (Method m);
    public Collection getAttributes (Constructor c);
}
