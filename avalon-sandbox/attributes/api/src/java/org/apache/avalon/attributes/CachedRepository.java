package org.apache.avalon.attributes;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;

interface CachedRepository {
    
    public static final CachedRepository EMPTY = new EmptyCachedRepository ();
    
    public Collection getAttributes () throws Exception;
    public Collection getAttributes (Field f) throws Exception;
    public Collection getAttributes (Method m) throws Exception;
    public Collection getAttributes (Constructor c) throws Exception;
}
