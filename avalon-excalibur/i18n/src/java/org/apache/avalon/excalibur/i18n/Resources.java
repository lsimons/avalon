/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * A class to simplify extracting localized strings, icons 
 * and other common resource from a ResourceBundle.
 *
 * Reworked to mirror behaviour of StringManager from Tomcat (format() to getString()).
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Resources
{
    private final static Random  RANDOM        = new Random();

    ///Local of Resources
    private final Locale    m_locale;

    ///Resource bundle referenced by manager
    private ResourceBundle  m_bundle;

    ///Base name of resource bundle
    private String          m_baseName;

    /**
     * Constructor that builds a manager in default locale.
     *
     * @param baseName the base name of ResourceBundle
     */
    public Resources( final String baseName )
    {
        this( baseName, Locale.getDefault() );
    }

    /**
     * Constructor that builds a manager in specified locale.
     *
     * @param baseName the base name of ResourceBundle
     * @param locale the Locale for resource bundle
     */
    public Resources( final String baseName, final Locale locale )
    {
        m_baseName = baseName;
        m_locale = locale;
    }

    /**
     * Retrieve a string from resource bundle and format it with specified args.
     *
     * @param key the key for resource
     * @param arg1 an arg
     * @return the formatted string
     */
    public String getString( final String key, final Object arg1 )
    {
        final Object[] args = new Object[] { arg1 };
        return format( key, args );
    }

    /**
     * Retrieve a string from resource bundle and format it with specified args.
     *
     * @param key the key for resource
     * @param arg1 an arg
     * @param arg2 an arg
     * @return the formatted string
     */
    public String getString( final String key, final Object arg1, final Object arg2 )
    {
        final Object[] args = new Object[] { arg1, arg2 };
        return format( key, args );
    }

    /**
     * Retrieve a string from resource bundle and format it with specified args.
     *
     * @param key the key for resource
     * @param arg1 an arg
     * @param arg2 an arg
     * @param arg3 an arg
     * @return the formatted string
     */
    public String getString( final String key, 
                             final Object arg1, 
                             final Object arg2, 
                             final Object arg3 )
    {
        final Object[] args = new Object[] { arg1, arg2, arg3 };
        return format( key, args );
    }

    /**
     * Retrieve a string from resource bundle and format it with specified args.
     *
     * @param key the key for resource
     * @param arg1 an arg
     * @param arg2 an arg
     * @param arg3 an arg
     * @param arg4 an arg
     * @return the formatted string
     */
    public String getString( final String key, 
                             final Object arg1, 
                             final Object arg2, 
                             final Object arg3,
                             final Object arg4 )
    {
        final Object[] args = new Object[] { arg1, arg2, arg3, arg4 };
        return format( key, args );
    }

    /**
     * Retrieve a string from resource bundle and format it with specified args.
     *
     * @param key the key for resource
     * @param arg1 an arg
     * @param arg2 an arg
     * @param arg3 an arg
     * @param arg4 an arg
     * @param arg5 an arg
     * @return the formatted string
     */
    public String getString( final String key,
                             final Object arg1,
                             final Object arg2,
                             final Object arg3,
                             final Object arg4,
                             final Object arg5 )
    {
        final Object[] args = new Object[] { arg1, arg2, arg3, arg4, arg5 };
        return format( key, args );
    }

    /**
     * Retrieve a string from resource bundle and format it with specified args.
     *
     * @param key the key for resource
     * @param args an array of args
     * @return the formatted string
     */
    public String format( final String key, final Object[] args )
    {
        try
        {
            final String pattern = getPatternString( key );
            return MessageFormat.format( pattern, args );
        }
        catch( final MissingResourceException mre )
        {
            return
                "Unable to locate resource '" + m_baseName +
                "' with key '" + key + "' due to: " + mre;
        }
    }

    /**
     * Retrieve a raw string from bundle.
     *
     * @param key the key of resource
     * @return the resource string
     */
    public String getString( final String key )
    {
        try
        {
            final ResourceBundle bundle = getBundle();
            return bundle.getString( key );
        }
        catch( final MissingResourceException mre )
        {
            return
                "Unable to locate resource '" + m_baseName +
                "' with key '" + key + "' due to: " + mre;
        }
    }

    /**
     * Retrieve underlying ResourceBundle.
     * If bundle has not been loaded it will be loaded by this method.
     * Access is given in case other resources need to be extracted
     * that this Manager does not provide simplified access to.
     *
     * @return the ResourceBundle
     * @exception MissingResourceException if an error occurs
     */
    public final ResourceBundle getBundle()
        throws MissingResourceException
    {
        if( null == m_bundle )
        {
            // bundle wasn't cached, so load it, cache it, and return it.
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if( null != classLoader )
            {
                m_bundle = ResourceBundle.getBundle( m_baseName, m_locale, classLoader );
            }
            else
            {
                m_bundle = ResourceBundle.getBundle( m_baseName, m_locale );
            }
        }
        return m_bundle;
    }

    /**
     * Utility method to retrieve a string from ResourceBundle.
     * If the key is a single string then that will be returned.
     * If key refers to string array then a random string will be chosen.
     * Other types cause an exception.
     *
     * @param key the key to resource
     * @return the string resource
     * @exception MissingResourceException if an error occurs
     */
    private String getPatternString( final String key )
        throws MissingResourceException
    {
        final ResourceBundle bundle = getBundle();
        final Object object = bundle.getObject( key );

        // is the resource a single string
        if( object instanceof String )
        {
            return (String)object;
        }
        else if( object instanceof String[] )
        {
            //if string array then randomly pick one
            final String[] strings = (String[])object;
            return strings[ RANDOM.nextInt( strings.length ) ];
        }
        else
        {
            throw new MissingResourceException( "Unable to find resource of appropriate type.",
                                                "java.lang.String",
                                                key );
        }
    }
}
