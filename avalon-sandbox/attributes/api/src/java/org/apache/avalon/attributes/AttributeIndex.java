package org.apache.avalon.attributes;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * An index providing a list of classes with given attributes. This
 * requires that the attribute is <code>Indexed</code> and that the
 * attribute indexer tool has been run on the jar file containing the
 * classes.
 */
public class AttributeIndex {
    
    private final HashMap index = new HashMap ();
    
    AttributeIndex (ClassLoader cl) throws Exception {
        Enumeration enum = cl.getResources ("META-INF/attrs.index");
        while (enum.hasMoreElements ()) {
            URL url = (URL) enum.nextElement ();
            loadFromURL (url);
        }
    }
    
    /**
     * Add a class to the index.
     */
    private void addClass (String attributeClass, String clazz) {
        Collection coll = (Collection) index.get (attributeClass);
        if (coll == null) {
            coll = new HashSet ();
            index.put (attributeClass, coll);
        }
        coll.add (clazz);
    }
    
    /**
     * Load the attrs.index from a given URL.
     */
    private void loadFromURL (URL url) throws Exception {
        URLConnection connection = url.openConnection ();
            BufferedReader br = new BufferedReader (new InputStreamReader (connection.getInputStream ()));
            try {
                String currentAttributeClass = null;
                String line = null;
                while ((line = br.readLine ()) != null) {
                    if (line.startsWith ("Attribute: ")) {
                        currentAttributeClass = line.substring ("Attribute: ".length ()).trim ();
                    } else if (line.startsWith ("Class: ")) {
                        String className = line.substring ("Class: ".length ()).trim ();
                        addClass (currentAttributeClass, className);
                    }
                }
            } finally {
                br.close ();
            }
    }
    
    /**
     * Gets a Collection of the classes that have an attribute of the specified class.
     * The Collection contains the class names (String).
     */    
    public Collection getClassesWithAttribute (String attributeClass) {
        if (index.containsKey (attributeClass)) {
            return (Collection) index.get (attributeClass);
        } else {
            return Collections.EMPTY_SET;
        }
    }
    
    /**
     * Gets a Collection of the classes that have an attribute of the specified class.
     * The Collection contains the class names (String).
     */
    public Collection getClassesWithAttribute (Class attributeClass) {
        return getClassesWithAttribute (attributeClass.getName ());
    }
    
}