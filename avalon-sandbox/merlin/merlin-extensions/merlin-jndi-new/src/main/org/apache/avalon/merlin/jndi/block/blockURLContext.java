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

package org.apache.avalon.merlin.jndi.block;

import javax.naming.*;
import javax.naming.spi.*;
import java.util.*;
import org.apache.avalon.merlin.jndi.StandardContext;

public class blockURLContext implements Context 
{
    protected Hashtable m_environment = null;

   /**
    * Creation of a new instance.
    * @param environment the context environment parameter
    */
    protected blockURLContext( Hashtable environment ) 
    {
        m_environment = environment;
    }

   /**
    * Resolves 'name' into a target context with remaining name.
    * For example, with a JNDI URL "jndi://dnsname/rest_name",
    * this method resolves "jndi://dnsname/" to a target context,
    * and returns the target context with "rest_name".
    * The definition of "root URL" and how much of the URL to
    * consume is implementation specific.
    * If rename() is supported for a particular URL scheme,
    * getRootURLContext(), getURLPrefix(), and getURLSuffix() 
    * must be in sync wrt how URLs are parsed and returned.
    *
    * For the "block" URL, the root URL is "block:/".
    *
    * @param url the base url
    * @param environment the environment parameter
    * @return the root context
    */
    protected ResolveResult getRootURLContext(String url, Hashtable env) 
      throws NamingException 
    {

        if (!url.startsWith("block:/")) 
        {
            throw new IllegalArgumentException(url + " is not a block URL");
        }

        String objName = url.length() > 7 ? url.substring(7) : null;

        // Represent object name as empty or single-component composite name.
        CompositeName remaining = new CompositeName();
        if (objName != null) 
        {
            remaining.add(objName);
        }

        // Get handle to the static namespace that we use for testing.
        // In an actual implementation, this might be the root
        // namespace on a particular server.

        Context ctx = StandardContext.getStaticNamespace(env);

        return (new ResolveResult(ctx, remaining));
    }

    /**
      * Returns the suffix of the url. The result should be identical to
      * that of calling getRootURLContext().getRemainingName(), but
      * without the overhead of doing anything with the prefix like
      * creating a context.
      *<p>
      * This method returns a Name instead of a String because to give
      * the provider an opportunity to return a Name (for example,
      * for weakly separated naming systems like COS naming).
      *<p>
      * The default implementation uses skips 'prefix', calls
      * UrlUtil.decode() on it, and returns the result as a single component 
      * CompositeName.
      * Subclass should override if this is not appropriate.
      * This method is used only by rename().
      * If rename() is supported for a particular URL scheme,
      * getRootURLContext(), getURLPrefix(), and getURLSuffix() 
      * must be in sync wrt how URLs are parsed and returned.
      *<p>
      * For many URL schemes, this method is very similar to URL.getFile(),
      * except getFile() will return a leading slash in the
      * 2nd, 3rd, and 4th cases. For schemes like "ldap" and "iiop",
      * the leading slash must be skipped before the name is an acceptable
      * format for operation by the Context methods. For schemes that treat the
      * leading slash as significant (such as "file"), 
      * the subclass must override getURLSuffix() to get the correct behavior.
      * Remember, the behavior must match getRootURLContext().
      *
      * URL                              Suffix
      * block://host:port                        <empty string>
      * block://host:port/rest/of/name             rest/of/name
      * block:///rest/of/name                  rest/of/name
      * block:/rest/of/name                  rest/of/name
      * block:rest/of/name                  rest/of/name
      */
    protected Name getURLSuffix(String prefix, String url) throws NamingException 
    {

        String suffix = url.substring(prefix.length());
        if (suffix.length() == 0) 
        {
            return new CompositeName();
        }

        if (suffix.charAt(0) == '/') 
        {
            suffix = suffix.substring(1); // skip leading slash
        }

        // # todo
        // transform any URL-encoded characters into their Unicode char
        // representation

        return new CompositeName().add(suffix);
    }

