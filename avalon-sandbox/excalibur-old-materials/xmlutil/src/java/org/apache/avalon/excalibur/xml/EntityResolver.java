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
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/07/07 05:22:00 $
 */
public interface EntityResolver
    extends Component, org.xml.sax.EntityResolver
{
    String ROLE = EntityResolver.class.getName();
}
