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
import javax.naming.spi.*;
import java.util.*;

/**
 * A naming context provider that manages a hierachical namespace.
 */
public class StandardContext implements Context 
{

    protected final static NameParser PARSER = new Parser();

    protected Hashtable m_environment;

    protected Hashtable m_bindings = new Hashtable(11);

    protected StandardContext m_parent = null;

    protected String m_atomic = null;


    StandardContext( Hashtable environment ) 
    {
        m_environment = (environment != null) ? (Hashtable)(environment.clone()) : null;
    }

    protected StandardContext( 
      StandardContext parent, String name, Hashtable environment, Hashtable bindings) 
    {
        this( environment );
        m_parent = parent;
        m_atomic = name;
        m_bindings = (Hashtable)bindings.clone();
    }

    protected Context createCtx( StandardContext parent, String name, Hashtable environment) 
    {
        return new StandardContext( parent, name, environment, new Hashtable() );
    }

    protected Context cloneCtx() 
    {
        return new StandardContext( m_parent, m_atomic, m_environment, m_bindings );
    }

    /**
     * Utility method for processing composite/compound name.
     * @param name The non-null composite or compound name to process.
     * @return The non-null string name in this namespace to be processed.
     */
    protected Name getMyComponents( Name name ) throws NamingException 
    {
        if (name instanceof CompositeName) 
        {
            if (name.size() > 1) 
            {
		    throw new InvalidNameException(name.toString() +
		      " has more components than namespace can handle");
            }

            // Turn component that belongs to us into compound name
            return PARSER.parse( name.get(0) );
        } 
        else 
        {
          // Already parsed
          return name;
        }
    }

    public Object lookup(String name) throws NamingException 
    {
        return lookup( PARSER.parse( name ) );
    }

