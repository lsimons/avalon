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

package org.apache.avalon.merlin.jndi;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.spi.*;
import java.util.*;

/**
 * A sample service provider that implements a hierarchical namespace in memory.
 * Shortcomings:
 * - nonatomic updates for hybrid naming/directory operations
 * - cannot accept null object for bind/rebind
 * - does not support filter (advanced) searches
 */
public class StandardDirectoryContext extends StandardContext implements DirContext 
{
    private Attributes m_attributes = new BasicAttributes();
    private Hashtable m_attributeBindings = new Hashtable(11);

    StandardDirectoryContext(Hashtable env) {
      super(env);
    }

    protected StandardDirectoryContext(StandardContext parent, String name, Hashtable inEnv, 
      Hashtable bindings, Attributes myAttrs, Hashtable bindingAttrs) 
    {
        super(parent, name, inEnv, bindings);

        try 
        {
            m_attributes = deepClone(myAttrs);
            m_attributeBindings = deepClone(bindingAttrs);
        } 
        catch (NamingException e) 
        {
            e.printStackTrace(); // debug
        }
    }

    protected Context createCtx( StandardContext parent, String name, Hashtable inEnv) 
    {
        return new StandardDirectoryContext( parent, name, inEnv, 
          new Hashtable(11), new BasicAttributes(), new Hashtable(11) );
    }

    protected Context cloneCtx() 
    {
        return new StandardDirectoryContext( m_parent, m_atomic, m_environment, m_bindings,
          m_attributes, m_attributeBindings);
    }

    public Attributes getAttributes(String name) throws NamingException 
    {
        return getAttributes(new CompositeName(name), null);
    }

    public Attributes getAttributes(Name name) throws NamingException 
    {
        return getAttributes(name, null);  // same as attrIds == null
    }
      
    public Attributes getAttributes( String name, String[] attrIds )
      throws NamingException 
    {
        return getAttributes( new CompositeName(name), null );
    }

    public Attributes getAttributes( Name name, String[] attrIds ) 
      throws NamingException 
    {
        if (name.isEmpty()) 
        {
            // Asking for attributes of this context.
            return deepClone(m_attributes);
        }
          
        // Extract components that belong to this namespace
        Name nm = getMyComponents(name);
        String atom = nm.get(0);
        Object inter = m_bindings.get(atom);

        if (nm.size() == 1) 
        {
            // Atomic name: Find object in internal data structure
            if (inter == null) 
            {
                throw new NameNotFoundException(name + " not found");
            }

            if (inter instanceof DirContext) 
            {
                return ((DirContext)inter).getAttributes("", attrIds);
            }
            else 
            {
                // Fetch object's attributes from this context
                Attributes attrs = (Attributes) m_attributeBindings.get(atom);
                if (attrs == null) 
                {
                    return new BasicAttributes();
                } 
                else 
                {
                    return deepClone(attrs);
                }
            }
        }
        else
        {
            if (!(inter instanceof DirContext)) 
            {
                final String error = 
                  atom + " does not name a dircontext";
                throw new NotContextException( error );
            }
            return ((DirContext)inter).getAttributes(  nm.getSuffix(1), attrIds );
        }
    }

    public void modifyAttributes(String name, int mod_op, Attributes attrs )
      throws NamingException 
    {
        modifyAttributes(new CompositeName(name), mod_op, attrs);
    }

    public void modifyAttributes(Name name, int mod_op, Attributes attrs)
      throws NamingException 
    {
        if (attrs == null || attrs.size() == 0) 
        {
            throw new IllegalArgumentException(
              "Cannot modify without an attribute");
        }

        // Turn it into a modification list and pass it on
        NamingEnumeration attrEnum = attrs.getAll();
        ModificationItem[] mods = new ModificationItem[attrs.size()];
        for (int i = 0; i < mods.length && attrEnum.hasMoreElements(); i++) 
        {
            mods[i] = new ModificationItem( mod_op, (Attribute)attrEnum.next() );
        }

        modifyAttributes(name, mods);
    }

    public void modifyAttributes(String name, ModificationItem[] mods)
      throws NamingException 
    {
        modifyAttributes(new CompositeName(name), mods);
    }

