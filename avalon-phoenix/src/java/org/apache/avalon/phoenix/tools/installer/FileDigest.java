package org.apache.avalon.phoenix.tools.installer;

import java.io.File;

class FileDigest
{
        
    /** Holds value of property name. */
    private File m_file;

    /** Holds value of property checksum.  */
    private long m_checksum;
    
    /** Holds value of property modified.  */
    private long m_modified;
    
    public FileDigest(final File file, final long checksum, final long modified)
    {
        m_file = file;
        m_checksum = checksum;
        m_modified = modified;
    }
        
    /** Getter for property name.
     * @return Value of property name.
     */
    public File getFile()
    {
        return m_file;
    }

    /** Getter for property checksum.
     * @return Value of property checksum.
     */
    public long getChecksum()
    {
        return m_checksum;
    }
    
    /** Getter for property modified.
     * @return Value of property modified.
     */
    public long getModified()
    {
        return m_modified;
    }
}

