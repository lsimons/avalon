/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.cpbuilder.verifier;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.components.cpbuilder.metadata.ClassLoaderDef;
import org.apache.avalon.phoenix.components.cpbuilder.metadata.ClassLoaderSetDef;
import org.apache.avalon.phoenix.components.cpbuilder.metadata.EntryDef;
import org.apache.avalon.phoenix.components.cpbuilder.metadata.JoinDef;

/**
 * Verify ClassLoader set is valid. Validity is defined as
 * <ul>
 *   <li>With exception of predefined names, all ClassLoader
 *       names should be defined starting with letters or '_'
 *       and then continuing with Alpha-Numeric characters,
 *       '-', '.' or '_'.</li>
 *   <li>No ClassLoader can have a parent ClassLoader that is
 *       not predefined or not defined in ClassLoaderSet.</li>
 *   <li>No "join" ClassLoader can link against a non-existent
 *       ClassLoader.</li>
 *   <li>No "join" ClassLoader can join multiple instances
 *       of same ClassLoader.</li>
 *   <li>No ClassLoader can have multiple entrys that point
 *       to the same location.</li>
 *   <li>No ClassLoader (either predefined, join or regular)
 *       can have the same name.</li>
 *   <li>The default ClassLoader must exist.</li>
 *   <li>There must be no circular dependencies between join
 *       classloaders.</li>
 * </ul>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.5 $ $Date: 2002/09/01 04:36:09 $
 */
