/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix;

/**
 * Class to exit phoenix. Call to shut down the server.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
public class Shutdown {
    public Shutdown() {
        (new Start()).dispose();
    }
}