    /**
      * Finds the prefix of a URL.
      * Default implementation looks for slashes and then extracts
      * prefixes using String.substring().
      * Subclass should override if this is not appropriate.
      * This method is used only by rename().
      * If rename() is supported for a particular URL scheme,
      * getRootURLContext(), getURLPrefix(), and getURLSuffix() 
      * must be in sync wrt how URLs are parsed and returned.
      *<p>
      * URL                              Prefix
      * block://host:port                        block://host:port
      * block://host:port/rest/of/name             block://host:port
      * block:///rest/of/name                  block://
      * block:/rest/of/name                  block:
      * block:rest/of/name                  block:
      */
    protected String getURLPrefix(String url) throws NamingException 
    {

        int start = url.indexOf(":");

        if (start < 0) 
        {
            throw new OperationNotSupportedException("Invalid URL: " + url);
        }
        ++start; // skip ':'

        if (url.startsWith("//", start)) 
        {
            start += 2;  // skip double slash
          
            // find last slash
            int posn = url.indexOf("/", start);
            if (posn >= 0) 
            {
                start = posn;
            } 
            else 
            {
                start = url.length();  // rest of URL
            }
        }

        // else 0 or 1 initial slashes; start is unchanged

        return url.substring(0, start);
    }

    /**
     * Determines whether two URLs are the same.
     * Default implementation uses String.equals().
     * Subclass should override if this is not appropriate.
     * This method is used by rename(). 
     */
    protected boolean urlEquals(String url1, String url2) 
    {
      return url1.equals(url2);
    }

    /**
     * Gets the context in which to continue the operation. This method
     * is called when this context is asked to process a multicomponent
     * Name in which the first component is a URL.
     * Treat the first component like a junction: resolve it and then use 
     * NamingManager.getContinuationContext() to get the target context in
     * which to operate on the remainder of the name (n.getSuffix(1)).
     */
    protected Context getContinuationContext(Name n) throws NamingException 
    {
        Object obj = lookup(n.get(0));
        CannotProceedException cpe = new CannotProceedException();
        cpe.setResolvedObj(obj);
        cpe.setEnvironment(m_environment);
        return NamingManager.getContinuationContext(cpe);
    }

    public Object lookup(String name) throws NamingException 
    {
        ResolveResult res = getRootURLContext(name, m_environment);
        Context ctx = (Context)res.getResolvedObj();
        try 
        {
            return ctx.lookup(res.getRemainingName() );
        } 
        finally 
        {
          ctx.close();
        }
    }

    public Object lookup(Name name) throws NamingException 
    {
        if (name.size() == 1) 
        {
            return lookup(name.get(0));
        } 
        else 
        {
            Context ctx = getContinuationContext(name);
            try 
            {
                return ctx.lookup(name.getSuffix(1));
            } 
            finally 
            {
                ctx.close();
            }
        }
    }

    public void bind(String name, Object obj) throws NamingException 
    {
        ResolveResult res = getRootURLContext(name, m_environment);
        Context ctx = (Context)res.getResolvedObj();
        try 
        {
            ctx.bind(res.getRemainingName(), obj);
        } 
        finally 
        {
            ctx.close();
        }
    }

    public void bind(Name name, Object obj) throws NamingException 
    {
        if (name.size() == 1) 
        {
            bind(name.get(0), obj);
        }
        else 
        {
            Context ctx = getContinuationContext(name);
            try 
            {
                ctx.bind(name.getSuffix(1), obj);
            } 
            finally 
            {
                ctx.close();
            }
        }
    }

    public void rebind(String name, Object obj) throws NamingException 
    {
        ResolveResult res = getRootURLContext(name, m_environment);
        Context ctx = (Context)res.getResolvedObj();
        try 
        {
            ctx.rebind(res.getRemainingName(), obj);
        } 
        finally 
        {
            ctx.close();
        }
    }

