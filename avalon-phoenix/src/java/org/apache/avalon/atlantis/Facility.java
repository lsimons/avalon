/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.atlantis;

import org.apache.avalon.component.Component;

/**
 * A Facility is a horizontal cut through the kernel. 
 * Unlike Components which offer a Service/Content interface, Facilitys 
 * are used to facilitate the non-Service/Form interface or life-cycle orientated
 * methods of Components. See documentation for a clearer explanation.
 *
 * Example Facilities would be 
 * <ul>
 *   <li>ConfigurationRepository that stores configuration data for components</li>
 *   <li>ThreadFacility that allows components to run in threads</li>
 *   <li>ContextUtility that builds context information for components</li>
 *   <li>ExportFacility that exports components to external users (perhaps via RMI)</li>
 *   <li>NamingFacility that binds compoents to a name in a directory</li>
 *   <li>ManagementFacility that manages components via JMX</li>
 * </ul>
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Facility 
    extends Component
{
}
