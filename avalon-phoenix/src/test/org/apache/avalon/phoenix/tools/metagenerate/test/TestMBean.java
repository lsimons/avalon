/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.metagenerate.test;

/**
 * Specifies methods to export via Management interface.
 *
 * @phoenix:mx-topic name="Greeting"
 *
 * @author  Huw Roberts <huw@mmlive.com>
 * @version 1.0
 */
public interface TestMBean
{
    /**
     * The greeting that is returned to each HTTP request
     *
     * @phoenix:mx-attribute
     */
    public void setGreeting( final String greeting );

    /**
     * Gets the greeting that is returned to each HTTP request
     *
     */
    String getGreeting();

    /**
     * Blah Blah
     * Blah Blah.
     *
     * @param parm1 parameter one
     * @param parm2 parameter two
     * @return some return thing
     * @phoenix:mx-operation
     */
    String someOperation( final String parm1, final String parm2 );

}