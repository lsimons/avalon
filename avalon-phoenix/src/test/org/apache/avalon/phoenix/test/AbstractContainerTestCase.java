/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.test;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.phoenix.components.ContainerConstants;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.components.assembler.Assembler;
import org.apache.avalon.phoenix.tools.configuration.ConfigurationBuilder;

/**
 * Abstract class which TestCases can extend.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.2.6.1 $ $Date: 2002/12/03 08:14:24 $
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
        final Configuration assembly = loadConfig( config );
        final Map parameters = new HashMap();
        parameters.put( ContainerConstants.ASSEMBLY_NAME, "test" );
        parameters.put( ContainerConstants.ASSEMBLY_CONFIG, assembly );
        return assembler.buildAssembly( parameters );
    }

    protected Configuration loadConfig( final String config )
        throws Exception
    {
        final URL resource = getClass().getResource( config );
        return ConfigurationBuilder.build( resource.toExternalForm() );
    }
}
