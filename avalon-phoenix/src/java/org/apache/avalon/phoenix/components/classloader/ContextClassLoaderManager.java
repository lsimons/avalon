package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;

/**
 * Basic ClassLoaderManager that just returns current
 * ContextClassLoader.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class ContextClassLoaderManager
    implements ClassLoaderManager
{
    public ClassLoader createClassLoader( final Configuration server,
                                          final File source,
                                          final File homeDirectory,
                                          final File workDirectory,
                                          final String[] classPath )
        throws Exception
    {
        return Thread.currentThread().getContextClassLoader();
    }
}
