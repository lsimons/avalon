/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.configuration;

/**
 * <code>Persistable</code> is a interface encapsulating the ability for a
 * Component to expose its current configuration status.  This allows for a
 * Component that is able to change it's configuration state during run-time,
 * and save the results to persistant storage.
 *
 * <p>
 * The contract surrounding the <code>Persistable</code> interface is that
 * it should only be called by the Component's parent.  It may be called any
 * time the configuration state is to be retrieved from the Component.  If a
 * <code>Persistable</code> component contains non-persistable components, the
 * <code>Persistable</code> component must store the original
 * <code>Configuration</code> used to configure the non-persistable component.
 * This way, a system can be restored to its full working state later.
 * </p>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Persistable extends Configurable
{
    Configuration persist();
}