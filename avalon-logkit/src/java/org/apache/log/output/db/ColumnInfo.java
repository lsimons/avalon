/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.db;

/**
 * A descriptor for each column stored in table.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class ColumnInfo
{
    ///Name of column
    private final String m_name;

    ///Type/Source of column
    private final int m_type;

    ///Auxilliary parameters (ie constant or sub-format)
    private final String m_aux; //may be null

    public ColumnInfo( final String name, final int type, final String aux )
    {
        m_name = name;
        m_type = type;
        m_aux = aux;
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

