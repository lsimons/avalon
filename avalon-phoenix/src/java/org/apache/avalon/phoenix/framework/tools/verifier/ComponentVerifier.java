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

package org.apache.avalon.phoenix.framework.tools.verifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.Recomposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Recontextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Reparameterizable;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Utility class to help verify that component respects the
 * rules of an Avalon component.
 *
 * @author Peter Donald
 * @version $Revision: 1.5 $ $Date: 2003/12/05 15:14:38 $
 */
public class ComponentVerifier
    extends AbstractLogEnabled
{
    /**
     * I18n utils.
     */
    private static final Resources REZ =
        ResourceManager.getPackageResources( ComponentVerifier.class );

    /**
     * Constant for array of 0 classes. Saves recreating array everytime
     * look up constructor with no args.
     */
    private static final Class[] EMPTY_TYPES = new Class[ 0 ];

    /**
     * The interfaces representing lifecycle stages.
     */
    private static final Class[] FRAMEWORK_CLASSES = new Class[]
    {
        LogEnabled.class,
        Contextualizable.class,
        Recontextualizable.class,
        Composable.class,
        Recomposable.class,
        Serviceable.class,
        Configurable.class,
        Reconfigurable.class,
        Parameterizable.class,
        Reparameterizable.class,
        Initializable.class,
        Startable.class,
        Suspendable.class,
        Disposable.class
    };

    /**
     * Verify that the supplied implementation class
     * and service classes are valid for a component.
     *
     * @param name the name of component
     * @param implementation the implementation class of component
     * @param services the classes representing services
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyComponent( final String name,
                                 final Class implementation,
                                 final Class[] services )
        throws VerifyException
    {
        verifyComponent( name, implementation, services, true );
    }

    /**
     * Verify that the supplied implementation class
     * and service classes are valid for a component.
     *
     * @param name the name of component
     * @param implementation the implementation class of component
     * @param services the classes representing services
     * @param buildable if true will verify that it is instantiateable
     *                  via class.newInstance(). May not be required for
     *                  some components that are created via a factory.
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyComponent( final String name,
                                 final Class implementation,
                                 final Class[] services,
                                 final boolean buildable )
        throws VerifyException
    {
        if( buildable )
        {
            verifyClass( name, implementation );
        }
        verifyLifecycles( name, implementation );
        verifyServices( name, services );
        verifyImplementsServices( name, implementation, services );
    }

    /**
     * Verify that the supplied implementation implements the specified
     * services.
     *
     * @param name the name of component
     * @param implementation the class representign component
     * @param services the services that the implementation must provide
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyImplementsServices( final String name,
                                          final Class implementation,
                                          final Class[] services )
        throws VerifyException
    {
        for( int i = 0; i < services.length; i++ )
        {
            if( !services[ i ].isAssignableFrom( implementation ) )
            {
                final String message =
                    REZ.getString( "verifier.noimpl-service.error",
                                   name,
                                   implementation.getName(),
                                   services[ i ].getName() );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Verify that the supplied class is a valid class for
     * a Component.
     *
     * @param name the name of component
     * @param clazz the class representing component
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyClass( final String name,
                             final Class clazz )
        throws VerifyException
    {
        verifyNoArgConstructor( name, clazz );
        verifyNonAbstract( name, clazz );
        verifyNonArray( name, clazz );
        verifyNonInterface( name, clazz );
        verifyNonPrimitive( name, clazz );
        verifyPublic( name, clazz );
    }

    /**
     * Verify that the supplied classes are valid classes for
     * a service.
     *
     * @param name the name of component
     * @param classes the classes representign services
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyServices( final String name,
                                final Class[] classes )
        throws VerifyException
    {
        for( int i = 0; i < classes.length; i++ )
        {
            verifyService( name, classes[ i ] );
        }
    }

    /**
     * Verify that the supplied class is a valid class for
     * a service.
     *
     * @param name the name of component
     * @param clazz the class representign service
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyService( final String name,
                               final Class clazz )
        throws VerifyException
    {
        verifyServiceIsaInterface( name, clazz );
        verifyServiceIsPublic( name, clazz );
        verifyServiceNotALifecycle( name, clazz );
    }

    /**
     * Verify that the implementation class does not
     * implement incompatible lifecycle interfaces.
     *
     * @param name the name of component
     * @param implementation the implementation class
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyLifecycles( final String name,
                                  final Class implementation )
        throws VerifyException
    {
        final boolean composable =
            Composable.class.isAssignableFrom( implementation ) ||
            Recomposable.class.isAssignableFrom( implementation );
        final boolean serviceable = Serviceable.class.isAssignableFrom( implementation );
        if( serviceable && composable )
        {
            final String message =
                REZ.getString( "verifier.incompat-serviceable.error",
                               name,
                               implementation.getName() );
            getLogger().error( message );
            throw new VerifyException( message );
        }

        final boolean configurable =
            Configurable.class.isAssignableFrom( implementation ) ||
            Reconfigurable.class.isAssignableFrom( implementation );
        final boolean parameterizable =
            Parameterizable.class.isAssignableFrom( implementation ) ||
            Reparameterizable.class.isAssignableFrom( implementation );
        if( parameterizable && configurable )
        {
            final String message =
                REZ.getString( "verifier.incompat-config.error",
                               name,
                               implementation.getName() );
            getLogger().error( message );
            throw new VerifyException( message );
        }
    }

    /**
     * Verify that the service implemented by
     * specified component is an interface.
     *
     * @param name the name of component
     * @param clazz the class representign service
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyServiceIsaInterface( final String name,
                                           final Class clazz )
        throws VerifyException
    {
        if( !clazz.isInterface() )
        {
            final String message =
                REZ.getString( "verifier.non-interface-service.error",
                               name,
                               clazz.getName() );
            getLogger().error( message );
            throw new VerifyException( message );
        }
    }

    /**
     * Verify that the service implemented by
     * specified component is public.
     *
     * @param name the name of component
     * @param clazz the class representign service
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyServiceIsPublic( final String name,
                                       final Class clazz )
        throws VerifyException
    {
        final boolean isPublic =
            Modifier.isPublic( clazz.getModifiers() );
        if( !isPublic )
        {
            final String message =
                REZ.getString( "verifier.non-public-service.error",
                               name,
                               clazz.getName() );
            getLogger().error( message );
            throw new VerifyException( message );
        }
    }

    /**
     * Verify that the service implemented by
     * specified component does not extend any lifecycle interfaces.
     *
     * @param name the name of component
     * @param clazz the class representign service
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyServiceNotALifecycle( final String name,
                                            final Class clazz )
        throws VerifyException
    {
        for( int i = 0; i < FRAMEWORK_CLASSES.length; i++ )
        {
            final Class lifecycle = FRAMEWORK_CLASSES[ i ];
            if( lifecycle.isAssignableFrom( clazz ) )
            {
                final String message =
                    REZ.getString( "verifier.service-isa-lifecycle.error",
                                   name,
                                   clazz.getName(),
                                   lifecycle.getName() );
                getLogger().error( message );
                throw new VerifyException( message );
            }
        }
    }

    /**
     * Verify that the component has a no-arg aka default
     * constructor.
     *
     * @param name the name of component
     * @param clazz the class representign component
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyNoArgConstructor( final String name,
                                        final Class clazz )
        throws VerifyException
    {
        try
        {
            final Constructor ctor = clazz.getConstructor( EMPTY_TYPES );
            if( !Modifier.isPublic( ctor.getModifiers() ) )
            {
                final String message =
                    REZ.getString( "verifier.non-public-ctor.error",
                                   name,
                                   clazz.getName() );
                getLogger().error( message );
                throw new VerifyException( message );
            }
        }
        catch( final NoSuchMethodException nsme )
        {
            final String message =
                REZ.getString( "verifier.missing-noargs-ctor.error",
                               name,
                               clazz.getName() );
            getLogger().error( message );
            throw new VerifyException( message );
        }
    }

    /**
     * Verify that the component is not represented by
     * abstract class.
     *
     * @param name the name of component
     * @param clazz the class representign component
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyNonAbstract( final String name,
                                   final Class clazz )
        throws VerifyException
    {
        final boolean isAbstract =
            Modifier.isAbstract( clazz.getModifiers() );
        if( isAbstract )
        {
            final String message =
                REZ.getString( "verifier.abstract-class.error",
                               name,
                               clazz.getName() );
            getLogger().error( message );
            throw new VerifyException( message );
        }
    }

    /**
     * Verify that the component is not represented by
     * abstract class.
     *
     * @param name the name of component
     * @param clazz the class representign component
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyPublic( final String name,
                              final Class clazz )
        throws VerifyException
    {
        final boolean isPublic =
            Modifier.isPublic( clazz.getModifiers() );
        if( !isPublic )
        {
            final String message =
                REZ.getString( "verifier.nonpublic-class.error",
                               name,
                               clazz.getName() );
            getLogger().error( message );
            throw new VerifyException( message );
        }
    }

    /**
     * Verify that the component is not represented by
     * primitive class.
     *
     * @param name the name of component
     * @param clazz the class representign component
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyNonPrimitive( final String name,
                                    final Class clazz )
        throws VerifyException
    {
        if( clazz.isPrimitive() )
        {
            final String message =
                REZ.getString( "verifier.primitive-class.error",
                               name,
                               clazz.getName() );
            getLogger().error( message );
            throw new VerifyException( message );
        }
    }

    /**
     * Verify that the component is not represented by
     * interface class.
     *
     * @param name the name of component
     * @param clazz the class representign component
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyNonInterface( final String name,
                                    final Class clazz )
        throws VerifyException
    {
        if( clazz.isInterface() )
        {
            final String message =
                REZ.getString( "verifier.interface-class.error",
                               name,
                               clazz.getName() );
            getLogger().error( message );
            throw new VerifyException( message );
        }
    }

    /**
     * Verify that the component is not represented by
     * an array class.
     *
     * @param name the name of component
     * @param clazz the class representign component
     * @throws VerifyException if error thrown on failure and
     *         component fails check
     */
    public void verifyNonArray( final String name,
                                final Class clazz )
        throws VerifyException
    {
        if( clazz.isArray() )
        {
            final String message =
                REZ.getString( "verifier.array-class.error",
                               name,
                               clazz.getName() );
            getLogger().error( message );
            throw new VerifyException( message );
        }
    }
}
