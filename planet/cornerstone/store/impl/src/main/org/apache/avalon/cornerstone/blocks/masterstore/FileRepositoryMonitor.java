package org.apache.avalon.cornerstone.blocks.masterstore;

import java.io.IOException;

/**
 * @author Paul Hammant
 * @version $Revision: 1.8 $
 */
public interface FileRepositoryMonitor {
    void repositoryCreated(Class aClass, String m_name, String m_destination, String childName);
    void keyRemoved(Class aClass, String key);
    void checkingKey(Class aClass, String key);
    void returningKey(Class aClass, String key);
    void returningObjectForKey(Class aClass, Object object, String key);
    void storingObjectForKey(Class xmlFilePersistentObjectRepository, Object value, String key);
    void initialized(Class aClass);
    void pathOpened(Class aClass, String m_path);
    void unExpectedIOException(Class aClass, String message, IOException ioe);
}
