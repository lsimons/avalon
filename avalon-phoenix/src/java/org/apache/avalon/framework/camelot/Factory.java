/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.camelot;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.container.Locator;

/**
 * This is the component that creates the components.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Factory
    extends Component
{
    String ROLE = "org.apache.avalon.framework.camelot.Factory";

    /**
     * Create a component whos position is indicated by locator.
     *
     * @param locator the locator indicating the component location
     * @return the component
     * @exception FactoryException if an error occurs
     */
    Object create( Locator locator )
        throws FactoryException;

    /**
     * Create a component whos position is indicated by locator.
     * Make sure it is of the correct type.
     *
     * @param locator the locator indicating the component location
     * @param clazz the expected type of component
     * @return the component
     * @exception FactoryException if an error occurs
     */
    Object create( Locator locator, Class clazz )
        throws FactoryException;
}
