/*
 * 1.0    1999/07/30 Niclas Hedhman     First Public Release
 *
 * Copyright (c) 1996-1999 Bali Automation. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * BALI AUTOMATION MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE 
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING 
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BALI AUTOMATION
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A 
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS 
 * DERIVATIVES.
 */
package org.apache.metro.facilities.reflector;

import java.util.EventObject;
import java.io.Serializable;

public final class ReflectionEvent extends EventObject
    implements Serializable
{
    static final long serialVersionUID = 1L;
    
    private final Object m_Object;
    private final Object m_Value;
    private final String m_Method;
    
    public ReflectionEvent( Object source, 
                            Object object, 
                            Object value, 
                            String method )
    {
        super(source);
        m_Object = object;
        m_Value = value;
        m_Method = method;
    }
    
    public Object getObject()
    {
        return m_Object;
    }
    
    public Object getValue()
    {
        return m_Value;
    }
    
    public String getMethod()
    {
        return m_Method;
    }
}
