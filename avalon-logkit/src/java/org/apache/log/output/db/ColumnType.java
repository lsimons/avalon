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
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ColumnType
{
    //x-db sequencer...
    public final static int     SEQUENCE        =  0;

    public final static int     STATIC          =  1;
    public final static int     CATEGORY        =  2;
    public final static int     CONTEXT         =  3;
    public final static int     MESSAGE         =  4;
    public final static int     TIME            =  5;
    public final static int     RELATIVE_TIME   =  6;
    public final static int     THROWABLE       =  7;
    public final static int     PRIORITY        =  8;
    public final static int     HOSTNAME        =  9;
    //public final static int     IPADDRESS       = 10;

    /**
     * The maximum value used for TYPEs. Subclasses can define their own TYPEs
     * starting at <code>MAX_TYPE + 1</code>.
     */
    //public final static int     MAX_TYPE        = IPADDRESS;

    public final static String  SEQUENCE_STR   = "sequence";
    public final static String  STATIC_STR     = "static";
    public final static String  CATEGORY_STR   = "category";
    public final static String  CONTEXT_STR    = "context";
    public final static String  MESSAGE_STR    = "message";
    public final static String  TIME_STR       = "time";
    public final static String  RELATIVE_TIME_STR = "rtime";
    public final static String  THROWABLE_STR  = "throwable";
    public final static String  PRIORITY_STR   = "priority";
    public final static String  HOSTNAME_STR   = "hostname";
    //public final static String  IPADDRESS_STR  = "ipaddress";


    public static int getTypeIdFor( final String type )
    {
        if( type.equalsIgnoreCase( CATEGORY_STR ) ) return CATEGORY;
        else if( type.equalsIgnoreCase( STATIC_STR ) ) return STATIC;
        else if( type.equalsIgnoreCase( CONTEXT_STR ) ) return CONTEXT;
        else if( type.equalsIgnoreCase( MESSAGE_STR ) ) return MESSAGE;
        else if( type.equalsIgnoreCase( PRIORITY_STR ) ) return PRIORITY;
        else if( type.equalsIgnoreCase( TIME_STR ) ) return TIME;
        else if( type.equalsIgnoreCase( RELATIVE_TIME_STR ) ) return RELATIVE_TIME;
        //else if( type.equalsIgnoreCase( IPADDRESS_STR ) ) return IPADDRESS;
        else if( type.equalsIgnoreCase( HOSTNAME_STR ) ) return HOSTNAME;
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

