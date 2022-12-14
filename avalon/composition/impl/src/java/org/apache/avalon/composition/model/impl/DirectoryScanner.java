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
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.avalon.composition.model.FileSelector;
import org.apache.avalon.composition.util.FileUtils;
import org.apache.avalon.composition.util.ScannerUtils;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.util.env.Env;

/**
 * TODO Write class description.
 * 
 * @author Apache Ant Development Team (Kuiper, Umasankar, Atherton, and Levy-Lambert)
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.7 $ $Date: 2004/05/09 23:51:08 $
 */
public class DirectoryScanner {

    private static final boolean ON_VMS = Env.isOpenVMS();
    
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
    
    /** Whether or not our results were built by a slow scan. */
    protected boolean haveSlowResults = false;

    /**
     * Whether or not the file system should be treated as a case sensitive
     * one.
     */
    protected boolean isCaseSensitive = true;
    
    /**
     * Whether or not symbolic links should be followed.
     */
    private boolean followSymlinks = true;

    /** Helper. */
    private static final FileUtils fileUtils = FileUtils.newFileUtils();
    
    /** Whether or not everything tested so far has been included. */
    protected boolean everythingIncluded = true;

    /**
     * Container-supplied logger instance.
     */
    private Logger m_logger;
    
    /**
     * Sole constructor.
     */
    public DirectoryScanner() {
    }

    /**
     * Tests whether or not a given path matches the start of a given
     * pattern up to the first "**".
     * <p>
     * This is not a general purpose test and should only be used if you
     * can live with false positives. For example, <code>pattern=**\a</code>
     * and <code>str=b</code> will yield <code>true</code>.
     *
     * @param pattern The pattern to match against. Must not be
     *                <code>null</code>.
     * @param str     The path to match, as a String. Must not be
     *                <code>null</code>.
     *
     * @return whether or not a given path matches the start of a given
     * pattern up to the first "**".
     */
    protected static boolean matchPatternStart(String pattern, String str)
    {
        return ScannerUtils.matchPatternStart(pattern, str);
    }
    
    /**
     * Tests whether or not a given path matches the start of a given
     * pattern up to the first "**".
     * <p>
     * This is not a general purpose test and should only be used if you
     * can live with false positives. For example, <code>pattern=**\a</code>
     * and <code>str=b</code> will yield <code>true</code>.
     *
     * @param pattern The pattern to match against. Must not be
     *                <code>null</code>.
     * @param str     The path to match, as a String. Must not be
     *                <code>null</code>.
     * @param isCaseSensitive Whether or not matching should be performed
     *                        case sensitively.
     *
     * @return whether or not a given path matches the start of a given
     * pattern up to the first "**".
     */
    protected static boolean matchPatternStart(String pattern,
            String str, boolean isCaseSensitive)
    {
        return ScannerUtils.matchPatternStart(pattern, str, isCaseSensitive);
    }

