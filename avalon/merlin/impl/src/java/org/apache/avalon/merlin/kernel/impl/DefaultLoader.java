

package org.apache.avalon.merlin.kernel.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Locale;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.merlin.kernel.Kernel;
import org.apache.avalon.merlin.kernel.KernelContext;
import org.apache.avalon.merlin.kernel.KernelException;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.repository.BlockManifest;
import org.apache.avalon.repository.Repository;
import org.xml.sax.SAXException;

/**
 * The DefaultLoader loads a Merlin Kernel based on a supplied argument map.
 */
public class DefaultLoader
{
    //--------------------------------------------------------------------------
    // static
    //--------------------------------------------------------------------------

   /**
    * The map key to a {@link java.io.File} that is the root directory for the 
    * local application repository.  This directory will be used during retrival 
    * and of resources such as jar files referenced by block include directives 
    * and classloader resource references.
    */
    public static final String MERLIN_REPOSITORY_DIR = 
      "merlin.repository.dir";

   /**
    * The map key to a {@link java.io.File} that is the directory used as the base anchor
    * for resolution of relative path references for jar extension library directory
    * statements in classloader directives.  If not supplied the value defaults to
    * the current working directory.
    */
    public static final String MERLIN_LIBRARY_DIR = 
      "merlin.library.dir";

   /**
    * The map key to a {@link java.io.File} that is the working base directory.  Working and
    * temporary directories are created relative to this root directory.  The default
    * value is the current working directory ${user.dir}. 
    */
    public static final String MERLIN_BASE_DIR = 
      "merlin.base.dir";

   /**
    * The map key to a {@link java.net.URL} of kernel configuration.  If not supplied
    * a default kernel defintion will be established. 
    */
    public static final String MERLIN_KERNEL_URL = 
      "merlin.kernel.url";

   /**
    * The map key to a {@link java.net.URL} of an optional configuration targets override file.
    */
    public static final String MERLIN_TARGETS_URL = 
      "merlin.targets.url";

   /**
    * The map key to a {@link Boolean} declaring the info generation option. If TRUE
    * a summary listing of the Merlin environment will be generated on system 
    * establishment.  This information is generally useful when validating current
    * version information and configuration paths.
    */
    public static final String MERLIN_POLICY_INFO = 
      "merlin.policy.info";

   /**
    * The map key to a {@link Boolean} declaring the debug generation option. If TRUE
    * debug level logging is automatically enabled on all channels (typically only
    * used when debugging an application).
    */
    public static final String MERLIN_POLICY_DEBUG = 
      "merlin.policy.debug";

    private static Resources REZ =
        ResourceManager.getPackageResources( DefaultLoader.class );

    //--------------------------------------------------------------------------
    // state
    //--------------------------------------------------------------------------

    private DefaultKernel m_kernel;

    //--------------------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------------------

   /**
    * Creation of a new loader.  The supplied repository is 
    * used as the default deployment repository.  Depending on command
    * line arguments, the repository established for runtime deployment
    * may be changed.
    *
    * @param repository the bootstrap repository from which merlin 
    *   system jar files have been resolved
    * @param map the set of kernel context parameters 
    */
    public DefaultLoader( final Repository repository, Map map ) 
      throws Exception
    {
        File repo = (File) map.get( MERLIN_REPOSITORY_DIR );
        File library = (File) map.get( MERLIN_LIBRARY_DIR );
        File base = (File) map.get( MERLIN_BASE_DIR );
        URL kernel = (URL) map.get( MERLIN_KERNEL_URL );
        URL targets = (URL) map.get( MERLIN_TARGETS_URL );
        boolean info = getBooleanValue( (Boolean) map.get( MERLIN_POLICY_INFO ), false );
        boolean debug = getBooleanValue( (Boolean) map.get( MERLIN_POLICY_DEBUG ), false );

        try
        {
            DefaultKernelContext context = 
              new DefaultKernelContext( 
                repository, repo, library, base, kernel, new URL[0], 
                targets, true, info, debug );
            m_kernel = new DefaultKernel( context );
        }
        catch( Throwable e )
        {
            final String message =
              "Internal error while attempting to establish the kernel.";
            final String error = ExceptionHelper.packException( message, e, true );
            throw new KernelException( error, e );
        }
    }

   /**
    * Return the kernel.
    * @return the kernel
    */
    public Kernel getKernel()
    {
        return m_kernel;
    }

   /**
    * Return the root containment model.
    * @return the containment model
    */
    public ContainmentModel getContainmentModel()
    {
        return m_kernel.getContainmentModel();
    }

   /**
    * Return the block matching the supplied model.
    * @return the containment block
    */
    public Block getBlock( ContainmentModel model ) throws KernelException
    {
        return m_kernel.getBlock( model );
    }

   /**
    * Return the root block.
    * @return the containment block
    */
    public Block getRootBlock()
    {
        return m_kernel.getRootBlock();
    }

    public void startup() throws Exception
    {
        m_kernel.startup();
    }

   /**
    * Resolve a service relative to the root container. 
    *
    * @param path the absolute or relative path to an appliance
    * @return the resolved object
    * @exception Exception if a resolution error occurs
    */
    public Object resolve( String path ) throws Exception
    {
        if( path == null ) throw new NullPointerException( "path" );

        try
        {
            Appliance appliance = m_kernel.getRootBlock().locate( path );
            if( appliance == null )
            {
                final String problem = 
                  "Unknown appliance: " + path;
                throw new IllegalArgumentException( problem );
            }
            return appliance.resolve();
        }
        catch( Throwable e )
        {
            final String error = 
              "Service resolution error from path: " + path;
            final String msg = ExceptionHelper.packException( error, e, false );
            throw new KernelException( msg, e );
        }
    }

    public void shutdown()
    {
        m_kernel.shutdown();
    }

    private boolean getBooleanValue( Boolean value, boolean fallback )
    {
        if( value == null ) return fallback;
        return value.booleanValue();
    }
}
