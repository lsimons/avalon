package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.framework.component.Component;

/**
 * PackageRepository
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2001/11/17 11:40:42 $
 */
public interface PackageRepository
    extends org.apache.avalon.excalibur.extension.PackageRepository, Component
{
    String ROLE = "org.apache.avalon.phoenix.components.classloader.PackageRepository";
}
