/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.avalon.http.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;

import org.apache.avalon.http.HttpRequestHandler;
import org.apache.avalon.http.HttpRequestHandlerException;

/**
 *
 * Provides an abstract class to be subclassed to create
 * an HTTP handlers suitable for a Web site. A subclass of
 * <code>AbstractHttpRequestHandler</code> must override at least 
 * one method, usually one of these:
 *
 * <ul>
 * <li> <code>doGet</code>, if the component supports HTTP GET requests
 * <li> <code>doPost</code>, for HTTP POST requests
 * <li> <code>doPut</code>, for HTTP PUT requests
 * <li> <code>doDelete</code>, for HTTP DELETE requests
 * </ul>
 *
 * <p>There's almost no reason to override the <code>service</code>
 * method. <code>service</code> handles standard HTTP
 * requests by dispatching them to the handler methods
 * for each HTTP request type (the <code>do</code><i>XXX</i>
 * methods listed above).
 *
 * <p>Likewise, there's almost no reason to override the 
 * <code>doOptions</code> and <code>doTrace</code> methods.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version        $Revision: 1.1 $
 *
 */
public abstract class AbstractHttpRequestHandler 
    implements HttpRequestHandler, java.io.Serializable
{
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";

    private static final String HEADER_IFMODSINCE = "If-Modified-Since";
    private static final String HEADER_LASTMOD = "Last-Modified";
    
    private static final String LSTRING_FILE =
        "javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings =
        ResourceBundle.getBundle(LSTRING_FILE);
   
    /**
     * Does nothing, because this is an abstract class.
     * 
     */
    public AbstractHttpRequestHandler() { }
    
    /**
     * Called by the server (via the <code>service</code> method) to
     * allow a component to handle a GET request. 
     *
     * <p>Overriding this method to support a GET request also
     * automatically supports an HTTP HEAD request. A HEAD
     * request is a GET request that returns no body in the
     * response, only the request header fields.</p>
     *
     * <p>When overriding this method, read the request data,
     * write the response headers, get the response's writer or 
     * output stream object, and finally, write the response data.
     * It's best to include content type and encoding. When using
     * a <code>PrintWriter</code> object to return the response,
     * set the content type before accessing the
     * <code>PrintWriter</code> object.
     *
     * <p>The component container must write the headers before
     * committing the response, because in HTTP the headers must be sent
     * before the response body.
     *
     * <p>Where possible, set the Content-Length header (with the
     * {@link javax.servlet.ServletResponse#setContentLength} method),
     * to allow the component container to use a persistent connection 
     * to return its response to the client, improving performance.
     * The content length is automatically set if the entire response fits
     * inside the response buffer.
     * 
     * <p>The GET method should be safe, that is, without
     * any side effects for which users are held responsible.
     * For example, most form queries have no side effects.
     * If a client request is intended to change stored data,
     * the request should use some other HTTP method.
     *
     * <p>The GET method should also be idempotent, meaning
     * that it can be safely repeated. Sometimes making a
     * method safe also makes it idempotent. For example, 
     * repeating queries is both safe and idempotent, but
     * buying a product online or modifying data is neither
     * safe nor idempotent. 
     *
     * <p>If the request is incorrectly formatted, <code>doGet</code>
     * returns an HTTP "Bad Request" message.
     * 
     *
     * @param request        an {@link HttpServletRequest} object that
     *                        contains the request the client has made
     *                        of the component
     *
     * @param response        an {@link HttpServletResponse} object that
     *                        contains the response the component sends
     *                        to the client
     * 
     * @exception IOException        if an input or output error is 
     *                                detected when the component handles
     *                                the GET request
     *
     * @exception ServletException        if the request for the GET
     *                                        could not be handled
     *
     * 
     * @see javax.servlet.ServletResponse#setContentType
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws HttpRequestHandlerException, IOException
    {
        String protocol = request.getProtocol();
        String msg = lStrings.getString("http.method_get_not_supported");
        if (protocol.endsWith("1.1")) 
        {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } 
        else 
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    /**
     *
     * Returns the time the <code>HttpServletRequest</code>
     * object was last modified,
     * in milliseconds since midnight January 1, 1970 GMT.
     * If the time is unknown, this method returns a negative
     * number (the default).
     *
     * <p>Servlets that support HTTP GET requests and can quickly determine
     * their last modification time should override this method.
     * This makes browser and proxy caches work more effectively,
     * reducing the load on server and network resources.
     *
     * @param request the <code>HttpServletRequest</code> 
     *   object that is sent to the component
     *
     * @return a <code>long</code> integer specifying
     *   the time the <code>HttpServletRequest</code>
     *   object was last modified, in milliseconds
     *   since midnight, January 1, 1970 GMT, or
     *   -1 if the time is not known
     */
    protected long getLastModified(HttpServletRequest request ) 
    {
        return -1;
    }

    /**
     * <p>Receives an HTTP HEAD request from the protected
     * <code>service</code> method and handles the
     * request.
     * The client sends a HEAD request when it wants
     * to see only the headers of a response, such as
     * Content-Type or Content-Length. The HTTP HEAD
     * method counts the output bytes in the response
     * to set the Content-Length header accurately.
     *
     * <p>If you override this method, you can avoid computing
     * the response body and just set the response headers
     * directly to improve performance. Make sure that the
     * <code>doHead</code> method you write is both safe
     * and idempotent (that is, protects itself from being
     * called multiple times for one HTTP HEAD request).
     *
     * <p>If the HTTP HEAD request is incorrectly formatted,
     * <code>doHead</code> returns an HTTP "Bad Request"
     * message.
     *
     *
     * @param request the request object that is passed
     *   to the component
     * @param response the response object that the component
     *   uses to return the headers to the clien
     * @exception IOException if an input or output error occurs
     * @exception HttpRequestHandlerException if the request for the HEAD
     *   could not be handled
     */

    protected void doHead(HttpServletRequest request, HttpServletResponse response )
        throws HttpRequestHandlerException, IOException
    {
        NoBodyResponse resp = new NoBodyResponse(response);
        doGet(request, resp);
        resp.setContentLength();
    }

    /**
     * Called by the server (via the <code>service</code> method)
     * to allow a component to handle a POST request.
     *
     * The HTTP POST method allows the client to send
     * data of unlimited length to the Web server a single time
     * and is useful when posting information such as
     * credit card numbers.
     *
     * <p>When overriding this method, read the request data,
     * write the response headers, get the response's writer or output
     * stream object, and finally, write the response data. It's best 
     * to include content type and encoding. When using a
     * <code>PrintWriter</code> object to return the response, set the 
     * content type before accessing the <code>PrintWriter</code> object. 
     *
     * <p>The component container must write the headers before committing the
     * response, because in HTTP the headers must be sent before the 
     * response body.
     *
     * <p>Where possible, set the Content-Length header (with the
     * {@link javax.servlet.ServletResponse#setContentLength} method),
     * to allow the component container to use a persistent connection 
     * to return its response to the client, improving performance.
     * The content length is automatically set if the entire response fits
     * inside the response buffer.  
     *
     * <p>When using HTTP 1.1 chunked encoding (which means that the response
     * has a Transfer-Encoding header), do not set the Content-Length header. 
     *
     * <p>This method does not need to be either safe or idempotent.
     * Operations requested through POST can have side effects for
     * which the user can be held accountable, for example, 
     * updating stored data or buying items online.
     *
     * <p>If the HTTP POST request is incorrectly formatted,
     * <code>doPost</code> returns an HTTP "Bad Request" message.
     *
     *
     * @param request an {@link HttpServletRequest} object that
     *   contains the request the client has made
     *   of the component
     * @param response an {@link HttpServletResponse} object that
     *   contains the response the component sends
     *   to the client
     * @exception IOException if an input or output error is 
     *   detected when the component handles
     *   the request
     * @exception HttpRequestHandlerException if the request for the POST
     *   could not be handled
     * @see javax.servlet.ServletOutputStream
     * @see javax.servlet.ServletResponse#setContentType
     */
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
        throws HttpRequestHandlerException, IOException
    {
        String protocol = request.getProtocol();
        String msg = lStrings.getString("http.method_post_not_supported");
        if (protocol.endsWith("1.1")) 
        {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        }
        else 
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    /**
     * Called by the server (via the <code>service</code> method)
     * to allow a component to handle a PUT request.
     *
     * The PUT operation allows a client to 
     * place a file on the server and is similar to 
     * sending a file by FTP.
     *
     * <p>When overriding this method, leave intact
     * any content headers sent with the request (including
     * Content-Length, Content-Type, Content-Transfer-Encoding,
     * Content-Encoding, Content-Base, Content-Language, Content-Location,
     * Content-MD5, and Content-Range). If your method cannot
     * handle a content header, it must issue an error message
     * (HTTP 501 - Not Implemented) and discard the request.
     * For more information on HTTP 1.1, see RFC 2068
     * <a href="http://info.internet.isi.edu:80/in-notes/rfc/files/rfc2068.txt"></a>.
     *
     * <p>This method does not need to be either safe or idempotent.
     * Operations that <code>doPut</code> performs can have side
     * effects for which the user can be held accountable. When using
     * this method, it may be useful to save a copy of the
     * affected URL in temporary storage.
     *
     * <p>If the HTTP PUT request is incorrectly formatted,
     * <code>doPut</code> returns an HTTP "Bad Request" message.
     *
     *
     * @param request        the {@link HttpServletRequest} object that
     *                        contains the request the client made of
     *                        the component
     *
     * @param response        the {@link HttpServletResponse} object that
     *                        contains the response the component returns
     *                        to the client
     *
     * @exception IOException        if an input or output error occurs
     *                                while the component is handling the
     *                                PUT request
     *
     * @exception HttpRequestHandlerException        if the request for the PUT
     *                                        cannot be handled
     *
     */
  
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
        throws HttpRequestHandlerException, IOException
    {
        String protocol = request.getProtocol();
        String msg = lStrings.getString("http.method_put_not_supported");
        if (protocol.endsWith("1.1")) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    /**
     * 
     * Called by the server (via the <code>service</code> method)
     * to allow a component to handle a DELETE request.
     *
     * The DELETE operation allows a client to remove a document
     * or Web page from the server.
     * 
     * <p>This method does not need to be either safe
     * or idempotent. Operations requested through
     * DELETE can have side effects for which users
     * can be held accountable. When using
     * this method, it may be useful to save a copy of the
     * affected URL in temporary storage.
     *
     * <p>If the HTTP DELETE request is incorrectly formatted,
     * <code>doDelete</code> returns an HTTP "Bad Request"
     * message.
     *
     *
     * @param request        the {@link HttpServletRequest} object that
     *                        contains the request the client made of
     *                        the component
     *
     *
     * @param response        the {@link HttpServletResponse} object that
     *                        contains the response the component returns
     *                        to the client                                
     *
     *
     * @exception IOException        if an input or output error occurs
     *                                while the component is handling the
     *                                DELETE request
     *
     * @exception HttpRequestHandlerException        if the request for the
     *                                        DELETE cannot be handled
     *
     */
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response)
        throws HttpRequestHandlerException, IOException
    {
        String protocol = request.getProtocol();
        String msg = lStrings.getString("http.method_delete_not_supported");
        if (protocol.endsWith("1.1")) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    private Method[] getAllDeclaredMethods(Class c) {
        if (c.getName().equals("javax.servlet.http.HttpServlet"))
            return null;
        
        int j=0;
        Method[] parentMethods = getAllDeclaredMethods(c.getSuperclass());
        Method[] thisMethods = c.getDeclaredMethods();
        
        if (parentMethods!=null) {
            Method[] allMethods =
                new Method[parentMethods.length + thisMethods.length];
            for (int i=0; i<parentMethods.length; i++) {
                allMethods[i]=parentMethods[i];
                j=i;
            }
            j++;
            for (int i=j; i<thisMethods.length+j; i++) {
                allMethods[i] = thisMethods[i-j];
            }
            return allMethods;
        }
        return thisMethods;
    }

    /**
     * Called by the server (via the <code>service</code> method)
     * to allow a component to handle a OPTIONS request.
     *
     * The OPTIONS request determines which HTTP methods 
     * the server supports and
     * returns an appropriate header. For example, if a component
     * overrides <code>doGet</code>, this method returns the
     * following header:
     *
     * <p><code>Allow: GET, HEAD, TRACE, OPTIONS</code>
     *
     * <p>There's no need to override this method unless the
     * component implements new HTTP methods, beyond those 
     * implemented by HTTP 1.1.
     *
     * @param request        the {@link HttpServletRequest} object that
     *                        contains the request the client made of
     *                        the component
     *
     *
     * @param response        the {@link HttpServletResponse} object that
     *                        contains the response the component returns
     *                        to the client                                
     *
     *
     * @exception IOException        if an input or output error occurs
     *                                while the component is handling the
     *                                OPTIONS request
     *
     * @exception HttpRequestHandlerException        if the request for the
     *                                        OPTIONS cannot be handled
     *
     */
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
        throws HttpRequestHandlerException, IOException
    {
        Method[] methods = getAllDeclaredMethods(this.getClass());
        
        boolean ALLOW_GET = false;
        boolean ALLOW_HEAD = false;
        boolean ALLOW_POST = false;
        boolean ALLOW_PUT = false;
        boolean ALLOW_DELETE = false;
        boolean ALLOW_TRACE = true;
        boolean ALLOW_OPTIONS = true;
        
        for (int i=0; i<methods.length; i++) {
            Method m = methods[i];
            
            if (m.getName().equals("doGet")) {
                ALLOW_GET = true;
                ALLOW_HEAD = true;
            }
            if (m.getName().equals("doPost")) 
                ALLOW_POST = true;
            if (m.getName().equals("doPut"))
                ALLOW_PUT = true;
            if (m.getName().equals("doDelete"))
                ALLOW_DELETE = true;
            
        }
        
        String allow = null;
        if (ALLOW_GET)
            if (allow==null) allow=METHOD_GET;
        if (ALLOW_HEAD)
            if (allow==null) allow=METHOD_HEAD;
            else allow += ", " + METHOD_HEAD;
        if (ALLOW_POST)
            if (allow==null) allow=METHOD_POST;
            else allow += ", " + METHOD_POST;
        if (ALLOW_PUT)
            if (allow==null) allow=METHOD_PUT;
            else allow += ", " + METHOD_PUT;
        if (ALLOW_DELETE)
            if (allow==null) allow=METHOD_DELETE;
            else allow += ", " + METHOD_DELETE;
        if (ALLOW_TRACE)
            if (allow==null) allow=METHOD_TRACE;
            else allow += ", " + METHOD_TRACE;
        if (ALLOW_OPTIONS)
            if (allow==null) allow=METHOD_OPTIONS;
            else allow += ", " + METHOD_OPTIONS;
        
        response.setHeader("Allow", allow);
    }
        
    /**
     * Called by the server (via the <code>service</code> method)
     * to allow a component to handle a TRACE request.
     *
     * A TRACE returns the headers sent with the TRACE
     * request to the client, so that they can be used in
     * debugging. There's no need to override this method. 
     *
     *
     *
     * @param request        the {@link HttpServletRequest} object that
     *                        contains the request the client made of
     *                        the component
     * @param response        the {@link HttpServletResponse} object that
     *                        contains the response the component returns
     *                        to the client                                
     * @exception IOException        if an input or output error occurs
     *                                while the component is handling the
     *                                TRACE request
     * @exception HttpRequestHandlerException        if the request for the
     *                                        TRACE cannot be handled
     */
    protected void doTrace(HttpServletRequest request, HttpServletResponse response) 
        throws HttpRequestHandlerException, IOException
    {
        
        int responseLength;
        
        String CRLF = "\r\n";
        String responseString = "TRACE "+ request.getRequestURI()+
            " " + request.getProtocol();
        
        Enumeration reqHeaderEnum = request.getHeaderNames();
        
        while( reqHeaderEnum.hasMoreElements() ) {
            String headerName = (String)reqHeaderEnum.nextElement();
            responseString += CRLF + headerName + ": " +
                request.getHeader(headerName); 
        }
        
        responseString += CRLF;
        
        responseLength = responseString.length();
        
        response.setContentType("message/http");
        response.setContentLength(responseLength);
        ServletOutputStream out = response.getOutputStream();
        out.print(responseString);        
        out.close();
        return;
    }                

    /**
     * Receives standard HTTP requests from the public
     * <code>service</code> method and dispatches
     * them to the <code>do</code><i>XXX</i> methods defined in 
     * this class. This method is an HTTP-specific version of the 
     * {@link javax.servlet.Servlet#service} method. There's no
     * need to override this method.
     *
     * @param request        the {@link HttpServletRequest} object that
     *                        contains the request the client made of
     *                        the component
     * @param response        the {@link HttpServletResponse} object that
     *                        contains the response the component returns
     *                        to the client                                
     * @exception IOException        if an input or output error occurs
     *                                while the component is handling the
     *                                TRACE request
     * @exception HttpRequestHandlerException        if the request for the
     *                                        TRACE cannot be handled
     * @see                                 javax.servlet.Servlet#service
     */
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws HttpRequestHandlerException, IOException
    {
        String method = request.getMethod();

        if (method.equals(METHOD_GET)) {
            long lastModified = getLastModified(request);
            if (lastModified == -1) {
                // component doesn't support if-modified-since, no reason
                // to go through further expensive logic
                doGet(request, response);
            } else {
                long ifModifiedSince = request.getDateHeader(HEADER_IFMODSINCE);
                if (ifModifiedSince < (lastModified / 1000 * 1000)) {
                    // If the component mod time is later, call doGet()
                    // Round down to the nearest second for a proper compare
                    // A ifModifiedSince of -1 will always be less
                    maybeSetLastModified(response, lastModified);
                    doGet(request, response);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                }
            }

        } else if (method.equals(METHOD_HEAD)) {
            long lastModified = getLastModified(request);
            maybeSetLastModified(response, lastModified);
            doHead(request, response);

        } else if (method.equals(METHOD_POST)) {
            doPost(request, response);
            
        } else if (method.equals(METHOD_PUT)) {
            doPut(request, response);        
            
        } else if (method.equals(METHOD_DELETE)) {
            doDelete(request, response);
            
        } else if (method.equals(METHOD_OPTIONS)) {
            doOptions(request,response);
            
        } else if (method.equals(METHOD_TRACE)) {
            doTrace(request,response);
            
        } else {
            //
            // Note that this means NO component supports whatever
            // method was requested, anywhere on this server.
            //

            String errMsg = lStrings.getString("http.method_not_implemented");
            Object[] errArgs = new Object[1];
            errArgs[0] = method;
            errMsg = MessageFormat.format(errMsg, errArgs);
            
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
        }
    }
    
    /*
     * Sets the Last-Modified entity header field, if it has not
     * already been set and if the value is meaningful.  Called before
     * doGet, to ensure that headers are set before response data is
     * written.  A subclass might have set this header already, so we
     * check.
     */
    private void maybeSetLastModified(HttpServletResponse response,
                                      long lastModified) {
        if (response.containsHeader(HEADER_LASTMOD))
            return;
        if (lastModified >= 0)
            response.setDateHeader(HEADER_LASTMOD, lastModified);
    }
   
    /**
     * Dispatches client requests to the protected
     * <code>service</code> method. There's no need to
     * override this method.
     *
     * 
     * @param request        the {@link HttpServletRequest} object that
     *                        contains the request the client made of
     *                        the component
     *
     *
     * @param response        the {@link HttpServletResponse} object that
     *                        contains the response the component returns
     *                        to the client                                
     *
     *
     * @exception IOException        if an input or output error occurs
     *                                while the component is handling the
     *                                TRACE request
     *
     * @exception HttpRequestHandlerException        if the request for the
     *                                        TRACE cannot be handled
     *
     * 
     * @see javax.servlet.Servlet#service
     *
     */
    public void service(ServletRequest request, ServletResponse response )
        throws HttpRequestHandlerException, IOException
    {
        HttpServletRequest httpRequest;
        HttpServletResponse httpResponse;
        
        try 
        {
            httpRequest = (HttpServletRequest) request;
            httpResponse = (HttpServletResponse) response;
        }
        catch (ClassCastException e) 
        {
            throw new HttpRequestHandlerException("non-HTTP request or response");
        }
        service( httpRequest, httpResponse );
    }
}

/*
 * A response that includes no body, for use in (dumb) "HEAD" support.
 * This just swallows that body, counting the bytes in order to set
 * the content length appropriately.  All other methods delegate directly
 * to the HTTP Servlet Response object used to construct this one.
 */
// file private
class NoBodyResponse implements HttpServletResponse {
    private HttpServletResponse                response;
    private NoBodyOutputStream                noBody;
    private PrintWriter                        writer;
    private boolean                        didSetContentLength;

    // file private
    NoBodyResponse(HttpServletResponse r) {
        response = r;
        noBody = new NoBodyOutputStream();
    }

    // file private
    void setContentLength() {
        if (!didSetContentLength)
          response.setContentLength(noBody.getContentLength());
    }


    // SERVLET RESPONSE interface methods

    public void setContentLength(int len) {
        response.setContentLength(len);
        didSetContentLength = true;
    }

    public void setContentType(String type)
      { response.setContentType(type); }

    public ServletOutputStream getOutputStream() throws IOException
      { return noBody; }

    public String getCharacterEncoding()
        { return response.getCharacterEncoding(); }

    public PrintWriter getWriter() throws UnsupportedEncodingException
    {
        if (writer == null) {
            OutputStreamWriter        w;

            w = new OutputStreamWriter(noBody, getCharacterEncoding());
            writer = new PrintWriter(w);
        }
        return writer;
    }

    public void setBufferSize(int size) throws IllegalStateException
      { response.setBufferSize(size); }

    public int getBufferSize()
      { return response.getBufferSize(); }

    public void reset() throws IllegalStateException
      { response.reset(); }
      
      public void resetBuffer() throws IllegalStateException
      { response.resetBuffer(); }

    public boolean isCommitted()
      { return response.isCommitted(); }

    public void flushBuffer() throws IOException
      { response.flushBuffer(); }

    public void setLocale(Locale loc)
      { response.setLocale(loc); }

    public Locale getLocale()
      { return response.getLocale(); }


    // HTTP SERVLET RESPONSE interface methods

    public void addCookie(Cookie cookie)
      { response.addCookie(cookie); }

    public boolean containsHeader(String name)
      { return response.containsHeader(name); }

    /** @deprecated */
    public void setStatus(int sc, String sm)
      { response.setStatus(sc, sm); }

    public void setStatus(int sc)
      { response.setStatus(sc); }

    public void setHeader(String name, String value)
      { response.setHeader(name, value); }

    public void setIntHeader(String name, int value)
      { response.setIntHeader(name, value); }

    public void setDateHeader(String name, long date)
      { response.setDateHeader(name, date); }

    public void sendError(int sc, String msg) throws IOException
      { response.sendError(sc, msg); }

    public void sendError(int sc) throws IOException
      { response.sendError(sc); }

    public void sendRedirect(String location) throws IOException
      { response.sendRedirect(location); }
    
    public String encodeURL(String url) 
      { return response.encodeURL(url); }

    public String encodeRedirectURL(String url)
      { return response.encodeRedirectURL(url); }
      
    public void addHeader(String name, String value)
      { response.addHeader(name, value); }
      
    public void addDateHeader(String name, long value)
      { response.addDateHeader(name, value); }
      
    public void addIntHeader(String name, int value)
      { response.addIntHeader(name, value); }

    /**
     * @deprecated        As of Version 2.1, replaced by
     *                         {@link HttpServletResponse#encodeURL}.
     *
     */
    public String encodeUrl(String url) 
      { return this.encodeURL(url); }
      
    /**
     * @deprecated        As of Version 2.1, replaced by
     *                        {@link HttpServletResponse#encodeRedirectURL}.
     *
     */     
    public String encodeRedirectUrl(String url)
      { return this.encodeRedirectURL(url); }
}


/*
 * Servlet output stream that gobbles up all its data.
 */
// file private
class NoBodyOutputStream extends ServletOutputStream {

    private static final String LSTRING_FILE =
        "javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings =
        ResourceBundle.getBundle(LSTRING_FILE);

    private int                contentLength = 0;

    // file private
    NoBodyOutputStream() {}

    // file private
    int getContentLength() {
        return contentLength;
    }

    public void write(int b) {
        contentLength++;
    }

    public void write(byte buf[], int offset, int len)
        throws IOException
    {
        if (len >= 0) {
            contentLength += len;
        } else {
            // XXX
            // isn't this really an IllegalArgumentException?
            
            String msg = lStrings.getString("err.io.negativelength");
            throw new IOException("negative length");
        }
    }
}
