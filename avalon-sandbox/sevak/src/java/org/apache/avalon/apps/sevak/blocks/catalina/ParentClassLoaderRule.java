/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.catalina;

import org.apache.catalina.Container;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;

import org.xml.sax.Attributes;

/**
 * A Parent Classloader Rule for Catalina
 *
 * @author  Daniel Krieg<dkrieg@kc.rr.com>
 * @version $Revision: 1.1 $ $Date: 2002/09/29 11:38:42 $
 */
public class ParentClassLoaderRule extends Rule
{
    private ClassLoader m_parentClassLoader = null;

    /**
     * Construct a ParentClassLoaderRule
     * @param digester The Digester
     * @param parentClassLoader The parent ClassLoader
     */
    public ParentClassLoaderRule(Digester digester, ClassLoader parentClassLoader)
    {
        super(digester);
        this.m_parentClassLoader = parentClassLoader;
    }

    /**
     * Begin operation
     * @param attributes the attributes
     * @throws Exception if an Exception
     */
    public void begin(Attributes attributes) throws Exception
    {
        Container top = (Container) digester.peek();
        top.setParentClassLoader(m_parentClassLoader);
    }
}
