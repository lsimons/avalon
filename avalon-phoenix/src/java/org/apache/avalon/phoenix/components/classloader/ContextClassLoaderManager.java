package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;

/**
 * Basic ClassLoaderManager that just returns current
 * ContextClassLoader.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @see ClassLoaderManager
 */
public class ContextClassLoaderManager
    implements ClassLoaderManager
{
    /**
     * Return current Context {@link ClassLoader}.
     *
     * @param environment ignored
     * @param source the source of application. (usually the name of the .sar file
     *               or else the same as baseDirectory)
     * @param baseDirectory the base directory of application
     * @param workDirectory the work directory of application
     * @return the ContextClassLoader created
     * @throws Exception if an error occurs
     */
    public ClassLoader createClassLoader( final Configuration environment,
                                          final File source,
                                          final File baseDirectory,
                                          final File workDirectory )
        throws Exception
    {
        return Thread.currentThread().getContextClassLoader();
    }
}
