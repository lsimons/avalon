/*
 * Created on Nov 10, 2003
 *
 * Copyright Bali Automation Sdn Bhd, Malaysia. All rights reserved.
 */
package org.apache.avalon.ide.repository.tools.compliance;

import org.apache.avalon.ide.repository.Compliance;

/**
 * @author niclas
 */
public class GenericCompliance implements Compliance
{
    private String m_Usage;
    
    public GenericCompliance( String usage )
    {
        m_Usage = usage;
    }
    
    /* (non-Javadoc)
     * @see org.apache.avalon.repository.Compliance#isCompatibleWith(java.lang.String)
     */
    public boolean isCompatibleWith(String usage)
    {
        return m_Usage.equals( usage );
    }

}
