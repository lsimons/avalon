package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.framework.component.Component;

/**
 * PackageRepository
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2001/11/19 12:21:30 $
 */
public interface PackageRepository
    extends org.apache.avalon.excalibur.extension.PackageRepository, Component
{
    String ROLE = "org.apache.avalon.phoenix.components.classloader.PackageRepository";
}
