/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml;

/**
 * The factory creates a DOMHandler encapsulating a DOM document. The document 
 * behaviour is setup by the factory.
 *
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/06 18:13:49 $
 */
public interface DOMHandlerFactory {
    
    String ROLE = DOMHandlerFactory.class.getName();
    
    DOMHandler createDOMHandler() throws Exception;    
}
