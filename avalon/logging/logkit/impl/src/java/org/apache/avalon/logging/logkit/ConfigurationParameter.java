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
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.avalon.util.criteria.Parameter;
import org.apache.avalon.util.criteria.CriteriaException;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * A parameter descriptor that supports transformation of a 
 * a string url or file to a configuration instance.
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $
 */
public class ConfigurationParameter extends Parameter
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

    private static final DefaultConfigurationBuilder BUILDER =
      new DefaultConfigurationBuilder();

    private static final Resources REZ =
      ResourceManager.getPackageResources( ConfigurationParameter.class );

    //--------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------

   /**
    * Transform a string to a string array.
    * @param key the parameter key
    * @param defaults the default string array
    */
    public ConfigurationParameter( 
      final String key, final Configuration defaults ) 
    {
        super( key, Configuration.class, defaults );
    }

   /**
    * Resolve a supplied string to a configuration
    * @param value the value to resolve
    * @exception CriteriaException if an error occurs
    */
    public Object resolve( Object value ) 
      throws CriteriaException
    {
        if( value == null ) return null;
        if( value instanceof Configuration )
        {
            return value;
        }
        if( value instanceof String )
        {
            try
            {
                return resolve( super.resolve( File.class, value ) );
            }
            catch( CriteriaException ce )
            {
                return resolve( super.resolve( URL.class, value ) );
            }
        }
        else if( value instanceof File )
        {
            File file = (File) value;
            if( !file.exists() )
            {
                final String error = 
                  REZ.getString( 
                    "parameter.configuration.fnf.error", 
                    file.toString() );
                throw new CriteriaException( error );
            }

            try
            {
                String path = file.toURL().toString();
                return BUILDER.build( path );
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( 
                    "parameter.configuration.file.error", 
                    file.toString() );
                throw new CriteriaException( error );
            }
        }
        else if( value instanceof URL )
        {
            try
            {
                String path = value.toString();
                return BUILDER.build( path );
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( 
                    "parameter.configuration.url.error", 
                    value.toString() );
                throw new CriteriaException( error );
            }
        }
        else
        {
            final String error = 
              REZ.getString( 
                "parameter.unknown", 
                value.getClass().getName(), Configuration.class.getName() );
            throw new CriteriaException( error );
        }
    }
}
