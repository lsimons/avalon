package org.apache.avalon.attributes.compiler;

import java.io.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;

import xjavadoc.*;
import xjavadoc.ant.*;
import java.util.*;

/**
 * Ant task to compile attributes.
 */
public class AttributeCompiler extends XJavadocTask {
    
    private final ArrayList fileSets = new ArrayList ();
    private File destDir;
    
    public AttributeCompiler () {
    }
    
    public void addFileset (FileSet set) {
        super.addFileset (set);
        fileSets.add (set);
    }
    
    
    public void setDestdir (File destDir) {
        this.destDir = destDir;
    }
    
    protected void copyImports (File source, PrintWriter dest) throws Exception {
        BufferedReader br = new BufferedReader (new FileReader (source));
        try {
            String line = null;
            while ((line = br.readLine ()) != null) {
                if (line.startsWith ("import ")) {
                    dest.println (line);
                }
            }
        } finally {
            br.close ();
        }
    }
    
    protected void addExpressions (Collection tags, PrintWriter pw, String collectionName, String fileName) {
        Iterator iter = tags.iterator ();
        while (iter.hasNext ()) {
            XTag tag = (XTag) iter.next ();
            
            String expression = tag.getName () + " " + tag.getValue ();
            
            if (Character.isUpperCase (expression.charAt (0))) {
                pw.println ("        " + collectionName + ".add (\n" +
                    "new " + expression + " // " + fileName + ":" + tag.getLineNumber () + "\n" +
                    ");");
            }
        }
    }
    
    protected boolean elementHasAttributes (Collection xElements) {
        Iterator iter = xElements.iterator ();
        while (iter.hasNext ()) {
            XProgramElement element = (XProgramElement) iter.next ();
            if (tagHasAttributes (element.getDoc ().getTags ())) {
                return true;
            }
        }
        return false;
    }
    
    protected String getParameterTypes (Collection parameters) {
        StringBuffer sb = new StringBuffer ();
        for (Iterator params = parameters.iterator (); params.hasNext ();) {
            XParameter parameter = (XParameter) params.next ();
            sb.append (parameter.getType ().getQualifiedName ());
            sb.append (parameter.getDimensionAsString ());
            
            if (params.hasNext ()) {
                sb.append (",");
            }
        }
        return sb.toString ();
    }
        
