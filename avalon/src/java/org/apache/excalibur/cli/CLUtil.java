/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.cli;

/**
 * CLUtil offers basic utility operations for use both internal and external to package.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class CLUtil
{
    protected static int              MAX_DESCRIPTION_COLUMN_LENGTH = 60;

    /**
     * Format options into StringBuffer and return.
     *
     * @param options[] the option descriptors
     * @return the formatted description/help for options
     */
    public static StringBuffer describeOptions( final CLOptionDescriptor[] options )
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < options.length; i++)
        {
            final char ch = (char) options[i].getId();
            final String name = options[i].getName();
            String description = options[i].getDescription();
            boolean needComma = false;

            sb.append('\t');

            if( Character.isLetter(ch) )
            {
                sb.append("-");
                sb.append(ch);
                needComma = true;
            }

            if (null != name)
            {
                if( needComma ) sb.append(", ");

                sb.append("--");
                sb.append(name);
                sb.append('\n');
            }

            if( null != description )
            {
                while( description.length() > MAX_DESCRIPTION_COLUMN_LENGTH )
                {
                    final String descriptionPart =
                        description.substring( 0, MAX_DESCRIPTION_COLUMN_LENGTH );
                    description =
                        description.substring( MAX_DESCRIPTION_COLUMN_LENGTH );
                    sb.append( "\t\t" );
                    sb.append( descriptionPart );
                    sb.append( '\n' );
                }

                sb.append( "\t\t" );
                sb.append( description );
                sb.append( '\n' );
            }
        }
        return sb;
    }

    /**
     * Private Constructor so that no instance can ever be created.
     *
     */
    private CLUtil()
    {
    }
}
