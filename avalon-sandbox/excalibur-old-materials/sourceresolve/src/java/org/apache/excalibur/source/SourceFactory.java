/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import org.apache.avalon.framework.component.Component;

/**
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version $Id: SourceFactory.java,v 1.2 2002/04/24 12:35:37 cziegeler Exp $
 */
public interface SourceFactory
   extends Component
{

    String ROLE = SourceFactory.class.getName();

    /**
     * Get a <code>Source</code> object.
     * @param parameters This is optional.
     */
    Source getSource( String location, Map parameters )
        throws MalformedURLException, IOException, SourceException;

}
