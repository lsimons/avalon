package org.apache.avalon.phoenix.tools.installer;

import java.io.File;

/**
 * FileDigest is a deployment information holder for a file. The information 
 * is used to verify if file was modified since deployed.
 */
class FileDigest
{
    private final File m_file;
    private final long m_checksum;
    private final long m_modified;
    
    /** Create a new FileDigest object.
     *
     * @param the file.
     * @param the checksum value of the file.
     * @param the modification time of the file.
     */
    public FileDigest( final File file, final long checksum, final long modified )
    {
        m_file = file;
        m_checksum = checksum;
        m_modified = modified;
    }
        
    /** Retrieve the file.
     *
     * @return the file.
     */
    public File getFile()
    {
        return m_file;
    }

    /** Retrieve the checksum calculated at deployment time.
     *
     * @return the checksum value.
     */
    public long getChecksum()
    {
        return m_checksum;
    }
    
    /** Retrieve the modification time at deployment time.
     *
     * @return the modification time.
     */
    public long getModified()
    {
        return m_modified;
    }
}

