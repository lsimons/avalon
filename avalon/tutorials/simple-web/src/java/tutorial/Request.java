/*
 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.
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
 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.
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
package tutorial;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a parser for an inbound HTTP connection.  The parser uses regular
 * expressions to examine the action line and host header line.  All other headers
 * and text are ignored.
 *
 * @author <a href="mailto:timothy.bennett@gxs.com">Timothy Bennett</a>
 */
public class Request {

  static class Action {
    private String m_name;
    static final Action GET = new Action("GET");
    static final Action POST = new Action("POST");
    static final Action PUT = new Action("PUT");
    static final Action HEAD = new Action("HEAD");

    private Action(String name) {
      m_name = name;
    }

    public String toString() {
      return m_name;
    }

    static Action parse(String s) throws IllegalArgumentException {
      if (s.equals("GET")) {
        return GET;
      }
      if (s.equals("POST")) {
        return POST;
      }
      if (s.equals("PUT")) {
        return PUT;
      }
      if (s.equals("HEAD")) {
        return HEAD;
      }
      throw new IllegalArgumentException(s);
    }
  }

  private Action m_action;
  private String m_version;
  private URI m_uri;

  private Request(Action action, String version, URI uri) {
    setAction(action);
    setVersion(version);
    setUri(uri);
  }

  private static Charset ascii = Charset.forName("US-ASCII");

  private static Pattern requestPattern = Pattern.compile(
      "\\A([A-Z]+) +([^ ]+) +HTTP/([0-9\\.]+)$"
      + ".*^Host: ([^ ]+)$.*\r\n\r\n", Pattern.MULTILINE | Pattern.DOTALL);

  public static Request parse(ByteBuffer bb) throws MalformedRequestException {
    CharBuffer cb = ascii.decode(bb);

    Matcher m = requestPattern.matcher(cb);
    if (!m.matches()) {
      throw new MalformedRequestException();
    }

    Action a;
    try {
      a = Action.parse(m.group(1));
    }
    catch (IllegalArgumentException e) {
      throw new MalformedRequestException(e);
    }

    URI u;
    try {
      u = new URI("http://" + m.group(4) + m.group(2));
    }
    catch (URISyntaxException e) {
      throw new MalformedRequestException(e);
    }

    return new Request(a, m.group(3), u);
  }

  public Action getAction() {
    return m_action;
  }

  public String getVersion() {
    return m_version;
  }

  public URI getUri() {
    return m_uri;
  }

  public boolean isGet() {
    if (m_action.toString().equals("GET")) {
      return true;
    }
    else {
      return false;
    }
  }

  private void setAction(Action action) {
    m_action = action;
  }

  private void setVersion(String version) {
    m_version = version;
  }

  private void setUri(URI uri) {
    m_uri = uri;
  }
}
