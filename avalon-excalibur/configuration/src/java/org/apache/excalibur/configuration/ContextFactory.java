/* 
 * Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.excalibur.configuration;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;

/**
 * ContextFactory is a utility class that provides support for the creation
 * context instances based on a XML context desciption.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: ContextFactory.java,v 1.10 2004/02/28 11:47:32 cziegeler Exp $
 */
public class ContextFactory
{

    //==========================================================
    // context utilities
    //==========================================================

    /**
     * Create context-attributes from entrys within &lt;context/&gt;-tag in config
     * @param config the context configuration
     * @return Context a context instance
     * @exception ConfigurationException if a context related error occurs
     */
    public static Context createContextFromConfiguration( Configuration config )
        throws ConfigurationException
    {
        return createContextFromConfiguration( null, config );
    }

    /**
     * Create context-attributes from entrys within &lt;context/&gt;-tag in config
     * @param parent the parent context
     * @param config the configuration element describing the context parameters
     * @return Context a context instance
     * @exception ConfigurationException if a context related error occurs
     */
    public static Context createContextFromConfiguration(
        Context parent, Configuration config )
        throws ConfigurationException
    {
        return createContextFromConfiguration( parent, config, null );
    }

    /**
     * Create context-attributes from entrys within &lt;context/&gt;-tag in config
     * @param parent the parent context
     * @param config the configuration element describing the context parameters
     * @param log a logging channel
     * @return Context a context instance
     * @exception ConfigurationException if a context related error occurs
     */
    public static Context createContextFromConfiguration(
        Context parent, Configuration config, Logger log )
        throws ConfigurationException
    {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String contextClassName = config.getAttribute(
            "class",
            "org.apache.avalon.framework.context.DefaultContext" );

        Class contextClass = null;

        try
        {
            contextClass = loader.loadClass( contextClassName );
        }
        catch( ClassNotFoundException cnfe )
        {
            throw new ConfigurationException(
                "Could not find context class " + contextClassName, cnfe );
        }

        Map map = new Hashtable();
        Context context = null;
        try
        {
            Constructor constructor = contextClass.getConstructor(
                new Class[]{Map.class, Context.class} );
            context = (Context)constructor.newInstance( new Object[]{map, parent} );
        }
        catch( Throwable e )
        {
            throw new ConfigurationException(
                "Unexpected exception while creating custom context form "
                + contextClassName, e );
        }

        final Configuration[] entrys = config.getChildren( "entry" );

        for( int i = 0; i < entrys.length; i++ )
        {
            final String className = entrys[ i ].getAttribute(
                "type", "java.lang.String" );
            final String paramName = entrys[ i ].getAttribute(
                "name", null );

            if( paramName == null )
            {
                throw new ConfigurationException(
                    "missing name for context-entry" );
            }

            try
            {
                Class[] params;
                Object[] values;
                Configuration entry = entrys[ i ];

                if( entry.getAttribute( "value", null ) != null )
                {
                    // Single argument String-constructor
                    params = new Class[ 1 ];
                    params[ 0 ] = Class.forName( "java.lang.String" );
                    Class[] consObjects = {Class.forName( "java.lang.String" )};
                    Constructor cons = params[ 0 ].getConstructor( consObjects );
                    values = new Object[ 1 ];
                    Object[] consValues = {
                        getContextValue( map, entry.getAttribute( "value" ) )
                    };
                    values[ 0 ] = cons.newInstance( consValues );

                    if( log != null )
                    {
                        log.debug( "add context-attr '" + paramName
                                   + "' class '" + className
                                   + "' with value '" + consValues[ 0 ] + "'" );
                    }
                }
                else
                {
                    // Multiple argument constructor
                    Configuration[] entryChilds = entry.getChildren( "parameter" );

                    params = new Class[ entryChilds.length ];
                    values = new Object[ entryChilds.length ];

                    if( log != null )
                    {
                        log.debug( "add context-attr '" + paramName
                                   + "' class '" + className + "' with "
                                   + entryChilds.length + " values" );
                    }

                    for( int p = 0; p < entryChilds.length; p++ )
                    {
                        String paramClassName = entryChilds[ p ].getAttribute(
                            "type", "java.lang.String" );
                        String paramValue = entryChilds[ p ].getAttribute( "value", null );

                        if( paramValue == null )
                        {
                            if( log != null )
                            {
                                log.debug( "value" + ( p + 1 ) + ": class '"
                                           + paramClassName + "' no value" );
                            }
                        }
                        else
                        {
                            paramValue = getContextValue( map, paramValue );
                            if( log != null )
                            {
                                log.debug( "value" + ( p + 1 ) + ": class '"
                                           + paramClassName + "' value '" + paramValue + "'" );
                            }
                        }

                        try
                        {
                            params[ p ] = loader.loadClass( paramClassName );

                            if( paramValue == null )
                            {
                                values[ p ] = params[ p ].newInstance();
                            }
                            else
                            {
                                Class[] consObjects = {Class.forName( "java.lang.String" )};
                                Constructor cons = params[ p ].getConstructor( consObjects );
                                Object[] consValues = {paramValue};
                                values[ p ] = cons.newInstance( consValues );
                            }
                        }
                        catch( ClassNotFoundException e )
                        {
                            // Class not found
                            // -> perhaps a primitve class?
                            if( paramClassName.equals( "int" ) )
                            {
                                params[ p ] = int.class;
                                values[ p ] = new Integer( paramValue );
                            }
                            else if( paramClassName.equals( "short" ) )
                            {
                                params[ p ] = short.class;
                                values[ p ] = new Short( paramValue );
                            }
                            else if( paramClassName.equals( "long" ) )
                            {
                                params[ p ] = long.class;
                                values[ p ] = new Long( paramValue );
                            }
                            else if( paramClassName.equals( "byte" ) )
                            {
                                params[ p ] = byte.class;
                                values[ p ] = new Byte( paramValue );
                            }
                            else if( paramClassName.equals( "double" ) )
                            {
                                params[ p ] = double.class;
                                values[ p ] = new Double( paramValue );
                            }
                            else if( paramClassName.equals( "float" ) )
                            {
                                params[ p ] = float.class;
                                values[ p ] = new Float( paramValue );
                            }
                            else if( paramClassName.equals( "char" ) )
                            {
                                params[ p ] = char.class;
                                values[ p ] = new Character( paramValue.charAt( 0 ) );
                            }
                            else if( paramClassName.equals( "boolean" ) )
                            {
                                params[ p ] = boolean.class;
                                values[ p ] = new Boolean( paramValue );
                            }
                            else
                            {
                                throw new ConfigurationException(
                                    "incorrect type '" + paramClassName
                                    + "' for context-attribute '" + paramName + "'", e );
                            }
                        }
                    }
                }

                Class paramClass;
                try
                {
                    paramClass = loader.loadClass( className );
                }
                catch( final ClassNotFoundException e )
                {
                    throw new ConfigurationException(
                        "incorrect type '" + className
                        + "' for context-attribute '" + paramName + "'",
                        e );
                }

                Object paramInstance;

                if( params.length > 0 )
                {
                    // using param contructor
                    Constructor cons = paramClass.getConstructor( params );
                    paramInstance = cons.newInstance( values );
                }
                else
                {
                    // using default constructor
                    paramInstance = paramClass.newInstance();
                }

                map.put( paramName, paramInstance );
            }
            catch( ConfigurationException e )
            {
                throw e;
            }
            catch( Exception e )
            {
                throw new ConfigurationException(
                    "Error add context-attribute '" + paramName
                    + "' from Configuration", e );
            }
        }
        return context;
    }

    /**
     * Resolving an attribute value by replacing ${context-param} with
     * the corresponding param out of current context.
     * @param map a map
     * @param rawValue a raw value
     * @return String the context attribute value
     * @exception ConfigurationException if context-param does not exists
     */
    private static String getContextValue( Map map, String rawValue )
        throws ConfigurationException
    {
        StringBuffer result = new StringBuffer( "" );
        int i = 0;
        int j = -1;
        while( ( j = rawValue.indexOf( "${", i ) ) > -1 )
        {
            if( i < j )
            {
                result.append( rawValue.substring( i, j ) );
            }
            int k = rawValue.indexOf( '}', j );
            final String ctxName = rawValue.substring( j + 2, k );
            final Object ctx = map.get( ctxName );
            if( ctx == null )
            {
                throw new ConfigurationException(
                    "missing entry '" + ctxName + "' in Context" );
            }
            result.append( ctx.toString() );
            i = k + 1;
        }
        if( i < rawValue.length() )
        {
            result.append( rawValue.substring( i, rawValue.length() ) );
        }
        return result.toString();
    }

}



