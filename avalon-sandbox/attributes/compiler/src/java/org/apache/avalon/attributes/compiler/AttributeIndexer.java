package org.apache.avalon.attributes.compiler;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.avalon.attributes.AttributeRepositoryClass;
import org.apache.avalon.attributes.Attributes;
import org.apache.avalon.attributes.Indexed;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import xjavadoc.*;
import xjavadoc.ant.*;
import java.util.*;

/**
 * Ant task to compile attributes.
 */
public class AttributeIndexer extends Task {
    
    private final ArrayList fileSets = new ArrayList ();
    private File destFile;
    private HashMap attributes = new HashMap ();
    
    public AttributeIndexer () {
    }
    
    protected void addAttribute (String className, String attributeName) {
        HashSet thisAttr = (HashSet) attributes.get (attributeName);
        if (thisAttr == null) {
            thisAttr = new HashSet ();
            attributes.put (attributeName, thisAttr);
        }
        thisAttr.add (className);
    }
    
    public void addFileset (FileSet set) {
        fileSets.add (set);
    }
    
    
    public void setDestfile (File destFile) {
        this.destFile = destFile;
    }
    
    private static final String SUFFIX = "$__org_apache_avalon_Attributes";
    private static final String CLASS_SUFFIX = SUFFIX + ".class";
    private static final String SOURCE_SUFFIX = SUFFIX + ".java";
    
    public void execute () throws BuildException {
        try {
            
            ArrayList urls = new ArrayList ();
            Iterator iter = fileSets.iterator ();
            while (iter.hasNext ()) {
                FileSet fs = (FileSet) iter.next ();
                urls.add (fs.getDir (project).toURL ());
            }
            
            ClassLoader cl = new URLClassLoader ((URL[]) urls.toArray (new URL[0]), this.getClass ().getClassLoader ());
            
            iter = fileSets.iterator ();
            while (iter.hasNext ()) {
                FileSet fs = (FileSet) iter.next ();
                File fromDir = fs.getDir(project);
                
                String[] srcFiles = fs.getDirectoryScanner(project).getIncludedFiles();
                
                for (int i = 0; i < srcFiles.length; i++) {
                    String className = srcFiles[i].replace ('/', '.').replace ('\\', '.');
                    if (className.endsWith (CLASS_SUFFIX)) {
                        String baseClassName = className.substring (0, className.length () - CLASS_SUFFIX.length ());
                        className = className.substring (0, className.length () - 6);
                        Class repoClass = cl.loadClass (className);
                        AttributeRepositoryClass repo = (AttributeRepositoryClass) repoClass.newInstance ();
                        Collection classAttrs = repo.getClassAttributes ();
                        
                        Collection indexedAttrs = new HashSet ();
                        Iterator inner = classAttrs.iterator ();
                        while (inner.hasNext ()) {
                            indexedAttrs.add (inner.next ().getClass ());
                        }
                        
                        indexedAttrs = Attributes.getClassesWithAttributeType (indexedAttrs, Indexed.class);
                        
                        inner = indexedAttrs.iterator ();
                        while (inner.hasNext ()) {
                            addAttribute (baseClassName, ((Class) inner.next ()).getName ());
                        }
                    }
                }
            }   
            
            destFile.getParentFile ().mkdirs ();
            PrintWriter pw = new PrintWriter (new FileWriter (destFile));
            try {
                Iterator attrs = attributes.keySet ().iterator ();
                while (attrs.hasNext ()) {
                    String attrName = (String) attrs.next ();
                    pw.println ("Attribute: " + attrName);
                    Iterator classes = ((Collection) attributes.get (attrName)).iterator ();
                    while (classes.hasNext ()) {
                        pw.println ("Class: " + classes.next ());
                    }
                    pw.println ();
                }
            } finally {
                pw.close ();
            }
        } catch (Exception e) {
            e.printStackTrace ();
            throw new BuildException (e.toString ());
        }
    }
}