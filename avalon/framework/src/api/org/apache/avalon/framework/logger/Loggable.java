/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.logger;

import org.apache.log.Logger;

/**
 * Interface through which to provide Loggers.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Loggable
{
    void setLogger( Logger logger );
}
