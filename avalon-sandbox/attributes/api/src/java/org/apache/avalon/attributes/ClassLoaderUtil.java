package org.apache.avalon.attributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClassLoaderUtil {
    
    private final static ClassLoaderUtil classLoaderUtil = new ClassLoaderUtil ();
    static {
        classLoaderUtil.addClassLoaderScanner (new URLClassLoaderScanner ());
    }
    
    private Map classLoaderScanners = new HashMap ();
    
    public static ClassLoaderUtil getClassLoaderUtil () {
        return classLoaderUtil;
    }
    
    public void addClassLoaderScanner (ClassLoaderScanner scanner) {
        classLoaderScanners.put (scanner.getClassLoaderClass (), scanner);
    }
    
    public Collection getClasses (ClassLoader cl) {
        ClassLoaderScanner scanner = (ClassLoaderScanner) classLoaderScanners.get (cl.getClass ());
        if (scanner != null) {
            return scanner.getClasses (cl);
        } else {
            throw new UnsupportedOperationException ("No ClassLoaderScanner for ClassLoader of type " + 
                cl.getClass ().getName ());
        }
    }
}