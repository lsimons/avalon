/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import org.apache.avalon.atlantis.SystemManager;

/**
 * This facility is responsible for managing phoenix instance. 
 * This includes managing embeddor, deployer and kernel.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface PhoenixManager
    extends SystemManager
{
}
