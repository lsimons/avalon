package org.apache.avalon.attributes.compiler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.avalon.attributes.AttributeRepositoryClass;
import org.apache.avalon.attributes.Attributes;
import org.apache.avalon.attributes.Indexed;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

/**
 * Ant task to compile attribute indexes. Usage:
 *
 * <pre><code>
 *     &lt;taskdef resource="org/apache/avalon/attributes/anttasks.properties"/&gt;
 *     
 *     &lt;attribute-indexer jarFile="myclasses.jar"&gt;
 *         &lt;classpath&gt;
 *             ...
 *         &lt;/classpath&gt;
 *     &lt;/attribute-indexer&gt;
 * </code></pre>
 *
 * The task will inspect the classes in the given jar and add a <tt>META-INF/attrs.index</tt>
 * file to it, which contains the index information. The classpath element is required and
 * must contain all dependencies for the attributes used.
 */
public class AttributeIndexer extends Task {
    
    private File jarFile;
    private HashMap attributes = new HashMap ();
    private Path classPath;
    
    private final static String INDEX_FILENAME = "META-INF/attrs.index";
    
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
    
    public void setJarfile (File jarFile) {
        this.jarFile = jarFile;
    }
    
    public Path createClasspath () {
        this.classPath = new Path(project);
        return classPath;
    }
    
    private static final String SUFFIX = "$__attributeRepository";
    private static final String CLASS_SUFFIX = SUFFIX + ".class";
    private static final String SOURCE_SUFFIX = SUFFIX + ".java";
    
    protected void copyEntry (JarFile jar, JarEntry entry, JarOutputStream outputStream) throws Exception {
        outputStream.putNextEntry (entry);
        
        if (!entry.isDirectory ()) {
            InputStream is = new BufferedInputStream (jar.getInputStream (entry));
            try {
                byte[] buffer = new byte[16384];
                while (true) {
                    int numRead = is.read (buffer, 0, 16384);
                    if (numRead == 0 || numRead == -1) {
                        break;
                    }
                    
                    outputStream.write (buffer, 0, numRead);
                }
            } finally {
                is.close ();
            }
        }
    }
    
    public void execute () throws BuildException {
        try {
            log ("Building attribute index for " + jarFile.getPath ());
            
            AntClassLoader cl = new AntClassLoader (this.getClass ().getClassLoader (), project, classPath, true);
            cl.addPathElement (jarFile.getPath ());
            
            JarFile jar = new JarFile (jarFile);
            File newJarFile = new File (jarFile.getPath () + ".new");
            JarOutputStream output = new JarOutputStream (new FileOutputStream (newJarFile));
            try {
                Enumeration enum = jar.entries ();
                while (enum.hasMoreElements ()) {
                    JarEntry entry = (JarEntry) enum.nextElement ();
                    if (!entry.isDirectory ()) {
                        String className = entry.getName ();
                        if (className.endsWith (CLASS_SUFFIX)) {
                            className = className.replace ('/', '.').replace ('\\', '.');
                            String baseClassName = className.substring (0, className.length () - CLASS_SUFFIX.length ()).replace ('$', '.');
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
                    
                    if (!entry.getName ().equals (INDEX_FILENAME)) {
                        copyEntry (jar, entry, output);
                    }
                }
                
                output.putNextEntry (new JarEntry (INDEX_FILENAME));
                
                
                Iterator attrs = attributes.keySet ().iterator ();
                while (attrs.hasNext ()) {
                    String attrName = (String) attrs.next ();
                    output.write (("Attribute: " + attrName + "\n").getBytes ());
                    
                    Iterator classes = ((Collection) attributes.get (attrName)).iterator ();
                    while (classes.hasNext ()) {
                        output.write (("Class: " + classes.next () + "\n").getBytes ());
                    }
                    output.write ("\n".getBytes ());
                }
            } finally {
                output.close ();
                jar.close ();
            }
            
            cl.cleanup ();
            
            jarFile.delete ();
            newJarFile.renameTo (jarFile);
        } catch (Exception e) {
            e.printStackTrace ();
            throw new BuildException (e.toString ());
        }
    }
}