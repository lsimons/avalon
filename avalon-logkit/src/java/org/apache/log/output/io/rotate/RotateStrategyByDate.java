/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.log.output.io.rotate;

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Rotation stragety based on SimpleDateFormat.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @version $Revision: 1.2 $ $Date: 2002/02/21 12:17:34 $
 */
public class RotateStrategyByDate
    implements RotateStrategy
{
    private SimpleDateFormat m_format;
    private Date m_date;
    private String m_current;

    public RotateStrategyByDate( final String pattern )
    {
        m_format = new SimpleDateFormat( pattern );
        m_date = new Date();
        m_current = m_format.format( m_date );
    }

    public void reset()
    {
        m_current = m_format.format( m_date );
    }

    public boolean isRotationNeeded( final String data, final File file )
    {
        m_date.setTime( System.currentTimeMillis() );
        if ( m_current.equals( m_format.format( m_date ) ) )
        {
            return false;
        }
        return true;
    }
}