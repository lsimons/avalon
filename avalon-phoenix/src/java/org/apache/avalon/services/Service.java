/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.services;

/**
 * This is a marker interface that all Services must implement.
 * A Service is a behavioural contract that an object implements.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @deprecated This is deprecated in favour of directly extending the interface in phoenix package
 */
public interface Service 
    extends org.apache.phoenix.Service
{
}
