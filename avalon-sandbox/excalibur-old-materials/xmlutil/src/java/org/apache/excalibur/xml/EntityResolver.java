/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml;

/**
 * A component that uses catalogs for resolving Entities.
 *
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/10/31 02:48:16 $
 */
public interface EntityResolver
    extends org.xml.sax.EntityResolver
{
    String ROLE = EntityResolver.class.getName();
}
