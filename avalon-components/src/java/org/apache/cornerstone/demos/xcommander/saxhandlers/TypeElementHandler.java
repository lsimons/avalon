/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.xcommander.saxhandlers;

import org.apache.cornerstone.demos.xcommander.ElementHandler;

/**
 * Classes that implement this interface can be subelements 
 * of &lt;constructor&gt;s and &lt;method&gt;s.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public interface TypeElementHandler 
    extends ElementHandler
{
    Class getTypeClass();
    Object getTypeValue();
}