    public void modifyAttributes( Name name, ModificationItem[] mods )
      throws NamingException 
    {
        if (name.isEmpty()) 
        {
            // Updating attributes of this context.
            doMods(m_attributes, mods);
            return;
        }
          
        // Extract components that belong to this namespace
        Name nm = getMyComponents(name);
        String atom = nm.get(0);
        Object inter = m_bindings.get(atom);

        if (nm.size() == 1) 
        {
            // Atomic name: Find object in internal data structure
            if (inter == null) 
            {
                throw new NameNotFoundException(name + " not found");
            }

            if (inter instanceof DirContext) 
            {
                ((DirContext)inter).modifyAttributes("", mods);
            } 
            else 
            {
                // Fetch object's attributes from this context
                Attributes attrs = (Attributes) m_attributeBindings.get(atom);
                if (attrs == null) 
                {
                    attrs = new BasicAttributes(); 
                    doMods(attrs, mods);
                    m_attributeBindings.put(atom, attrs);
                } 
                else 
                {
                    doMods(attrs, mods);
                }
            }
            return;
        } 
        else 
        {
            // Intermediate name: Consume name in this context and continue
            if (!(inter instanceof DirContext)) 
            {
                throw new NotContextException(atom +
                  " does not name a dircontext");
            }

            ((DirContext)inter).modifyAttributes(nm.getSuffix(1), mods);
        }
    }

    /**
     * Apply modifications to attrs in place.
     * NOTE: Simplified implementation:
     *       - Modifications NOT performed atomically
     *            - Attribute names case-sensitive 
     *       - All attributes can be multivalued
     */
    private void doMods(Attributes attrs, ModificationItem[] mods) 
      throws NamingException 
    {
        Attribute attr;
        String id;
        for (int i = 0; i < mods.length; i++) 
        {
            attr = mods[i].getAttribute();
            id = attr.getID();
            switch (mods[i].getModificationOp()) 
            {
                case ADD_ATTRIBUTE: 
                {
                    Attribute origAttr = attrs.get(id);
                    if (origAttr == null) 
                    {
                        // No previous attribute, just add
                        attrs.put(attr);
                    } 
                    else 
                    {
                        // Append values of new attribute
                        NamingEnumeration newVals = attr.getAll();
                        Object val;
                        while (newVals.hasMore()) 
                        {
                            val = newVals.next();
                            if (!origAttr.contains(val)) 
                            {
                                origAttr.add(val);
                            }
                        }
                    }
                    break;
                }
                case REPLACE_ATTRIBUTE:
                if (attr.size() == 0) 
                {
                    // Remove entire attribute
                    attrs.remove(id);
                } 
                else 
                {
                    // Replace or add using new attribute
                    attrs.put(attr);
                }
                break;

                case REMOVE_ATTRIBUTE:
                if (attr.size() == 0) 
                {
                    // Remove entire attribute
                    attrs.remove(id);
                } 
                else 
                {
                    // Remove specified values
                    Attribute origAttr = attrs.get(id);
                    if (origAttr != null) 
                    {
                        NamingEnumeration remVals = attr.getAll();
                        while (remVals.hasMore()) 
                        {
                            origAttr.remove(remVals.next());
                        }
                        if (origAttr.size() == 0) 
                        {
                            attrs.remove(id);
                        }
                    } // else nothing to remove
                }
                break;
            }
        }
    }

    // Override unbind() to deal with attributes
    // destroySubcontext() already uses unbind() so we just need to
    // override unbind() to affect both unbind() and destroySubcontext().
    public void unbind(Name name) throws NamingException 
    {
        try 
        {
            // Get attributes that belong to name
            Attributes attrs = getAttributes(name);

            // Remove those attributes
            if (attrs.size() != 0) 
            {
                modifyAttributes(name, DirContext.REMOVE_ATTRIBUTE, attrs);
            }
        } 
        catch (NamingException e) 
        {
          // ignore
        }

        // Remove from namespace
        super.unbind(name);
    }

    // Override StandardContext version to account for attributes
    public void bind(Name name, Object obj) throws NamingException 
    {
        bind(name, obj, null);
    }

    public void bind(String name, Object obj, Attributes attrs)
      throws NamingException 
    {
        bind(new CompositeName(name), obj, attrs);
    }

