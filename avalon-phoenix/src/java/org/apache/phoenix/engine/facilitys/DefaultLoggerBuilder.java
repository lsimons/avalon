/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilitys;

import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.atlantis.Facility;
import org.apache.avalon.camelot.Entry;
import org.apache.avalon.camelot.pipeline.LoggerBuilder;
import org.apache.log.Logger;

/**
 * Component responsible for building logger for entry.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultLoggerBuilder
    extends AbstractLoggable
    implements LoggerBuilder, Facility
{
    public Logger createLogger( final String name, final Entry entry )
    {
        return  getLogger().getChildLogger( name );
    }
}
