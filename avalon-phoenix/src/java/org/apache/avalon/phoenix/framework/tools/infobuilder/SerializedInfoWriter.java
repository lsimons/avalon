/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.framework.tools.infobuilder;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;

/**
 * Write {@link ComponentInfo} objects to a stream as serialized objects.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/03/01 03:39:47 $
 */
public class SerializedInfoWriter
    implements InfoWriter
{
    public void writeComponentInfo( final ComponentInfo info,
                                    final OutputStream outputStream )
        throws Exception
    {
        final ObjectOutputStream oos = new ObjectOutputStream( outputStream );
        oos.writeObject( info );
        oos.flush();
    }
}
