/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.camelot;

import org.apache.avalon.framework.component.Component;

/**
 * This contains information relating to a component.
 * (ie BlockInfo, BeanInfo, MailetInfo etc).
 *
 * There is currently also two different sub-interfaces - Descriptor and Locator.
 * Descriptor describes static meta information about component while Locator
 * locates it in the system.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Info
    extends Component
{
}
