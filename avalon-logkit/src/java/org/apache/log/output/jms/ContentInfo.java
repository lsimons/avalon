/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.jms;

/**
 * A descriptor for message content.
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 */
public class ContentInfo
{
    ///Type/Source of content
    private final int      m_type;

    ///Auxilliary parameters (ie constant or sub-format)
    private final String   m_aux; //may be null

    public ContentInfo( final int type, final String aux )
    {
        m_type = type;
        m_aux = aux;
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

