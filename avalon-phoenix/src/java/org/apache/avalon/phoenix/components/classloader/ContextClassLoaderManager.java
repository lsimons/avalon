package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.net.URL;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;

/**
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class ContextClassLoaderManager
    extends AbstractLoggable
    implements ClassLoaderManager
{
    public ClassLoader createClassLoader( final Configuration server,
                                          final File homeDirectory,
                                          final String[] classPath )
        throws Exception
    {
        return Thread.currentThread().getContextClassLoader();
    }
}
