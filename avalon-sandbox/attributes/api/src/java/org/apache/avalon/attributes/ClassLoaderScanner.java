package org.apache.avalon.attributes;

import java.util.Collection;

public interface ClassLoaderScanner {
    public Class getClassLoaderClass ();
    public Collection getClasses (ClassLoader cl);
}