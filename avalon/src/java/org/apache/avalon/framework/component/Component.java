/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.component;

/**
 * This interface identifies classes that can be used as <code>Components</code>
 * by a <code>Composable</code>.
 * <br />
 *
 * The contract surrounding the <code>Component</code> is that it is
 * used, but not a user.  When a class implements this interface, it
 * is stating that other entities may use that class.  As part of the
 * contract with the system, a <code>Component</code> must always
 * declare an empty constructor.
 * <br />
 *
 * A <code>Component</code> is the basic building block of the Avalon.
 * When a class implements this interface, it allows itself to be
 * managed by a <code>ComponentManager</code> and used by an outside
 * element called a <code>Composable</code>.  The <code>Composable</code>
 * must know what type of <code>Component</code> it is accessing, so
 * it will re-cast the <code>Component</code> into the type it needs.
 * <br />
 *
 * In order for a <code>Component</code> to be useful you must either
 * extend this interface, or implement this interface in conjunction
 * with one that actually has methods.  The new interface is the contract
 * with the <code>Composable</code> that this is a particular type of
 * component, and as such it can perform those functions on that type
 * of component.
 * <br />
 *
 * For example, we want a component that performs a logging function
 * so we extend the <code>Component</code> to be a <code>LoggingComponent</code>.
 *
 * <pre>
 *   interface LoggingComponent extends Component {
 *       log(String message);
 *   }
 * </pre>
 *
 * Now all <code>Composable</code>s that want to use this type of component,
 * will re-cast the <code>Component</code> into a <code>LoggingComponent</code>
 * and the <code>Composable</code> will be able to use the <code>log</code>
 * method.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:bloritsch@apache.org>Berin Loritsch</a>
 */
public interface Component
{
}
