package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;

/**
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class ContextClassLoaderManager
    extends AbstractLogEnabled
    implements ClassLoaderManager
{
    public ClassLoader createClassLoader( final Configuration server,
                                          final File source,
                                          final File homeDirectory,
                                          final String[] classPath )
        throws Exception
    {
        return Thread.currentThread().getContextClassLoader();
    }
}
