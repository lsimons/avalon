/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */

package org.apache.avalon.cornerstone.blocks.soapification;

import java.lang.reflect.Method;

public class DynamicProxy implements java.lang.reflect.InvocationHandler {

  protected Object mObj;

  public DynamicProxy(Object obj) 
  {
      mObj = obj;
  }
	 
  public static Object newInstance(Object obj) 
  {
      return java.lang.reflect.Proxy.newProxyInstance(
             obj.getClass().getClassLoader(),
             obj.getClass().getInterfaces(),
             new DynamicProxy(obj));
  }

  public static Object newInstance(Object obj, Class[] interfacesToPublish) 
  {
      return java.lang.reflect.Proxy.newProxyInstance(
             obj.getClass().getClassLoader(),
             interfacesToPublish,
             new DynamicProxy(obj));
  }

  public Object invoke( Object o, Method method, Object[] args ) throws Throwable {
      return method.invoke( mObj, args );
  }

}



