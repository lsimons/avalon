/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import org.apache.avalon.atlantis.Application;
import org.apache.avalon.camelot.Entry;

/**
 * This is the DefaultServerKernel for phoenix.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultServerKernel 
    extends AbstractServerKernel
{
    public final static String BANNER = Constants.SOFTWARE + " " + Constants.VERSION;

    public void init() 
        throws Exception 
    {
        System.out.println();
        System.out.println( BANNER );

        super.init();
    }

    protected Application newApplication( final String name, final Entry entry )
        throws Exception
    {
        //It is here where you could return new EASServerApplication()
        //if you wanted to host multiple different types of apps
        return new DefaultServerApplication();
    }
}
