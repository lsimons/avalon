/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.phoenix.components.extensions.pkgmgr;

import java.util.ArrayList;
import java.util.List;
import org.apache.avalon.excalibur.extension.Extension;

/**
 * Basic Implementation Of PackageManager Interface used to manage
 * "Optional Packages" (formerly known as "Standard Extensions").
 * The "Optional Packages" are stored on file system in a number of
 * directories.
 *
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2003/12/05 15:14:36 $
 * @see ExtensionManager
 * @todo Determine an appropriate interface to this service and
 *       an appropriate mechanism via which to do searching and
 *       expansion of a package set. At that point separate out
 *       implementation and interface for mechanism.
 */
public class PackageManager
{
    ///Ordered list of repositories to search in
    private ExtensionManager m_repository;

    /**
     * Construct a PackageManager for a repositories.
     *
     * @param repository the repository to use in PackageManager
     */
    public PackageManager( final ExtensionManager repository )
    {
        if( null == repository )
        {
            throw new NullPointerException( "repository" );
        }

        m_repository = repository;
    }

    /**
     * Return the {@link OptionalPackage} that provides specified
     * {@link Extension}. If the specified {@link Extension}
     * can not be found then <code>null</code> is returned. If there is
     * multiple implementations that satisfy {@link Extension},
     * then an {@link OptionalPackage} returned is based on the
     * following heristic;
     *
     * <p>Return the first Optional Package. (This heuristic will
     * be replaced in time).</p>
     *
     * @param extension Description of the extension that needs to be provided by
     *                  optional package
     * @return an array of optional packages that satisfy extension and
     *         the extensions dependencies
     * @see OptionalPackage
     * @see Extension
     */
    public OptionalPackage getOptionalPackage( final Extension extension )
    {
        final OptionalPackage[] packages = m_repository.getOptionalPackages( extension );
        if( null == packages || 0 == packages.length )
        {
            return null;
        }

        //The best candidate is always returned first so we
        //can just return it.
        return packages[ 0 ];
    }

    /**
     * Build a list of dependencies based on specified {@link Extension}s.
     * Each specified {@link Extension} is expected to be a required extension
     * of another "Optional Package".
     *
     * <p>If the required {@link Extension} can not be found locally then
     * an UnsatisfiedPackageException is thrown. if an {@link OptionalPackage}
     * is found locally that satisfies specified required {@link Extension}
     * then it is returned in the array of OptionalPackages. scanDependencies() is then recursively
     * called on all of the candidates required extensions.</p>
     *
     * @param required the array of required Extensions.
     * @param available the array of Extensions already available to caller.
     * @return the list of OptionalPackages that satisfy required Extensions
     * @throws UnsatisfiedExtensionException if extensions could not be satisified
     * @see #scanDependencies
     */
    public OptionalPackage[] scanDependencies( final Extension required,
                                               final Extension[] available )
        throws UnsatisfiedExtensionException
    {
        return scanDependencies( new Extension[]{required}, available );
    }

    /**
     * Build a list of dependencies based on specified {@link Extension}.
     * The specified {@link Extension} is expected to be a required extension
     * of another "Optional Package".
     *
     * <p>If the required {@link Extension} can not be found locally then
     * an UnsatisfiedPackageException is thrown. if an {@link OptionalPackage}
     * is found locally that satisfies specified required {@link Extension}
     * then it is returned in the array of OptionalPackages. scanDependencies() is then recursively
     * called on all of the candidates required extensions.</p>
     *
     * @param required the array of required Extensions.
     * @param available the array of Extensions already available to caller.
     * @return the list of OptionalPackages that satisfy required Extensions
     * @throws UnsatisfiedExtensionException if extensions could not be satisified
     * @see #scanDependencies
     */
    public OptionalPackage[] scanDependencies( final Extension[] required,
                                               final Extension[] available )
        throws UnsatisfiedExtensionException
    {
        final ArrayList dependencies = new ArrayList();
        final ArrayList unsatisfied = new ArrayList();

        scanDependencies( required, available, dependencies, unsatisfied );

        if( 0 != unsatisfied.size() )
        {
            final Extension extension = (Extension)unsatisfied.get( 0 );
            throw new UnsatisfiedExtensionException( extension );
        }

        return (OptionalPackage[])dependencies.toArray( new OptionalPackage[ 0 ] );
    }

    /**
     * Build a list of dependencies based on specified {@link Extension}s.
     * Each specified {@link Extension} is expected to be a required extension
     * of another "Optional Package".
     *
     * <p>If the required {@link Extension} can not be found locally then
     * it is placed in list of unsatisfied Extensions. If a candidate {@link Extension}
     * is found locally that satisfies specified required {@link Extension}
     * then it is added to list of dependencies. scanDependencies() is then recursively
     * called on all of the candidates required extensions.</p>
     *
     * @param required the array of required Extensions.
     * @param available the array of Extensions already available to caller.
     * @param dependencies the list of dependencies.
     * @param unsatisfied the list of unsatisfied (ie non-local) dependencies.
     * @see #scanDependencies
     */
    public void scanDependencies( final Extension[] required,
                                  final Extension[] available,
                                  final List dependencies,
                                  final List unsatisfied )
    {
        for( int i = 0; i < required.length; i++ )
        {
            scanDependencies( required[ i ], available, dependencies, unsatisfied );
        }
    }

    /**
     * Build a list of dependencies based on specified {@link Extension}.
     * The specified {@link Extension} is expected to be a required extension
     * of another "Optional Package".
     *
     * <p>If the required {@link Extension} can not be found locally then
     * it is placed in list of unsatisfied Extensions. If a candidate {@link OptionalPackage}
     * is found locally that satisfies specified required {@link Extension}
     * then it is added to list of dependencies. scanDependencies() is then recursively
     * called on all of the candidates required extensions.</p>
     *
     * @param required the required Extension.
     * @param available the array of Extensions already available to caller.
     * @param dependencies the list of OptionalPackages required to satisfy extension.
     * @param unsatisfied the list of unsatisfied (ie non-local) dependencies.
     * @see #scanDependencies
     */
    public void scanDependencies( final Extension required,
                                  final Extension[] available,
                                  final List dependencies,
                                  final List unsatisfied )
    {
        //Check to see if extension is satisifed by the
        //list of available extensions passed in
        for( int i = 0; i < available.length; i++ )
        {
            final Extension other = available[ i ];
            if( other.isCompatibleWith( required ) )
            {
                return;
            }
        }

        //Check to see if extension is satisifed by one
        //of the extensions already found
        final int size = dependencies.size();
        for( int i = 0; i < size; i++ )
        {
            final OptionalPackage optionalPackage = (OptionalPackage)dependencies.get( i );
            if( optionalPackage.isCompatible( required ) )
            {
                return;
            }
        }

        final OptionalPackage optionalPackage = getOptionalPackage( required );
        if( null == optionalPackage )
        {
            if( !unsatisfied.contains( required ) )
            {
                unsatisfied.add( required );
            }
        }
        else
        {
            if( !dependencies.contains( optionalPackage ) )
            {
                dependencies.add( optionalPackage );
            }

            scanDependencies( optionalPackage.getRequiredExtensions(),
                              available,
                              dependencies,
                              unsatisfied );
        }
    }
}
