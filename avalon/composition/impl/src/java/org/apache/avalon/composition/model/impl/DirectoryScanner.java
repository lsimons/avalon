/*
 * Copyright  2000-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.avalon.composition.model.impl;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.avalon.composition.model.FileSelector;

/**
 * TODO Write class description.
 * 
 * @author Apache Ant Development Team (Kuiper, Umasankar, Atherton, and Levy-Lamber)
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/04/17 19:16:01 $
 */
public class DirectoryScanner {

    /**
     * Patterns which should be excluded by default.
     *
     * <p>Note that you can now add patterns to the list of default
     * excludes.  Added patterns will not become part of this array
     * that has only been kept around for backwards compatibility
     * reasons.</p>
     *
     * @deprecated use the {@link #getDefaultExcludes
     * getDefaultExcludes} method instead.
     */
    protected static final String[] DEFAULTEXCLUDES = {
        // Miscellaneous typical temporary files
        "**/*~",
        "**/#*#",
        "**/.#*",
        "**/%*%",
        "**/._*",

        // CVS
        "**/CVS",
        "**/CVS/**",
        "**/.cvsignore",

        // SCCS
        "**/SCCS",
        "**/SCCS/**",

        // Visual SourceSafe
        "**/vssver.scc",

        // Subversion
        "**/.svn",
        "**/.svn/**",

        // Mac
        "**/.DS_Store"
    };

    /**
     * Patterns which should be excluded by default.
     *
     * @see #addDefaultExcludes()
     */
    private static Vector defaultExcludes = new Vector();
    static {
        resetDefaultExcludes();
    }

    /** The base directory to be scanned. */
    private File basedir;

    /** The patterns for the files to be included. */
    protected String[] includes;

    /** The patterns for the files to be excluded. */
    protected String[] excludes;

    /** Selectors that will filter which files are in our candidate list. */
    protected FileSelector[] selectors = null;

    /** The files which matched at least one include and no excludes
     *  and were selected.
     */
    protected Vector filesIncluded;

    /** The files which did not match any includes or selectors. */
    protected Vector filesNotIncluded;

    /**
     * The files which matched at least one include and at least
     * one exclude.
     */
    protected Vector filesExcluded;

    /** The directories which matched at least one include and no excludes
     *  and were selected.
     */
    protected Vector dirsIncluded;

    /** The directories which were found and did not match any includes. */
    protected Vector dirsNotIncluded;

    /**
     * The directories which matched at least one include and at least one
     * exclude.
     */
    protected Vector dirsExcluded;

    /** The files which matched at least one include and no excludes and
     *  which a selector discarded.
     */
    protected Vector filesDeselected;

    /** The directories which matched at least one include and no excludes
     *  but which a selector discarded.
     */
    protected Vector dirsDeselected;

    /**
     * Whether or not the file system should be treated as a case sensitive
     * one.
     */
    protected boolean isCaseSensitive = true;
    
    /**
     * Sole constructor.
     */
    public DirectoryScanner() {
    }

    /**
     * Tests whether or not a given path matches a given pattern.
     *
     * @param pattern The pattern to match against. Must not be
     *                <code>null</code>.
     * @param str     The path to match, as a String. Must not be
     *                <code>null</code>.
     * @param isCaseSensitive Whether or not matching should be performed
     *                        case sensitively.
     *
     * @return <code>true</code> if the pattern matches against the string,
     *         or <code>false</code> otherwise.
     */
    protected static boolean matchPath(String pattern, String str,
                                       boolean isCaseSensitive) {
        return ScannerUtils.matchPath(pattern, str, isCaseSensitive);
    }

    /**
     * Tests whether or not a string matches against a pattern.
     * The pattern may contain two special characters:<br>
     * '*' means zero or more characters<br>
     * '?' means one and only one character
     *
     * @param pattern The pattern to match against.
     *                Must not be <code>null</code>.
     * @param str     The string which must be matched against the pattern.
     *                Must not be <code>null</code>.
     *
     * @return <code>true</code> if the string matches against the pattern,
     *         or <code>false</code> otherwise.
     */
    public static boolean match(String pattern, String str) {
        return ScannerUtils.match(pattern, str);
    }

    /**
     * Go back to the hard wired default exclude patterns
     */
    public static void resetDefaultExcludes() {
        defaultExcludes = new Vector();

        for (int i = 0; i < DEFAULTEXCLUDES.length; i++) {
            defaultExcludes.add(DEFAULTEXCLUDES[i]);
        }
    }

