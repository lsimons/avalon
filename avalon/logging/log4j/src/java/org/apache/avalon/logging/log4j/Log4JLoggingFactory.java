 
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

package org.apache.avalon.logging.log4j;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.configuration.Configuration;

import org.apache.avalon.logging.provider.LoggingCriteria;
import org.apache.avalon.logging.provider.LoggingFactory;
import org.apache.avalon.logging.provider.LoggingManager;
import org.apache.avalon.logging.provider.LoggingException;

import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Factory;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * A Log4J factory.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $
 */
public class Log4JLoggingFactory
    implements LoggingFactory
{
    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultLoggingCriteria.class );
    
    private final ClassLoader    m_Classloader;
    private final InitialContext m_Context;
    private       File           m_BaseDirectory;
   
   /**
    * Creation of a new default factory.
    * @param context the repository inital context
    * @param classloader the factory classloader
    */
    public Log4JLoggingFactory( InitialContext context, ClassLoader classloader )
    {
        m_Context = context;
        m_Classloader = classloader;
    }

    //--------------------------------------------------------------------------
    // LoggingFactory
    //--------------------------------------------------------------------------

   /**
    * Return of map containing the default parameters.
    *
    * @return the default parameters 
    */
    public LoggingCriteria createDefaultLoggingCriteria()
    {
        return new DefaultLoggingCriteria( m_Context );
    }

   /**
    * Create a new LoggingManager using the supplied logging criteria.
    *
    * @param criteria the logging system factory criteria
    * @exception LoggingException is a logging system creation error occurs
    */
    public LoggingManager createLoggingManager( LoggingCriteria criteria ) 
      throws LoggingException
    {
        try
        {
            return (LoggingManager) create( criteria );
        }
        catch( Throwable e )
        {
            final String error = 
              "Cannot build logging manager.";
            throw new LoggingException( error, e );
        }
    }

    //--------------------------------------------------------------------------
    // Factory
    //--------------------------------------------------------------------------

   /**
    * Return a new instance of default criteria for the factory.
    * @return a new criteria instance
    */
    public Map createDefaultCriteria()
    {
        return createDefaultLoggingCriteria();
    }

   /**
    * Create a new instance of an application.
    * @return the application instance
    */
    public Object create() throws Exception
    {
        return new LoggingManagerImpl();
    }

   /**
    * Create a new instance of an application.
    * @param criteriaMap the creation criteria
    * @return the application instance
    */
    public Object create( Map criteriaMap ) 
        throws Exception
    {
        if( null == criteriaMap )
        {
            throw new NullPointerException( "criteriaMap" );
        }

        final LoggingCriteria criteria = getLoggingCriteria( criteriaMap );
        m_BaseDirectory = criteria.getBaseDirectory();

        final Configuration config = criteria.getConfiguration();
        Configuration srcConf = config.getChild( "src" );
        Configuration updateConf = config.getChild( "update" );
        String src = srcConf.getValue();
        src = resolveSource( src );
        System.out.println( src );
        long updateInterval = updateConf.getValueAsLong( 0 );
        if( updateInterval > 0 )
        {
            configureWithWatch( src, updateInterval );
        }
        else
        {
            configureWithOutWatch( src );
        }
        return new LoggingManagerImpl();
    }
    
    private void configureWithWatch( String src, long interval )
    {
        if( src.endsWith( ".xml" ) )
        {
            DOMConfigurator.configureAndWatch( src, interval );
        }
        else
        {
            PropertyConfigurator.configureAndWatch( src, interval );
        }
    }
    
    private void configureWithOutWatch( String src )
    {
        if( src.endsWith( ".xml" ) )
        {
            DOMConfigurator.configure( src );
        }
        else
        {
            PropertyConfigurator.configure( src );
        }
    }

    private String resolveSource( String src )
    {
        try
        {
            URL url = new URL( src );
            if( url.getProtocol().equals( "file" ) )
                return resolveToBaseDir( src );
            return src;
        } catch( MalformedURLException e )
        {
            return resolveToBaseDir( src );
        }
    }
    
    private String resolveToBaseDir( String src )
    {
        if( src.startsWith( "/" ) )
            return src;
        File f = new File( m_BaseDirectory, src );
        return f.getAbsolutePath();
    }
    
    private LoggingCriteria getLoggingCriteria( Map map )
    {
        if( map instanceof LoggingCriteria )
        {
            return (LoggingCriteria) map;
        }
        else
        {
            final String error = 
              REZ.getString( 
                "factory.bad-criteria", 
                map.getClass().getName() );
            throw new IllegalArgumentException( error );
        }
    }

}
