/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.fortress.impl;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * A class holding metadata about a component handler.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/02/07 16:08:11 $
 */
public class ComponentHandlerMetaData
{
    private final String m_name;
    private final String m_classname;
    private final Configuration m_configuration;
    private final boolean m_lazyActivation;

   /**
    * Creation of a new impl handler meta data instance.
    * @param name the handler name
    * @param classname the handler classname
    * @param configuration the handler configuration
    * @param laxyActivation the activation policy
    */
    public ComponentHandlerMetaData( final String name,
                                     final String classname,
                                     final Configuration configuration,
                                     final boolean lazyActivation )
    {
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == classname )
        {
            throw new NullPointerException( "classname" );
        }
        if( null == configuration )
        {
            throw new NullPointerException( "configuration" );
        }

        m_name = name;
        m_classname = classname;
        m_configuration = configuration;
        m_lazyActivation = lazyActivation;
    }

   /**
    * Returns the handler name
    * @return the handler name
    */
    public String getName()
    {
        return m_name;
    }

   /**
    * Returns the handler classname
    * @return the classname
    */
    public String getClassname()
    {
        return m_classname;
    }

   /**
    * Returns the handler configuration
    * @return the configuration
    */
    public Configuration getConfiguration()
    {
        return m_configuration;
    }

   /**
    * Returns the handler activation policy
    * @return the activation policy
    */
    public boolean isLazyActivation()
    {
        return m_lazyActivation;
    }
}
