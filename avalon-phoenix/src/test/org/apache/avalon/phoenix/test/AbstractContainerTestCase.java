/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.test;

import junit.framework.TestCase;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.tools.assembler.Assembler;
import org.apache.avalon.phoenix.tools.configuration.ConfigurationBuilder;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.configuration.Configuration;
import java.io.File;
import java.net.URL;

/**
 * Abstract class which TestCases can extend.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/10/01 06:16:59 $
 */
public class AbstractContainerTestCase
    extends TestCase
{
    public AbstractContainerTestCase( String name )
    {
        super( name );
    }

    protected SarMetaData assembleSar( final String config )
        throws Exception
    {
        final Assembler assembler = new Assembler();
        assembler.enableLogging( new ConsoleLogger() );
        final ClassLoader classLoader = getClass().getClassLoader();
        final Configuration assembly = loadConfig( config );
        return assembler.assembleSar( "test", assembly,
                                      new File( "." ), classLoader );
    }

    protected Configuration loadConfig( final String config )
        throws Exception
    {
        final URL resource = getClass().getResource( config );
        return ConfigurationBuilder.build( resource.toExternalForm() );
    }
}
