/* 
 * Copyright  The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */ 
package org.apache.excalibur.proxy.test;

import org.apache.excalibur.proxy.ProxyGenerator;
import org.apache.testlet.AbstractTestlet;
import org.apache.testlet.TestFailedException;  
 
/** 
 * This is used to test Proxy generation for correctness. 
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a> 
 */ 
public final class ProxyGeneratorTestlet 
    extends AbstractTestlet 
{  
    public interface Interface1 
    { 
        void method1();
    }

    public interface Interface2 extends Interface1
    { 
        void method2();
    } 

    public interface Interface3
    { 
        void method3( int x );
    }

    public interface Interface4
    { 
        void method4( String x );
    }

    public interface Interface5
    { 
        void method3( String x );
    }
 
    public interface Interface6
    { 
        void method2();
    } 

    public interface Interface7
    { 
        void method3( double x );
    }

    public interface Interface8
    { 
        void method3( double x, double y );
    }

    public interface Interface9
    { 
        int method4( double x, double y );
    }


    public interface Interface10
    { 
        double method10( double x, double y );
    }

    public static class ClassA 
        implements Interface1, Interface3, Interface4, Interface5, Interface9, Interface10
    { 
        public void method1() {}
        public void method3( int x ) {}
        public void method3( String x ) {}
        public void method4( String x ) {}
        public int method4( double x, double y ) { return 0; }
        public double method10( double x, double y ) { return 0.0; }
    } 

    public static class ClassB implements Interface2, Interface6, Interface7, Interface8
    { 
        public void method1() {}
        public void method2() {}
        public void method3( double x ) {}
        public void method3( double x, double y ) {}
    } 

    public void testNoParamMethod() 
        throws Exception 
    { 
        final Class[] interfaces = new Class[] { Interface1.class };
        final Object object = new ClassA();
        final Object result = doTest( object, interfaces );
        ((Interface1)result).method1();
    }

    public void testExtendedInterfaceHidden() 
        throws Exception 
    { 
        final Class[] interfaces = new Class[] { Interface1.class };
        final Object object = new ClassB();
        final Object result = doTest( object, interfaces );
        ((Interface1)result).method1();
        assert( !(result instanceof Interface2) );
    }

    public void testExtendedInterface() 
        throws Exception 
    { 
        final Class[] interfaces = new Class[] { Interface1.class, Interface2.class };
        final Object object = new ClassB();
        final Object result = doTest( object, interfaces );
        ((Interface1)result).method1();
        ((Interface2)result).method1();
        ((Interface2)result).method2();
    }

    public void testIntParamInterface() 
        throws Exception 
    { 
        final Class[] interfaces = new Class[] { Interface3.class };
        final Object object = new ClassA();
        final Object result = doTest( object, interfaces );
        ((Interface3)result).method3(2);
    }

    public void testStringParamInterface() 
        throws Exception 
    { 
        final Class[] interfaces = new Class[] { Interface4.class };
        final Object object = new ClassA();
        final Object result = doTest( object, interfaces );
        ((Interface4)result).method4("Hello");
    }

    public void testOverloadedStringParamInterface() 
        throws Exception 
    { 
        final Class[] interfaces = new Class[] { Interface5.class };
        final Object object = new ClassA();
        final Object result = doTest( object, interfaces );
        ((Interface5)result).method3("Hello");
    }

    public void testDuplicateMethodInterface() 
        throws Exception 
    { 
        final Class[] interfaces = new Class[] { Interface2.class, Interface6.class };
        final Object object = new ClassB();
        final Object result = doTest( object, interfaces );
        ((Interface6)result).method2();
        ((Interface2)result).method2();
    }

    public void testDoubleParamInterface() 
        throws Exception 
    { 
        final Class[] interfaces = new Class[] { Interface7.class };
        final Object object = new ClassB();
        final Object result = doTest( object, interfaces );
        ((Interface7)result).method3(2.0);
    }

    public void test2DoubleParamInterface() 
        throws Exception 
    { 
        final Class[] interfaces = new Class[] { Interface8.class };
        final Object object = new ClassB();
        final Object result = doTest( object, interfaces );
        ((Interface8)result).method3(2.0,2.0);
    }

    public void testIntReturnInterface() 
        throws Exception 
    { 
        final Class[] interfaces = new Class[] { Interface9.class };
        final Object object = new ClassA();
        final Object result = doTest( object, interfaces );
        final int x = ((Interface9)result).method4(2.0,2.0);
    }

    public void testDoubleReturnInterface() 
        throws Exception 
    { 
        final Class[] interfaces = new Class[] { Interface10.class };
        final Object object = new ClassA();
        final Object result = doTest( object, interfaces );
        final double x = ((Interface10)result).method10(2.0,2.0);
    }

    protected Object doTest( final Object object, final Class[] interfaces )
        throws Exception 
    {
        final Object result =
            ProxyGenerator.generateProxy( object, interfaces );

        if( null == result )
        {
            throw new TestFailedException( "Proxy object failed to be created." );
        }

        for( int i = 0; i < interfaces.length; i++ )
        {
            if( !interfaces[ i ].isInstance( result ) )
            {
                throw new TestFailedException( "Interface " + interfaces[ i ] +
                                               " not implemented by proxy." );
            }
        }
        
        return result;
    }
}




