/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.metagenerate;

/**
 * Abstract Helper
 * @author Paul Hammant
 */
public abstract class AbstractHelper
{
    /**
     * Replace a test with another in a string
     * @param source The string to be changed.
     * @param term The term to replace.
     * @param replacement To replace with.
     * @return The resulting string.
     */
    protected String replaceString(final String source, String term, String replacement)
    {
        String retval = source;
        int ix = retval.indexOf(term);
        if (ix != -1)
        {
            retval =
                    retval.substring(0, ix)
                    + replacement
                    + retval.substring(ix + term.length(), retval.length());
        }
        return retval;
    }
}
