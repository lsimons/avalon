/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component.servlet;

/**
 * Servlet containers do not have a guaranteed order in which servlets will
 *  be destroyed like there is with initialization.  This means that the
 *  servlet which created and controls an object may be destroyed while other
 *  servlets are still using it. This presents a problem in environments where
 *  common objects are placed into the ServletContext and used by more than
 *  one servlet.
 *
 * To solve this problem an object is placed into the ServletContext wrapped
 *  in a ReferenceProxy.  Whe nthe servlet is ready to be shutdown.  A proxy
 *  latch will monitor these proxies waiting for them to be gced.  When all
 *  proxies have been disposed, it can be known that there are no external
 *  references to the contained components remaining.
 *
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/21 12:45:11 $
 * @since 4.2
 */
interface ReferenceProxy
{
}
