/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.deployer;

import org.apache.avalon.framework.info.ComponentInfo;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.tools.infobuilder.LegacyBlockInfoReader;
import org.apache.excalibur.containerkit.factory.DefaultComponentFactory;
import java.io.InputStream;

/**
 * A Phoenix-specific {@link org.apache.excalibur.containerkit.factory.ComponentFactory}
 * that makes sure {@link ComponentInfo} is loaded via BlockInfo loader.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1.2.1 $ $Date: 2002/12/03 08:15:33 $
 */
public class PhoenixComponentFactory
    extends DefaultComponentFactory
{
    private final LegacyBlockInfoReader m_infoReader = new LegacyBlockInfoReader();

    public PhoenixComponentFactory( final ClassLoader classLoader )
    {
        super( classLoader );
    }

    public void enableLogging( final Logger logger )
    {
        super.enableLogging( logger );
        m_infoReader.enableLogging( logger );
    }

    protected ComponentInfo createComponentInfo( final String implementationKey )
        throws Exception
    {
        final String xinfo = implementationKey.replace( '.', '/' ) + ".xinfo";
        final InputStream inputStream = getClassLoader().getResourceAsStream( xinfo );
        if( null == inputStream )
        {
            final String message =
                "Missing BlockInfo for class " + implementationKey;
            throw new Exception( message );
        }

        return m_infoReader.createComponentInfo( implementationKey, inputStream );
    }

}

