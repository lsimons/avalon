package org.apache.avalon.attributes;

import java.util.Iterator;
import java.util.List;

/**
 * Thrown when an attribute repository class can't be
 * loaded because it resulted in a circular dependency.
 */
public class CircularDependencyError extends RepositoryError {
   
    /**
     * Create a new CircularDependencyError.
     *
     * @param className the name of the class that started it all.
     * @param dependencyList a list of the classes that the original
     *                       class depended on, the classes they
     *                       depended on, and so on. The list should
     *                       show the chain of dependencies that resulted
     *                       in the exception being thrown.
     */
    public CircularDependencyError (String className, List dependencyList) {
        super (className + ":" + listDeps (dependencyList), null);
    }
    
    /**
     * Joins together the elements of a list with <code>-&gt;</code>
     * delimiters. Used to show the sequence that resulted in the circular
     * dependency.
     */
    private static String listDeps (List dependencyList) {
        StringBuffer sb = new StringBuffer ();
        Iterator iter = dependencyList.iterator ();
        while (iter.hasNext ()) {
            sb.append (iter.next ());
            if (iter.hasNext ()) {
                sb.append (" -> ");
            }
        }
        
        return sb.toString ();
    }
}