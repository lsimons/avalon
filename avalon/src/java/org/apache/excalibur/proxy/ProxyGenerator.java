/**
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.proxy;

import gnu.bytecode.Access;
import gnu.bytecode.ClassType;
import gnu.bytecode.ClassTypeWriter;
import gnu.bytecode.CodeAttr;
import gnu.bytecode.Field;
import gnu.bytecode.Scope;
import gnu.bytecode.Type;
import gnu.bytecode.Variable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log.LogKit;
import org.apache.log.Logger;

/**
 * A class to generate proxies for objects.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class ProxyGenerator
{
    protected final static boolean    DEBUG       = false;
    protected final static Logger     LOGGER      =
        ( DEBUG ) ? LogKit.getLoggerFor( "ProxyGenerator" ) : null;

    protected final static Object     MONITOR     = new Object();
    protected final static ClassType  BASE_CLASS  =
        (ClassType)Type.getType( "java.lang.Object" );

    protected static long             c_currentId;

    /**
     * Private constructor to block subclassing.
     *
     */
    private ProxyGenerator()
    {
    }

    /**
     * Way to generate unique id for each class.
     *
     * @return a unique id
     */
    protected static long getNextId()
    {
        synchronized( MONITOR )
        {
            return c_currentId++;
        }
    }

    /**
     * Generate a proxy for object with certain interfaces.
     *
     * @param object the object
     * @param interfaces[] the interfaces
     * @return the proxy object
     * @exception IllegalArgumentException if an error occurs
     */
    public static Object generateProxy( final Object object,
                                        final Class[] interfaces )
        throws IllegalArgumentException
    {
        if( DEBUG )
        {
            LOGGER.debug( "Generating proxy for " + object.getClass().getName() );
            LOGGER.debug( "Interfaces generating:" );

            for( int i = 0; i < interfaces.length; i++ )
            {
                LOGGER.debug( interfaces[ i ].getName() );
            }
        }

        for( int i = 0; i < interfaces.length; i++ )
        {
            if( !interfaces[ i ].isInterface() )
            {
                throw new IllegalArgumentException( "Class " + interfaces[ i ].getName() +
                                                    " is not an interface" );
            }
            else if( !interfaces[ i ].isInstance( object ) )
            {
                throw new IllegalArgumentException( "Object does not implement interface " +
                                                    interfaces[ i ].getName() );
            }
        }

        final HashMap methodSet = determineMethods( interfaces );

        final String classname = "org.apache.avalon.tmp.Proxy" + getNextId();

        if( DEBUG ) { LOGGER.debug( "Generating proxy named " + classname ); }

        final ClassType proxy = createProxyType( classname );

        //generate all interface declarations
        generateInterfaces( proxy, interfaces );

        final ClassType target =
            (ClassType)Type.make( object.getClass() );

        target.doFixups();

        //generate variables/constructor
        generateBase( proxy, target );

        //generate methods
        final Iterator methods = methodSet.values().iterator();
        while( methods.hasNext() )
        {
            generateMethod( proxy, target, (Method)methods.next() );
        }

        if( DEBUG )
        {
            //help while debugging
            //ClassTypeWriter.print( target, System.out, 0 );
            //try { proxy.writeToFile( "/tmp/" + classname.replace('.','/') + ".class" ); }
            //catch( final Throwable throwable ) { throwable.printStackTrace(); }
        }

        proxy.doFixups();

        Class proxyClass = null;
        try
        {
            final byte[] classData = proxy.writeToArray();

            //extremely inneficient - must fix in future
            final ProxyClassLoader classLoader =
                new ProxyClassLoader( object.getClass().getClassLoader() );

            proxyClass = classLoader.loadClass( classname, true, classData );
            final Constructor ctor =
                proxyClass.getConstructor( new Class[] { object.getClass() } );
            return ctor.newInstance( new Object[] { object } );
        }
        catch( final Throwable throwable ) { throwable.printStackTrace(); }

        return null;
    }

    /**
     * Create Proxy class.
     *
     * @param classname name of class
     * @return the proxy class
     */
    protected static ClassType createProxyType( final String classname )
    {
        final ClassType proxy = new ClassType( classname );
        proxy.setModifiers( Access.PUBLIC | /*ACC_SUPER*/ 0x0020 | Access.FINAL );
        proxy.setSuper( BASE_CLASS );

        return proxy;
    }

    /**
     * generate the list of Interfaces class implements.
     *
     * @param proxy the proxy class
     * @param interfaces[] the interfaces to add
     */
    protected static void generateInterfaces( final ClassType proxy,
                                              final Class[] interfaces )
    {
        final ClassType[] interfaceTypes = new ClassType[ interfaces.length ];

        for( int i = 0; i < interfaceTypes.length; i++ )
        {
            interfaceTypes[ i ] = (ClassType)Type.getType( interfaces[ i ].getName() );
        }

        proxy.setInterfaces( interfaceTypes );
    }

    /**
     * Generate code for wrapper method.
     *
     * @param proxy the class to add to
     * @param target the class wrapping
     * @param method the method to wrap
     */
    protected static void generateMethod( final ClassType proxy,
                                          final ClassType target,
                                          final Method method )
    {
        final Class[] parameters = method.getParameterTypes();
        final Type[] parameterTypes = new Type[ parameters.length ];

        for( int i = 0; i < parameterTypes.length; i++ )
        {
            parameterTypes[ i ] = Type.getType( parameters[ i ].getName() );
        }

        final Type returnType =
            Type.getType( method.getReturnType().getName() );

        final gnu.bytecode.Method newMethod =
            proxy.addMethod( method.getName(),
                             Access.PUBLIC,
                             parameterTypes,
                             returnType );

        newMethod.init_param_slots();
        newMethod.pushScope();
        final CodeAttr code = newMethod.getCode();

        //put m_core on stack;
        final Field field = proxy.getField( "m_core" );

        code.emitPushThis();
        code.emitGetField( field );

        for( int i = 0; i < parameterTypes.length; i++ )
        {
            code.emitLoad( code.getArg( 1 + i ) );
        }

        //call target method
        final gnu.bytecode.Method targetMethod =
            target.getMethod( method.getName(), parameterTypes );
        code.emitInvokeVirtual( targetMethod );

        //return
        code.emitReturn();
        newMethod.popScope();
    }

    /**
     * Generate constructor code and field data.
     *
     * @param proxy the representation of class so far
     * @param target the type that is wrapped
     */
    protected static void generateBase( final ClassType proxy,
                                        final Type target )
    {
        final Field field = proxy.addField( "m_core", target );
        field.flags |= Access.PRIVATE;

        final gnu.bytecode.Method constructor =
            proxy.addMethod( "<init>",
                             Access.PUBLIC,
                             new Type[] { target },
                             Type.void_type );

        final gnu.bytecode.Method superConstructor
            = proxy.getSuperclass().addMethod( "<init>",
                                               Access.PUBLIC,
                                               null,
                                               Type.void_type );

        constructor.init_param_slots();
        constructor.pushScope();
        final CodeAttr code = constructor.getCode();

        //super();
        code.emitPushThis();
        code.emitInvokeSpecial( superConstructor );

        //m_core = param1;
        code.emitPushThis();
        code.emitLoad( code.getArg( 1 ) );
        code.emitPutField( field );

        //return
        code.emitReturn();

        constructor.popScope();
    }

    /**
     * Determine the methods that must be implemented to
     * implement interface, eliminating duplicates.
     *
     * @param interfaces[] the interfaces to extract methods from
     * @return methods
     */
    protected static HashMap determineMethods( final Class[] interfaces )
    {
        final HashMap methodSet = new HashMap();
        final StringBuffer sb = new StringBuffer();

        for( int i = 0; i < interfaces.length; i++ )
        {
            if( DEBUG )
            {
                LOGGER.debug( "Scanning interface " + interfaces[ i ].getName() +
                              " for methods" );
            }

            final Method[] methods = interfaces[ i ].getMethods();

            //for each method generate a pseudo signature
            //Add the method to methodSet under that signature.
            //This is to ensure that only one version of a method is
            //entered into set even if multiple interfaces declare it

            for( int j = 0; j < methods.length; j++ )
            {
                sb.append( methods[ j ].getName() );
                sb.append( '(' );

                final Class[] parameters = methods[ j ].getParameterTypes();

                for( int k = 0; k < parameters.length; k++ )
                {
                    sb.append( parameters[ k ].getName() );
                    sb.append( ' ' );
                }

                sb.append( ";)" );

                if( DEBUG )
                {
                    LOGGER.debug( "Found method with pseudo-signature " + sb );
                }

                methodSet.put( sb.toString(), methods[ j ] );
                sb.setLength( 0 );
            }
        }

        return methodSet;
    }
}
