/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.framework.tools.infobuilder;

import java.io.InputStream;
import java.io.ObjectInputStream;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;

/**
 * Create {@link ComponentInfo} objects from stream made up of
 * serialized object.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/03/01 03:39:47 $
 */
public class SerializedInfoReader
    implements InfoReader
{
    public ComponentInfo createComponentInfo( final String implementationKey,
                                              final InputStream inputStream )
        throws Exception
    {
        final ObjectInputStream ois = new ObjectInputStream( inputStream );
        return (ComponentInfo)ois.readObject();
    }
}
