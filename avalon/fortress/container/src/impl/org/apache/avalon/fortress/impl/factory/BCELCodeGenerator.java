/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.avalon.fortress.impl.factory;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 	<code>BCELCodeGenerator</code> creates implementations for the
 * 	{@link org.apache.bcel.classfile.Method Method}s and
 *  {@link org.apache.bcel.classfile.Field Field}s needed in creating a
 * 	<code>WrapperClass</code>.
 * </p>
 *
 * @author <a href="mailto:olaf.bergner@gmx.de>Olaf Bergner</a>
 */

public final class BCELCodeGenerator
{
    //***************************************************************************
    // Fields
    //***************************************************************************

    /**
     * The name of the field holding the wrapped class in the generated
     * wrapper, e.g.
     * <pre>
     * 	<code>
     * 		private final <ClassToWrap> WRAPPED_CLASS_FN;
     * 	</code>
     * </pre>
     */
    private static final String WRAPPED_CLASS_FN = "m_wrappedClass";

    /**
     * The name of the field accessor used to ask the generated wrapper for
     * the wrapped class instance.
     */
    private static final String ACCESSOR_METHOD_NAME = "getWrappedObject";

    /**
     * The name of the wrapper class to be created.
     */
    private String m_wrapperClassName;

    /**
     * The name of the superclass of the wrapper class to be generated.
     */
    private String m_wrapperSuperclassName;

    /**
     * Class object holding the type of the object we want to create a
     * wrapper for.
     */
    private JavaClass m_classToWrap;

    /**
     * The {@link org.apache.bcel.generic.Type Type} of the class we want to
     * create a wrapper for.
     */
    private Type m_classToWrapType;

    /**
     * The {@link org.apache.bcel.generic.ClassGen ClassGen} instance to use for
     * code generation.
     */
    private ClassGen m_classGenerator;

    /**
     * The {@link org.apache.bcel.generic.ConstantPoolGen ConstantPoolGen}
     * instance to use for code generation.
     */
    private ConstantPoolGen m_constPoolGenerator;

    /**
     * The {@link org.apache.bcel.generic.InstructionList InstructionList} instance
     * to use during code generation.
     */
    private final InstructionList m_instructionList = new InstructionList();

    /**
     * The {@link org.apache.bcel.generic.InstructionFactory InstructionFactory} to
     * use during code gereration.
     */
    private InstructionFactory m_instructionFactory;

    /**
     * Flag indicating whether this instance is already initialized or not.
     */
    private boolean m_isInitialized = false;

    /**
     * Default constructor.
     */
    public BCELCodeGenerator()
    {
        // Left blank
    }

    public void init(
        final String wrapperClassName,
        final String wrapperSuperclassName,
        final JavaClass classToWrap,
        final ClassGen classGenerator )
        throws IllegalArgumentException
    {
        if ( classToWrap == null )
        {
            final String message = "Target class must not be <null>.";
            throw new IllegalArgumentException( message );
        }
        if ( classToWrap.isAbstract() || !classToWrap.isClass() )
        {
            final String message =
                "Target class must neither be abstract nor an interface.";
            throw new IllegalArgumentException( message );
        }
        if ( classGenerator == null )
        {
            final String message = "ClassGenerator must not be <null>.";
            throw new IllegalArgumentException( message );
        }

        m_wrapperClassName = wrapperClassName;
        m_wrapperSuperclassName = wrapperSuperclassName;
        m_classToWrap = classToWrap;
        m_classToWrapType = new ObjectType( m_classToWrap.getClassName() );
        m_classGenerator = classGenerator;
        m_constPoolGenerator = m_classGenerator.getConstantPool();
        m_instructionFactory =
            new InstructionFactory( m_classGenerator, m_constPoolGenerator );

        m_isInitialized = true;
    }

    /**
     * Create a field declaration of the form
     * <pre>
     * 	<code>
     * 		private <ClassToWrap> WRAPPED_CLASS_FN;
     * 	</code>
     * </pre>
     *
     * @return Field
     *
     * @throws IllegalStateException If this instance is not initialized.
     */
    public Field createWrappedClassField() throws IllegalStateException
    {
        if ( !isInitialized() )
        {
            final String message =
                "BCELMethodFieldImplementationGenerator is not initialized.";
            throw new IllegalStateException( message );
        }

        final FieldGen fg =
            new FieldGen(
                Constants.ACC_PRIVATE,
                m_classToWrapType,
                WRAPPED_CLASS_FN,
                m_constPoolGenerator );

        return fg.getField();
    }

