/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.framework.component.Component;

/**
 * PackageRepository
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2001/11/24 10:33:09 $
 */
public interface PackageRepository
    extends org.apache.avalon.excalibur.extension.PackageRepository, Component
{
    String ROLE = "org.apache.avalon.phoenix.components.classloader.PackageRepository";
}
