/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.format;

/**
 * This formatter extends PatternFormatter so that
 * CascadingExceptions are formatted with all nested exceptions.
 *
 * @deprecated Use <code>org.apache.avalon.framework.logger.AvalonFormatter</code>
 *             instead of this one.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class AvalonFormatter
    extends org.apache.avalon.framework.logger.AvalonFormatter
{
    public AvalonFormatter()
    {
        super( "%{time} [%7.7{priority}] (%{category}): %{message}\\n%{throwable}" );
    }

    public AvalonFormatter( final String pattern )
    {
        super( pattern );
    }
}
