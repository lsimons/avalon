/*
 * Created on Nov 10, 2003
 *
 * Copyright Bali Automation Sdn Bhd, Malaysia. All rights reserved.
 */
package org.apache.avalon.ide.repository.tools.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.avalon.ide.repository.Version;

/**
 * @author niclas
 */
public class AvalonDateVersion implements Version
{
    static private final SimpleDateFormat m_Format;

    private Date m_Date;

    static {
        m_Format = new SimpleDateFormat("YYYYMMDD.HHmmss");
    }
    /** Version class for YYYYMMDD.hhmmss format.
     * 
     * @param ver Version string.
     */
    public AvalonDateVersion(String ver) throws ParseException
    {
        m_Date = m_Format.parse(ver);
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.repository.Version#toExternalName()
     */
    public String toExternalName()
    {
        return m_Format.format(m_Date);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        if (equals(o))
            return 0;
        if (!(o instanceof AvalonDateVersion))
            return -1;
        long t = m_Date.getTime();
        long tComp = ((AvalonDateVersion) o).m_Date.getTime();
        if (t < tComp)
            return -1;
        else
            return 1;
    }

    public boolean equals(Object o)
    {
        if (o == null)
            return false;
        if (!(o instanceof AvalonDateVersion))
            return false;
        long t = m_Date.getTime();
        long tComp = ((AvalonDateVersion) o).m_Date.getTime();
        return t == tComp;
    }

    public int hashCode()
    {
        return m_Date.hashCode();
    }

}