    /**
     * Create the wrapper class' default constructor:
     * <pre>
     * 	<code>
     * 		public <wrapperClass>(<classToWrap> classToWrap)
     * 		{
     * 			this.<WRAPPED_CLASS_FN> = classToWrap;
     * 		}
     * 	</code>
     * </pre>
     *
     * @return The created default constructor
     *
     * @throws IllegalStateException If this instance is not initialized.
     */
    public Method createDefaultConstructor() throws IllegalStateException
    {
        if ( !isInitialized() )
        {
            final String message =
                "BCELMethodFieldImplementationGenerator is not initialized.";
            throw new IllegalStateException( message );
        }
        final MethodGen mg =
            new MethodGen(
                Constants.ACC_PUBLIC,
                Type.VOID,
                new Type[]{m_classToWrapType},
                new String[]{"classToWrap"},
                "<init>",
                m_wrapperClassName,
                m_instructionList,
                m_constPoolGenerator );

        m_instructionList.append(
            m_instructionFactory.createLoad( Type.OBJECT, 0 ) );
        m_instructionList.append(
            m_instructionFactory.createInvoke(
                m_wrapperSuperclassName,
                "<init>",
                Type.VOID,
                Type.NO_ARGS,
                Constants.INVOKESPECIAL ) );
        m_instructionList.append(
            m_instructionFactory.createLoad( Type.OBJECT, 0 ) );
        m_instructionList.append(
            m_instructionFactory.createLoad( Type.OBJECT, 1 ) );
        m_instructionList.append(
            m_instructionFactory.createFieldAccess(
                m_wrapperClassName,
                WRAPPED_CLASS_FN,
                m_classToWrapType,
                Constants.PUTFIELD ) );
        m_instructionList.append( m_instructionFactory.createReturn( Type.VOID ) );
        mg.setMaxStack();
        mg.setMaxLocals();

        return extractMethod( mg );
    }

    /**
     * Create a field accessor for the wrapped class instance of the form
     * <pre>
     * 	<code>
     * 		public Object <ACCESSOR_METHOD_NAME>()
     * 		{
     * 			return this.<WRAPPED_CLASS_FN>;
     * 		}
     * 	</code>
     * </pre>
     * @return Method
     * @throws IllegalStateException
     */
    public Method createWrappedClassAccessor() throws IllegalStateException
    {
        if ( !isInitialized() )
        {
            final String message =
                "BCELMethodFieldImplementationGenerator is not initialized.";
            throw new IllegalStateException( message );
        }

        final MethodGen mg =
            new MethodGen(
                Constants.ACC_PUBLIC,
                Type.OBJECT,
                Type.NO_ARGS,
                new String[]{
                },
                ACCESSOR_METHOD_NAME,
                m_classToWrap.getClassName(),
                m_instructionList,
                m_constPoolGenerator );

        m_instructionList.append(
            m_instructionFactory.createLoad( Type.OBJECT, 0 ) );
        m_instructionList.append(
            m_instructionFactory.createFieldAccess(
                m_wrapperClassName,
                WRAPPED_CLASS_FN,
                m_classToWrapType,
                Constants.GETFIELD ) );
        m_instructionList.append(
            m_instructionFactory.createReturn( Type.OBJECT ) );

        mg.setMaxStack();
        mg.setMaxLocals();

        return extractMethod( mg );
    }

