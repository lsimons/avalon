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
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;

/**
 * Create the BCELWrapper for the component
 *
 * @author <a href="mailto:olaf.bergner@gmx.de>Olaf Bergner</a>
 */


final class BCELWrapperGenerator

{
    /**
     * The suffix to be appended to the name of the wrapped class when creating
     * the name of the wrapper class.
     */
    private static final String WRAPPER_CLASS_SUFFIX = "$BCELWrapper";

    /**
     * The name of the superclass of the wrapper class to be generated.
     */
    private static final String WRAPPER_SUPERCLASS_NAME = "java.lang.Object";

    /**
     * The name of the interface each generated wrapper class has to implement.
     */
    private static final String WRAPPER_CLASS_INTERFACE_NAME =
        WrapperClass.class.getName();

    /**
     * The <code>BCELCodeGenerator</code> to use for
     * byte code generation.
     */
    private final BCELCodeGenerator m_codeGenerator;

    /**
     * The <code>ClassGen</code> instance to use for byte code generation.
     */
    private ClassGen m_classGenerator = null;

    /**
     * The <code>ClassLoader</code> to use when loading a class generated by this
     * <code>BCELWrapperGenerator</code>.
     */
    private final BCELClassLoader m_bcelClassLoader;

    /**
     * @author <a href="mailto:olaf.bergner@gmx.de>Olaf Bergner</a>
     */


    private final class BCELClassLoader extends ClassLoader

    {
        /**
         * The <i>byte code</i> representing the wrapper class created by the
         * enclosing <code>BCELWrapperGenerated</code>. This field will be
         * managed by the <code>BCELWrapperGenerator</code>.
         */
        private byte[] m_byteCode = null;

        /**
         * Constructs a <code>BCELClassLoader</code> with the specified class
         * loader as its parent.
         *
         * @param parent The parent <code>ClassLoader</code>
         */
        public BCELClassLoader( final ClassLoader parent )
        {
            super( parent );
        }

        /**
         * Constructs a <code>BCELClassLoader</code> with no parent.
         *
         */
        public BCELClassLoader()
        {
            super();
        }

        /**
         *
         * @see java.lang.ClassLoader#findClass(String)
         */
        protected Class findClass( final String name ) throws ClassNotFoundException
        {
            // Check if the requested class falls within the domain of
            // the BCELWrapperGenerator
            if ( name.endsWith( WRAPPER_CLASS_SUFFIX ) )
            {
                return super.defineClass(
                    name,
                    getByteCode(),
                    0,
                    getByteCode().length );
            }

            return super.findClass( name );
        }

        /**
         * Passes in the <code>byte code</code> to use when loading a class
         * created by the <code>BCELWrapperGenerator</code>.
         * This method will be called by the <code>BCELWrapperGenerator</code>
         * prior to asking this class loader for the generated wrapper class.
         *
         * @param byteCode The <code>byte code</code> to use when loading
         * 					a generated class
         *
         * @throws IllegalArgumentException If <code>byteCode</code> is null or
         * 			empty
         */
        private void setByteCode( final byte[] byteCode )
            throws IllegalArgumentException
        {
            if ( byteCode == null || byteCode.length == 0 )
            {
                final String message =
                    "Parameter byteCode must neither be <null> nor empty.";
                throw new IllegalArgumentException( message );
            }

            m_byteCode = byteCode;
        }

        /**
         * Clears the <code>byte code</code>, setting it to <code>null</code>.
         * This method will be called by the <code>BCELWrapperGenerator</code>
         * immediately after this class loader has returned the generated wrapper
         * class.
         */
        private void clearByteCode()
        {
            m_byteCode = null;
        }

        /**
         * Returns the <code>byte code</code> to use when loading a generated
         * class.
         *
         * @return The <code>byte code</code> for defining the generated class
         */
        private byte[] getByteCode()
        {
            return m_byteCode;
        }
    } // End BCELClassLoader

    /**
     * No-args default constructor.
     */
    public BCELWrapperGenerator()
    {
        m_codeGenerator = new BCELCodeGenerator();
        m_bcelClassLoader =
            new BCELClassLoader( Thread.currentThread().getContextClassLoader() );
    }

