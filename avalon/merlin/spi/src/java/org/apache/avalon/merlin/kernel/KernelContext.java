

package org.apache.avalon.merlin.kernel;

import java.io.File;
import java.net.URL;

import org.apache.avalon.repository.Repository;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.model.ModelFactory;
import org.apache.avalon.composition.model.ContainmentContext;
import org.apache.excalibur.mpool.PoolManager;

/**
 * The KernelContext declares the information needed to establish
 * a new kernel instance.
 */
public interface KernelContext
{
   /**
    * Return the model factory.
    * @return the factory
    */
    ModelFactory getModelFactory();

   /**
    * Return the runtime repository.
    * @return the repository
    */
    Repository getRepository();

   /**
    * Return the home path
    * @return the home path (possibly null)
    */
    File getHomePath();

   /**
    * Return the temporary directory path
    * @return the path (possibly null)
    */
    File getTempPath();

   /**
    * Return the library path
    * @return the path (possibly null)
    */
    File getLibraryPath();

   /**
    * Return the root containment context.
    * @return the kernel directive url
    */
    ContainmentContext getContainmentContext();

   /**
    * Return the URLs to install into the kerenel on startup.
    * @return the block directive urls
    */
    URL[] getInstallSequence();

   /**
    * Return the kernel debug flag.
    * @return the debug flag
    */
    boolean getDebugFlag();

   /**
    * Return the kernel server flag.
    * @return the server flag
    */
    boolean getServerFlag();

   /**
    * Return the logging manager for the kernel.
    * @return the logging manager
    */
    LoggingManager getLoggingManager();
    
   /**
    * Return the kernel logging channel.
    * @return the kernel logging channel
    */
    Logger getKernelLogger();
    
   /**
    * Return the kernel pool manager.
    * @return the pool manager
    */
    PoolManager getPoolManager();
    
   /**
    * Return the set of target overrides.
    * @return the target override directives
    */
    TargetDirective[] getTargetDirectives();

}
