package org.apache.avalon.attributes.test;

public class ThreadSafe {
    
    public ThreadSafe () {
    }
    
    public boolean equals (Object o) {
        return o instanceof ThreadSafe;
    }
    
    public int hashCode () {
        return 0;
    }
    
    public String toString () {
        return "[ThreadSafe]";
    }
}