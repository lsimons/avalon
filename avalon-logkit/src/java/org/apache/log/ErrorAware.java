/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

/**
 * Interface implemented by components that wish to 
 * delegate ErrorHandling to an ErrorHandler.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ErrorAware
{
    /**
     * Provide component with ErrorHandler.
     *
     * @param errorHandler the errorHandler
     */
    void setErrorHandler( ErrorHandler errorHandler );
}
