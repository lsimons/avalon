package org.apache.avalon.attributes.test;

import org.apache.avalon.attributes.Inheritable;

/**
 * Declares a dependency.
 * 
 * @Inheritable
 */
public class Dependency {
    
    private final Class clazz;
    private final String name;
    
    public Dependency (Class clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }
    
    public Class getDependencyClass () {
        return clazz;
    }
    
    public String getDependencyName () {
        return name;
    }
    
    public boolean equals (Object o) {
        return o instanceof Dependency &&
            ((Dependency) o).clazz == clazz &&
            ((Dependency) o).name.equals (name);
    }
    
    public int hashCode () {
        return clazz.hashCode () ^ name.hashCode ();
    }
    
    public String toString () {
        return "[Dependency on " + clazz.getName () + " via name \"" + name + "\"]";
    }
}