/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml.xpath;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * Noop NodeList.
 *
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/07/10 08:53:17 $ $Author: donaldp $
 */
final class EmptyNodeList
    implements NodeList
{
    public Node item( int index )
    {
        return null;
    }

    public int getLength()
    {
        return 0;
    }
}
