/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.logging.logkit;

import java.io.File;
import java.net.URL;
import java.io.IOException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;

import org.apache.avalon.logging.provider.LoggingCriteria;
import org.apache.avalon.logging.provider.LoggingRuntimeException;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.ArtifactHandler;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.main.DefaultBuilder;

import org.apache.avalon.util.criteria.CriteriaException;
import org.apache.avalon.util.criteria.Criteria;
import org.apache.avalon.util.criteria.Parameter;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.excalibur.configuration.ConfigurationUtil;

/**
 * DefaultLoggingCriteria is a class holding the values supplied by a user 
 * for application to a LoggingManager factory.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $
 */
public class DefaultLoggingCriteria extends Criteria 
  implements LoggingCriteria
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

    private static final File BASEDIR = getDefaultBaseDirectory();

    private static final String IMPLEMENTATION_KEY = "avalon.logging.implementation";

    private static final String LOGGING_PROPERTIES = "avalon.logging.properties";

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultLoggingCriteria.class );

   /**
    * The factory parameters template.
    * @return the set of parameters constraining the criteria
    */
    private static Parameter[] buildParameters( InitialContext context ) 
    {
        return new Parameter[] {
            new ConfigurationParameter( 
              LOGGING_CONFIGURATION_KEY, 
              new DefaultConfiguration( "logging" ) ),
            new Parameter( 
              LOGGING_BASEDIR_KEY, 
              File.class, 
              BASEDIR ),
            new LoggerParameter( 
              LOGGING_BOOTSTRAP_KEY, 
              new ConsoleLogger( ConsoleLogger.LEVEL_WARN ) ),
            new Parameter( 
              FACTORY_ARTIFACT_KEY, 
              String.class,
              null )
        };
    }

    private static File getDefaultBaseDirectory()
    {
        String base = System.getProperty( "basedir" );
        if( null != base )
        {
            return getCanonicalForm( new File( base ) );
        }
        return getCanonicalForm( 
          new File( System.getProperty( "user.dir" ) ) );
    }

    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final InitialContext m_context;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of a new default logging criteria.
    * @param context the initial repository context
    */
    public DefaultLoggingCriteria( InitialContext context )
    {
        super( buildParameters( context ) );
        m_context = context;
    }

    //--------------------------------------------------------------
    // LoggingCriteria
    //--------------------------------------------------------------

   /**
    * Set the bootstrap logging channel
    * @param logger the boootstrap logging channel
    */
    public void setBootstrapLogger( Logger logger )
    {
        put( LOGGING_BOOTSTRAP_KEY, logger );
    }

   /**
    * Set the base directory.
    * @param dir the base directory
    */
    public void setBaseDirectory( File dir )
    {
        put( LOGGING_BASEDIR_KEY, dir );
    }

   /**
    * Set the logging system configuration
    * @param config the configuration
    */
    public void setConfiguration( Configuration config )
    {
        put( LOGGING_CONFIGURATION_KEY, config );
    }

   /**
    * Set the artifact referencing the implementation factory.
    * @param artifact the implementation artifact
    */
    public void setFactoryArtifact( Artifact artifact )
    {
        put( FACTORY_ARTIFACT_KEY, artifact );
    }

   /**
    * Get the bootstrap logging channel
    * @return the boootstrap logging channel
    */
    public Logger getBootstrapLogger()
    {
        return (Logger) get( LOGGING_BOOTSTRAP_KEY );
    }

   /**
    * Return the base directory for logging resources.
    * @return the base directory
    */
    public File getBaseDirectory()
    {
        return (File) get( LOGGING_BASEDIR_KEY );
    }

   /**
    * Return the logging system configuration
    * @return the configuration
    */
    public Configuration getConfiguration()
    {
        return (Configuration) get( LOGGING_CONFIGURATION_KEY );
    }

   /**
    * Return the artifact reference to the logging implementation factory .
    * @return the logging implementation factory classname
    * @exception IllegalStateException if the url is not an artifact url
    */
    public Artifact getFactoryArtifact() throws IOException
    {
        String value = (String) get( FACTORY_ARTIFACT_KEY );
        if( null == value )
        {
            return getDefaultImplementationArtifact( m_context );
        }
        else
        {
            return Artifact.createArtifact( value );
        }
    }

    private static Artifact getDefaultImplementationArtifact( 
      InitialContext context )
    {
        try
        {
            return DefaultBuilder.createImplementationArtifact( 
              DefaultLoggingCriteria.class.getClassLoader(), 
              context.getInitialCacheDirectory(),
              BASEDIR, 
              LOGGING_PROPERTIES, 
              IMPLEMENTATION_KEY );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "criteria.artifact.default.error", 
                BASEDIR, LOGGING_PROPERTIES, IMPLEMENTATION_KEY );
            throw new LoggingRuntimeException( error, e );
        }
    }

    private static File getCanonicalForm( File file )
    {
        try
        {
            return file.getCanonicalFile();
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "criteria.artifact.cononical.error", 
                file.toString() );
            throw new LoggingRuntimeException( error, e );
        }
    }

}
