/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.verifier.test;

import java.io.File;
import java.net.URL;
import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.tools.assembler.Assembler;
import org.apache.avalon.phoenix.tools.configuration.ConfigurationBuilder;
import org.apache.avalon.phoenix.tools.verifier.SarVerifier;

/**
 *  An basic test case for the LogManager.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/10/01 00:08:40 $
 */
public class VerifierTestCase
    extends TestCase
{
    public VerifierTestCase( final String name )
    {
        super( name );
    }

    public void testBasic()
        throws Exception
    {
        verify( "assembly1.xml" );
    }

    public void testComplex()
        throws Exception
    {
        verify( "assembly2.xml" );
    }

    private void verify( final String config ) throws Exception
    {
        final SarMetaData sarMetaData = assembleSar( config );
        final ClassLoader classLoader = getClass().getClassLoader();
        final SarVerifier verifier = new SarVerifier();
        verifier.enableLogging( new ConsoleLogger() );
        verifier.verifySar( sarMetaData, classLoader );
    }

    private SarMetaData assembleSar( final String config ) throws Exception
    {
        final Assembler assembler = new Assembler();
        assembler.enableLogging( new ConsoleLogger() );
        final ClassLoader classLoader = getClass().getClassLoader();
        final Configuration assembly = loadConfig( config );
        return assembler.assembleSar( "test", assembly,
                                      new File( "." ), classLoader );
    }

    private Configuration loadConfig( final String config )
        throws Exception
    {
        final URL resource = getClass().getResource( config );
        return ConfigurationBuilder.build( resource.toExternalForm() );
    }
}
