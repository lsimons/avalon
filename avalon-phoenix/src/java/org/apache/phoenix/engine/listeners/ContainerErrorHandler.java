/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.listeners;

/**
 * This interface abstracts handling of container errors.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ContainerErrorHandler 
{
    void errorAddingComponent( String name, Throwable cause );
    void errorLoadingComponent( String name, Throwable cause );
    void errorStartingComponent( String name, Throwable cause );
    void errorStoppingComponent( String name, Throwable cause );
    void errorUnloadingComponent( String name, Throwable cause );
    void errorRemovingComponent( String name, Throwable cause );
}