    /**
     * Create a method declaration/definition of the form
     * <pre>
     * 	<code>
     * 		public <returnType> <methodName>(<parameterTypes>)
     * 			throws <exceptionNames>
     * 		{
     * 			return this.<WRAPPED_CLASS_FN>.<methodName>(<parameterTypes>);
     * 		}
     * 	</code>
     * </pre>
     *
     * @param methodName     The name of the method to create
     * @param returnType     The return type of the method to create
     * @param parameterTypes The array of parameter types of the method to create
     * @param exceptionNames The array of the names of the exceptions the method
     * 						  to create might throw
     *
     * @return Method		  The {@link org.apache.bcel.classfile.Method Method}
     * 						  object representing the created method
     *
     * @throws IllegalArgumentException If any of the parameters passed in is null.
     * @throws IllegalStateException If this instance is not initialized.
     */
    public Method createMethodWrapper(
        final String methodName,
        final Type returnType,
        final Type[] parameterTypes,
        final String[] exceptionNames )
        throws IllegalArgumentException, IllegalStateException
    {
        if ( !isInitialized() )
        {
            final String message =
                "BCELMethodFieldImplementationGenerator is not initialized.";
            throw new IllegalStateException( message );
        }
        if ( methodName == null
            || returnType == null
            || parameterTypes == null
            || exceptionNames == null )
        {
            final String message = "None of the parameters may be <null>.";
            throw new IllegalArgumentException( message );
        }

        final MethodGen mg =
            new MethodGen(
                Constants.ACC_PUBLIC,
                returnType,
                parameterTypes,
                null,
                methodName,
                m_wrapperClassName,
                m_instructionList,
                m_constPoolGenerator );

        // Create throws clause
        for ( int i = 0; i < exceptionNames.length; i++ )
        {
            mg.addException( exceptionNames[i] );
        }

        // Loading the wrapped class instance onto the stack ...
        m_instructionList.append(
            m_instructionFactory.createLoad( Type.OBJECT, 0 ) );
        m_instructionList.append(
            m_instructionFactory.createFieldAccess(
                m_wrapperClassName,
                WRAPPED_CLASS_FN,
                m_classToWrapType,
                Constants.GETFIELD ) );

        // Loading all parameters onto the stack ...
        short stackIndex = 1;
        // Stack index 0 is occupied by the wrapped class instance.
        for ( int i = 0; i < parameterTypes.length; ++i )
        {
            m_instructionList.append(
                m_instructionFactory.createLoad( parameterTypes[i], stackIndex ) );
            stackIndex += parameterTypes[i].getSize();
        }

        // Invoking the specified method with the loaded parameters on
        // the wrapped class instance ...
        m_instructionList.append(
            m_instructionFactory.createInvoke(
                m_classToWrap.getClassName(),
                methodName,
                returnType,
                parameterTypes,
                Constants.INVOKEVIRTUAL ) );

        // Creating return statement ...
        m_instructionList.append( m_instructionFactory.createReturn( returnType ) );

        mg.setMaxStack();
        mg.setMaxLocals();

        return extractMethod( mg );
    }

    /**
     * Create a method declaration/definition of the form
     * <pre>
     * 	<code>
     * 		public <returnType> <methodName>(<parameterTypes>)
     * 			throws <exceptionNames>
     * 		{
     * 			return this.<WRAPPED_CLASS_FN>.<methodName>(<parameterTypes>);
     * 		}
     * 	</code>
     * </pre>
     *
     * @param methodToWrap The <code>Method</code> to create a wrapper for.
     *
     * @return Method		The wrapper method.
     *
     * @throws IllegalArgumentException If <code>methodToWrao</code> is null.
     * @throws IllegalStateException If this instance is not initialized.
     */
    public Method createMethodWrapper( final Method methodToWrap )
        throws IllegalArgumentException, IllegalStateException
    {
        if ( methodToWrap == null )
        {
            final String message = "Method parameter must not be <null>.";
            throw new IllegalArgumentException( message );
        }

        return createMethodWrapper(
            methodToWrap.getName(),
            methodToWrap.getReturnType(),
            methodToWrap.getArgumentTypes(),
            methodToWrap.getExceptionTable().getExceptionNames() );
    }

