/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.verifier;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Verifier
    extends Component
{
    String ROLE = "org.apache.avalon.phoenix.tools.verifier.Verifier";

    void verifySar( SarMetaData sar, ClassLoader classLoader )
        throws VerifyException;
}
