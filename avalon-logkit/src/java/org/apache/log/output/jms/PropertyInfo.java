/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.jms;

/**
 * A descriptor for each message property.
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 */
public class PropertyInfo 
{
    ///Name of property
    private final String   m_name;

    ///Type/Source of property
    private final int      m_type;

    ///Auxilliary parameters (ie constant or sub-format)
    private final String   m_aux; //may be null

    public PropertyInfo( final String name, final int type, final String aux )
    {
        m_type = type;
        m_aux = aux;
        m_name = name;
    }

    public String getName()
    {
        return m_name;
    }

    public int getType()
    {
        return m_type;
    }

    public String getAux()
    {
        return m_aux;
    }
}