    /**
     * NOTE: Simplified implementation: add attributes and object nonatomically
     * Does not accept null obj (throws NullPointerException)
     */
    public void bind(Name name, Object obj, Attributes attrs)
      throws NamingException 
    {
        if (name.isEmpty()) {
            throw new InvalidNameException("Cannot bind empty name");
        }

        // Extract components that belong to this namespace
        Name nm = getMyComponents(name);
        String atom = nm.get(0);
        Object inter = m_bindings.get(atom);

        if (nm.size() == 1) 
        {
            // Atomic name: Find object in internal data structure
            if (inter != null) 
            {
                throw new NameAlreadyBoundException(
                    "Use rebind to override");
            }

            // Call getStateToBind for using any state factories
            DirStateFactory.Result res = 
              DirectoryManager.getStateToBind(
                obj, new CompositeName().add(atom), this, m_environment, attrs);

            // Add object to internal data structure
            m_bindings.put(atom, res.getObject());

            // Add attributes
            if (res.getAttributes() != null) 
            {
                m_attributeBindings.put(atom, deepClone(res.getAttributes()));
            }
        } 
        else 
        {
            // Intermediate name: Consume name in this context and continue
            if (!(inter instanceof Context)) 
            {
                throw new NotContextException(atom + 
                  " does not name a context");
            }
            ((DirContext)inter).bind(nm.getSuffix(1), obj, attrs);
        }
    }

    // Override StandardContext version to account for attributes
    public void rebind(Name name, Object obj) throws NamingException 
    {
      rebind(name, obj, null);
    }

    public void rebind(String name, Object obj, Attributes attrs)
      throws NamingException 
    {
        rebind(new CompositeName(name), obj, attrs);
    }

    /**
     * NOTE: Simplified implementation: remove object first, then add back
     * Does not accept null obj (throws NullPointerException)
     */
    public void rebind(Name name, Object obj, Attributes attrs)
      throws NamingException 
    {
        if (name.isEmpty()) {
            throw new InvalidNameException("Cannot bind empty name");
        }

        // Extract components that belong to this namespace
        Name nm = getMyComponents(name);
        String atom = nm.get(0);

        if (nm.size() == 1) 
        {
            // Atomic name
 
            // Call getStateToBind for using any state factories
            DirStateFactory.Result res = DirectoryManager.getStateToBind(obj, 
              new CompositeName().add(atom), this, m_environment, attrs);

            // Add object to internal data structure
            m_bindings.put(atom, res.getObject());

            // Add attributes
            if (res.getAttributes() != null) 
            {
                m_attributeBindings.put(atom, deepClone(res.getAttributes()));
            }
        } 
        else 
        {
            // Intermediate name: Consume name in this context and continue
            Object inter = m_bindings.get(atom);
            if (!(inter instanceof Context)) 
            {
                throw new NotContextException(atom + 
                  " does not name a context");
            }
            ((DirContext)inter).rebind(nm.getSuffix(1), obj, attrs);
        }
    }

    public DirContext createSubcontext(String name, Attributes attrs)
      throws NamingException 
    {
        return createSubcontext(new CompositeName(name), attrs);
    }

    /**
     * NOTE: Simplified implementation: add attributes and object nonatomically
     */
    public DirContext createSubcontext(Name name, Attributes attrs)
      throws NamingException 
    {
        // First create context
        DirContext ctx = (DirContext)createSubcontext(name);


        // Add attributes
        if (attrs != null && attrs.size() > 0) 
        {
            ctx.modifyAttributes("", DirContext.ADD_ATTRIBUTE, attrs);
        }
        return ctx;
    }

    public DirContext getSchema(String name) throws NamingException 
    {
        return getSchema(new CompositeName(name));
    }

    public DirContext getSchema(Name name) throws NamingException 
    {
        throw new OperationNotSupportedException("Not implemented yet");
    }

    public DirContext getSchemaClassDefinition(String name)
      throws NamingException 
    {
        return getSchemaClassDefinition(new CompositeName(name));
    }

    public DirContext getSchemaClassDefinition(Name name)
      throws NamingException 
    {
          throw new OperationNotSupportedException("Not implemented yet");
    }

    public NamingEnumeration search(String name, Attributes matchingAttrs)
      throws NamingException {
          return search(new CompositeName(name), matchingAttrs);
    }

    public NamingEnumeration search(Name name, Attributes matchingAttrs) 
      throws NamingException 
    {
          return search(name, matchingAttrs, null);
    }

    public NamingEnumeration search(String name, 
      Attributes matchingAttrs, String[] attrsRet)
      throws NamingException 
    {
        return search(new CompositeName(name), matchingAttrs, attrsRet);
    }