    /**
     * Sets the base directory to be scanned. This is the directory which is
     * scanned recursively. All '/' and '\' characters are replaced by
     * <code>File.separatorChar</code>, so the separator used need not match
     * <code>File.separatorChar</code>.
     *
     * @param basedir The base directory to scan.
     *                Must not be <code>null</code>.
     */
    public void setBasedir(String basedir) {
        setBasedir(new File(basedir.replace('/', File.separatorChar).replace(
                '\\', File.separatorChar)));
    }

    /**
     * Sets the base directory to be scanned. This is the directory which is
     * scanned recursively.
     *
     * @param basedir The base directory for scanning.
     *                Should not be <code>null</code>.
     */
    public void setBasedir(File basedir) {
        this.basedir = basedir;
    }

    /**
     * Returns the base directory to be scanned.
     * This is the directory which is scanned recursively.
     *
     * @return the base directory to be scanned
     */
    public File getBasedir() {
        return basedir;
    }

    /**
     * Find out whether include exclude patterns are matched in a
     * case sensitive way
     * @return whether or not the scanning is case sensitive
     * @since ant 1.6
     */
    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }
    /**
     * Sets whether or not include and exclude patterns are matched
     * in a case sensitive way
     *
     * @param isCaseSensitive whether or not the file system should be
     *                        regarded as a case sensitive one
     */
    public void setCaseSensitive(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    /**
     * Sets the list of include patterns to use. All '/' and '\' characters
     * are replaced by <code>File.separatorChar</code>, so the separator used
     * need not match <code>File.separatorChar</code>.
     * <p>
     * When a pattern ends with a '/' or '\', "**" is appended.
     *
     * @param includes A list of include patterns.
     *                 May be <code>null</code>, indicating that all files
     *                 should be included. If a non-<code>null</code>
     *                 list is given, all elements must be
     * non-<code>null</code>.
     */
    public void setIncludes(String[] includes) {
        if (includes == null) {
            this.includes = null;
        } else {
            this.includes = new String[includes.length];
            for (int i = 0; i < includes.length; i++) {
                String pattern;
                pattern = includes[i].replace('/', File.separatorChar).replace(
                        '\\', File.separatorChar);
                if (pattern.endsWith(File.separator)) {
                    pattern += "**";
                }
                this.includes[i] = pattern;
            }
        }
    }


    /**
     * Sets the list of exclude patterns to use. All '/' and '\' characters
     * are replaced by <code>File.separatorChar</code>, so the separator used
     * need not match <code>File.separatorChar</code>.
     * <p>
     * When a pattern ends with a '/' or '\', "**" is appended.
     *
     * @param excludes A list of exclude patterns.
     *                 May be <code>null</code>, indicating that no files
     *                 should be excluded. If a non-<code>null</code> list is
     *                 given, all elements must be non-<code>null</code>.
     */
    public void setExcludes(String[] excludes) {
        if (excludes == null) {
            this.excludes = null;
        } else {
            this.excludes = new String[excludes.length];
            for (int i = 0; i < excludes.length; i++) {
                String pattern;
                pattern = excludes[i].replace('/', File.separatorChar).replace(
                        '\\', File.separatorChar);
                if (pattern.endsWith(File.separator)) {
                    pattern += "**";
                }
                this.excludes[i] = pattern;
            }
        }
    }

    /**
     * Scans the base directory for files which match at least one include
     * pattern and don't match any exclude patterns. If there are selectors
     * then the files must pass muster there, as well.
     *
     * @exception IllegalStateException if the base directory was set
     *            incorrectly (i.e. if it is <code>null</code>, doesn't exist,
     *            or isn't a directory).
     */
    public void scan() throws IllegalStateException {
        if (basedir == null) {
            throw new IllegalStateException("No basedir set");
        }
        if (!basedir.exists()) {
            System.out.println("basedir=[" + basedir + "]");
            throw new IllegalStateException("basedir " + basedir
                                            + " does not exist");
        }
/*        
        if (!basedir.isDirectory()) {
            throw new IllegalStateException("basedir " + basedir
                                            + " is not a directory");
        }

        if (includes == null) {
            // No includes supplied, so set it to 'matches all'
            includes = new String[1];
            includes[0] = "**//*.jar";
        }
        if (excludes == null) {
            excludes = new String[0];
        }
        if (selectors == null) {
            selectors = new FileSelector[1];
            selectors[0] = new JarFileSelector();
        }
        
        filesIncluded    = new Vector();
        filesNotIncluded = new Vector();
        filesExcluded    = new Vector();
        filesDeselected  = new Vector();
        dirsIncluded     = new Vector();
        dirsNotIncluded  = new Vector();
        dirsExcluded     = new Vector();
        dirsDeselected   = new Vector();

        if (isIncluded("")) {
            if (!isExcluded("")) {
                if (isSelected("", basedir)) {
                    dirsIncluded.addElement("");
                } else {
                    dirsDeselected.addElement("");
                }
            } else {
                dirsExcluded.addElement("");
            }
        } else {
            dirsNotIncluded.addElement("");
        }
        checkIncludePatterns();
        clearCaches();
*/        
    }
    
    /**
     * this routine is actually checking all the include patterns in
     * order to avoid scanning everything under base dir
     */
    private void checkIncludePatterns() {
    }
    
    /**
     * Tests whether or not a name matches against at least one include
     * pattern.
     *
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against at least one
     *         include pattern, or <code>false</code> otherwise.
     */
    protected boolean isIncluded(String name) {
        for (int i = 0; i < includes.length; i++) {
            if (matchPath(includes[i], name, isCaseSensitive)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether or not a name matches against at least one exclude
     * pattern.
     *
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against at least one
     *         exclude pattern, or <code>false</code> otherwise.
     */
    protected boolean isExcluded(String name) {
        for (int i = 0; i < excludes.length; i++) {
            if (matchPath(excludes[i], name, isCaseSensitive)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether a name should be selected.
     *
     * @param name the filename to check for selecting
     * @param file the java.io.File object for this filename
     * @return <code>false</code> when the selectors says that the file
     *         should not be selected, <code>true</code> otherwise.
     */
    protected boolean isSelected(String name, File file) {
        if (selectors != null) {
            for (int i = 0; i < selectors.length; i++) {
                if (!selectors[i].isSelected(basedir, name, file)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * temporary table to speed up the various scanning methods below
     *
     * @since Ant 1.6
     */
    private Map fileListMap = new HashMap();

    /**
     * Returns a cached result of list performed on file, if
     * available.  Invokes the method and caches the result otherwise.
     *
     * @since Ant 1.6
     */
    private String[] list(File file) {
        String[] files = (String[]) fileListMap.get(file);
        if (files == null) {
            files = file.list();
            if (files != null) {
                fileListMap.put(file, files);
            }
        }
        return files;
    }

    /**
     * List of all scanned directories.
     */
    private Set scannedDirs = new HashSet();

    /**
     * Has the directory with the given path relative to the base
     * directory already been scanned?
     *
     * <p>Registers the given directory as scanned as a side effect.</p>
     */
    private boolean hasBeenScanned(String vpath) {
        return !scannedDirs.add(vpath);
    }

    /**
     * Clear internal caches.
     */
    private void clearCaches() {
        fileListMap.clear();
        scannedDirs.clear();
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{basedir=[" + basedir.getAbsolutePath() + "];");
        buffer.append("isCaseSensitive=[" + isCaseSensitive + "];");
        buffer.append("includes=(");
        for (int i = 0; i < includes.length; i++) {
            if (i != 0) buffer.append(",");
            buffer.append(includes[i]);
        }
        buffer.append(");");
        buffer.append("excludes=(");
        for (int i = 0; i < excludes.length; i++) {
            if (i != 0) buffer.append(",");
            buffer.append(excludes[i]);
        }
        buffer.append(")}");
        return buffer.toString();
    }
 
    public class JarFileSelector implements FileSelector {

        /**
         * Method that each selector will implement to create their
         * selection behaviour.
         *
         * @param basedir A java.io.File object for the base directory
         * @param filename The name of the file to check
         * @param file A File object for this filename
         * @return whether the file should be selected or not
         */
        public boolean isSelected(File basedir, String filename, File file) {
            if (getExtension(file).equals("jar")) {
                return true;
            }
            return false;
        }
        
        /**
         * Extract extension from filename.
         *
         * @param f File object
         * @return the file extension stripped from the filename
         */
        private String getExtension(final File f) {
            String extension = "";
            final String name = f.getName();
            int i = name.lastIndexOf(".");
            if (i > 0) {
                extension = name.substring(i + 1);
            } 
            return extension;
        }
    }
}
