/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.xcommander;

/**
 * This empty interface identifies classes which can be called using XCommander.
 * Results returned from methods which are called on XCommands (and any public
 * method can be called) should always be objects whose toString() method returns
 * valid xml (though the xml declaration may be omitted).
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public interface XCommand
{
}
