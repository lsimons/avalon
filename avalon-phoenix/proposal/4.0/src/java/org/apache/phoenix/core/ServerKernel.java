/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.core;

import org.apache.framework.logger.Loggable;
import org.apache.framework.context.Contextualizable;
import org.apache.phoenix.applications.ServerApplication;

/**
 * The ServerKernel is the core of the Phoenix system.
 * The kernel is responsible for orchestrating low level services
 * such as loading, configuring and destroying blocks. It also
 * gives access to basic facilities such as scheduling sub-systems,
 * protected execution contexts, naming and directory services etc.
 *
 * Note that no facilities are available until after the Kernel has been
 * contextualized, configured and initialized.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ServerKernel
    extends Loggable, Kernel, Contextualizable, ServerApplication
    // and thus extends Application, Runnable, Initializable, Startable,
    // Stoppable, Disposable, Container, Component
{
}
