/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.i18n;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * A class used to manage resource bundles.
 */
public class ResourceGroup
{
    protected final static Random  RANDOM        = new Random();
    protected final HashMap        m_bundles     = new HashMap();
    protected final Locale         m_locale;

    /**
     * Create a ResourceGroup to manage resource bundles for a particular locale.
     *
     * @param locale the locale
     */
    public ResourceGroup( final Locale locale )
    {
        m_locale = locale;
    }

    public Locale getLocale()
    {
        return m_locale;
    }

    public String format( final String base, final String key, final Object[] args )
    {
        final String pattern = getPattern( base, key );
        final MessageFormat messageFormat = new MessageFormat( pattern );
        messageFormat.setLocale( m_locale );
        return messageFormat.format( args );
    }

    public ResourceBundle getBundle( final String base )
        throws MissingResourceException
    {
        ResourceBundle result = (ResourceBundle) m_bundles.get( base );
        if( null != result ) return result;

        // bundle wasn't cached, so load it, cache it, and return it.
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        result = ResourceBundle.getBundle( base, m_locale, classLoader );

        m_bundles.put( base, result );

        return result;
    }

    public String getPattern( final String base, final String key )
        throws MissingResourceException
    {
        final ResourceBundle bundle = getBundle( base );
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
