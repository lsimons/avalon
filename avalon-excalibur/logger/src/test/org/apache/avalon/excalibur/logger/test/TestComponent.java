/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger.test;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.logger.Logger;

/**
 * TestComponent.
 *
 * @author <a href="mailto:giacomo@apache,org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 02:34:16 $
 */
public interface TestComponent
    extends Component
{
    String ROLE = TestComponent.class.getName();

    void test( Logger defaultLogger, String message );
}
