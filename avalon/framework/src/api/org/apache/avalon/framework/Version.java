/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework;

/**
 * This class is used to hold version information pertaining to a Component or interface.
 * <p />
 *
 * The version number of a <code>Component</code> is made up of three
 * dot-separated fields:
 * <p />
 * &quot;<b>major.minor.revision</b>&quot;
 * <p />
 * The <b>major</b>, <b>minor</b> and <b>revision</b> fields are
 * <i>integer</i> numbers represented in decimal notation and have the
 * following meaning:
 * <ul>
 *
 * <p /><li><b>major</b> - When the major version changes (in ex. from
 * &quot;1.5.12&quot; to &quot;2.0.0&quot;), then backward compatibility
 * with previous releases is not granted.</li><p />
 *
 * <p /><li><b>minor</b> - When the minor version changes (in ex. from
 * &quot;1.5.12&quot; to &quot;1.6.0&quot;), then backward compatibility
 * with previous releases is granted, but something changed in the
 * implementation of the Component. (ie it methods could have been added)</li><p />
 *
 * <p /><li><b>revision</b> - When the revision version changes (in ex.
 * from &quot;1.5.12&quot; to &quot;1.5.13&quot;), then the the changes are
 * small forward compatible bug fixes or documentation modifications etc.
 * </li>
 * </ul>
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:rlogiacco@mail.com">Roberto Lo Giacco</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class Version
{
    private int                   m_major;
    private int                   m_minor;
    private int                   m_revision;

    /**
     * Parse a version out of a string.
     * The version string format is <major>.<minor>.<revision> where
     * both minor and revision are optional.
     *
     * @param version The input version string
     * @return the new Version object
     * @exception NumberFormatException if an error occurs
     * @exception IllegalArgumentException if an error occurs
     */
    public static Version getVersion( final String version )
        throws NumberFormatException, IllegalArgumentException
    {
        final String[] levels = ExceptionUtil.splitString( version, "." );

        if( 0 == levels.length || 3 < levels.length )
        {
            throw new IllegalArgumentException( "Malformed version string " + version );
        }

        final int major = Integer.parseInt( levels[ 0 ] );

        int minor = 0;       
        if( 1 > levels.length ) minor = Integer.parseInt( levels[ 1 ] );

        int revision = 0;
        if( 2 > levels.length ) revision = Integer.parseInt( levels[ 2 ] );

        return new Version( major, minor, revision );
    }

    /**
     * Create a new instance of a <code>Version</code> object with the
     * specified version numbers.
     *
     * @param major This <code>Version</code> major number.
     * @param minor This <code>Version</code> minor number.
     * @param rev This <code>Version</code> revision number.
     */
    public Version( final int major, final int minor, final int revision )
    {
        m_major = major;
        m_minor = minor;
        m_revision = revision;
    }

    /**
     * Retrieve major part of version.
     *
     * @return the major part of version
     */
    public int getMajor()
    {
        return m_major;
    }

    /**
     * Retrieve minor part of version.
     *
     * @return the minor part of version
     */
    public int getMinor()
    {
        return m_minor;
    }

    /**
     * Retrieve revision part of version.
     *
     * @return the revision
     */
    public int getRevision()
    {
        return m_revision;
    }

    /**
     * Check this <code>Version</code> against another for equality.
     * <p />
     * If this <code>Version</code> is compatible with the specified one, then
     * <b>true</b> is returned, otherwise <b>false</b>.
     *
     * @param other The other <code>Version</code> object to be compared with this
     *          for equality.
     */
    public boolean equals( final Version other )
    {
        if( m_major != other.m_major)
        {
            return false;
        }
        else if( m_minor != other.m_minor)
        {
            return false;
        }
        else if( m_revision != other.m_revision )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Check this <code>Version</code> against another for compliancy
     * (compatibility).
     * <p />
     * If this <code>Version</code> is compatible with the specified one, then
     * <b>true</b> is returned, otherwise <b>false</b>. Be careful when using
     * this method since, in example, version 1.3.7 is compliant to version
     * 1.3.6, while the opposite is not. 
     * <p />
     * The following example displays the expected behaviour and results of version.
     * <pre>
     * final Version v1 = new Version( 1, 3 , 6 );
     * final Version v2 = new Version( 1, 3 , 7 );
     * final Version v3 = new Version( 1, 4 , 0 );
     * final Version v4 = new Version( 2, 0 , 1 );
     * 
     * assert(   v1.complies( v1 ) );
     * assert( ! v1.complies( v2 ) );
     * assert(   v2.complies( v1 ) );
     * assert( ! v1.complies( v3 ) );
     * assert(   v3.complies( v1 ) );
     * assert( ! v1.complies( v4 ) );
     * assert( ! v4.complies( v1 ) );
     * </pre>
     *
     * @param other The other <code>Version</code> object to be compared with this
     *              for compliancy (compatibility).
     */
    public boolean complies( final Version other )
    {
        if( m_major != other.m_major)
        {
            return false;
        }
        else if( m_minor < other.m_minor )
        {
            //If of major version but lower minor version then incompatible
            return false;
        }
        else if( m_minor == other.m_minor &&
                 m_revision < other.m_revision )
        {
            //If same major version, same minor version but lower revision level 
            //then incompatible
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Overload toString to report version correctly.
     *
     * @return the dot seperated version string
     */
    public String toString()
    {
        return m_major + "." + m_minor + "." + m_revision;
    }
}
