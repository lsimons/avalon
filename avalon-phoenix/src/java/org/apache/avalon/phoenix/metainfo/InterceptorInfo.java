/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metainfo;

/**
 * This class describes block invocation interceptor.
 * 
 * @author <a href="mailto:igorfie at yahoo.com">Igor Fedorenko</a>
 * @version $Revision: 1.1.2.1 $ $Date: 2002/10/15 22:14:06 $
 */
public class InterceptorInfo {

    private final String m_classname;

    public InterceptorInfo( final String classname )
    {
        m_classname = classname;
    }

    public String getClassname()
    {
        return m_classname;
    }
}
