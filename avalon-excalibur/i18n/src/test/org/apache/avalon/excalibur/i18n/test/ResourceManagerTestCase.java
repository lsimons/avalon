/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.i18n.test;

import java.util.MissingResourceException;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import junit.framework.TestCase;

/**
 * TestCase for ResourceManager.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class ResourceManagerTestCase
    extends TestCase
{
    public ResourceManagerTestCase( final String name )
    {
        super( name );
    }

    public void testClassResources()
    {
        try
        {
            final Resources resources =
                ResourceManager.getClassResources( getClass() );

            resources.getBundle();
        }
        catch ( final MissingResourceException mre )
        {
            fail( "Unable to find class resource for class " + getClass() );
        }
    }

    public void testPackageResources()
    {
        try
        {
            final Resources resources =
                ResourceManager.getPackageResources( getClass() );

            resources.getBundle();
        }
        catch ( final MissingResourceException mre )
        {
            fail( "Unable to find package resources for class " + getClass() );
        }
    }
}
