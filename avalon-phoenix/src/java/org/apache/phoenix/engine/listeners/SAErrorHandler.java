/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.listeners;

import org.apache.phoenix.metainfo.ServiceDescriptor;

/**
 * This interface abstracts handling of container errors.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface SAErrorHandler 
{
    //On block add
    void missingDependency( String block, String missingDependency );
    void unknownDependency( String block, String unknownDependency );

    void unimplementedService( String block, ServiceDescriptor service );

    void startPhase( String phase );
    void endPhase( String phase );

    void startTraversingDependencies( String block, boolean isReverse );

    /**
     * Indicate that the last dependency on stack has dependencies that are being traversed.
     *
     * @param role the role of dependency
     * @param service the service offered by dependency
     */
    void traversingDependency( String role, String service );

    void endTraversingDependencies();

    void beginBlocksPhase( String block );
    void blockNotPreparedForPhase();
    void blockUsingClassLoader( ClassLoader classLoader );
    void serviceNotOffered( String dependency, ServiceDescriptor service );
    void endBlocksPhase();
}
