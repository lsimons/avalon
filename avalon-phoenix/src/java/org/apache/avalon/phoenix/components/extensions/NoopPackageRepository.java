/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.extensions;

import org.apache.avalon.excalibur.extension.Extension;
import org.apache.avalon.excalibur.extension.OptionalPackage;
import org.apache.avalon.phoenix.interfaces.PackageRepository;

/**
 * A Noop PackageRepository that can't provide any extensions. 
 * This is for use in certain environments (ala Servlets) that
 * require apps to be be self-contained.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2001/11/24 10:34:59 $
 */
public class NoopPackageRepository
    extends PackageRepository
{
    /**
     * Return all the <code>OptionalPackage</code>s that satisfy specified
     * <code>Extension</code>. 
     *
     * @param extension Description of the extension that needs to be provided by 
     *                  optional packages
     * @see #getOptionalPackage()
     * @see OptionalPackage
     * @see Extension
     */
    public OptionalPackage[] getOptionalPackages( final Extension extension )
    {
        return new OptionalPackage[ 0 ];
    }
}
