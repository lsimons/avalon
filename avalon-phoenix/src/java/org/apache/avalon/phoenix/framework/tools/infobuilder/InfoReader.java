/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.framework.tools.infobuilder;

import java.io.InputStream;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;

/**
 * Simple interface used to create {@link ComponentInfo}
 * objects from a stream. This abstraction was primarily created
 * so that the Info objesct could be built from non-XML sources
 * and no XML classes need be in the classpath.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/03/01 03:39:47 $
 */
public interface InfoReader
{
    /**
     * Create a {@link ComponentInfo} from stream
     *
     * @param implementationKey the name of component type that we are looking up
     * @param inputStream the stream that the resource is loaded from
     * @return the newly created {@link ComponentInfo}
     * @throws Exception if unable to create info
     */
    ComponentInfo createComponentInfo( String implementationKey,
                                       InputStream inputStream )
        throws Exception;
}
