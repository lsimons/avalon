/* 
 * Copyright (C) The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */
package org.apache.phoenix.engine.facilities;

import java.net.URL;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.Initializable;
import org.apache.avalon.camelot.DefaultFactory;
import org.apache.avalon.camelot.DefaultLoader;
import org.apache.avalon.camelot.Loader;

/**
 * This component used to create blocks.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
class SarBlockFactory
    extends DefaultFactory
    implements Composer, Initializable
{
    protected Loader            m_loader;
    protected ClassLoader       m_classLoader;

    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
    {
        m_classLoader = (ClassLoader)componentManager.lookup( "java.lang.ClassLoader" );
    }

    public void init()
        throws Exception
    {
        m_loader = new DefaultLoader( m_classLoader );
    }

    /**
     * Overidden so that there is only one Loader per Application rather than one per archive.
     *
     * @param url the url to archive that contains code (ignored)
     * @return the Loader
     */
    protected Loader getLoaderFor( final URL url )
    {
        return m_loader;
    }
}