    /**
     * Creates an implementation for the supplied {@link org.apache.bcel.classfile.JavaClass JavaClass}
     * instance representing an interface.
     *
     * @param interfaceToImplement The interface we want to create an implementation for
     * @return Method[]            An array of {@link org.apache.bcel.classfile.Method Method}
     * 								instances representing the interface implementation.
     * @throws IllegalArgumentException If <code>interfaceToImplement</code> is <code>null</code>
     * 									 or does not represent an interface
     * @throws IllegalStateException    If this instance has not been initialized
     */
    public Method[] createImplementation( final JavaClass interfaceToImplement )
        throws IllegalArgumentException, IllegalStateException
    {
        if ( interfaceToImplement == null )
        {
            final String message = "Interface to implement must not be <null>.";
            throw new IllegalArgumentException( message );
        }
        if ( !interfaceToImplement.isInterface() )
        {
            final String message =
                "Supplied JavaClass parameter is not an interface.";
            throw new IllegalArgumentException( message );
        }
        if ( !isInitialized() )
        {
            final String message =
                "BCELInterfaceImplementationGenerator is not initialized.";
            throw new IllegalStateException( message );
        }

        final Method[] interfaceMethods = extractMethods( interfaceToImplement );
        final List gmList = new ArrayList();
        for ( int i = 0; i < interfaceMethods.length; ++i )
        {
            final Method im = interfaceMethods[i];

            // Skip <clinit> method ...
            if ( im.getName().equals( "<clinit>" ) )
            {
                continue;
            }

            // Extract exception names ...
            final ExceptionTable exTable = im.getExceptionTable();
            final String[] exceptionNames = ( exTable == null ? new String[]{
            }
                : exTable.getExceptionNames() );
            final Method generatedMethod =
                createMethodWrapper(
                    im.getName(),
                    im.getReturnType(),
                    im.getArgumentTypes(),
                    exceptionNames );

            gmList.add( generatedMethod );
        }

        return (Method[]) gmList.toArray( new Method[gmList.size()] );
    }

    /**
     * Extracts the {@link org.apache.bcel.classfile.Method Method} out of
     * the supplied {@link org.apache.bcel.generic.MethodGen MethodGen} instance,
     * clears the {@link org.apache.bcel.generic.InstructionList InstructionList}
     * and returns the extracted <code>Method</code>.
     *
     * @param mg The {@link org.apache.bcel.generic.MethodGen MethodGen} instance
     * 			  holding the {@link org.apache.bcel.classfile.Method Method} to
     * 			  extract
     * @return   The extracted {@link org.apache.bcel.classfile.Method Method}
     */
    private Method extractMethod( final MethodGen mg )
    {
        final Method m = mg.getMethod();
        m_instructionList.dispose();
        return m;
    }

    /**
     * Has this instance already been initialized?
     *
     * @return TRUE, if this instance has already been initialized, FALSE otherwise
     */
    private boolean isInitialized()
    {
        return m_isInitialized;
    }

    /**
     * Extracts the collection of {@link org.apache.bcel.classfile.Method Method}s
     * declared in the supplied {@link org.apache.bcel.classfile.JavaClass JavaClass}
     * instance. This instance is supposed to represent an interface.
     *
     * @param interfaceToImplement The {@link org.apache.bcel.classfile.JavaClass JavaClass}
     * 								instance representing the interface we are asking for
     * 								its methods.
     * @return Method[]			The array of {@link org.apache.bcel.classfile.Method Method}s
     * 								declared by the interface
     * @throws IllegalArgumentException If <code>interfaceToImplement</code> does not represent an interface
     * @throws NullPointerException if the <code>interfaceToImplement</code> is <code>null</code>
     */
    static Method[] extractMethods( final JavaClass interfaceToImplement )
        throws IllegalArgumentException, NullPointerException
    {
        if ( interfaceToImplement == null )
        {
            final String message = "JavaClass parameter must not be <null>.";
            throw new NullPointerException( message );
        }
        if ( !interfaceToImplement.isInterface() )
        {
            final String message = "JavaClass parameter must be an interface";
            throw new IllegalArgumentException( message );
        }

        Set methods = new HashSet();
        extractMethods( interfaceToImplement, methods );
        JavaClass[] interfaces = interfaceToImplement.getInterfaces();
        for ( int i = 0; i < interfaces.length; i++ )
        {
            extractMethods( interfaces[i], methods );
        }

        return (Method[]) methods.toArray( new Method[]{} );
    }

    private static final void extractMethods( final JavaClass interfaceToImplement, final Set methods )
    {
        Method[] meth = interfaceToImplement.getMethods();
        for ( int m = 0; m < meth.length; m++ )
        {
            methods.add( meth[m] );
        }
    }
}
