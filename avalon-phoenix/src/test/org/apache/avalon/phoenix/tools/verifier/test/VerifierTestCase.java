/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.verifier.test;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.test.AbstractContainerTestCase;
import org.apache.avalon.phoenix.tools.verifier.SarVerifier;

/**
 *  An basic test case for the LogManager.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2002/10/01 06:18:22 $
 */
public class VerifierTestCase
    extends AbstractContainerTestCase
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

    protected void verify( final String config ) throws Exception
    {
        final SarMetaData sarMetaData = assembleSar( config );
        final ClassLoader classLoader = getClass().getClassLoader();
        final SarVerifier verifier = new SarVerifier();
        verifier.enableLogging( new ConsoleLogger() );
        verifier.verifySar( sarMetaData, classLoader );
    }
}
