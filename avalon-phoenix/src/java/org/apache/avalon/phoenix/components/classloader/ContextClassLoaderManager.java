package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.util.HashMap;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;
import org.apache.avalon.phoenix.interfaces.ClassLoaderSet;

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
    public ClassLoaderSet createClassLoaderSet( final Configuration environment,
                                                final File baseDirectory,
                                                final File workDirectory )
        throws Exception
    {
        final ClassLoader classLoader =
            Thread.currentThread().getContextClassLoader();
        return new ClassLoaderSet( classLoader, new HashMap() );
    }
}
