/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
*/

package org.apache.excalibur.store.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.coyotegulch.jisp.KeyObject;

/**
 * Wrapper class for Keys to be compatible with the
 * Jisp KeyObject.
 * 
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Id: JispKey.java,v 1.1 2003/11/09 12:47:17 leosimons Exp $
 */
public final class JispKey extends KeyObject 
{
    final static long serialVersionUID = -1216913992804571313L;

    protected Object m_Key;

    static protected JispKey NULL_KEY = new JispKey("");
    
    public JispKey() {
        this("");
    }
    
    /**
     *  Constructor for the JispKey object
     *
     * @param keyValue the key
     */
    public JispKey(Object keyValue) 
    {
        m_Key = keyValue;
    }

    /**
     * Compares two Keys
     *
     * @param key the KeyObject to be compared
     * @return 0 if equal, 1 if greater, -1 if less
     */

    public int compareTo(KeyObject key) 
    {
        if (key instanceof JispKey) 
        {
            final JispKey other = (JispKey)key;
            if ( other.m_Key.hashCode() == m_Key.hashCode() ) 
            {
                if ( m_Key == other.m_Key || m_Key.equals(other.m_Key) ) 
                {
                    return KEY_EQUAL;
                }
                // we have the same hashcode, but different keys
                // this is usually an error condition, but we deal
                // with it anway
                // if they would have the same classname, they
                // can only have the same hashCode if they are equal:
                int comp = m_Key.getClass().getName().compareTo(other.m_Key.getClass().getName());
                if ( comp < 0 ) 
                {
                    return KEY_LESS;
                }
                return KEY_MORE;
            } 
            else 
            {
                if ( m_Key.hashCode() < other.m_Key.hashCode() ) 
                {
                    return KEY_LESS;
                }
                return KEY_MORE;
            }
        } 
        else 
        {
            return KEY_ERROR;
        }
    }

    /**
     *  Composes a null Kewy
     *
     * @return a null Key
     */
    public KeyObject makeNullKey() 
    {
        return NULL_KEY;
    }

    /**
     * The object implements the writeExternal method to save its contents
     * by calling the methods of DataOutput for its primitive values or
     * calling the writeObject method of ObjectOutput for objects, strings,
     * and arrays.
     *
     * @param out the stream to write the object to
     * @exception IOException
     */
    public void writeExternal(ObjectOutput out)
    throws IOException 
    {
        out.writeObject(m_Key);
    }

    /**
     * The object implements the readExternal method to restore its contents
     * by calling the methods of DataInput for primitive types and readObject
     * for objects, strings and arrays. The readExternal method must read the
     * values in the same sequence and with the same types as were written by writeExternal.
     *
     * @param in the stream to read data from in order to restore the object
     * @exception IOException
     * @exception ClassNotFoundException
     */
    public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException 
    {
        m_Key = in.readObject();
    }

    /**
     * Return the real key
     */
    public Object getKey() 
    {
        return m_Key;
    }
}