    /**
     */
    public Class createWrapper( final Class classToWrap ) throws Exception
    {
        if ( classToWrap == null )
        {
            final String message = "Class to wrap must not be <null>.";
            throw new IllegalArgumentException( message );
        }

        // Guess work interfaces ...
        final Class[] interfacesToImplement =
            AbstractObjectFactory.guessWorkInterfaces( classToWrap );

        // Get JavaClasses as required by BCEL for the wrapped class and its interfaces
        final JavaClass javaClassToWrap = lookupClass( classToWrap );
        final JavaClass[] javaInterfacesToImplement =
            lookupClasses( interfacesToImplement );

        // The name of the wrapper class to be generated
        final String wrapperClassName =
            classToWrap.getName() + WRAPPER_CLASS_SUFFIX;

        // Create BCEL class generator
        m_classGenerator =
            new ClassGen(
                wrapperClassName,
                WRAPPER_SUPERCLASS_NAME,
                null,
                Constants.ACC_FINAL
            |Constants.ACC_PUBLIC
            |Constants.ACC_SUPER,
                extractInterfaceNames( interfacesToImplement ) );

        // Initialize method-field generator
        m_codeGenerator.init(
            wrapperClassName,
            WRAPPER_SUPERCLASS_NAME,
            javaClassToWrap,
            m_classGenerator );

        final byte[] byteCode = buildWrapper( javaInterfacesToImplement );
        // TODO: Check synchronization
        Class generatedClass;
        synchronized ( m_bcelClassLoader )
        {
            m_bcelClassLoader.setByteCode( byteCode );
            generatedClass = m_bcelClassLoader.loadClass( wrapperClassName );
            m_bcelClassLoader.clearByteCode();
        }

        return generatedClass;
    }

    /**
     * Takes a <code>Class</code> instance as a its parameter and returns corresponding
     * the <code>JavaClass</code> instance as used by <b>BCEL</b>.
     *
     * @param clazz The <code>Class</code> instance we want to turn into a
     * 				 <code>JavaClass</code>
     * @return The <code>JavaClass</code> representing the given <code>Class</code>
     * 			instance
     */
    private JavaClass lookupClass( final Class clazz )
    {
        return Repository.lookupClass( clazz );
    }

    /**
     * Takes an array of <code>Class</code> instances and returns an array holding
     * the corresponding <code>JavaClass</code> instances as used by <b>BCEL</b>.
     *
     * @param classes      An array holding <code>Class</code> instances we
     * 						want to turn into <code>JavaClass</code> instances
     * @return JavaClass[] An array of <code>JavaClass</code> instances representing
     * 						the given <code>Class</code> instances
     */
    private JavaClass[] lookupClasses( final Class[] classes )
    {
        final JavaClass[] javaClasses = new JavaClass[classes.length];
        for ( int i = 0; i < classes.length; ++i )
        {
            javaClasses[i] = lookupClass( classes[i] );
        }

        return javaClasses;
    }

    /**
     * Takes an array of <code>Class</code> instances supposed to represent
     * interfaces and returns a list of the names of those interfaces.
     *
     * @param interfaces An array of <code>Class</code> instances
     * @return String[]  An array of the names of those <code>Class</code> instances
     */
    private String[] extractInterfaceNames( final Class[] interfaces )
    {
        final String[] ifaceNames = new String[interfaces.length + 1];
        for ( int i = 0; i < interfaces.length; ++i )
        {
            ifaceNames[i] = interfaces[i].getName();
        }
        // Add interface WrapperClass to the list of interfaces to be implemented
        ifaceNames[ifaceNames.length - 1] = WRAPPER_CLASS_INTERFACE_NAME;

        return ifaceNames;
    }

    /**
     * Generates the wrapper byte code for a given interface.
     *
     * @param interfacesToImplement The interfaces we want to generate wrapper
     * 								 byte code for
     * @return byte[] The generated byte code
     */
    private byte[] buildWrapper( final JavaClass[] interfacesToImplement )
    {
        // Create field for the wrapped class
        m_classGenerator.addField( m_codeGenerator.createWrappedClassField() );

        // Create default constructor
        m_classGenerator.addMethod( m_codeGenerator.createDefaultConstructor() );

        //Create field accessor for wrapped class instance
        m_classGenerator.addMethod(
            m_codeGenerator.createWrappedClassAccessor() );

        // Implement interfaces
        Method[] interfaceMethods;
        for ( int i = 0; i < interfacesToImplement.length; ++i )
        {
            interfaceMethods =
                m_codeGenerator.createImplementation( interfacesToImplement[i] );
            for ( int j = 0; j < interfaceMethods.length; ++j )
            {
                m_classGenerator.addMethod( interfaceMethods[j] );
            }
        }

        return m_classGenerator.getJavaClass().getBytes();
    }
}