    public Object lookup( Name name ) throws NamingException 
    {
        if (name.isEmpty()) 
        {
            // Asking to look up this context itself.  Create and return
            // a new instance with its own independent environment.
            return cloneCtx();
        }

        //
        // extract components that belong to this namespace
        //

        Name nm = getMyComponents(name);
        String atom = nm.get(0);
        Object inter = m_bindings.get(atom);

        if (nm.size() == 1) 
        {
            // 
            // find object in internal data structure
            //

            if (inter == null ) 
            {
		    throw new NameNotFoundException(name + " not found");
            }

            //
            // Call getObjectInstance for using any object factories
            //

            try 
            {
		    return NamingManager.getObjectInstance(inter, 
		      new CompositeName().add(atom), 
		      this, m_environment);
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
            //
            // intermediate name
            // consume name in this context and continue
            //

            if (!(inter instanceof Context)) 
            {
		    throw new NotContextException(atom +
		      " does not name a context");
            }

            return ((Context)inter).lookup(nm.getSuffix(1));
        }
    }

    public void bind(String name, Object obj) throws NamingException 
    {
        bind(new CompositeName(name), obj);
    }

    public void bind(Name name, Object obj) throws NamingException 
    {
        if (name.isEmpty()) 
        {
            throw new InvalidNameException("Cannot bind empty name");
        }

        //
        // extract components that belong to this namespace
        //

        Name nm = getMyComponents(name);
        String atom = nm.get(0);
        Object inter = m_bindings.get(atom);

        if (nm.size() == 1) 
        {
            //
            // Atomic name: Find object in internal data structure
            //

            if (inter != null) 
            {
		    throw new NameAlreadyBoundException(
                  "Use rebind to override");
            }

            //
            // call getStateToBind for using any state factories
            //

            obj = NamingManager.getStateToBind(obj, 
		    new CompositeName().add(atom), 
		    this, m_environment);

            //
            // add object to internal data structure
            //

            m_bindings.put(atom, obj);
        }
        else 
        {
            // 
            // intermediate name
            // consume name in this context and continue
            //

            if (!(inter instanceof Context)) 
            {
                throw new NotContextException(
                 atom + " does not name a context");
            }
            ((Context)inter).bind(nm.getSuffix(1), obj);
        }
    }

    public void rebind(String name, Object obj ) throws NamingException 
    {
        rebind(new CompositeName(name), obj );
    }

    public void rebind(Name name, Object obj) throws NamingException 
    {
        if (name.isEmpty()) 
        {
            final String error = "Cannot bind empty name";
            throw new InvalidNameException( error );
        }

        //
        // Extract components that belong to this namespace
        //

        Name nm = getMyComponents(name);
        String atom = nm.get(0);

        if (nm.size() == 1) 
        {
            // 
            // atomic name
            // call getStateToBind for using any state factories
            //

            obj = NamingManager.getStateToBind(obj, 
		    new CompositeName().add(atom), 
		    this, m_environment);

            // 
            // add object to internal data structure
            //

            m_bindings.put(atom, obj);
        } 
        else 
        {
            //
            // intermediate name
            // consume name in this context and continue
            //

            Object inter = m_bindings.get(atom);
            if (!(inter instanceof Context)) 
            {
                final String error = 
                  atom + " does not name a context";
		    throw new NotContextException( error );
            }
            ((Context)inter).rebind(nm.getSuffix(1), obj);
        }
    }

    public void unbind(String name) throws NamingException 
    {
        unbind(new CompositeName(name));
    }

    public void unbind( Name name ) throws NamingException 
    {
        if (name.isEmpty()) {
            throw new InvalidNameException("Cannot unbind empty name");
        }

        //
        // extract components that belong to this namespace
        //

        Name nm = getMyComponents(name);
        String atom = nm.get(0);

        //
        // remove object from internal data structure
        //

        if (nm.size() == 1) 
        {
            //
            // find object in internal data structure
            //

            m_bindings.remove(atom);
        }
        else 
        {
            //
            // intermediate name
            // consume name in this context and continue
            //

            Object inter = m_bindings.get(atom);
            if (!(inter instanceof Context)) 
            {
                final String error = 
                  atom + " does not name a context";
		    throw new NotContextException( error );
            }
            ((Context)inter).unbind(nm.getSuffix(1));
        }
    }

    public void rename(String oldname, String newname) throws NamingException 
    {
        rename(new CompositeName(oldname), new CompositeName(newname));
    }

    public void rename(Name oldname, Name newname) throws NamingException 
    {
        if (oldname.isEmpty() || newname.isEmpty()) {
            throw new InvalidNameException("Cannot rename empty name");
        }

        //
        // extract components that belong to this namespace
        //

        Name oldnm = getMyComponents(oldname);
        Name newnm = getMyComponents(newname);

        //
        // simplistic implementation
        // support only rename within same context
        //

        if (oldnm.size() != newnm.size()) 
        {
          throw new OperationNotSupportedException(
		"Do not support rename across different contexts");
        }

        String oldatom = oldnm.get(0);
        String newatom = newnm.get(0);

        if (oldnm.size() == 1) 
        {
            
            //
            // Atomic name: Add object to internal data structure
            // Check if new name exists
            //

            if (m_bindings.get(newatom) != null) 
            {
		    throw new NameAlreadyBoundException(newname.toString() +
		    " is already bound");
            }

            //
            // Check if old name is bound
            //

            Object oldBinding = m_bindings.remove(oldatom);
            if (oldBinding == null) 
            {
	 	    throw new NameNotFoundException(oldname.toString() + " not bound");
            }

            m_bindings.put(newatom, oldBinding);
        } 
        else 
        {
            //
            // Simplistic implementation: support only rename within same context
            //

            if (!oldatom.equals(newatom)) 
            {
		    throw new OperationNotSupportedException(
		      "Do not support rename across different contexts");
            }

            //
            // Intermediate name: Consume name in this context and continue
            //

            Object inter = m_bindings.get(oldatom);
            if (!(inter instanceof Context)) 
            {
		    throw new NotContextException(oldatom +
		      " does not name a context");
            }
            ((Context)inter).rename(oldnm.getSuffix(1), newnm.getSuffix(1));
        }
    }

    public NamingEnumeration list(String name) throws NamingException 
    {
        return list(new CompositeName(name));
    }

    public NamingEnumeration list(Name name) throws NamingException 
    {
        if (name.isEmpty()) 
        {
            //
            // listing this context
            //

            return new ListOfNames(m_bindings.keys());
        } 

        //
        // Perhaps 'name' names a context
        //

        Object target = lookup(name);
        if (target instanceof Context) 
        {
            return ((Context)target).list("");
        }
        throw new NotContextException(name + " cannot be listed");
    }

    public NamingEnumeration listBindings(String name) throws NamingException 
    {
        return listBindings(new CompositeName(name));
    }

    public NamingEnumeration listBindings(Name name) throws NamingException 
    {
        if (name.isEmpty()) 
        {
            //
            // listing this context
            //

            return new ListOfBindings(m_bindings.keys());
        } 

        //
        // Perhaps 'name' names a context
        //

        Object target = lookup(name);
        if (target instanceof Context) {
            return ((Context)target).listBindings("");
        }
        throw new NotContextException(name + " cannot be listed");
    }

    public void destroySubcontext(String name) throws NamingException 
    {
        destroySubcontext(new CompositeName(name));
    }

    public void destroySubcontext(Name name) throws NamingException 
    {
        if (name.isEmpty()) {
            throw new InvalidNameException(
		"Cannot destroy context using empty name");
        }

        //
        // Simplistic implementation: not checking for nonempty context first
        // Use same implementation as unbind

        unbind(name);
    }

    public Context createSubcontext(String name) throws NamingException 
    {
        return createSubcontext(new CompositeName(name));
    }

    public Context createSubcontext(Name name) throws NamingException 
    {
        if (name.isEmpty()) {
            throw new InvalidNameException("Cannot bind empty name");
        }

        //
        // Extract components that belong to this namespace
        //

        Name nm = getMyComponents(name);
        String atom = nm.get(0);
        Object inter = m_bindings.get(atom);

        if (nm.size() == 1) 
        {
            //
            // Atomic name: Find object in internal data structure
            //

            if (inter != null) 
            {
		    throw new NameAlreadyBoundException(
                    "Use rebind to override");
            }

            //
            // Create child
            //

            Context child = createCtx(this, atom, m_environment);

            //
            // Add child to internal data structure
            //

            m_bindings.put(atom, child);

            return child;
        } 
        else 
        {
            //
            // Intermediate name
            // consume name in this context and continue
            //

            if (!(inter instanceof Context)) 
            {
		    throw new NotContextException(atom + 
		      " does not name a context");
            }
            return ((Context)inter).createSubcontext(nm.getSuffix(1));
        }
    }

    public Object lookupLink(String name) throws NamingException 
    {
        return lookupLink(new CompositeName(name));
    }

    public Object lookupLink(Name name) throws NamingException 
    {
        return lookup(name);
    }

    public NameParser getNameParser(String name) throws NamingException 
    {
        return getNameParser(new CompositeName(name));
    }

    public NameParser getNameParser(Name name) throws NamingException 
    {
        //
        // Do lookup to verify name exists
        //

        Object obj = lookup(name);
        if (obj instanceof Context) 
        {
            ((Context)obj).close();
        }
        return PARSER;
    }

    public String composeName(String name, String prefix)
            throws NamingException 
    {
        Name result = composeName(new CompositeName(name),
                                  new CompositeName(prefix));
        return result.toString();
    }

    public Name composeName(Name name, Name prefix) throws NamingException 
    {
        Name result;

        //
        // Both are compound names, compose using compound name rules
        //

        if (!(name instanceof CompositeName) &&
          !(prefix instanceof CompositeName)) 
        {
            result = (Name)(prefix.clone());
            result.addAll(name);
            return new CompositeName().add(result.toString());
        }

        //
        // Simplistic implementation: do not support federation
        //

        throw new OperationNotSupportedException(
          "Do not support composing composite names");
    }

    public Object addToEnvironment(String propName, Object propVal)
            throws NamingException 
    {
        if (m_environment == null) 
        {
            m_environment = new Hashtable(5, 0.75f);
        } 
        return m_environment.put(propName, propVal);
    }

    public Object removeFromEnvironment(String propName) 
            throws NamingException 
    {
        if (m_environment == null)
            return null;

        return m_environment.remove(propName);
    }

    public Hashtable getEnvironment() throws NamingException 
    {
        if (m_environment == null)
        {
            //
            // Must return non-null
            //

            return new Hashtable(3, 0.75f);
        } 
        else 
        {
            return (Hashtable)m_environment.clone();
        }
    }

    public String getNameInNamespace() throws NamingException 
    {
        StandardContext ancestor = m_parent;

        //
        // No ancestor
        //

        if (ancestor == null) 
        {
            return "";
        }

        Name name = PARSER.parse("");
        name.add(m_atomic);

        //
        // Get parent's names
        //

        while (ancestor != null && ancestor.m_atomic != null) 
        {
            name.add(0, ancestor.m_atomic);
            ancestor = ancestor.m_parent;
        }
          
        return name.toString();
    }

    public String toString() 
    {
        if (m_atomic != null) 
        {
            return m_atomic;
        } 
        else 
        {
            return "ROOT";
        }
    }

    public void close() throws NamingException 
    {
    }

    // Class for enumerating name/class pairs
    class ListOfNames implements NamingEnumeration 
    {
        protected Enumeration names;

        ListOfNames (Enumeration names) 
        {
            this.names = names;
        }

        public boolean hasMoreElements() 
        {
            try 
            {
		    return hasMore();
            } 
            catch (NamingException e) 
            {
		    return false;
            }
        }

        public boolean hasMore() throws NamingException 
        {
            return names.hasMoreElements();
        }

        public Object next() throws NamingException 
        {
            String name = (String)names.nextElement();
            String className = m_bindings.get(name).getClass().getName();
            return new NameClassPair(name, className);
        }

        public Object nextElement() 
        {
            try 
            {
		    return next();
            } 
            catch (NamingException e) 
            {
		    throw new NoSuchElementException(e.toString());
            }
        }

        public void close() 
        {
        }
    }

    // Class for enumerating m_bindings
    class ListOfBindings extends ListOfNames 
    {

        ListOfBindings(Enumeration names) 
        {
            super(names);
        }

        public Object next() throws NamingException 
        {
            String name = (String)names.nextElement();
            Object obj = m_bindings.get(name);

            try 
            {
		    obj = NamingManager.getObjectInstance(obj, 
		      new CompositeName().add(name), StandardContext.this, 
		      StandardContext.this.m_environment);
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

    private static StandardContext ROOT;
    static 
    {
        try 
        {
            ROOT = new StandardContext(null);

            Context a = ROOT.createSubcontext("a");
            Context b = a.createSubcontext("b");
            Context c = b.createSubcontext("c");

            ROOT.createSubcontext("x");
            ROOT.createSubcontext("y");
        }
        catch (NamingException e) 
        {
            // ignore
        }
    }

    public static Context getStaticNamespace(Hashtable env) 
    {
        return ROOT;
    }
}
