package org.apache.avalon.attributes;

/**
 * Thrown when an attribute repository class can't be
 * loaded or instantiated.
 */
public class RepositoryError extends Error {
   
    private final Throwable nested;
    
    public RepositoryError () {
        this (null, null);
    }
    
    public RepositoryError (String message) {
        this (message, null);
    }
    
    public RepositoryError (Throwable nested) {
        this (nested.toString(), nested);
    }
    
    public RepositoryError (String message, Throwable nested) {
        super (message);
        this.nested = nested;
    }
    
    public Throwable getNested () {
        return nested;
    }
}