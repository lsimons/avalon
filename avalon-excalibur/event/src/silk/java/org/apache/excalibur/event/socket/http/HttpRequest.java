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
 * This interface represents a single HTTP client request.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface HttpRequest
{
    /** request code corresponding to a GET request. */
    int REQUEST_GET = 0;
    /** request code corresponding to a POST request. */
    int REQUEST_POST = 1;

    /** version code corresponding to HTTP/0.9. */
    int HTTPVER_09 = 0;
    /** version code corresponding to HTTP/1.0. */
    int HTTPVER_10 = 1;
    /** version code corresponding to HTTP/1.1. */
    int HTTPVER_11 = 2;

    /**
     * Return the code corresponding to the request. Each 
     * code has one of the REQUEST_* values from this class.
     * @since Sep 30, 2002
     * 
     * @return int
     *  The code corresponding to the request
     */
    int getRequestType();
    
    /**
     * Returns the requested URL.
     * @since Sep 30, 2002
     * 
     * @return String 
     *  The requested url.
     */
    String getURL();

    /**
     * Return the code corresponding to the HTTP version. 
     * Each code has one of the HTTPVER_* values from this 
     * class.
     * @since Sep 30, 2002
     * 
     * @return int
     *  The http version for this request
     */
    int getHttpVersion();

    /**
     * Return the corresponding HTTP connection.
     * @since Sep 30, 2002
     * 
     * @return HttpConnection
     *  The corresponding HTTP connection.
     */
    HttpConnection getConnection();

    /**
     * Return the header line corresponding to the given key.
     * For example, to get the 'User-Agent' field from the 
     * header, use <tt>getHeader("User-Agent")</tt>.
     * @since Sep 30, 2002
     * 
     * @param key
     *  The key of the header.
     * @return String
     *  The header line corresponding to the given key or
     *  <code>null</code> if it does not exist.
     */
    String getHeader(String key);

    /**
     * Return an Iterator of keys in the queryMap string, 
     * if any.
     * @since Sep 30, 2002
     * 
     * @return Iterator
     *  An Iterator of keys in the queryMap string, if any.
     */
    Iterator getParameterKeys();

    /**
     * Return the value associated with the given queryMap key.
     * If a key as more than one value then only the first value
     * will be returned.
     * @since Sep 30, 2002
     * 
     * @param key
     *  The key for the parameter
     * @return String
     *  The value associated with the given queryMap key
     */
    String getParameter(String key);

    /**
     * Return the set of values associated with the given 
     * queryMap key.
     * @since Sep 30, 2002
     * 
     * @param key
     *  The key for the parameter
     * @return String[]
     *  The values associated with the given queryMap key
     */
    String[] getParameters(String key);

    /**
     * Returns the payload of this request.
     * @since Sep 30, 2002
     * 
     * @return Buffer
     *  The payload of this request.
     */
    Buffer getContent();
}
