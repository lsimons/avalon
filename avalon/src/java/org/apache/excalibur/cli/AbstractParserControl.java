/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.cli;

/**
 * Class to inherit from so when in future when new controls are added
 * clients will no have to implement them.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class AbstractParserControl
    implements ParserControl
{
    public boolean isFinished( int lastOptionCode )
    {
        return false;
    }
}
