package org.apache.avalon.attributes;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Collection;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;

public class URLClassLoaderScanner implements ClassLoaderScanner {
    
    public Class getClassLoaderClass () {
        return URLClassLoader.class;
    }
    
    private Collection getAllClassesFromJarFile (ClassLoader cl, JarFile jar) throws Exception {
        HashSet result = new HashSet ();
        Enumeration enum = jar.entries ();
        while (enum.hasMoreElements ()) {
            JarEntry entry = (JarEntry) enum.nextElement ();
            if (!entry.isDirectory ()) {
                String className = entry.getName ();
                if (className.endsWith (".class")) {
                    className = className.substring (0, className.length () - 6);
                    className.replace ('/', '.').replace ('\\', '.').replace (':', '.');
                    try {
                        Class clazz = cl.loadClass (className);
                        result.add (clazz);
                    } catch (Exception e) {
                    }
                }
            }
        }
        return result;
    }
    
    private Collection getAllClassesFromDirectory (ClassLoader cl, File dir, String prefix) throws Exception {
        HashSet result = new HashSet ();
        File[] files = dir.listFiles ();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory ()) {
                String newPrefix = prefix.equals ("") ? (files[i].getName () + ".") : (prefix + files[i].getName () + ".");
                result.addAll (getAllClassesFromDirectory (cl, files[i], newPrefix));
            } else if (files[i].getName ().endsWith (".class")) {
                String className = prefix + files[i].getName ().substring (0, files[i].getName ().length () - 6);
                try {
                    Class clazz = cl.loadClass (className);
                    result.add (clazz);
                } catch (Exception e) {
                }
            }
        }
        return result;
    }
    
    private Collection getAllClassesFromDirectory (ClassLoader cl, File dir) throws Exception {
        return getAllClassesFromDirectory (cl, dir, "");
    }
    
    private Collection getAllClasses (ClassLoader cl, URL url) throws Exception {
        if ("file".equals (url.getProtocol ())) {
            File f = new File (url.getPath ());
            if (f.exists ()) {
                if (f.isDirectory ()) {
                    return getAllClassesFromDirectory (cl, f);
                } else {
                    return getAllClassesFromJarFile (cl, new JarFile (f));
                }                
            } else {
                return new ArrayList (0);
            }
        } else {
            return new ArrayList (0);
        }
    }
    
    public Collection getClasses (ClassLoader cl) {
        URLClassLoader classLoader = (URLClassLoader) cl;
        
        URL[] sourceURLs = classLoader.getURLs ();
        HashSet result = new HashSet ();
        
        try {
            for (int i = 0; i < sourceURLs.length; i++) {
                result.addAll (getAllClasses (classLoader, sourceURLs[i]));
            }
        } catch (Exception e) {
        }
        
        return result;
    }
}