    public NamingEnumeration search(Name name,
      Attributes matchingAttrs, String[] attrsRet)
      throws NamingException 
    {

        // Vector for storing answers
        Vector answer = new Vector();
        Binding item;

        // Get context
        Context target = (Context)lookup(name);

        try 
        {
            // List context
            NamingEnumeration objs = target.listBindings("");
            Attributes attrs;

            // For each item on list, examine its attributes
            while (objs.hasMore()) 
            {
                item = (Binding)objs.next();
                if (item.getObject() instanceof DirContext) 
                {
                    attrs = ((DirContext)item.getObject()).getAttributes("");
                } 
                else 
                {
                    attrs = getAttributes(item.getName());
                }
                if( contains(attrs, matchingAttrs ) ) 
                {
                    answer.addElement(
                      new SearchResult(item.getName(), null,
                        selectAttributes(attrs, attrsRet)));
                }
            } 
        } 
        finally 
        {
            target.close();
        }

        return new WrapEnum(answer.elements());
    }

   /** 
    * Returns true if superset contains subset.
    */
    private static boolean contains(Attributes superset, Attributes subset) 
      throws NamingException 
    {
        if (subset == null)
        {
            return true;  // an empty set is always a subset
        }

        NamingEnumeration m = subset.getAll();
        while (m.hasMore()) 
        {
            if (superset == null) 
            {
                return false;  // contains nothing
            }
            Attribute target = (Attribute) m.next();
            Attribute fromSuper = superset.get(target.getID());
            if( fromSuper == null ) 
            {
                return false;
            } 
            else 
            {
                // check whether attribute values match
                if (target.size() > 0) 
                {
                    NamingEnumeration vals = target.getAll();
                    while (vals.hasMore()) 
                    {
                        if (!fromSuper.contains(vals.next())) 
                        {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    /*
     * Returns an Attributes instance containing only attributeIDs given in
     * "attributeIDs" whose values come from the given DSContext.
     */
    private static Attributes selectAttributes(Attributes originals,
      String[] attrIDs) throws NamingException 
    {

      if (attrIDs == null)
          return originals;

      Attributes result = new BasicAttributes();

      for(int i=0; i<attrIDs.length; i++) 
      {
          Attribute attr = originals.get(attrIDs[i]);
          if(attr != null) {
            result.put(attr);
          }
      }
      
      return result;
    }

    class WrapEnum implements NamingEnumeration 
    {
        Enumeration enum;

        WrapEnum(Enumeration enum) 
        {
            this.enum = enum;
        }

        public boolean hasMore() throws NamingException 
        {
            return hasMoreElements();
        }

        public boolean hasMoreElements() 
        {
            return enum.hasMoreElements();
        }

        public Object next() throws NamingException 
        {
            return nextElement();
        }

        public Object nextElement() 
        {
            return enum.nextElement();
        }

        public void close() throws NamingException 
        {
            enum = null;
        }
    }

    public NamingEnumeration search(String name,
      String filter, SearchControls cons) throws NamingException 
    {
        return search(new CompositeName(name), filter, cons);
    }

    public NamingEnumeration search(Name name,
      String filter, SearchControls cons) throws NamingException 
    {
        throw new OperationNotSupportedException(
          "Filter search not supported");
    }

    public NamingEnumeration search(String name,
      String filterExpr, Object[] filterArgs, SearchControls cons)
      throws NamingException 
    {
        return search(new CompositeName(name), filterExpr, filterArgs, cons);
    }

    public NamingEnumeration search(Name name,
      String filterExpr, Object[] filterArgs, SearchControls cons)
      throws NamingException 
    {
        // Fill in expression
        String filter = format(filterExpr, filterArgs);
          
        return search(name, filter, cons);
    }

    // Utility for turning a filter expression with arguments into a filter string

    /**
     * Formats the expression <tt>expr</tt> using arguments from the array
     * <tt>args</tt>.
     *
     * <code>{i}</code> specifies the <code>i</code>'th element from 
     * the array <code>args</code> is to be substituted for the
     * string "<code>{i}</code>".
     *
     * To escape '{' or '}' (or any other character), use '\'.
     *
     * Uses getEncodedStringRep() to do encoding.
     */
    private static String format(String expr, Object[] args) 
      throws NamingException 
    {

        int param;
        int where = 0, start = 0;
        StringBuffer answer = new StringBuffer(expr.length());

        while ((where = findUnescaped('{', expr, start)) >= 0) 
        {
            int pstart = where + 1; // skip '{'
            int pend = expr.indexOf('}', pstart);

            if (pend < 0) 
            {
                throw new InvalidSearchFilterException("unbalanced {: " + expr);
            }

            // at this point, pend should be pointing at '}'
            try 
            {
                param = Integer.parseInt(expr.substring(pstart, pend));
            } 
            catch (NumberFormatException e) 
            {
                throw new InvalidSearchFilterException(
                  "integer expected inside {}: " + expr);
            }

            if (param >= args.length) 
            {
                throw new InvalidSearchFilterException(
                  "number exceeds argument list: " + param);
            }

            answer.append(expr.substring(start, where)).append(
             getEncodedStringRep(args[param]));
            start = pend + 1; // skip '}'
        }

        if (start < expr.length())
        {
            answer.append(expr.substring(start));
        }
        return answer.toString();
    }

   /**
    * Finds the first occurrence of <tt>ch</tt> in <tt>val</tt> starting
    * from position <tt>start</tt>. It doesn't count if <tt>ch</tt>
    * has been escaped by a backslash (\)
    */
    private static int findUnescaped(char ch, String val, int start) 
    {
        int len = val.length();

        while (start < len) 
        {
            int where = val.indexOf(ch, start);
            // if at start of string, or not there at all, or if not escaped
            if (where == start || where == -1 || val.charAt(where-1) != '\\')
            {
                return where;
            }

            // start search after escaped star
            start = where + 1;
        }
        return -1;
    }


    // Writes the hex representation of a byte to a StringBuffer.
    private static void hexDigit(StringBuffer buf, byte x) 
    {
        char c;

        c = (char) ((x >> 4) & 0xf);
        if (c > 9)
        {
            c = (char) ((c-10) + 'A');
        }
        else
        {
            c = (char)(c + '0');
        } 
        buf.append(c);
        c = (char) (x & 0xf);
        if (c > 9)
        {
            c = (char)((c-10) + 'A');
        }
        else
        {
            c = (char)(c + '0');
        }
        buf.append(c);
    }


    /**
     * Returns the string representation of an object (such as an attr value).
     * If obj is a byte array, encode each item as \xx, where xx is hex encoding
     * of the byte value.
     * Else, if obj is not a String, use its string representation (toString()).
     * Special characters in obj (or its string representation) are then 
     * encoded appropriately according to RFC 2254.
     *       *      \2a
     *       (      \28
     *       )      \29
     *       \      \5c
     *       NUL    \00
     */
    private static String getEncodedStringRep(Object obj) throws NamingException 
    {
        String str;
        if( obj == null )
        {
            return null;
        }

        if (obj instanceof byte[]) 
        {
            // binary data must be encoded as \hh where hh is a hex char
            byte[] bytes = (byte[])obj;
            StringBuffer b1 = new StringBuffer(bytes.length*3);
            for (int i = 0; i < bytes.length; i++) 
            {
                b1.append('\\');
                hexDigit(b1, bytes[i]);
            }
            return b1.toString();
        }
        if (!(obj instanceof String)) 
        {
            str = obj.toString();
        } 
        else 
        {
            str = (String)obj;
        }
        int len = str.length();
        StringBuffer buf = new StringBuffer(len);
        char ch;
        for (int i = 0; i < len; i++) 
        {
            switch (ch=str.charAt(i)) 
            {
                case '*': 
                buf.append("\\2a");
                break;
                case '(':
                buf.append("\\28");
                break;
                case ')':
                buf.append("\\29");
                break;
                case '\\':
                buf.append("\\5c");
                break;
                case 0:
                buf.append("\\00");
                break;
                default:
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    public static void main(String[] args) 
    {
        try 
        {
            DirContext ctx = new StandardDirectoryContext(null);

            DirContext a = ctx.createSubcontext("a", 
              new BasicAttributes("fact", "the letter A"));
            DirContext b = ctx.createSubcontext("b",
              new BasicAttributes("fact", "the letter B"));
            Context c = b.createSubcontext("c");

            System.out.println("c's full name: " + c.getNameInNamespace());
            System.out.println("attributes of a: " + ctx.getAttributes("a"));

            System.out.println("list: " );
            NamingEnumeration enum = ctx.list("");
            while (enum.hasMore()) 
            {
                System.out.println(enum.next());
            }

            System.out.println("search: " );
            enum = ctx.search("", new BasicAttributes("fact", "the letter A"));
            while (enum.hasMore()) 
            {
                System.out.println(enum.next());
            }

        } 
        catch (NamingException e) 
        {
            e.printStackTrace();
        }
    }

    private Attributes deepClone(Attributes orig) throws NamingException 
    {
        if (orig.size() == 0) 
        {
            return (Attributes)orig.clone();
        }

        BasicAttributes copy = new BasicAttributes();

        NamingEnumeration enum = orig.getAll();
        while (enum.hasMore()) 
        {
            copy.put((Attribute)((Attribute)enum.next()).clone());
        }

        return copy;
    }

    private Hashtable deepClone(Hashtable orig) throws NamingException 
    {
        if (orig.size() == 0) 
        {
            return (Hashtable)orig.clone();
        }

        Hashtable copy = new Hashtable();

        Enumeration enum = orig.keys();
        while (enum.hasMoreElements()) 
        {
            String key = (String)enum.nextElement();
            Attributes attrs = (Attributes) orig.get(key);

            copy.put(key, deepClone(attrs));
        }

        return copy;
    }

    // Some overrides that take directory into account

    // Need to get attributes and use DirectoryManager.getObjectInstance
    public Object lookup(Name name) throws NamingException 
    {
        if (name.isEmpty()) 
        {
            // Asking to look up this context itself.  Create and return
            // a new instance with its own independent environment.
            return cloneCtx();
        } 

        // Extract components that belong to this namespace
        Name nm = getMyComponents(name);
        String atom = nm.get(0);
        Object inter = m_bindings.get(atom);

        if (nm.size() == 1) 
        {
            // Atomic name: Find object in internal data structure
            if (inter == null) 
            {
                throw new NameNotFoundException(name + " not found");
            }

            // Get attributes
            Attributes attrs;
            if (inter instanceof DirContext) 
            {
                attrs = ((DirContext)inter).getAttributes("");
            } 
            else 
            {
                // Fetch object's attributes from this context
                attrs = (Attributes) m_attributeBindings.get(atom);
            }

            // Call getObjectInstance for using any object factories
            try 
            {
                return DirectoryManager.getObjectInstance(inter, 
                  new CompositeName().add(atom), 
                  this, m_environment, attrs);
            } 
            catch (Exception e) 
            {
                NamingException ne = new NamingException(
                  "getObjectInstance failed");
                ne.setRootCause(e);
                throw ne;
            }
        } 
        else 
        {
            // Intermediate name: Consume name in this context and continue
            if (!(inter instanceof Context)) 
            {
                throw new NotContextException(atom +
                  " does not name a context");
            }
            return ((Context)inter).lookup(nm.getSuffix(1));
        }
    }

    public NamingEnumeration listBindings(Name name) throws NamingException 
    {
        if (name.isEmpty()) 
        {
            // listing this context
            return new ListOfDirBindings(m_bindings.keys());
        } 

        // Perhaps 'name' names a context
        Object target = lookup(name);
        if (target instanceof Context) 
        {
            return ((Context)target).listBindings("");
        }
        throw new NotContextException(name + " cannot be listed");
    }

    // Class for enumerating m_bindings
    class ListOfDirBindings extends ListOfNames 
    {

        ListOfDirBindings(Enumeration names) 
        {
          super(names);
        }

        public Object next() throws NamingException 
        {
            String name = (String)names.nextElement();
            Object obj = m_bindings.get(name);

            try
            {
                // Get attributes
                Attributes attrs;
                if (obj instanceof DirContext) 
                {
                    attrs = ((DirContext)obj).getAttributes("");
                } 
                else 
                {
                    // Fetch object's attributes from this context
                    attrs = (Attributes) m_attributeBindings.get(name);
                }

                obj = DirectoryManager.getObjectInstance(
                  obj, new CompositeName().add(name), StandardDirectoryContext.this, 
                  StandardDirectoryContext.this.m_environment, attrs);
            } 
            catch (Exception e) 
            {
                NamingException ne = new NamingException(
                  "getObjectInstance failed");
                ne.setRootCause(e);
                throw ne;
            }

            return new Binding(name, obj);
        }
    }
}