    /**
     * Tests whether or not a given path matches a given pattern.
     *
     * @param pattern The pattern to match against. Must not be
     *                <code>null</code>.
     * @param str     The path to match, as a String. Must not be
     *                <code>null</code>.
     *
     * @return <code>true</code> if the pattern matches against the string,
     *         or <code>false</code> otherwise.
     */
    protected static boolean matchPath(String pattern, String str) 
    {
        return ScannerUtils.matchPath(pattern, str);
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
                                       boolean isCaseSensitive) 
    {
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
    public static boolean match(String pattern, String str) 
    {
        return ScannerUtils.match(pattern, str);
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
     * @param isCaseSensitive Whether or not matching should be performed
     *                        case sensitively.
     *
     *
     * @return <code>true</code> if the string matches against the pattern,
     *         or <code>false</code> otherwise.
     */
    protected static boolean match(String pattern, String str,
                                   boolean isCaseSensitive) 
    {
        return ScannerUtils.match(pattern, str, isCaseSensitive);
    }

    /**
     * Get the list of patterns that should be excluded by default.
     *
     * @return An array of <code>String</code> based on the current
     *         contents of the <code>defaultExcludes</code>
     *         <code>Vector</code>.
     */
    public static String[] getDefaultExcludes() 
    {
        return (String[]) defaultExcludes.toArray(new String[defaultExcludes
                                                             .size()]);
    }

    /**
     * Add a pattern to the default excludes unless it is already a
     * default exclude.
     *
     * @param s   A string to add as an exclude pattern.
     * @return    <code>true</code> if the string was added
     *            <code>false</code> if it already
     *            existed.
     */
    public static boolean addDefaultExclude(String s) 
    {
        if (defaultExcludes.indexOf(s) == -1) 
        {
            defaultExcludes.add(s);
            return true;
        }
        return false;
    }

    /**
     * Remove a string if it is a default exclude.
     *
     * @param s   The string to attempt to remove.
     * @return    <code>true</code> if <code>s</code> was a default
     *            exclude (and thus was removed),
     *            <code>false</code> if <code>s</code> was not
     *            in the default excludes list to begin with
     */
    public static boolean removeDefaultExclude(String s) 
    {
        return defaultExcludes.remove(s);
    }

    /**
     * Go back to the hard wired default exclude patterns
     */
    public static void resetDefaultExcludes() 
    {
        defaultExcludes = new Vector();

        for (int i = 0; i < DEFAULTEXCLUDES.length; i++) 
        {
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
    public void setBasedir(String basedir) 
    {
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
    public void setBasedir(File basedir) 
    {
        this.basedir = basedir;
    }

    /**
     * Returns the base directory to be scanned.
     * This is the directory which is scanned recursively.
     *
     * @return the base directory to be scanned
     */
    public File getBasedir() 
    {
        return basedir;
    }

    /**
     * Find out whether include exclude patterns are matched in a
     * case sensitive way
     * @return whether or not the scanning is case sensitive
     * @since ant 1.6
     */
    public boolean isCaseSensitive() 
    {
        return isCaseSensitive;
    }
    /**
     * Sets whether or not include and exclude patterns are matched
     * in a case sensitive way
     *
     * @param isCaseSensitive whether or not the file system should be
     *                        regarded as a case sensitive one
     */
    public void setCaseSensitive(boolean isCaseSensitive) 
    {
        this.isCaseSensitive = isCaseSensitive;
    }

    /**
     * gets whether or not a DirectoryScanner follows symbolic links
     *
     * @return flag indicating whether symbolic links should be followed
     *
     * @since ant 1.6
     */
    public boolean isFollowSymlinks() 
    {
        return followSymlinks;
    }

    /**
     * Sets whether or not symbolic links should be followed.
     *
     * @param followSymlinks whether or not symbolic links should be followed
     */
    public void setFollowSymlinks(boolean followSymlinks) 
    {
        this.followSymlinks = followSymlinks;
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
    public void setIncludes(String[] includes) 
    {
        if (includes == null) 
        {
            this.includes = null;
        }
        else 
        {
            this.includes = new String[includes.length];
            for (int i = 0; i < includes.length; i++) 
            {
                String pattern;
                pattern = includes[i].replace('/', File.separatorChar).replace(
                        '\\', File.separatorChar);
                if (pattern.endsWith(File.separator)) 
                {
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
    public void setExcludes(String[] excludes) 
    {
        if( excludes == null) 
        {
            this.excludes = null;
        }
        else 
        {
            this.excludes = new String[excludes.length];
            for (int i = 0; i < excludes.length; i++) 
            {
                String pattern;
                pattern = excludes[i].replace('/', File.separatorChar).replace(
                        '\\', File.separatorChar);
                if (pattern.endsWith(File.separator)) 
                {
                    pattern += "**";
                }
                this.excludes[i] = pattern;
            }
        }
    }

    /**
     * Sets the selectors that will select the filelist.
     *
     * @param selectors specifies the selectors to be invoked on a scan
     */
    public void setSelectors(FileSelector[] selectors) 
    {
        this.selectors = selectors;
    }


    /**
     * Returns whether or not the scanner has included all the files or
     * directories it has come across so far.
     *
     * @return <code>true</code> if all files and directories which have
     *         been found so far have been included.
     */
    public boolean isEverythingIncluded() 
    {
        return everythingIncluded;
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
    public void scan() throws IllegalStateException, IOException 
    {
        if (basedir == null) 
        {
            throw new IllegalStateException("No basedir set");
        }
        if (!basedir.exists())
        {
            System.out.println("basedir=[" + basedir + "]");
            throw new IllegalStateException("basedir " + basedir
                                            + " does not exist");
        }
        
        if (!basedir.isDirectory()) 
        {
            throw new IllegalStateException("basedir " + basedir
                                            + " is not a directory");
        }

        if (includes == null) 
        {
            // No includes supplied, so set it to 'matches all'
            includes = new String[1];
            includes[0] = "*.jar";
        }
        if (excludes == null) 
        {
            excludes = new String[0];
        }
        if (selectors == null) 
        {
            selectors = new FileSelector[1];
            selectors[0] = new DefaultFileSelector();
        }
        
        filesIncluded    = new Vector();
        filesNotIncluded = new Vector();
        filesExcluded    = new Vector();
        filesDeselected  = new Vector();
        dirsIncluded     = new Vector();
        dirsNotIncluded  = new Vector();
        dirsExcluded     = new Vector();
        dirsDeselected   = new Vector();

        if (isIncluded("")) 
        {
            if (!isExcluded("")) 
            {
                if (isSelected("", basedir)) 
                {
                    dirsIncluded.addElement("");
                } 
                else 
                {
                    dirsDeselected.addElement("");
                }
            } 
            else 
            {
                dirsExcluded.addElement("");
            }
        } 
        else 
        {
            dirsNotIncluded.addElement("");
        }
        checkIncludePatterns();
        clearCaches();
        
    }
    
    /**
     * this routine is actually checking all the include patterns in
     * order to avoid scanning everything under base dir
     */
    private void checkIncludePatterns() throws IOException 
    {
        Hashtable newroots = new Hashtable();

        // put in the newroots vector the include patterns without
        // wildcard tokens

        for( int icounter = 0; icounter < includes.length; icounter++ ) 
        {
            String newpattern =
                ScannerUtils.rtrimWildcardTokens(includes[icounter]);
            newroots.put(newpattern, includes[icounter]);
            getLogger().debug(
              "newpattern=[" + newpattern + "]; " 
              + "includes[" + icounter + "]=[" 
              + includes[icounter] + "]" );
        }

        if (newroots.containsKey("")) 
        {
            // we are going to scan everything anyway
            scandir(basedir, "", true);
        } 
        else 
        {
            // only scan directories that can include matched files or
            // directories
            Enumeration enum2 = newroots.keys();

            File canonBase = null;
            canonBase = basedir.getCanonicalFile();
            getLogger().debug("canonBase=[" + canonBase + "]");

            while (enum2.hasMoreElements()) 
            {
                String currentelement = (String) enum2.nextElement();
                getLogger().debug("currentelement=[" + currentelement + "]");
                String originalpattern = (String) newroots.get(currentelement);
                getLogger().debug("originalpattern=[" + originalpattern + "]");
                File myfile = new File(basedir, currentelement);
                getLogger().debug("myfile=[" + myfile.getAbsolutePath() + "]");

                if (myfile.exists()) 
                {
                    // may be on a case insensitive file system.  We want
                    // the results to show what's really on the disk, so
                    // we need to double check.
                    File canonFile = myfile.getCanonicalFile();
                    String path = fileUtils.removeLeadingPath(canonBase,
                                                              canonFile);
                    if (!path.equals(currentelement) || ON_VMS) 
                    {
                        myfile = findFile(basedir, currentelement);
                        if( null != myfile ) 
                        {
                            currentelement =
                              fileUtils.removeLeadingPath(
                                basedir, myfile );
                        }
                    }
                }

                if( ( myfile == null || !myfile.exists() ) && !isCaseSensitive ) 
                {
                    File f = findFileCaseInsensitive(basedir, currentelement);
                    if (f.exists()) 
                    {
                        // adapt currentelement to the case we've
                        // actually found
                        currentelement = 
                          fileUtils.removeLeadingPath(
                            basedir, f );
                        myfile = f;
                    }
                }

                if( myfile != null && myfile.exists() ) 
                {
                    if (!followSymlinks
                        && isSymlink(basedir, currentelement)) 
                    {
                        continue;
                    }

                    if (myfile.isDirectory()) 
                    {
                        getLogger().debug("file is a directory!");
                        if( isIncluded(currentelement)
                            && currentelement.length() > 0) 
                        {
                            getLogger().debug(
                              "calling accounForIncludedDir(" 
                              + currentelement + "," 
                              + myfile.getAbsolutePath() 
                              + ")" );
                            accountForIncludedDir( currentelement, myfile, true );
                        }
                        else 
                        {
                            if (currentelement.length() > 0) 
                            {
                                if(
                                  currentelement.charAt(
                                    currentelement.length() - 1)
                                    != File.separatorChar ) 
                                {
                                    currentelement =
                                      currentelement + File.separatorChar;
                                }
                            }
                            getLogger().debug(
                               "calling scandir("
                               + myfile.getAbsolutePath() 
                               + "," 
                               + currentelement 
                               + ")" );
                            scandir( myfile, currentelement, true );
                        }
                    } 
                    else 
                    {
                        if( isCaseSensitive
                          && originalpattern.equals(currentelement) ) 
                        {
                            accountForIncludedFile(currentelement, myfile);
                        } 
                        else if( !isCaseSensitive
                          && originalpattern.equalsIgnoreCase(currentelement) ) 
                        {
                            accountForIncludedFile(currentelement, myfile);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Scans the given directory for files and directories. Found files and
     * directories are placed in their respective collections, based on the
     * matching of includes, excludes, and the selectors.  When a directory
     * is found, it is scanned recursively.
     *
     * @param dir   The directory to scan. Must not be <code>null</code>.
     * @param vpath The path relative to the base directory (needed to
     *              prevent problems with an absolute path when using
     *              dir). Must not be <code>null</code>.
     * @param fast  Whether or not this call is part of a fast scan.
     *
     * @see #filesIncluded
     * @see #filesNotIncluded
     * @see #filesExcluded
     * @see #dirsIncluded
     * @see #dirsNotIncluded
     * @see #dirsExcluded
     */
    protected void scandir(File dir, String vpath, boolean fast)
        throws IOException
    {
        getLogger().debug("Inside scandir");
        
        if (dir == null) 
        {
            throw new IOException("dir must not be null.");
        } 
        else if (!dir.exists()) 
        {
            throw new IOException(dir + " doesn't exists.");
        } 
        else if (!dir.isDirectory()) 
        {
            throw new IOException(dir + " is not a directory.");
        }

        // avoid double scanning of directories, can only happen in fast mode
        if (fast && hasBeenScanned(vpath)) 
        {
            return;
        }
        String[] newfiles = dir.list();

        if (newfiles == null) 
        {
            /*
             * two reasons are mentioned in the API docs for File.list
             * (1) dir is not a directory. This is impossible as
             *     we wouldn't get here in this case.
             * (2) an IO error occurred (why doesn't it throw an exception
             *     then???)
             */
            throw new IOException(
              "IO error scanning directory "
              + dir.getAbsolutePath());
        }

        if (!followSymlinks) 
        {
            Vector noLinks = new Vector();
            for (int i = 0; i < newfiles.length; i++) 
            {
                try 
                {
                    if (fileUtils.isSymbolicLink(dir, newfiles[i])) 
                    {
                        String name = vpath + newfiles[i];
                        File   file = new File(dir, newfiles[i]);
                        if (file.isDirectory()) 
                        {
                            dirsExcluded.addElement(name);
                        }
                        else 
                        {
                            filesExcluded.addElement(name);
                        }
                    } 
                    else 
                    {
                        noLinks.addElement(newfiles[i]);
                    }
                } 
                catch (IOException ioe) 
                {
                    String msg = "IOException caught while checking "
                        + "for links, couldn't get canonical path!";
                    // will be caught and redirected to Ant's logging system
                    System.err.println(msg);
                    noLinks.addElement(newfiles[i]);
                }
            }
            newfiles = new String[noLinks.size()];
            noLinks.copyInto(newfiles);
        }

        for (int i = 0; i < newfiles.length; i++) 
        {
            String name = vpath + newfiles[i];
            File   file = new File(dir, newfiles[i]);
            getLogger().debug("file=[" + file.getAbsolutePath() + "]");
            if (file.isDirectory()) 
            {
                getLogger().debug("this is a directory.");
                if (isIncluded(name)) 
                {
                    getLogger().debug("and it will be included");
                    accountForIncludedDir(name, file, fast);
                } 
                else 
                {
                    getLogger().debug("and it will not be included");
                    everythingIncluded = false;
                    dirsNotIncluded.addElement(name);
                    if (fast && couldHoldIncluded(name)) 
                    {
                        scandir(file, name + File.separator, fast);
                    }
                }
                if (!fast) 
                {
                    scandir(file, name + File.separator, fast);
                }
            } 
            else if (file.isFile()) 
            {
                getLogger().debug("this is a file");
                if (isIncluded(name)) 
                {
                    getLogger().debug("and it will be included");
                    accountForIncludedFile(name, file);
                } 
                else 
                {
                    getLogger().debug("and it will not be included");
                    everythingIncluded = false;
                    filesNotIncluded.addElement(name);
                }
            }
        }
    }
    
    /**
     * process included file
     * @param name  path of the file relative to the directory of the fileset
     * @param file  included file
     */
    private void accountForIncludedFile(String name, File file) 
    {
        if (!filesIncluded.contains(name)
            && !filesExcluded.contains(name)
            && !filesDeselected.contains(name)) 
        {

            if (!isExcluded(name)) 
            {
                if (isSelected(name, file)) 
                {
                    filesIncluded.addElement(name);
                } 
                else 
                {
                    everythingIncluded = false;
                    filesDeselected.addElement(name);
                }
            } 
            else 
            {
                everythingIncluded = false;
                filesExcluded.addElement(name);
            }
        }
    }

    /**
     *
     * @param name path of the directory relative to the directory of
     * the fileset
     * @param file directory as file
     * @param fast
     */
    private void accountForIncludedDir(String name, File file, boolean fast) 
        throws IOException
    {
        getLogger().debug("Inside accountForIncludeDir");
        
        if (!dirsIncluded.contains(name)
            && !dirsExcluded.contains(name)
            && !dirsDeselected.contains(name)) 
        {

            getLogger().debug("waypoint-1");
            if (!isExcluded(name)) 
            {
                getLogger().debug("waypoint-2a");
                if (isSelected(name, file)) 
                {
                    getLogger().debug("waypoint-3a");
                    dirsIncluded.addElement(name);
                    if (fast) 
                    {
                        getLogger().debug("calling scandir(" +
                                file.getAbsolutePath() + "," +
                                name + File.separator);
                        scandir(file, name + File.separator, fast);
                    }
                } 
                else 
                {
                    getLogger().debug("waypoint-3b");
                    everythingIncluded = false;
                    dirsDeselected.addElement(name);
                    if (fast && couldHoldIncluded(name)) 
                    {
                        getLogger().debug("waypoint-4b");
                        scandir(file, name + File.separator, fast);
                    }
                }

            } 
            else 
            {
                getLogger().debug("waypoint-2b");
                everythingIncluded = false;
                dirsExcluded.addElement(name);
                if (fast && couldHoldIncluded(name)) 
                {
                    getLogger().debug("waypoint-3c");
                    scandir(file, name + File.separator, fast);
                }
            }
        }
    }
    
    /**
     * Tests whether or not a name matches against at least one include
     * pattern.
     *
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against at least one
     *         include pattern, or <code>false</code> otherwise.
     */
    protected boolean isIncluded(String name) 
    {
        for (int i = 0; i < includes.length; i++) 
        {
            if (matchPath(includes[i], name, isCaseSensitive)) 
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether or not a name matches the start of at least one include
     * pattern.
     *
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against the start of at
     *         least one include pattern, or <code>false</code> otherwise.
     */
    protected boolean couldHoldIncluded(String name) 
    {
        for (int i = 0; i < includes.length; i++) 
        {
            if (matchPatternStart(includes[i], name, isCaseSensitive)) 
            {
                if (isMorePowerfulThanExcludes(name, includes[i])) 
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *  find out whether one particular include pattern is more powerful
     *  than all the excludes
     *  note : the power comparison is based on the length of the include pattern
     *  and of the exclude patterns without the wildcards
     *  ideally the comparison should be done based on the depth
     *  of the match, that is to say how many file separators have been matched
     *  before the first ** or the end of the pattern
     *
     *  IMPORTANT : this function should return false "with care"
     *
     *  @param name the relative path that one want to test
     *  @param includepattern  one include pattern
     *  @return true if there is no exclude pattern more powerful than this include pattern
     */
    private boolean isMorePowerfulThanExcludes(String name, String includepattern) 
    {
        String soughtexclude = name + File.separator + "**";
        for (int counter = 0; counter < excludes.length; counter++) 
        {
            if (excludes[counter].equals(soughtexclude))  
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Tests whether or not a name matches against at least one exclude
     * pattern.
     *
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against at least one
     *         exclude pattern, or <code>false</code> otherwise.
     */
    protected boolean isExcluded(String name) 
    {
        for (int i = 0; i < excludes.length; i++) 
        {
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
    protected boolean isSelected(String name, File file) 
    {
        if (selectors != null) 
        {
            for (int i = 0; i < selectors.length; i++) 
            {
                if (!selectors[i].isSelected(basedir, name, file)) 
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the names of the files which matched at least one of the
     * include patterns and none of the exclude patterns.
     * The names are relative to the base directory.
     *
     * @return the names of the files which matched at least one of the
     *         include patterns and none of the exclude patterns.
     */
    public String[] getIncludedFiles()
    {
        String[] files = new String[filesIncluded.size()];
        filesIncluded.copyInto(files);
        Arrays.sort(files);
        return files;
    }

    /**
     * temporary table to speed up the various scanning methods below
     */
    private Map fileListMap = new HashMap();

    /**
     * Returns a cached result of list performed on file, if
     * available.  Invokes the method and caches the result otherwise.
     *
     * @since Ant 1.6
     */
    private String[] list(File file) 
    {
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
     * From <code>base</code> traverse the filesystem in a case
     * insensitive manner in order to find a file that matches the
     * given name.
     *
     * @return File object that points to the file in question.  if it
     * hasn't been found it will simply be <code>new File(base,
     * path)</code>.
     *
     * @since Ant 1.6
     */
    private File findFileCaseInsensitive(File base, String path)
        throws IOException
    {
        File f = findFileCaseInsensitive(
                   base, ScannerUtils.tokenizePath(path));
        return  f == null ? new File(base, path) : f;
    }

    /**
     * From <code>base</code> traverse the filesystem in a case
     * insensitive manner in order to find a file that matches the
     * given stack of names.
     *
     * @return File object that points to the file in question or null.
     *
     * @since Ant 1.6
     */
    private File findFileCaseInsensitive(File base, Vector pathElements)
        throws IOException
    {
        if (pathElements.size() == 0) 
        {
            return base;
        } 
        else 
        {
            if (!base.isDirectory()) 
            {
                return null;
            }
            String[] files = list(base);
            if (files == null) 
            {
                throw new IOException("IO error scanning directory "
                                         + base.getAbsolutePath());
            }
            String current = (String) pathElements.remove(0);
            for (int i = 0; i < files.length; i++) 
            {
                if (files[i].equals(current)) 
                {
                    base = new File(base, files[i]);
                    return findFileCaseInsensitive(base, pathElements);
                }
            }
            for (int i = 0; i < files.length; i++) 
            {
                if (files[i].equalsIgnoreCase(current)) 
                {
                    base = new File(base, files[i]);
                    return findFileCaseInsensitive(base, pathElements);
                }
            }
        }
        return null;
    }

    /**
     * From <code>base</code> traverse the filesystem in order to find
     * a file that matches the given name.
     *
     * @return File object that points to the file in question or null.
     *
     * @since Ant 1.6
     */
    private File findFile(File base, String path) throws IOException 
    {
        return findFile(base, ScannerUtils.tokenizePath(path));
    }

    /**
     * From <code>base</code> traverse the filesystem in order to find
     * a file that matches the given stack of names.
     *
     * @return File object that points to the file in question or null.
     *
     * @since Ant 1.6
     */
    private File findFile(File base, Vector pathElements) throws IOException 
    {
        if (pathElements.size() == 0) 
        {
            return base;
        } 
        else 
        {
            if (!base.isDirectory()) 
            {
                return null;
            }
            String[] files = list(base);
            if (files == null) 
            {
                throw new IOException("IO error scanning directory "
                                         + base.getAbsolutePath());
            }
            String current = (String) pathElements.remove(0);
            for (int i = 0; i < files.length; i++) 
            {
                if (files[i].equals(current)) 
                {
                    base = new File(base, files[i]);
                    return findFile(base, pathElements);
                }
            }
        }
        return null;
    }

    /**
     * Do we have to traverse a symlink when trying to reach path from
     * basedir?
     * @since Ant 1.6
     */
    private boolean isSymlink(File base, String path) 
    {
        return isSymlink(base, ScannerUtils.tokenizePath(path));
    }

    /**
     * Do we have to traverse a symlink when trying to reach path from
     * basedir?
     * @since Ant 1.6
     */
    private boolean isSymlink(File base, Vector pathElements) 
    {
        if (pathElements.size() > 0) 
        {
            String current = (String) pathElements.remove(0);
            try 
            {
                if (fileUtils.isSymbolicLink(base, current)) 
                {
                    return true;
                } 
                else 
                {
                    base = new File(base, current);
                    return isSymlink(base, pathElements);
                }
            } 
            catch (IOException ioe) 
            {
                String msg = "IOException caught while checking "
                    + "for links, couldn't get canonical path!";
                // will be caught and redirected to Ant's logging system
                System.err.println(msg);
                return false;
            }
        }
        return false;
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
    private boolean hasBeenScanned(String vpath) 
    {
        return !scannedDirs.add(vpath);
    }

    /**
     * Clear internal caches.
     */
    private void clearCaches() 
    {
        fileListMap.clear();
        scannedDirs.clear();
    }
    
    public String toString() 
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{basedir=[" + basedir.getAbsolutePath() + "];");
        buffer.append("isCaseSensitive=[" + isCaseSensitive + "];");
        buffer.append("includes=(");
        for (int i = 0; i < includes.length; i++) 
        {
            if (i != 0) buffer.append(",");
            buffer.append(includes[i]);
        }
        buffer.append(");");
        buffer.append("excludes=(");
        for (int i = 0; i < excludes.length; i++) 
        {
            if (i != 0) buffer.append(",");
            buffer.append(excludes[i]);
        }
        buffer.append(")}");
        return buffer.toString();
    }

    public Logger getLogger() 
    {
        return m_logger;
    }
    
    public void setLogger(Logger logger) 
    {
        m_logger = logger;
    }
 
}
