/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.store.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.coyotegulch.jisp.KeyObject;

/**
 * Wrapper class for String Keys to be compatible with the
 * Jisp KeyObject.
 *
 * @author <a href="mailto:g-froehlich@gmx.de">Gerhard Froehlich</a>
 * @version CVS $Id: JispStringKey.java,v 1.3 2003/02/25 16:28:23 bloritsch Exp $
 */
final class JispStringKey extends KeyObject {
    final static long serialVersionUID = -6894793231339165076L;
    private String m_Key;

    /**
     *  Constructor for the JispStringKey object
     */
    public JispStringKey() {
        m_Key = new String("");
    }

    /**
     *  Constructor for the JispStringKey object
     *
     * @param keyValue the Value of the Key as String
     */
    public JispStringKey(String keyValue) {
        m_Key = keyValue;
    }

    /**
     * Compares two String Keys
     *
     * @param key the KeyObject to be compared
     * @return 0 if equal, 1 if greater, -1 if less
     */

    public int compareTo(KeyObject key) {
        if (key instanceof JispStringKey) {
            int comp = m_Key.trim().compareTo(((JispStringKey) key).m_Key.trim());
            if (comp == 0) {
                return KEY_EQUAL;
            } else {
                if (comp < 0) {
                    return KEY_LESS;
                } else {
                    return KEY_MORE;
                }
            }
        } else {
            return KEY_ERROR;
        }
    }

    /**
     *  Composes a null Kewy
     *
     * @return a null Key
     */
    public KeyObject makeNullKey() {
        return new JispStringKey();
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
        throws IOException {
        String outKey;
        outKey = new String(m_Key);
        out.writeUTF(outKey);
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
        throws IOException, ClassNotFoundException {
        m_Key = in.readUTF();
    }

    /**
     * Overrides the toString() method
     *
     * @return the Key as String
     */
    public String toString() {
        return m_Key;
    }
}



