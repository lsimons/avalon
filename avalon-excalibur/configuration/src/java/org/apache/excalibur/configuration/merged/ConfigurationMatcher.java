/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.configuration.merged;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * Used interally by the ConfigurationMerger to see if a configuration matches for merge
 * purposes.
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public interface ConfigurationMatcher
{
    boolean isMatch( final Configuration c );
}
