/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.logger;

/**
 * Facade for loggers.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Logger
{
    void debug( String message );
    void debug( String message, Throwable throwable );
    boolean isDebugEnabled();

    void info( String message );
    void info( String message, Throwable throwable );
    boolean isInfoEnabled();

    void warn( String message );
    void warn( String message, Throwable throwable );
    boolean isWarnEnabled();

    void error( String message );
    void error( String message, Throwable throwable );
    boolean isErrorEnabled();

    void fatalError( String message );
    void fatalError( String message, Throwable throwable );
    boolean isFatalErrorEnabled();

    Logger getChildLogger( String name );
}
