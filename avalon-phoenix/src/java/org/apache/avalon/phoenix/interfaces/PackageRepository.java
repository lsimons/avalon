/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

/**
 * PackageRepository
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.7 $ $Date: 2002/05/10 09:13:41 $
 */
public interface PackageRepository
    extends org.apache.avalon.excalibur.extension.PackageRepository
{
    String ROLE = PackageRepository.class.getName();
}
