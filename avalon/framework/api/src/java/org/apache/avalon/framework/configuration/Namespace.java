/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.configuration;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The namespace object is used in configuration schemas where namespace is
 * important.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class Namespace 
    implements Serializable
{
    private static final    boolean VALIDATE_PREFIX = true;
    private static final    boolean IGNORE_PREFIX   = false;
    private static volatile boolean c_policy        = VALIDATE_PREFIX;

    private final        String  m_prefix;
    private final        String  m_uri;
    private final        boolean m_validatePrefix;

    /**
     * Hide constructor so that the default factory methods must be used
     */
    private Namespace()
    {
        this("", "", true);
    }

    /**
     * Create a Namespace object with a prefix and uri.
     */
    private Namespace( final String prefix, final String uri, final boolean validatePrefix )
    {
        m_prefix = prefix;
        m_uri = uri;
        m_validatePrefix = validatePrefix;
    }

    /**
     * Get the prefix portion of the namespace.  Please note that a namespace
     * is considered unique if the prefix is different even if the uri is the
     * same.
     *
     * @return prefix as string
     */
    public final String getPrefix()
    {
        return m_prefix;
    }

    /**
     * Get the URI portion of the namespace.
     *
     * @return URI as string
     */
    public final String getURI()
    {
        return m_uri;
    }

    /**
     * Checks to see if the Namespace object is the same as another one or as
     * a string representatio of one.
     */
    public final boolean equals( final Object check )
    {
        boolean isEqual = false;

        if( check instanceof Namespace )
        {
            final Namespace other = (Namespace)check;
            if( m_validatePrefix )
            {
                isEqual = getPrefix().equals( other.getPrefix() );

                if( isEqual )
                {
                    isEqual = getURI().equals( other.getURI() );
                }
            }
            else
            {
                isEqual = getURI().equals( other.getURI() );
            }
        }
        else if ( check instanceof String )
        {
            isEqual = toString().equals( check );
        }

        return isEqual;
    }

    /**
     * Convert a Namespace into a string
     */
    public final String toString()
    {
        final StringBuffer xmlns = new StringBuffer("xmlns");

        if( !( "".equals( getPrefix() ) ) )
        {
            xmlns.append( ":" ).append( getPrefix() );
        }

        xmlns.append( "=\"" ).append( getURI() ).append( "\"" );

        return xmlns.toString();
    }

    /**
     * Parse an xmlns declaration to find the prefix
     */
    private static final String prefix(final String xmlns)
    {
        if (null == xmlns)
        {
            return "";
        }

        if (!(xmlns.startsWith("xmlns")))
        {
            throw new IllegalStateException("The namespace is not in the proper format");
        }

        String prefix = "";
        final String sub = xmlns.substring("xmlns".length());
        final int uristart = sub.indexOf("=\"");

        if (sub.charAt(0) == ':' )
        {
            if (uristart > 1)
            {
                prefix = sub.substring(1, uristart);
            }
            else
            {
                throw new IllegalStateException("The namespace is not in the proper format");
            }
        }

        return prefix;
    }

    /**
     * Parse an xmlns declaration to find the prefix
     */
    private static final String uri(final String xmlns)
    {
        if (null == xmlns)
        {
            return "";
        }

        return xmlns.substring(xmlns.indexOf("\""), xmlns.lastIndexOf("\""));
    }

    /**
     * Get an instance of the Namespace for the String representation.
     *
     * @param  xmlns  A string representation of the namespace (xmlns="")
     *
     * @return a Namespace object
     */
    public static final Namespace getNamespace( final String xmlns )
    {
        return getNamespace( Namespace.prefix(xmlns), Namespace.uri(xmlns) );
    }

    /**
     * Get an instance of the Namespace with separate prefix and uri strings
     *
     * @param  prefix  The prefix portion of the namespace
     * @param  uri     The uri portion of the namespace
     *
     * @return a Namespace object
     */
    public static final synchronized Namespace getNamespace( final String prefix, final String uri )
    {
        String pre = prefix;
        String loc = uri;

        if ( null == prefix )
        {
            pre = "";
        }

        if ( null == uri )
        {
            loc = "";
        }

        return new Namespace( pre, loc, true );
    }

    public static final synchronized void setPolicy( final boolean prefixValidating )
    {
        c_policy = prefixValidating;
    }

    public static final synchronized boolean getPolicy()
    {
        return c_policy;
    }
}