    public void rebind(Name name, Object obj) throws NamingException 
    {
        if (name.size() == 1) 
        {
            rebind(name.get(0), obj);
        } 
        else 
        {
            Context ctx = getContinuationContext(name);
            try 
            {
                ctx.rebind(name.getSuffix(1), obj);
            } 
            finally 
            {
                ctx.close();
            }
        }
    }

    public void unbind(String name) throws NamingException 
    {
        ResolveResult res = getRootURLContext(name, m_environment);
        Context ctx = (Context)res.getResolvedObj();
        try 
        {
            ctx.unbind(res.getRemainingName());
        } 
        finally 
        {
            ctx.close();
        }
    }

    public void unbind(Name name) throws NamingException 
    {
        if (name.size() == 1) 
        {
            unbind(name.get(0));
        } 
        else 
        {
            Context ctx = getContinuationContext(name);
            try 
            {
                ctx.unbind(name.getSuffix(1));
            } 
            finally 
            {
                ctx.close();
            }
        }
    }

    public void rename(String oldName, String newName) throws NamingException 
    {
        String oldPrefix = getURLPrefix(oldName);
        String newPrefix = getURLPrefix(newName);
        if (!urlEquals(oldPrefix, newPrefix)) 
        {
            throw new OperationNotSupportedException(
              "Renaming using different URL prefixes not supported : " +
              oldName + " " + newName);
        }

        ResolveResult res = getRootURLContext(oldName, m_environment);
        Context ctx = (Context)res.getResolvedObj();
        try 
        {
            ctx.rename(res.getRemainingName(), getURLSuffix(newPrefix, newName));
        } 
        finally 
        {
            ctx.close();
        }
    }

    public void rename(Name name, Name newName) throws NamingException
    {
        if (name.size() == 1) 
        {
            if (newName.size() != 1) 
            {
                throw new OperationNotSupportedException(
                  "Renaming to a Name with more components not supported: " + newName);
            }
            rename(name.get(0), newName.get(0));
        } 
        else 
        {
            // > 1 component with 1st one being URL
            // URLs must be identical; cannot deal with diff URLs
            if (!urlEquals(name.get(0), newName.get(0))) 
            {
                throw new OperationNotSupportedException(
                  "Renaming using different URLs as first components not supported: " +
                  name + " " + newName );
            }
      
            Context ctx = getContinuationContext(name);
            try 
            {
                ctx.rename(name.getSuffix(1), newName.getSuffix(1));
            } 
            finally 
            {
                ctx.close();
            }
        }
    }

    public NamingEnumeration list( String name ) throws NamingException 
    {
        ResolveResult res = getRootURLContext(name, m_environment);
        Context ctx = (Context)res.getResolvedObj();
        try 
        {
            return ctx.list(res.getRemainingName());
        }
        finally 
        {
            ctx.close();
        }
    }

    public NamingEnumeration list(Name name) throws NamingException 
    {
        if (name.size() == 1) 
        {
            return list(name.get(0));
        } 
        else 
        {
            Context ctx = getContinuationContext(name);
            try 
            {
                return ctx.list(name.getSuffix(1));
            } 
            finally 
            {
                ctx.close();
            }
        }
    }

    public NamingEnumeration listBindings(String name) 
      throws NamingException 
    {
        ResolveResult res = getRootURLContext(name, m_environment);
        Context ctx = (Context)res.getResolvedObj();
        try 
        {
            return ctx.listBindings(res.getRemainingName());
        } 
        finally 
        {
            ctx.close();
        }
    }

    public NamingEnumeration listBindings(Name name) throws NamingException 
    {
        if (name.size() == 1) 
        {
            return listBindings(name.get(0));
        } 
        else 
        {
            Context ctx = getContinuationContext(name);
            try 
            {
                return ctx.listBindings(name.getSuffix(1));
            } 
            finally 
            {
                ctx.close();
            }
        }
    }

