/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.xpath;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simple node list wrapper around a List object.
 *
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version CVS $Revision: 1.5 $ $Date: 2003/02/25 16:28:29 $ $Author: bloritsch $
 */
final class SimpleNodeList
    implements NodeList
{
    private final List m_list;

    SimpleNodeList( final List list )
    {
        m_list = list;
    }

    public Node item( final int index )
    {
        return (Node)m_list.get( index );
    }

    public int getLength()
    {
        return m_list.size();
    }
}
