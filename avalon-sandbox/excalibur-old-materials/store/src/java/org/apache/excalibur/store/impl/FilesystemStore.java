/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.store.impl;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.store.Store;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.BitSet;
import java.util.Enumeration;

/**
 * Stores objects on the filesystem: String objects as text files,
 * all other objects are serialized.
 *
 * @author ?
 * @author <a href="mailto:vgritsenko@apache.org">Vadim Gritsenko</a>
 * @version CVS $Id: FilesystemStore.java,v 1.1 2002/05/02 08:55:39 cziegeler Exp $
 */
public class FilesystemStore
extends AbstractLogEnabled
implements Store, ThreadSafe {

    /** The directory repository */
    protected File directoryFile;
    protected volatile String directoryPath;

    /**
     * Sets the repository's location
     */
    public void setDirectory(final String directory)
    throws IOException {
        this.setDirectory(new File(directory));
    }

    /**
     * Sets the repository's location
     */
    public void setDirectory(final File directory)
    throws IOException {
        this.directoryFile = directory;

        /* Save directory path prefix */
        this.directoryPath = FileUtil.getFullFilename(this.directoryFile);
        this.directoryPath += File.separator;

        /* Does directory exist? */
        if (!this.directoryFile.exists()) {
            /* Create it anew */
            if (!this.directoryFile.mkdir()) {
                throw new IOException(
                "Error creating store directory '" + this.directoryPath + "': ");
            }
        }

        /* Is given file actually a directory? */
        if (!this.directoryFile.isDirectory()) {
            throw new IOException("'" + this.directoryPath + "' is not a directory");
        }

        /* Is directory readable and writable? */
        if (!(this.directoryFile.canRead() && this.directoryFile.canWrite())) {
            throw new IOException(
                "Directory '" + this.directoryPath + "' is not readable/writable"
            );
        }
    }

    /**
     * Returns the repository's full pathname
     */
    public String getDirectoryPath() {
        return this.directoryPath;
    }

    /**
     * Get the File object associated with the given unique key name.
     */
    public synchronized Object get(final Object key) {
        final File file = fileFromKey(key);

        if (file != null && file.exists()) {
            if (this.getLogger().isDebugEnabled()) {
                getLogger().debug("Found file: " + key);
            }
            try {
                // FIXME
                return null;
                //return IOUtils.deserializeObject(file);
            } catch (Exception any) {
                getLogger().error("Error during deseralization.", any);
            }
        } else {
            if (this.getLogger().isDebugEnabled()) {
                getLogger().debug("NOT Found file: " + key);
            }
        }

        return null;
    }

    /**
     * Store the given object in a persistent state.
     * 1) Null values generate empty directories.
     * 2) String values are dumped to text files
     * 3) Object values are serialized
     */
    public synchronized void store(final Object key, final Object value)
    throws IOException {
        final File file = fileFromKey(key);

        /* Create subdirectories as needed */
        final File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        /* Store object as file */
        if (value == null) { /* Directory */
            if (file.exists()) {
                if (!file.delete()) { /* FAILURE */
                    getLogger().error("File cannot be deleted: " + file.toString());
                    return;
                }
            }

            file.mkdir();
        } else if (value instanceof String) {
            /* Text file */
            // FIXME
            //IOUtils.serializeString(file, (String) value);
        } else {
            /* Serialized Object */
            // FIXME
            //IOUtils.serializeObject(file, value);
        }
    }

    /**
     * Holds the given object in a volatile state.
     */
    public synchronized void hold(final Object key, final Object value)
    throws IOException {
        this.store(key, value);
        final File file = (File) this.fileFromKey(key);
        if (file != null) {
          file.deleteOnExit();
        }
    }

    /**
     * Remove the object associated to the given key.
     */
    public synchronized void remove(final Object key) {
        final File file = fileFromKey(key);
        if (file != null) {
            file.delete();
        }
    }

    /**
     * Indicates if the given key is associated to a contained object.
     */
    public synchronized boolean containsKey(final Object key) {
        final File file = fileFromKey(key);
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    /**
     * Returns the list of stored files as an Enumeration of Files
     */
    public synchronized Enumeration keys() {
        final FSEnumeration enum = new FSEnumeration();
        this.addKeys(enum, this.directoryFile);
        return enum;
    }

    /**
     * Returns count of the objects in the store, or -1 if could not be
     * obtained.
     */
    public synchronized int size() {
        return countKeys(this.directoryFile);
    }

    protected void addKeys(FSEnumeration enum, File directory) {
        final int subStringBegin = this.directoryFile.getAbsolutePath().length() + 1;
        final File[] files = directory.listFiles();
        for (int i=0; i<files.length; i++) {
            if (files[i].isDirectory()) {
                this.addKeys(enum, files[i]);
            } else {
                enum.add(this.decode(files[i].getAbsolutePath().substring(subStringBegin)));
            }
        }
    }

    protected int countKeys(File directory) {
        int count = 0;
        final File[] files = directory.listFiles();
        for (int i=0; i<files.length; i++) {
            if (files[i].isDirectory()) {
                count += this.countKeys(files[i]);
            } else {
                count ++;
            }
        }
        return count;
    }

    final class FSEnumeration implements Enumeration {
        private String[] array;
        private int      index;
        private int      length;

        FSEnumeration() {
            this.array = new String[16];
            this.length = 0;
            this.index = 0;
        }

        public void add(String key) {
            if (this.length == array.length) {
                String[] newarray = new String[this.length + 16];
                System.arraycopy(this.array, 0, newarray, 0, this.array.length);
                this.array = newarray;
            }
            this.array[this.length] = key;
            this.length++;
        }

        public boolean hasMoreElements() {
            return (this.index < this.length);
        }

        public Object nextElement() {
            if (this.hasMoreElements()) {
                this.index++;
                return this.array[index-1];
            }
            return null;
        }
    }

    /* Utility Methods*/
    protected File fileFromKey(final Object key) {
        File file = new File(this.directoryFile, this.encode(key.toString()));
        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();
        return file;
    }

    public String getString(final Object key)
    throws IOException {
        final File file = (File) this.fileFromKey(key);
        if (file != null) {
            // FIXME
            //return IOUtils.deserializeString(file);
        }

        return null;
    }

    public synchronized void free() {}

    public synchronized Object getObject(final Object key)
    throws IOException, ClassNotFoundException
    {
        final File file = (File) this.fileFromKey(key);
        if (file != null) {
            // FIXME
            //return IOUtils.deserializeObject(file);
        }

        return null;
    }

    /**
     * Inverse of encode exept it do not use path.
     * So decode(encode(s) - m_path) = s.
     * In other words it returns a String that can be used as key to retive
     * the record contained in the 'filename' file.
     */
    protected String decode( String filename )
    {
        return java.net.URLDecoder.decode( filename );
    }

    /** A BitSet defining the characters which don't need encoding */
    static BitSet charactersDontNeedingEncoding;
    static final int characterCaseDiff = ('a' - 'A');

    /** Initialize the BitSet */
    static
    {
        charactersDontNeedingEncoding = new BitSet(256);
        int i;
        for (i = 'a'; i <= 'z'; i++)
        {
            charactersDontNeedingEncoding.set(i);
        }
        for (i = 'A'; i <= 'Z'; i++)
        {
            charactersDontNeedingEncoding.set(i);
        }
        for (i = '0'; i <= '9'; i++)
        {
            charactersDontNeedingEncoding.set(i);
        }
        charactersDontNeedingEncoding.set('-');
        charactersDontNeedingEncoding.set('_');
        charactersDontNeedingEncoding.set('(');
        charactersDontNeedingEncoding.set(')');
    }

    /**
     * Returns a String that uniquely identifies the object.
     * <b>Note:</b> since this method uses the Object.toString()
     * method, it's up to the caller to make sure that this method
     * doesn't change between different JVM executions (like
     * it may normally happen). For this reason, it's highly recommended
     * (even if not mandated) that Strings be used as keys.
     */
    public String encode(String s) {
        final StringBuffer out = new StringBuffer( s.length() );
        final ByteArrayOutputStream buf = new ByteArrayOutputStream( 32 );
        final OutputStreamWriter writer = new OutputStreamWriter( buf );
        for (int i = 0; i < s.length(); i++)
        {
            int c = (int)s.charAt(i);
            if (charactersDontNeedingEncoding.get(c))
            {
                out.append((char)c);
            }
            else
            {
                try
                {
                    writer.write(c);
                    writer.flush();
                }
                catch(IOException e)
                {
                    buf.reset();
                    continue;
                }
                byte[] ba = buf.toByteArray();
                for (int j = 0; j < ba.length; j++)
                {
                    out.append('%');
                    char ch = Character.forDigit((ba[j] >> 4) & 0xF, 16);
                    // converting to use uppercase letter as part of
                    // the hex value if ch is a letter.
                    if (Character.isLetter(ch))
                    {
                        ch -= characterCaseDiff;
                    }
                    out.append(ch);
                    ch = Character.forDigit(ba[j] & 0xF, 16);
                    if (Character.isLetter(ch))
                    {
                        ch -= characterCaseDiff;
                    }
                    out.append(ch);
                }
                buf.reset();
            }
        }

        return out.toString();
    }

}
