/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.activity;

/**
 * The Suspendable interface is used when a component
 * will need to temporarily halt execution of a component.
 * The execution may be halted so that you can reconfigure/
 * recompose/recontextualize component.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Suspendable
{
    /**
     * Suspends the component.
     */
    void suspend();

    /**
     * Resumes the component.
     */
    void resume();
}