    public void destroySubcontext(String name) throws NamingException 
    {
        ResolveResult res = getRootURLContext(name, m_environment);
        Context ctx = (Context)res.getResolvedObj();
        try 
        {
            ctx.destroySubcontext(res.getRemainingName());
        } 
        finally 
        {
            ctx.close();
        }
    }

    public void destroySubcontext(Name name) throws NamingException 
    {
        if (name.size() == 1) 
        {
            destroySubcontext(name.get(0));
        } 
        else 
        {
            Context ctx = getContinuationContext(name);
            try 
            {
                ctx.destroySubcontext(name.getSuffix(1));
            } 
            finally 
            {
                ctx.close();
            }
        }
    }

    public Context createSubcontext(String name) throws NamingException 
    {
        ResolveResult res = getRootURLContext(name, m_environment);
        Context ctx = (Context)res.getResolvedObj();
        try 
        {
            return ctx.createSubcontext(res.getRemainingName());
        } 
        finally 
        {
            ctx.close();
        }
    }

    public Context createSubcontext(Name name) throws NamingException 
    {
        if (name.size() == 1) 
        {
            return createSubcontext(name.get(0));
        } 
        else 
        {
            Context ctx = getContinuationContext(name);
            try 
            {
                return ctx.createSubcontext(name.getSuffix(1));
            } 
            finally 
            {
                ctx.close();
            }
        }
    }

    public Object lookupLink(String name) throws NamingException 
    {
        ResolveResult res = getRootURLContext(name, m_environment);
        Context ctx = (Context)res.getResolvedObj();
        try 
        {
            return ctx.lookupLink(res.getRemainingName());
        } 
        finally 
        {
            ctx.close();
        }
    }

    public Object lookupLink(Name name) throws NamingException 
    {
        if (name.size() == 1) 
        {
            return lookupLink(name.get(0));
        } 
        else 
        {
            Context ctx = getContinuationContext(name);
            try 
            {
                return ctx.lookupLink(name.getSuffix(1));
            } 
            finally 
            {
                ctx.close();
            }
        }
    }

    public NameParser getNameParser(String name) throws NamingException 
    {
        ResolveResult res = getRootURLContext(name, m_environment);
        Context ctx = (Context)res.getResolvedObj();
        try  
        {
            return ctx.getNameParser(res.getRemainingName());
        } 
        finally 
        {
            ctx.close();
        }
    }

    public NameParser getNameParser(Name name) throws NamingException 
    {
        if (name.size() == 1) 
        {
            return getNameParser(name.get(0));
        } 
        else 
        {
            Context ctx = getContinuationContext(name);
            try 
            {
                return ctx.getNameParser(name.getSuffix(1));  
            } 
            finally 
            {
                ctx.close();
            }
        }
    }

    public String composeName(String name, String prefix)
      throws NamingException 
    {
        if (prefix.equals("")) 
        {
            return name;
        } 
        else if (name.equals("")) 
        {
            return prefix;
        } 
        else 
        {
            return (prefix + "/" + name);
        }
    }

    public Name composeName(Name name, Name prefix) throws NamingException 
    {
        Name result = (Name)prefix.clone();
        result.addAll(name);
        return result;
    }

    public String getNameInNamespace() throws NamingException 
    {
        return ""; // A URL context's name is ""
    }

    public Object removeFromEnvironment(String propName)
      throws NamingException 
    {
        if (m_environment == null) 
        {
            return null;
        }
        m_environment = (Hashtable)m_environment.clone();
        return m_environment.remove(propName);
    }

    public Object addToEnvironment(String propName, Object propVal)
      throws NamingException 
    {
          m_environment = (m_environment == null) ?       
            new Hashtable(11, 0.75f) : (Hashtable)m_environment.clone();
          return m_environment.put(propName, propVal);
    }

    public Hashtable getEnvironment() throws NamingException 
    {
        if (m_environment == null) 
        {
            return new Hashtable(5, 0.75f);
        } 
        else 
        {
            return (Hashtable)m_environment.clone();
        }
    }

    public void close() throws NamingException 
    {
    }
}

