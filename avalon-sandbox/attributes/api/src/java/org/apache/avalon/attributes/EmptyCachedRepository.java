package org.apache.avalon.attributes;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Null implementation of a cached repository.
 */
class EmptyCachedRepository implements CachedRepository {
    
    private final static Collection EMPTY_COLLECTION = new ArrayList (0);
    
    public Collection getAttributes () {
        return EMPTY_COLLECTION;
    }
    
    public Collection getAttributes (Field f) {
        return EMPTY_COLLECTION;
    }
    
    public Collection getAttributes (Method m) {
        return EMPTY_COLLECTION;
    }
    
    public Collection getAttributes (Constructor c) {
        return EMPTY_COLLECTION;
    }
}
