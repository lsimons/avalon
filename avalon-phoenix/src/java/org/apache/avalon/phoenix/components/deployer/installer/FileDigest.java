package org.apache.avalon.phoenix.components.deployer.installer;

import java.io.File;

/**
 * FileDigest holds file deployment information. Information
 * used to verify if file was modified since deployment.
 *
 * @version $Revision: 1.2 $ $Date: 2002/07/26 09:49:20 $
 */
public class FileDigest
{
    private final File m_file;

    private final long m_checksum;

    /** Create a new FileDigest object.
     *
     * @param file the file.
     * @param checksum the checksum value of the file.
     */
    public FileDigest( final File file, final long checksum )
    {
        m_file = file;
        m_checksum = checksum;
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
}

