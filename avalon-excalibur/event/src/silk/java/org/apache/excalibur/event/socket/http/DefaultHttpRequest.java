/*
 * Copyright (c) 2001 by Matt Welsh and The Regents of the University of 
 * California. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice and the following
 * two paragraphs appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */

/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.http;


import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.excalibur.event.socket.Buffer;

/**
 * This class represents a single HTTP client request.
 * 
 * @version $Revision: 1.1 $
 * Author: Matt Welsh <mdw@cs.berkeley.edu>
 * Code to parse HTTP queryMap strings by Eric Wagner.
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultHttpRequest implements HttpRequest
{
    //private Vector m_rawHeader;
    //public long m_timestamp;
    //private int m_userClass = -2;
    
    /** The type of request performed */    
    private int m_request;
    /** request code corresponding to a GET request. */
    public static final int REQUEST_GET = 0;
    /** request code corresponding to a POST request. */
    public static final int REQUEST_POST = 1;

    /** The version of http requested being used */
    private int m_httpVersion;
    /** version code corresponding to HTTP/0.9. */
    public static final int HTTPVER_09 = 0;
    /** version code corresponding to HTTP/1.0. */
    public static final int HTTPVER_10 = 1;
    /** version code corresponding to HTTP/1.1. */
    public static final int HTTPVER_11 = 2;

    /** Default value for a queryMap key. */
    public static final String QUERY_KEY_SET = "true";

    /** The http Connection that this request comes from */
    private HttpConnection m_httpConnection;
    /** The requested url */
    private String m_url;
    /** A map of header name value pairs */
    private Map m_header;
    /** A map of query parameters */
    private Map m_queryMap;
    /** The request's payload */
    private final Buffer m_payload;

    //---------------------------- DefaultHttpRequest constructors
    /**
     * Creates an DefaultHttpRequest from the given connection, request 
     * string, URL, HTTP version, header, and payload.
     * @since Sep 30, 2002
     * 
     * @param header
     *  A map of header name value pairs
     * @param httpConnection
     *  The http Connection that this request comes from
     * @param httpVersion
     *  The version of http requested being used
     * @param payload
     *  The request's payload
     * @param request
     *  The type of request performed
     * @param url
     *  The requested url
     */
    DefaultHttpRequest(
        HttpConnection httpConnection,
        String request,
        String url,
        int httpVersion,
        Map header,
        Buffer payload)
        throws IOException
    {
        m_payload = payload;
        m_httpConnection = httpConnection;
        m_httpVersion = httpVersion;
        m_header = header;
        
        m_url = url;
        
        createQueryMap(url);

        m_request = REQUEST_GET;
        if (request.equalsIgnoreCase("post"))
        {
            m_request = REQUEST_POST;
        }
    }

    //--------------------------- overridden methods in Object
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        String s = "DefaultHttpRequest[";
        switch (m_request)
        {
            case REQUEST_GET :
                s += "GET ";
                break;
            case REQUEST_POST :
                s += "POST ";
                break;
            default :
                s += "??? ";
                break;
        }
        s += m_url + " ";
        switch (m_httpVersion)
        {
            case HTTPVER_09 :
                s += "HTTP/0.9";
                break;
            case HTTPVER_10 :
                s += "HTTP/1.0";
                break;
            case HTTPVER_11 :
                s += "HTTP/1.1";
                break;
        }

        if (m_header != null)
        {
            final Iterator e = m_header.keySet().iterator();
            while (e.hasNext())
            {
                String key = (String) e.next();
                String val = (String) m_header.get(key);
                s += "\n\t" + key + " " + val;
            }
        }
        s += "]";
        return s;
    }

    //--------------------------- HttpRequest implementation
    /**
     * @see HttpRequest#getRequestType()
     */
    public int getRequestType()
    {
        return m_request;
    }
    
    /**
     * @see HttpRequest#getURL()
     */
    public String getURL()
    {
        return m_url;
    }

    /**
     * @see HttpRequest#getHttpVersion()
     */
    public int getHttpVersion()
    {
        return m_httpVersion;
    }

    /**
     * @see HttpRequest#getConnection()
     */
    public HttpConnection getConnection()
    {
        return m_httpConnection;
    }

    /**
     * @see HttpRequest#getHeader(String)
     */
    public String getHeader(String key)
    {
        if (m_header == null)
        {
            return null;
        }
        return (String) m_header.get(key);
    }

    /**
     * @see HttpRequest#getParameterKeys()
     */
    public Iterator getParameterKeys()
    {
        if (m_queryMap == null)
        {
            return null;
        }
        return m_queryMap.keySet().iterator();
    }

    /**
     * @see HttpRequest#getParameter(String)
     */
    public String getParameter(String key)
    {
        if (m_queryMap == null)
            return null;
        Object val = m_queryMap.get(key);
        if (val == null)
            return null;
        else if (val instanceof String)
            return (String) val;
        else
        {
            Vector vec = (Vector) val;
            return (String) vec.elementAt(0);
        }
    }

    /**
     * @see HttpRequest#getParameters(String)
     */
    public String[] getParameters(String key)
    {
        if (m_queryMap == null)
            return null;
        Object val = m_queryMap.get(key);
        if (val == null)
            return null;
        else if (val instanceof String)
        {
            String ret[] = new String[1];
            ret[0] = (String) val;
            return ret;
        }
        else
        {
            Vector vec = (Vector) val;
            Object ret[] = vec.toArray();
            String sret[] = new String[ret.length];
            for (int i = 0; i < ret.length; i++)
            {
                sret[i] = (String) ret[i];
            }
            return sret;
        }
    }

    /**
     * @see HttpRequest#getContent()
     */
    public Buffer getContent()
    {
        return m_payload;
    }

    //---------------------------- DefaultHttpRequest specific implementation
    /**
     * Indicates whether this request requires a header to 
     * be sent in the response (that is, whether this is HTTP/1.0 
     * or later).
     * @since Sep 30, 2002
     * 
     * @return boolean 
     *  Whether this request requires a header to be sent in 
     *  the response
     */
    boolean headerNeeded()
    {
        if (getHttpVersion() > DefaultHttpRequest.HTTPVER_09)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Helper method that decodes special characters in URLs 
     * @since Sep 30, 2002
     * 
     * @param encoded
     *  The encoded url
     * @return String
     *  The decoded url
     */
    private static String decodeURL(String encoded)
    {
        final StringBuffer out = new StringBuffer(encoded.length());
        int i = 0;
        int j = 0;

        while (i < encoded.length())
        {
            char ch = encoded.charAt(i);
            i++;
            if (ch == '+')
                ch = ' ';
            else if (ch == '%')
            {
                try
                {
                    ch =
                        (char) Integer.parseInt(
                            encoded.substring(i, i + 2),
                            16);
                    i += 2;
                }
                catch (StringIndexOutOfBoundsException se)
                {
                    // If nothing's there, just ignore it
                }
            }
            out.append(ch);
            j++;
        }
        return out.toString();
    }

    /**
     * Helper method that creates the query map from
     * the url string.
     * @since Sep 30, 2002
     * 
     * @param url
     *  The url with the parameters attached
     */
    protected void createQueryMap(String url)
    {
        // Check to see if there is a queryMap string
        final int question = url.indexOf('?');
        if (question != -1)
        {
            m_queryMap = new HashMap();
            
            m_url = url.substring(0, question);
            
            final StringTokenizer st =
                new StringTokenizer(url.substring(question + 1), ";&");
            while (st.hasMoreTokens())
            {
                final String nameValue = decodeURL(st.nextToken());
                final int equals = nameValue.indexOf('=');
        
                if (equals == -1)
                {
                    addParameter(nameValue, QUERY_KEY_SET);
                }
                else
                {
                    final String key = nameValue.substring(0, equals);
                    final String value = nameValue.substring(equals + 1);
                    addParameter(key, value);
                }
            }
        }
    }
    
    /**
     * Allows to add a key and value to the query Map set.
     * @since Sep 30, 2002
     * 
     * @param key
     *  The key under which to add the value
     * @param val
     *  The value to be added
     */
    private void addParameter(String key, String val)
    {
        Object oldval = m_queryMap.get(key);
        if (oldval == null)
        {
            m_queryMap.put(key, val);
        }
        else if (oldval instanceof String)
        {
            m_queryMap.remove(key);
            Vector vec = new Vector(2);
            vec.addElement(oldval);
            vec.addElement(val);
            m_queryMap.put(key, vec);
        }
        else
        {
            Vector vec = (Vector) oldval;
            vec.addElement(val);
        }
    }


//    /* For ClassQueueElementIF */
//
//    public int getRequestClass()
//    {
//        if (m_userClass == -2)
//        {
//            String s = getHeader("User-Class");
//            if (s != null)
//            {
//                try
//                {
//                    m_userClass = Integer.parseInt(s);
//                }
//                catch (NumberFormatException e)
//                {
//                    m_userClass = -1;
//                }
//            }
//            else
//            {
//                m_userClass = -1;
//            }
//        }
//        return m_userClass;
//    }
//
//    public void setRequestClass(int theclass)
//    {
//        this.m_userClass = theclass;
//    }
}