    protected void generateClass (XClass xClass) throws Exception {
        if (!hasAttributes (xClass)) {
            return;
        }
        
        String name = xClass.getQualifiedName ();
        File sourceFile = getSourceFile (name);
        File destFile = new File (destDir, name.replace ('.', '/') + "$Attributes.java");
        
        if (destFile.exists () && destFile.lastModified () >= sourceFile.lastModified ()) {
            return;
        }
        
        String packageName = xClass.getContainingPackage().getName ();
        String className = xClass.getName ();
        
        destFile.getParentFile ().mkdirs ();
        PrintWriter pw = new PrintWriter (new FileWriter (destFile));
        
        if (packageName != null && !packageName.equals ("")) {
            pw.println ("package " + packageName + ";");
        }
        
        copyImports (sourceFile, pw);
        
        pw.println ("public class " + className + "$Attributes implements org.apache.avalon.attributes.AttributeRepositoryClass {");
        {
            pw.println ("    private static final java.util.Set classAttributes = new java.util.HashSet ();");
            pw.println ("    private static final java.util.Map fieldAttributes = new java.util.HashMap ();");
            pw.println ("    private static final java.util.Map methodAttributes = new java.util.HashMap ();");
            pw.println ("    private static final java.util.Map constructorAttributes = new java.util.HashMap ();");
            pw.println ();
            
            pw.println ("    static {");
            pw.println ("        initClassAttributes ();");
            pw.println ("        initMethodAttributes ();");
            pw.println ("        initFieldAttributes ();");
            pw.println ("        initConstructorAttributes ();");
            pw.println ("    }");
            pw.println ();
            
            pw.println ("    public java.util.Set getClassAttributes () { return classAttributes; }");
            pw.println ("    public java.util.Map getFieldAttributes () { return fieldAttributes; }");
            pw.println ("    public java.util.Map getConstructorAttributes () { return constructorAttributes; }");
            pw.println ("    public java.util.Map getMethodAttributes () { return methodAttributes; }");
            pw.println ();
            
            pw.println ("    private static void initClassAttributes () {");
            addExpressions (xClass.getDoc ().getTags (), pw, "classAttributes", sourceFile.getPath ());
            pw.println ("    }");
            pw.println ();
            
            // ---- Field Attributes
            
            pw.println ("    private static void initFieldAttributes () {");
            pw.println ("        java.util.Set attrs = null;");
            for (Iterator iter = xClass.getFields ().iterator (); iter.hasNext ();) {
                XField member = (XField) iter.next ();
                String key = member.getName ();
                
                pw.println ("        attrs = new java.util.HashSet ();");
                addExpressions (member.getDoc ().getTags (), pw, "attrs", sourceFile.getPath ());
                pw.println ("        fieldAttributes.put (\"" + key + "\", attrs);");
                pw.println ("        attrs = null;");
                pw.println ();
            }
            pw.println ("    }");
            
            // ---- Method Attributes
            
            pw.println ("    private static void initMethodAttributes () {");
            pw.println ("        java.util.Set attrs = null;");
            for (Iterator iter = xClass.getMethods ().iterator (); iter.hasNext ();) {
                XMethod member = (XMethod) iter.next ();
                StringBuffer sb = new StringBuffer ();
                sb.append (member.getName ()).append ("(");
                sb.append (getParameterTypes (member.getParameters ()));
                sb.append (")");
                String key = sb.toString ();
                
                pw.println ("        attrs = new java.util.HashSet ();");
                addExpressions (member.getDoc ().getTags (), pw, "attrs", sourceFile.getPath ());
                pw.println ("        methodAttributes.put (\"" + key + "\", attrs);");
                pw.println ("        attrs = null;");
                pw.println ();
            }
            pw.println ("    }");
            
            
            // ---- Constructor Attributes
            
            pw.println ("    private static void initConstructorAttributes () {");
            pw.println ("        java.util.Set attrs = null;");
            for (Iterator iter = xClass.getConstructors ().iterator (); iter.hasNext ();) {
                XConstructor member = (XConstructor) iter.next ();
                StringBuffer sb = new StringBuffer ();
                sb.append ("(");
                sb.append (getParameterTypes (member.getParameters ()));
                sb.append (")");
                String key = sb.toString ();
                
                pw.println ("        attrs = new java.util.HashSet ();");
                addExpressions (member.getDoc ().getTags (), pw, "attrs", sourceFile.getPath ());
                pw.println ("        constructorAttributes.put (\"" + key + "\", attrs);");
                pw.println ("        attrs = null;");
                pw.println ();
            }
            pw.println ("    }");            
        }
        pw.println ("}");
        
        pw.close ();
    }
    
    protected File getSourceFile (String qualifiedName) throws BuildException {
        String path = qualifiedName.replace ('.', '/') + ".java";
        Iterator iter = fileSets.iterator ();
        while (iter.hasNext ()) {
            FileSet fs = (FileSet) iter.next ();
            File maybe = new File (fs.getDir (project), path);
            if (maybe.exists ()) {
                return maybe;
            }
        }
        throw new BuildException ("Could not find source file for " + qualifiedName);
    }
    
    protected boolean hasAttributes (XClass xClass) {
        if (tagHasAttributes (xClass.getDoc ().getTags ()) ||
            elementHasAttributes (xClass.getFields ()) ||
            elementHasAttributes (xClass.getMethods ()) ||
            elementHasAttributes (xClass.getConstructors ()) ) {
            return true;
        }
        return false;
    }

    
    protected boolean isAttribute (XTag tag) {
        return Character.isUpperCase (tag.getName ().charAt (0));
    }
    
    protected void start() throws BuildException {
        destDir.mkdirs ();
        
        XJavaDoc doc = getXJavaDoc ();
        Iterator iter = doc.getSourceClasses ().iterator ();
        try {
            while (iter.hasNext ()) {
                
                XClass xClass = (XClass) iter.next ();
                if (!xClass.isInner ()) {
                    generateClass (xClass);
                }                
            }
        } catch (Exception e) {
            throw new BuildException (e.toString (), e);
        }
    }
    
    protected boolean tagHasAttributes (Collection tags) {
        Iterator iter = tags.iterator ();
        while (iter.hasNext ()) {
            XTag tag = (XTag) iter.next ();
            if (isAttribute (tag)) {
                return true;
            }
        }
        return false;
    }
}