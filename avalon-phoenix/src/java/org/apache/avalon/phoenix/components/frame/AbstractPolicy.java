/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.frame;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.PropertyPermission;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.log.Logger;

/**
 * Abstract policy extended in avalon.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractPolicy
    extends Policy
    implements Component, Loggable
{
    private final ArrayList        m_entries  = new ArrayList();
    private Logger                 m_logger;

    /**
     * Internal Policy Entry holder class.
     */
    private static class PolicyEntry
    {
        CodeSource   m_codeSource;
        Permissions  m_permissions;
    }

    public void setLogger( final Logger logger )
    {
        m_logger = logger;
    }

    /**
     * Overide so we can have a per-application security policy with
     * no side-effects to other applications.
     *
     * @param codeSource the codeSource to get permissions for
     * @return the PermissionCollection
     */
    public PermissionCollection getPermissions( CodeSource codeSource )
    {
        codeSource = normalize( codeSource );

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "getPermissions(" + codeSource.getLocation() + ");" );
        }

        final Permissions permissions = new Permissions();
        final int size = m_entries.size();

        for( int i = 0; i < size; i++ )
        {
            final PolicyEntry entry = (PolicyEntry)m_entries.get( i );
            if( entry.m_codeSource.implies( codeSource ) )
            {
                copyPermissions( permissions, entry.m_permissions );
            }
        }

        return permissions;
    }

    /**
     * Refresh policy. Ignored in this implementation.
     */
    public void refresh()
    {
    }

    /**
     * Create a permission set for a codeBase.
     * These are read-write permissions and can be written till until the
     * time in which they are applied to code.
     *
     * @param location the location of codes to apply permission set to.
     * @param signers a comma seperated string of thos who signed codebase
     * @return the new permission set
     * @exception MalformedURLException if location string is malformed
     */
    protected Permissions createPermissionSetFor( final String location,
                                                  final Certificate[] signers )
        throws MalformedURLException
    {
        return createPermissionSetFor( new URL( location ), signers );
    }

    protected Permissions createPermissionSetFor( final URL url,
                                                  final Certificate[] signers )
    {
        getLogger().debug( "createPermissionSetFor(" + url + ");" );

        final PolicyEntry entry = new PolicyEntry();
        entry.m_codeSource = new CodeSource( url, signers );
        entry.m_codeSource = normalize( entry.m_codeSource );
        entry.m_permissions = new Permissions();

        m_entries.add( entry );
        return entry.m_permissions;
    }

    protected final Logger getLogger()
    {
        return m_logger;
    }

    /**
     * Normalizing CodeSource involves removing relative addressing
     * (like .. and .) for file urls.
     *
     * @param codeSource the codeSource to be normalized
     * @return the normalized codeSource
     */
    private CodeSource normalize( final CodeSource codeSource )
    {
        final URL initialLocation = codeSource.getLocation();

        // This is a bit of a hack.  I don't know why CodeSource should behave like this
        // Fear not, this only seems to be a problem for home grown classloaders.
        // - Paul Hammant, Nov 2000
        if( null == initialLocation ) return codeSource;

        String location = null;

        if( !initialLocation.getProtocol().equalsIgnoreCase( "file" ) )
        {
            location = initialLocation.getFile();
            location = FileUtil.normalize( location );
        }
        else
        {
            final File file = new File( initialLocation.getFile() );
            location = file.getAbsoluteFile().toString().replace( File.separatorChar, '/' );
            location =  FileUtil.normalize( location );
        }

        URL finalLocation = null;

        try
        {
            finalLocation = new URL( initialLocation.getProtocol(),
                                     initialLocation.getHost(),
                                     initialLocation.getPort(),
                                     location );
        }
        catch( final MalformedURLException mue )
        {
            getLogger().warn( "Error building codeBase", mue );
        }

        return new CodeSource( finalLocation, codeSource.getCertificates() );
    }

    /**
     * Utility method to cpoy permissions from specified source to specified destination.
     *
     * @param destination the destination of permissions
     * @param source the source of permissions
     */
    private void copyPermissions( final Permissions destination, 
                                  final Permissions source )
    {
        final Enumeration enum = source.elements();
        while( enum.hasMoreElements() )
        {
            destination.add( (Permission)enum.nextElement() );
        }
    }
}
