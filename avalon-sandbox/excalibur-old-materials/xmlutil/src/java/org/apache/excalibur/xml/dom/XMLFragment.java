/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/**
 * This interface must be implemented by classes willing
 * to provide an XML representation of their current state.
 * <br/>
 *
 * @author <a href="mailto:sylvain.wallez@anyware-tech.com">Sylvain Wallez</a>
 * @author <a href="mailto:ricardo@apache.org">Ricardo Rocha</a> for the original XObject class
 * @version CVS $Revision: 1.1 $ $Date: 2003/01/14 09:39:36 $
 */
public interface XMLFragment
{
    /**
     * Appends children representing the object's state to the given node.
     */
    void toDOM( Node node ) throws DOMException;
}
