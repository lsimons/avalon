/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.example;

import org.apache.avalon.framework.component.Component;

/**
 * This example application creates a component which registers several
 *  Instruments for the example.
 *
 * Note, this code ignores exceptions to keep the code simple.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/29 16:05:19 $
 * @since 4.1
 */
public interface ExampleInstrumentable
    extends Component
{
    String ROLE = ExampleInstrumentable.class.getName();

    /**
     * Example action method.
     */
    void doAction();
}