public class ClassLoaderVerifier
    extends AbstractLogEnabled
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( ClassLoaderVerifier.class );

    public void verifyClassLoaderSet( final ClassLoaderSetDef set )
        throws Exception
    {
        String message = null;

        message = REZ.getString( "valid-names.notice" );
        getLogger().info( message );
        verifyNames( set );

        message = REZ.getString( "valid-parents.notice" );
        getLogger().info( message );
        verifyParents( set );

        message = REZ.getString( "valid-links.notice" );
        getLogger().info( message );
        verifyLinks( set );

        message = REZ.getString( "default-loader.notice" );
        getLogger().info( message );
        verifyDefaultLoaderExists( set );

        message = REZ.getString( "unique-classloader.notice" );
        getLogger().info( message );
        verifyUniqueClassLoaderNames( set );

        message = REZ.getString( "unique-joins.notice" );
        getLogger().info( message );
        verifyUniqueJoinNames( set );

        message = REZ.getString( "unique-predefined.notice" );
        getLogger().info( message );
        verifyUniquePredefinedNames( set );

        message = REZ.getString( "unique-joins-entrys.notice" );
        getLogger().info( message );
        verifyUniqueJoinEntrys( set );

        message = REZ.getString( "unique-classpath-entrys.notice" );
        getLogger().info( message );
        verifyUniqueClassLoaderEntrys( set );

        //TODO: Verify that the joins form a directed graph with no loops
    }

    /**
     * Verify that all the classloaders have valid names.
     *
     * @throws Exception if validity check fails
     */
    private void verifyNames( ClassLoaderSetDef set )
        throws Exception
    {
        final ClassLoaderDef[] classLoaders = set.getClassLoaders();
        for( int i = 0; i < classLoaders.length; i++ )
        {
            final String name = classLoaders[ i ].getName();
            verifyName( name );
        }

        final JoinDef[] joins = set.getJoins();
        for( int i = 0; i < joins.length; i++ )
        {
            final String name = joins[ i ].getName();
            verifyName( name );
        }
    }

    /**
     * Verify that all the classloaders have valid parents.
     *
     * @throws Exception if validity check fails
     */
    private void verifyParents( ClassLoaderSetDef set )
        throws Exception
    {
        final ClassLoaderDef[] classLoaders = set.getClassLoaders();
        for( int i = 0; i < classLoaders.length; i++ )
        {
            final ClassLoaderDef classLoader = classLoaders[ i ];
            final String parent = classLoader.getParent();
            if( isLoaderDefined( parent, set ) )
            {
                final String message =
                    REZ.getString( "invalid-parent.error",
                                   classLoader.getName(),
                                   parent );
                throw new Exception( message );
            }
        }
    }

    /**
     * Verify that each join ClassLoader only
     * links to ClassLoaders that exist.
     *
     * @throws Exception if validity check fails
     */
    private void verifyLinks( final ClassLoaderSetDef set )
        throws Exception
    {
        final JoinDef[] joins = set.getJoins();
        for( int i = 0; i < joins.length; i++ )
        {
            verifyLinks( joins[ i ], set );
        }
    }

    /**
     * Verify that each join ClassLoader only
     * links to ClassLoaders that exist.
     *
     * @throws Exception if validity check fails
     */
    private void verifyLinks( final JoinDef join,
                              final ClassLoaderSetDef set )
        throws Exception
    {
        final String[] classloaders = join.getClassloaders();
        for( int i = 0; i < classloaders.length; i++ )
        {
            final String classloader = classloaders[ i ];
            if( !isLoaderDefined( classloader, set ) )
            {
                final String message =
                    REZ.getString( "bad-join-link.error",
                                   join.getName(),
                                   classloader );
                throw new Exception( message );
            }
        }
    }

    /**
     * Verify that all the classloaders have valid names.
     *
     * @throws Exception if validity check fails
     */
    private void verifyName( final String name )
        throws Exception
    {
        final int size = name.length();
        if( 0 == size )
        {
            final String message =
                REZ.getString( "empty-name.error",
                               name );
            throw new Exception( message );
        }
        final char ch = name.charAt( 0 );
        if( !Character.isLetter( ch ) &&
            '_' != ch )
        {
            final String message =
                REZ.getString( "name-invalid-start.error",
                               name );
            throw new Exception( message );
        }

        for( int i = 1; i < size; i++ )
        {
            final char c = name.charAt( i );
            if( !Character.isLetterOrDigit( c ) &&
                '_' != c &&
                '-' != c &&
                '.' != c )
            {
                final String message =
                    REZ.getString( "name-invalid-char.error",
                                   name,
                                   String.valueOf( c ) );
                throw new Exception( message );
            }
        }
    }

    /**
     * Verify that each regular ClassLoader only
     * contains unique entrys.
     *
     * @throws Exception if validity check fails
     */
    private void verifyUniqueClassLoaderEntrys( final ClassLoaderSetDef set )
        throws Exception
    {
        final ClassLoaderDef[] classLoaders = set.getClassLoaders();
        for( int i = 0; i < classLoaders.length; i++ )
        {
            verifyUniqueClassLoaderEntrys( classLoaders[ i ] );
        }
    }

    /**
     * Verify that each regular ClassLoader only
     * contains unique entrys.
     *
     * @throws Exception if validity check fails
     */
    private void verifyUniqueClassLoaderEntrys( final ClassLoaderDef classLoader )
        throws Exception
    {
        final EntryDef[] entrys = classLoader.getEntrys();
        for( int i = 0; i < entrys.length; i++ )
        {
            final EntryDef entry = entrys[ i ];
            final String location = entry.getLocation();
            for( int j = i + 1; j < entrys.length; j++ )
            {
                final EntryDef other = entrys[ j ];
                if( location.equals( other.getLocation() ) )
                {
                    final String message =
                        REZ.getString( "classloader-dup-entrys.error",
                                       classLoader.getName(),
                                       location );
                    throw new Exception( message );
                }
            }
        }
    }

    /**
     * Verify that each join only contains unique classloaders.
     *
     * @throws Exception if validity check fails
     */
    private void verifyUniqueJoinEntrys( final ClassLoaderSetDef set )
        throws Exception
    {
        final JoinDef[] joins = set.getJoins();
        for( int i = 0; i < joins.length; i++ )
        {
            verifyUniqueJoinEntrys( joins[ i ] );
        }
    }

    /**
     * Verify that specified join only contains unique classloaders.
     *
     * @throws Exception if validity check fails
     */
    private void verifyUniqueJoinEntrys( final JoinDef join )
        throws Exception
    {
        final String[] classloaders = join.getClassloaders();
        for( int j = 0; j < classloaders.length; j++ )
        {
            final String name = classloaders[ j ];
            for( int k = j + 1; k < classloaders.length; k++ )
            {
                final String other = classloaders[ k ];
                if( other.equals( name ) )
                {
                    final String message =
                        REZ.getString( "join-dup-entrys.error",
                                       join.getName(),
                                       name );
                    throw new Exception( message );
                }
            }
        }
    }

    /**
     * Verify that the Predefined names are unique set.
     *
     * @throws Exception if validity check fails
     */
    private void verifyUniquePredefinedNames( final ClassLoaderSetDef set )
        throws Exception
    {
        final String[] predefined = set.getPredefined();
        for( int i = 0; i < predefined.length; i++ )
        {
            final String name = predefined[ i ];
            for( int j = i + 1; j < predefined.length; j++ )
            {
                final String other = predefined[ j ];
                if( other.equals( name ) )
                {
                    final String message =
                        REZ.getString( "duplicate-name.error",
                                       "predefined",
                                       "predefined",
                                       name );
                    throw new Exception( message );
                }
            }
        }
    }

    /**
     * Verify that the ClassLoader names are unique throughout the set.
     *
     * @param set the set of ClassLoader defs to search in
     * @throws Exception if validity check fails
     */
    private void verifyUniqueClassLoaderNames( final ClassLoaderSetDef set )
        throws Exception
    {
        final ClassLoaderDef[] classLoaders = set.getClassLoaders();
        for( int i = 0; i < classLoaders.length; i++ )
        {
            final ClassLoaderDef classLoader = classLoaders[ i ];
            verifyUniqueName( set,
                              classLoader.getName(),
                              "classloader",
                              classLoader );
        }
    }

    /**
     * Verify that the specified name is unique in set
     * except for specified entity.
     *
     * @param set the set of classloaders
     * @param name the name
     * @param type the type of classloder (used for exception messages)
     * @param entity the entity to skip (ie the one the name refers to)
     * @throws Exception if validity check fails
     */
    private void verifyUniqueName( final ClassLoaderSetDef set,
                                   final String name,
                                   final String type,
                                   final Object entity )
        throws Exception
    {
        //Make sure our join does not have same name as a
        //predefined ClassLoader
        if( set.isPredefined( name ) )
        {
            final String message =
                REZ.getString( "duplicate-name.error",
                               type,
                               "predefined",
                               name );
            throw new Exception( message );
        }

        //Make sure no joins have same name as our join
        final JoinDef[] joins = set.getJoins();
        for( int j = 0; j < joins.length; j++ )
        {
            final JoinDef other = joins[ j ];
            if( other == entity )
            {
                continue;
            }
            if( other.getName().equals( name ) )
            {
                final String message =
                    REZ.getString( "duplicate-name.error",
                                   type,
                                   "join",
                                   name );
                throw new Exception( message );
            }
        }

        final ClassLoaderDef[] classLoaders = set.getClassLoaders();
        for( int j = 0; j < classLoaders.length; j++ )
        {
            final ClassLoaderDef other = classLoaders[ j ];
            if( other == entity )
            {
                continue;
            }
            if( other.getName().equals( name ) )
            {
                final String message =
                    REZ.getString( "duplicate-name.error",
                                   type,
                                   "classloader",
                                   name );
                throw new Exception( message );
            }
        }
    }

    /**
     * Verify that the join names are unique throughout the set.
     *
     * @param set the set of ClassLoader defs to search in
     * @throws Exception if validity check fails
     */
    private void verifyUniqueJoinNames( final ClassLoaderSetDef set )
        throws Exception
    {
        final JoinDef[] joins = set.getJoins();
        for( int i = 0; i < joins.length; i++ )
        {
            final JoinDef join = joins[ i ];
            verifyUniqueName( set,
                              join.getName(),
                              "join",
                              join );
        }
    }

    /**
     * Verify that the default loader is defined.
     *
     * @param set the set of ClassLoader defs to search in
     * @throws Exception if validity check fails
     */
    private void verifyDefaultLoaderExists( final ClassLoaderSetDef set )
        throws Exception
    {
        final String name = set.getDefault();
        if( !isLoaderDefined( name, set ) )
        {
            final String message =
                REZ.getString( "missing-default-loader.error",
                               name );
            throw new Exception( message );
        }
    }

    /**
     * Return true if specified loader is defined in set.
     *
     * @param name the name of loader
     * @param set the set to search
     * @return true if specified loader is defined in set.
     */
    private boolean isLoaderDefined( final String name,
                                     final ClassLoaderSetDef set )
    {
        if( set.isPredefined( name ) )
        {
            return true;
        }
        else if( null != set.getClassLoader( name ) )
        {
            return true;
        }
        else if( null != set.getJoin( name ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
