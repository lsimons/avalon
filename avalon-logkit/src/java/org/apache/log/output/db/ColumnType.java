/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.db;

/**
 * A class to hold all constants for ColumnTypes.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class ColumnType
{
    //x-db sequencer...
    public static final int SEQUENCE = 0;

    public static final int STATIC = 1;
    public static final int CATEGORY = 2;
    public static final int CONTEXT = 3;
    public static final int MESSAGE = 4;
    public static final int TIME = 5;
    public static final int RELATIVE_TIME = 6;
    public static final int THROWABLE = 7;
    public static final int PRIORITY = 8;
    public static final int HOSTNAME = 9;
    //public static final int     IPADDRESS       = 10;

    /**
     * The maximum value used for TYPEs. Subclasses can define their own TYPEs
     * starting at <code>MAX_TYPE + 1</code>.
     */
    //public static final int     MAX_TYPE        = IPADDRESS;

    public static final String SEQUENCE_STR = "sequence";
    public static final String STATIC_STR = "static";
    public static final String CATEGORY_STR = "category";
    public static final String CONTEXT_STR = "context";
    public static final String MESSAGE_STR = "message";
    public static final String TIME_STR = "time";
    public static final String RELATIVE_TIME_STR = "rtime";
    public static final String THROWABLE_STR = "throwable";
    public static final String PRIORITY_STR = "priority";
    public static final String HOSTNAME_STR = "hostname";
    //public static final String  IPADDRESS_STR  = "ipaddress";


    public static int getTypeIdFor( final String type )
    {
        if( type.equalsIgnoreCase( CATEGORY_STR ) )
            return CATEGORY;
        else if( type.equalsIgnoreCase( STATIC_STR ) )
            return STATIC;
        else if( type.equalsIgnoreCase( CONTEXT_STR ) )
            return CONTEXT;
        else if( type.equalsIgnoreCase( MESSAGE_STR ) )
            return MESSAGE;
        else if( type.equalsIgnoreCase( PRIORITY_STR ) )
            return PRIORITY;
        else if( type.equalsIgnoreCase( TIME_STR ) )
            return TIME;
        else if( type.equalsIgnoreCase( RELATIVE_TIME_STR ) )
            return RELATIVE_TIME;
        //else if( type.equalsIgnoreCase( IPADDRESS_STR ) ) return IPADDRESS;
        else if( type.equalsIgnoreCase( HOSTNAME_STR ) )
            return HOSTNAME;
        else if( type.equalsIgnoreCase( THROWABLE_STR ) )
        {
            return THROWABLE;
        }
        else
        {
            throw new IllegalArgumentException( "Unknown Type " + type );
        }
    }
}

