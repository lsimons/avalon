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
package org.apache.metro.facilities.reflector.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.Map;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Properties;
import java.util.Enumeration;

import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.avalon.framework.activity.Disposable;

import org.apache.metro.facilities.reflector.ReflectorService;
import org.apache.metro.facilities.reflector.ReflectionListener;
import org.apache.metro.facilities.reflector.ReflectionEvent;
import org.apache.metro.facilities.reflector.ReflectionException;

/** The Reflector Implementation is capable of digging through any
*   Java application that is added as a root object.
*
* @avalon.component name="reflector" lifestyle="singleton"
* @avalon.service type="org.apache.metro.facilities.reflector.ReflectorService"
*/
public final class ReflectorImpl
    implements ReflectorService, Disposable
{
    private Map        m_Properties;
    private Vector     m_Listeners;
    private Hashtable  m_RootObjects;
    
    public ReflectorImpl()
    {
        super();
        m_RootObjects = new Hashtable();
    }

    public void addRootObject( String name, Object obj )
    {
        m_RootObjects.put( name, obj );
        ReflectionEvent event = 
            new ReflectionEvent(this, name, obj, "addRootObject" );
        fireEvent(event);
    }

    public void removeRootObject( String name )
    {
        m_RootObjects.remove(name);
        ReflectionEvent event = 
            new ReflectionEvent(this, name, null, "removeRootObject" );
        fireEvent(event);
    }

    public Object getRootObject( String name )
    {
        Object result = m_RootObjects.get(name);
        ReflectionEvent event = 
            new ReflectionEvent(this, name, result, "getRootObject" );
        fireEvent(event);
        return result;
    }

    public Map getRootObjects()
    {
        return m_RootObjects;
    }
    
    public String[] getAllRootNames() 
    {
        synchronized( m_RootObjects )
        {
            Enumeration list = m_RootObjects.keys();
            String[] names = new String[m_RootObjects.size()];
            
            for( int i=0; list.hasMoreElements() ; i++ )
                names[i] = (String) list.nextElement();
            
            ReflectionEvent event = 
                new ReflectionEvent(this, null, names, "getAllRootNames" );
            fireEvent(event);
            return names;
        }
    }

    public String[] getNames( String object) 
        throws ReflectionException
    {
        Object container;
        
        try
        {
            if( object == null )
                return getAllRootNames();
            if( object.equals("") )
                return getAllRootNames();
            container = resolveObject(object);
        } catch( Exception e )
        {
            if( e instanceof ReflectionException )
                throw (ReflectionException) e.fillInStackTrace();
            else
                throw new ReflectionException( "Exception in Server.", e );
        }
        String[] result = getNames( container );
        ReflectionEvent event = 
            new ReflectionEvent(this, object, result, "getNames" );
        fireEvent(event);
        return result;
    }
    
    public String[] getNames( Object container) 
        throws ReflectionException
    {
        Vector names = new Vector();
        
        try
        {
            if( container == null )
                return getAllRootNames();
            
            if( container instanceof Collection )
                return getCollectionNames((Collection) container);
            else if( container instanceof Map )
                return getMapNames( (Map) container);
            else if( container instanceof Dictionary )
                return getDictionaryNames( (Dictionary) container);
            else if( container.getClass().isArray() )
                return getArrayNames( (Object[]) container);
            else
            {
                /////  NORMAL OBJECT
                // Retrieve all PUBLIC, non-STATIC fields.
                Field[] flds = container.getClass().getDeclaredFields();
                for( int i=0 ; i < flds.length ; i++ )
                {
                    int mod = flds[i].getModifiers();
                    if( Modifier.isPublic( mod ) && !Modifier.isStatic( mod ) )
                    {
                        String str = flds[i].getName();
                        int j = str.indexOf( '_' );
                        if( j == 1 )
                            str = str.substring( 2 );
                        names.addElement( str);
                    }
                }
                // Retrieve PUBLIC non-STATIC GET methods
                Method[] methods = container.getClass().getMethods();
                for( int i=0 ; i < methods.length ; i++ )
                {
                    int mod = methods[i].getModifiers();
                    if( Modifier.isPublic( mod ) && ! Modifier.isStatic( mod ))
                    {
                        String str = methods[i].getName();
                        if( str.startsWith("get") )
                        {
                            if( methods[i].getParameterTypes().length == 0 )
                            {
                                str = str.substring( 3 );
                                if( ! names.contains(str) )
                                    names.addElement( str );
                            }
                        }
                        else if( str.startsWith("is") )
                        {
                            if( methods[i].getParameterTypes().length == 0 )
                            {
                                str = str.substring( 2 );
                                if( ! names.contains(str) )
                                    names.addElement( str );
                            }
                        }
                    }
                }
            }
        } catch( Exception e )
        {
            if( e instanceof ReflectionException )
                throw (ReflectionException) e.fillInStackTrace();
            else
                throw new ReflectionException( "Exception in Server.", e );
        }
        String[] ret = new String[names.size()];
        for( int i= 0; i < names.size() ; i++ )
            ret[i] = (String) names.elementAt(i);
        return ret;
    }

    public String get( String object )
        throws ReflectionException
    {
        String str = null;
        try
        {
            Object obj = resolveObject( object );
            str = "" + obj;
        } catch( Exception e )
        {
            if( e instanceof ReflectionException )
                throw (ReflectionException) e.fillInStackTrace();
            else
                throw new ReflectionException( "Exception in ReflectorServer.", e );
        }
        ReflectionEvent event = 
            new ReflectionEvent(this, object, str, "get" );
        fireEvent(event);
        return str;
    }

    public void set( String object, String value )
        throws ReflectionException
    {
        String str = null;

        try
        {
            String container = dropLast( object );
            String member = getLast(object);
            Object obj = resolveObject( container );
            setObject( obj, member, value ); //calls private setObject( Object, String, String)
            ReflectionEvent event = 
                new ReflectionEvent(this, object, value, "set" );
            fireEvent(event);
        } catch( Exception e )
        {
            if( e instanceof ReflectionException )
                throw (ReflectionException) e.fillInStackTrace();
            else
                throw new ReflectionException( "Exception in ReflectorServer.", e );
        }
    }
    
    public Object getObject( String object )
        throws ReflectionException
    {
        Object obj = null;
        try
        {
            obj = resolveObject( object );
        } catch( Exception e )
        {
            if( e instanceof ReflectionException )
                throw (ReflectionException) e.fillInStackTrace();
            else
                throw new ReflectionException( "Exception in Server.", e );
        }
        ReflectionEvent event = 
            new ReflectionEvent(this, object, obj, "getObject" );
        fireEvent(event);
        return obj;
    }
    
    public Object getObject( Object container, String memberName )
        throws ReflectionException
    {
        try
        {
            if( container == null || container.equals("") )
            {
                Object root = m_RootObjects.get(memberName);
                return root;
            }
            if( container.getClass().isPrimitive() )
                return null;
            if( memberName.startsWith("[") )
            {
                return getCollection(container, memberName);
            }
            else
            {
                Method method = findMethod( container, "get" + memberName );
                if( method == null )
                    method = findMethod( container, "is" + memberName );
                
                Object obj = null;
                
                if( method != null )
                {
                    try
                    {
                        obj = method.invoke(container, new Object[0] );
                    } catch( IllegalAccessException e )
                    {
                        throw new ReflectionException( container.getClass().toString() + ".get" + memberName + "() is not declared public.", e );
                    } catch( IllegalArgumentException e )
                    {
                        throw new ReflectionException(container.getClass().toString() + ".get" + memberName + "() received illegal arguments. Internal Error?", e);
                    } catch( InvocationTargetException e )
                    {
                        Exception exc;
                        Throwable t = e.getTargetException();
                        if( t instanceof Exception )
                            exc = (Exception) t;
                        else
                            exc = null;
                        throw new ReflectionException( container.getClass().toString() + ".get" + memberName + "() resulted in an exception.\n" + e , exc );
                    }
                    ReflectionEvent event = 
                        new ReflectionEvent(this, container, obj, "getObject[" + memberName + "]" );
                    fireEvent(event);
                    return obj;
                }
                if( method == null )
                {
                    Field fld=findField( container, memberName );
                    obj = fld.get( container );
                    ReflectionEvent event = 
                        new ReflectionEvent(this, container, obj, "getObject[" + memberName + "]" );
                    fireEvent(event);
                    return obj;
                }
            }
            return null;
        } catch( Exception e )
        {
            if( e instanceof ReflectionException )
                throw (ReflectionException) e.fillInStackTrace();
            else
                throw new ReflectionException( "Container="+container + ", member=" + memberName, e );
        }
    }

    public void setObject( String objectname, Object object )
        throws ReflectionException
    {
        String str = null;

        try
        {
            String containerName = dropLast( objectname );
            String member = getLast(objectname);
            Object container = resolveObject( containerName );
            setObject( container, member, object );
            
            ReflectionEvent event = 
                new ReflectionEvent(this, objectname, object, "setObject" );
            fireEvent(event);
        } catch( Exception e )
        {
            if( e instanceof ReflectionException )
                throw (ReflectionException) e.fillInStackTrace();
            else
                throw new ReflectionException( "Exception in Server.", e );
        }
    }

    public void setObject( Object container, String member, Object value )
        throws ReflectionException
    {
        try
        {
            Object obj = null;
            Class cls = container.getClass();
            Field[] flds = cls.getDeclaredFields();
            
            if( member.startsWith("[") )
            {
                setCollection(container, member, value);
            }
            else
            {
                Method method = findMethod( container, "set" + member );
                if( method != null )
                {
                    try
                    {
                        Class[] argTypes = method.getParameterTypes();
                        Object[] args = new Object[1];
                        args[0] = value;
                        obj = method.invoke(container, args );
                        ReflectionEvent event = 
                            new ReflectionEvent(this, container, obj, "setObject[" + member + "]" );
                        fireEvent(event);
                    } catch( IllegalAccessException e )
                    {
                        throw new ReflectionException( container.getClass().toString() + ".set" + member + "() is not declared public.", e);
                    } catch( IllegalArgumentException e )
                    {
                        throw new ReflectionException( container.getClass().toString() + ".set" + member + "() .", e);
                    }
                }
                if( method == null )
                {
                    for( int i=0; i < flds.length ; i++ )
                    {
                        if( matchField( flds[i].getName(), member ) )
                        {
                            setObject( flds[i], container, value );
                            ReflectionEvent event = 
                                new ReflectionEvent(this, container, obj, "setObject[" + member + "]" );
                            fireEvent(event);
                        }
                    }
                }
            }
        } catch( Exception e )
        {
            if( e instanceof ReflectionException )
                throw (ReflectionException) e.fillInStackTrace();
            else
                throw new ReflectionException( "Container=:" + container + ", Member=" + member + ", Object=" + value, e );
        }
    }

    public String getContainer( String objectname )
    {
        String result = dropLast(objectname);
        ReflectionEvent event = 
            new ReflectionEvent(this, objectname, result, "getContainer" );
        fireEvent(event);
        return result;
    }
    
    public String getMember( String objectname )
    {
        String result = getLast(objectname);
        ReflectionEvent event = 
            new ReflectionEvent(this, objectname, result, "getMember" );
        fireEvent(event);
        return result;
    }
    
    private Object resolveObject( String name )
        throws ReflectionException
    {
        Object obj = null;

        if( "".equals(name) || name == null)
            return null;
        String container = dropLast(name);
        String member = getLast(name);
        obj = resolveObject( container );
        obj = getObject( obj, member );
        return obj;
    }

    private String dropLast( String name )
    {
        int pos1 = name.lastIndexOf( '[' );
        int pos2 = name.lastIndexOf( '.' );
        int pos3 = name.lastIndexOf( ']' );
        if( pos1 == -1 && pos2 == -1 )
            return "";
        String result = name.substring( 0, pos3 > pos2 ? pos1 : pos2 );
        return result;
    }

    private String getLast( String name )
    {
        int pos1 = name.lastIndexOf( '[' );
        int pos2 = name.lastIndexOf( '.' );
        int pos3 = name.lastIndexOf( ']' );
        if( pos1 == -1 && pos2 == -1 )
            return name;
        String result = name.substring( pos3 > pos2 ? pos1 : pos2+1 );
        return result;
    }

    public String getClassName( String name )
        throws ReflectionException
    {
        return getClass(name).getName();
    }
    
    public String getClassName( Object container, String memberName )
        throws ReflectionException
    {
        return getClass(container, memberName).getName();
    }
    
    public Class getClass( String name )
        throws ReflectionException
    {
        if( "".equals(name) || name == null)
            return null;
        String container = dropLast(name);
        String member = getLast(name);
        Object obj = resolveObject( container );
        return getClass( obj, member );
    }
    
    public Class getClass( Object container, String memberName )
        throws ReflectionException
    {
        try
        {
            if( container == null || container.equals("") )
            {
                Object root = m_RootObjects.get(memberName);
                return root.getClass();
            }
            if( isPrimitive(container) )
                return null;
            
            if( memberName.startsWith("[") )
            {
                return getClassInCollection(container, memberName);
            }
            else
            {
                Method method = findMethod( container, "get" + memberName );
                if( method == null )
                    method = findMethod( container, "is" + memberName );

                if( method != null )
                {
                    Class retClass = method.getReturnType();
                    if( Modifier.isNative(retClass.getModifiers()) )
                        return retClass;

                    // Native classes are not properly serialized/deserialized over RMI.

                    return expandNativeClass( retClass );
                }
                
                Field fld=findField( container, memberName );
                return fld.getType();
            }
        } catch( Exception e )
        {
            if( e instanceof ReflectionException )
                throw (ReflectionException) e.fillInStackTrace();
            else
                throw new ReflectionException( "Container="+container + ", member=" + memberName, e );
        }
    }

    public boolean isSettable( String name )
        throws ReflectionException
    {
        if( "".equals(name) || name == null)
            return false;
        String container = dropLast(name);
        String member = getLast(name);
        Object obj = resolveObject( container );
        return isSettable( obj, member );
    }

    public boolean isSettable( Object container, String memberName)
        throws ReflectionException
    {
        try
        {
            if( container == null || "".equals(container) )
                return false;
            if( isPrimitive(container) )
                return false;
            
            Method method = findMethod( container, "set" + memberName );
            
            if( method != null )
                return true;
            else
                return false;
        } catch( Exception e )
        {
            if( e instanceof ReflectionException )
                throw (ReflectionException) e.fillInStackTrace();
            else
                throw new ReflectionException( "Container="+container + ", member=" + memberName, e );
        }
    }

    private void setPrimitive( Field fld, Object obj, String value )
        throws IllegalAccessException, ClassNotFoundException
    {
        Class fldClass = fld.getType();
        if( fldClass == Boolean.TYPE )
        {
            fld.setBoolean(obj, (new Boolean(value)).booleanValue());
        }
        else if( fldClass == Double.TYPE )
        {
            fld.setDouble(obj, (new Double(value)).doubleValue());
        }
        else if( fldClass == Integer.TYPE )
        {
            fld.setInt(obj, (new Integer(value)).intValue());
        }
        else if( fldClass == Short.TYPE )
        {
            fld.setShort(obj, (new Short(value)).shortValue());
        }
        else if( fldClass == Byte.TYPE )
        {
            fld.setByte(obj, (new Byte(value)).byteValue());
        }
        else if( fldClass == Float.TYPE )
        {
            fld.setFloat(obj, (new Float(value)).floatValue());
        }
        else if( fldClass == Long.TYPE )
        {
            fld.setLong(obj, (new Long(value)).longValue());
        }
        else if( fldClass == Character.TYPE )
        {
            fld.setChar(obj, value.charAt(0) );
        }
        else if( fldClass == Void.TYPE )
        {
            throw new IllegalAccessException( " Can not set void primitives." );
        }
        else 
            throw new ClassNotFoundException( "Invalid primitive Class.Type()." );
    }

    private Object convPrimitive( Class type, String value )
        throws IllegalAccessException, ClassNotFoundException, 
               InstantiationException, NoSuchMethodException, InvocationTargetException
    {
        Object ret ;
        if( type.isPrimitive() )
        {
            if( type.equals(Boolean.TYPE) )
                ret = new Boolean( value );
            else if( type.equals(Double.TYPE) )
                ret = new Double( value );
            else if( type.equals(Integer.TYPE) )
                ret = new Integer( value );
            else if( type.equals(Short.TYPE) )
                ret = new Short( value );
            else if( type.equals(Byte.TYPE) )
                ret = new Byte( value );
            else if( type.equals(Float.TYPE) )
                ret = new Float( value );
            else if( type.equals(Long.TYPE ) )
                ret = new Long( value );
            else if( type.equals(Character.TYPE) )
                ret = new Character( value.charAt(0) );
            else if( type.equals( String.class ) )
                ret = value;
            else if( type == Void.TYPE )
                throw new IllegalAccessException( " Can not set void primitives." );
            else
                throw new IllegalAccessException( " Unknown primitive type." );
        }
        else
        {
            if( Boolean.class.isAssignableFrom(type) )
                ret = new Boolean( value );
            else if( Double.class.isAssignableFrom(type))
                ret = new Double( value );
            else if( Integer.class.isAssignableFrom(type))
                ret = new Integer( value );
            else if( Short.class.isAssignableFrom(type))
                ret = new Short( value );
            else if( Byte.class.isAssignableFrom(type))
                ret = new Byte( value );
            else if( Float.class.isAssignableFrom(type))
                ret = new Float( value );
            else if( Long.class.isAssignableFrom(type))
                ret = new Long( value );
            else if( Character.class.isAssignableFrom(type))
                ret = new Character( value.charAt(0) );
            else if( type.equals( String.class ) )
                ret = value;
            else if( Void.class.isAssignableFrom(type) )
                throw new IllegalAccessException( " Can not set void primitives." );
            else if( Number.class.isAssignableFrom(type))
            {
                ret = new Double( value );
            }
            else
            {
                // Class name is assumed in value...
                // create a new instance of this class and return as the 
                // value to assign
                ret = Class.forName(value).newInstance();
            }
        }
        return ret;
    }
    
    private Class expandNativeClass( Class nativeClass )
    {
        Class ret = nativeClass;
        
        if( nativeClass.equals(Boolean.TYPE) )
        {
            ret = Boolean.class;
        }
        else if( nativeClass.equals(Double.TYPE) )
        {
            ret = Double.class;
        }
        else if( nativeClass.equals(Integer.TYPE) )
        {
            ret = Integer.class;
        }
        else if( nativeClass.equals(Short.TYPE) )
        {
            ret = Short.class;
        }
        else if( nativeClass.equals(Byte.TYPE) )
        {
            ret = Byte.class;
        }
        else if( nativeClass.equals(Float.TYPE) )
        {
            ret = Float.class;
        }
        else if( nativeClass.equals(Long.TYPE ) )
        {
            ret = Long.class;
        }
        else if( nativeClass.equals(Character.TYPE) )
        {
            ret = Character.class;
        }
        return ret;
    }
    
    private Object getCollection( Object container, String memberName )
    {
        Object obj = null;
     
        String index = memberName.substring( 1, memberName.length()-1);
        int numindex = 0;
        try
        {   
            if( index.startsWith( "\'" ) )
                index = index.substring( 1, index.length() - 1);
            numindex = Integer.parseInt( index );
        } catch( NumberFormatException e ) {}  // Ignore
        
        if( container.getClass().isArray() )
        {
            obj = Array.get(container, numindex);
        }
        else if( container instanceof List )
        {
            obj = ((List) container).get(numindex);
        }
        else if( container instanceof Collection )
        {
            synchronized( container )
            {
                Object arr[] = ((Collection) container).toArray();
                if( numindex < arr.length )
                    obj = arr[numindex];
            }
        }
        else if( container instanceof Map )
        {
            synchronized( container )
            {
                obj = ((Map) container).get(index);
            }
        }
        else if( container instanceof Dictionary )
        {
            synchronized( container )
            {
                obj = ((Dictionary) container).get(index);
            }
        }
        return obj;
    }
    
    private void setCollection( Object container, String memberName, Object value )
        throws ReflectionException
    {
        try
        {
            Object obj = null;
            
            String index = memberName.substring( 1, memberName.length()-1);
            int numindex = 0;
            try
            {
                numindex = Integer.parseInt( index );            
            } catch( NumberFormatException e ){}  // Ignore
            synchronized( container )
            {
                if( container.getClass().isArray() )
                {
                    obj = Array.get(container, numindex );
                    if( ! obj.getClass().isInstance( value ) )
                    {
                        if( value instanceof String )
                            value = convPrimitive( obj.getClass(), (String) value);
                    }
                    Array.set(container, numindex, value);
                }
                else if( container instanceof List )
                {
                    obj = ((List) container).get(numindex);
                    if( ! obj.getClass().isInstance( value ) )
                    {
                        if( value instanceof String )
                            value = convPrimitive( obj.getClass(), (String) value);
                    }
                    ((List) container).set(numindex, value);
                }
                else if( container instanceof Collection )
                {
                    Iterator collection = ((Collection) container).iterator();
                    for( int i=0; i < numindex && collection.hasNext(); collection.next() );
                    obj = collection.next();
                    if( ! obj.getClass().isInstance( value ) )
                    {
                        if( value instanceof String )
                            value = convPrimitive( obj.getClass(), (String) value);
                    }
                    collection.remove();
                    ((Collection) container).add(value);
                }
                else if( container instanceof Map )
                {
                    index = index.substring( 1, index.length() - 1 );
                    obj = ((Map) container).get( index );
                    if( ! obj.getClass().isInstance( value ) )
                    {
                        if( value instanceof String )
                            value = convPrimitive( obj.getClass(), (String) value);
                    }
                    ((Map) container).put(index, value);
                }
                else if( container instanceof Dictionary )
                {
                    index = index.substring( 1, index.length() - 1 );
                    obj = ((Dictionary) container).get( index );
                    if( ! obj.getClass().isInstance( value ) )
                    {
                        if( value instanceof String )
                            value = convPrimitive( obj.getClass(), (String) value);
                    }
                    ((Dictionary) container).put(index, value);
                }
            }
        } catch( Exception e )
        {
            if( e instanceof ReflectionException )
                throw (ReflectionException) e.fillInStackTrace();
            else
                throw new ReflectionException( "Container=" + container + ", Member=" + memberName + ", Value=" + value, e );
        }
    }
    
    private Class getClassInCollection(Object container, String memberName)
    {
        String index = memberName.substring( 1, memberName.length()-1);
        int numindex = 0;
        try
        {   
            if( index.startsWith( "\'" ) )
                index = index.substring( 1, index.length() - 1);
            numindex = Integer.parseInt( index );
        } catch( NumberFormatException e ) {}  // Ignore
        
        Class result = null;
        Object obj;
        synchronized( container )
        {
            if( container.getClass().isArray() )
            {
            // If primitive types, Serialization does not work anyway,
            // so the expansion to wrapper classes is OK, for now.
                result = Array.get(container, numindex).getClass();
            }
            else if( container instanceof List )
            {
                obj = ((List) container).get(numindex);
                if( obj == null )
                    return null;
                result = obj.getClass();
            }
            else if( container instanceof Collection )
            {
                Object arr[] = ((Collection) container).toArray();
                if( numindex < arr.length )
                    result = arr[numindex].getClass();
                else
                result = null;
            }
            else if( container instanceof Map )
            {
                obj = ((Map) container).get(index);
                if( obj == null )
                    return null;
                result = obj.getClass();
            }
            else if( container instanceof Dictionary)
            {
                obj = ((Dictionary) container).get(index);
                if( obj == null )
                    return null;
                result = obj.getClass();
            }
    }
        return result;
    }

    private void setObject( Object container, String member, String value )
        throws ReflectionException
    {
        try
        {
            Object obj = null;
            Class cls = container.getClass();
            Field[] flds = cls.getDeclaredFields();
            
            if( member.startsWith("[") )
            {
                setCollection(container, member, value);
            }
            else
            {
                Method method = findMethod( container, "set" + member );
                if( method != null )
                {
                    try
                    {
                        Class[] argTypes = method.getParameterTypes(); 
                        Object arg = convPrimitive( argTypes[0], value);
                        Object[] args = { arg };
                        obj = method.invoke(container, args );
                    } catch( IllegalAccessException e )
                    {
                        throw new ReflectionException( container.getClass().toString() + ".set" + member + "() is not declared public.", e);
                    } catch( IllegalArgumentException e )
                    {
                        throw new ReflectionException( container.getClass().toString() + ".set" + member + "() .", e);
                    }
                }
                if( method == null )
                {
                    for( int i=0; i < flds.length ; i++ )
                    {
                        if( matchField( flds[i].getName(), member ) )
                        {
                            if( flds[i].getType().isPrimitive() )
                                setPrimitive( flds[i], container, value );
                            else
                                setObject( flds[i], container, value );
                        }
                    }
                }
            }
        } catch( Exception e )
        {
            if( e instanceof ReflectionException )
                throw (ReflectionException) e.fillInStackTrace();
            else
                throw new ReflectionException( "Container=" + container + ", Member=" + member + ", Value=" + value, e );
        }
    }
    
    private void setObject( Field fld, Object obj, Object value )
        throws IllegalAccessException, InvocationTargetException, 
        InstantiationException
    {
        // Create a new instance of the object
        // Assign instance to reference.
        Class fldClass = fld.getType();
        
        Constructor[] constructors = fldClass.getConstructors();
        Constructor constr = null;
        for( int j=0; j < constructors.length ; j++ )
        {
            Class[] params = constructors[j].getParameterTypes();
            if( params.length == 1 && params[0].getName().equals("String"))
            {
                constr = constructors[j];
                break;
            }
        }
        
        if( constr != null )
        {
            Object[] args = {value};
            Object newObj = constr.newInstance( args );
            fld.set(obj, newObj );
        }
        else
            throw new IllegalAccessException( "No valid Constructors available for object:" + fldClass );
    }   
         
    private Field findField( Object container, String memberName )
        throws NoSuchFieldException
    {
        Class cls = container.getClass();
        try
        {
            Field fld = cls.getDeclaredField( memberName );
            return fld;
        } catch( NoSuchFieldException e )
        {
            Field fld = cls.getDeclaredField( "m_" + memberName );
            return fld;
        }            
    }

    private Method findMethod( Object container, String methodName )
    {
        Method[] methods = container.getClass().getMethods();
        for( int i = 0; i < methods.length ; i++ )
        {
            if( methods[i].getName().equals( methodName ) )
                return methods[i];
        }
        return null;
    }

    private boolean matchField( String programmatic, String global )
    {
        int i = programmatic.indexOf( '_' );
        if( i >= 0 )
            programmatic = programmatic.substring( i + 1 );
        return global.equalsIgnoreCase( programmatic );
    }

    private boolean isPrimitive( Object obj )
    {
        // This doesn't seem to work!!!
        // Class cls = obj.getClass();
        // return cls.isPrimitive();
        
        // So need to do this????
        if( obj instanceof Boolean )
            return true;
        else if( obj instanceof Double )
            return true;
        else if( obj instanceof Integer )
            return true;
        else if( obj instanceof Long )
            return true;
        else if( obj instanceof Byte )
            return true;
        else if( obj instanceof Float )
            return true;
        else if( obj instanceof Character )
            return true;
        else if( obj instanceof Short )
            return true;
        else if( obj instanceof Void )
            return true;
        else
            return false;
    }

    private String[] getCollectionNames(Collection container)
    {
        String[] sa = new String[container.size()];
        for( int i=0; i < container.size() ; i++ )
            sa[i] = "" + i;
        return sa;
    }
    
    private String[] getMapNames( Map container)
    {
        String[] sa = new String[container.size()];
        int i=0;
        
        Iterator list = container.keySet().iterator();
        while( list.hasNext() )
        {
            sa[i++] = list.next().toString();
        }
        return sa;
    }
    
    private String[] getDictionaryNames( Dictionary container)
    {
        String[] sa = new String[container.size()];
        int i=0;
        
        Enumeration list = container.keys();
        while( list.hasMoreElements() )
        {
            sa[i++] = list.nextElement().toString();
        }
        return sa;
    }
    
    private String[] getArrayNames( Object[] container)
    {
        String[] sa = new String[container.length];
        for( int i=0 ; i < sa.length ; i++ )
            sa[i] = "" + i;
        return sa;
    }
    
    public synchronized void addReflectionListener( ReflectionListener listener )
    {
        Vector v;
        if( m_Listeners == null )
            v = new Vector();
        else
            v = (Vector) m_Listeners.clone();
        v.add( listener );
        m_Listeners = v;
    }
    
    public synchronized void removeReflectionListener( ReflectionListener listener )
    {
        if( m_Listeners == null )
            return;
        Vector v = (Vector) m_Listeners.clone();
        v.remove( listener );
        m_Listeners = v;
    }
    
    private void fireEvent( ReflectionEvent event )
    {
        // Use intermediary Vector reference for atomicity without synchronized
        Vector v = m_Listeners;
        
        if( v == null )
            return;
        for( int i=0; i < v.size() ; i++ )
        {
            ReflectionListener listener = (ReflectionListener) v.elementAt(i);
            listener.reflected( event );
        }
    }

    public void dispose()
    {
        if( m_Listeners != null )
            m_Listeners.removeAllElements();
        m_Listeners = null;
        
        m_RootObjects.clear();
        m_RootObjects = null;
    }
}
