/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml;

import org.apache.avalon.framework.component.Component;

/**
 * A component that uses catalogs for resolving Entities.
 *
 * @deprecated Moved to org.apache.excalibur.xml package. Removed dependency
 * on Component.
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/10/31 02:48:37 $
 */
public interface EntityResolver
    extends Component, org.xml.sax.EntityResolver
{
    String ROLE = EntityResolver.class.getName();
}
