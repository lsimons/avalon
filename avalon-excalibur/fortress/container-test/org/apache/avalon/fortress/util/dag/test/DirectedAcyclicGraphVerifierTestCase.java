/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.avalon.fortress.util.dag.test;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.avalon.fortress.util.dag.*;


/**
 * DirectedAcyclicGraphVerifierTestCase.java does XYZ
 *
 * @author <a href="bloritsch.at.d-haven.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public class DirectedAcyclicGraphVerifierTestCase extends TestCase
{
    public DirectedAcyclicGraphVerifierTestCase( String name )
    {
        super( name );
    }

    public void testIsDAG()
    {
        try
        {
            Vertex root = new Vertex( "Root" );
            root.addDependency( new Vertex( "Child1" ) );
            root.addDependency( new Vertex( "Child2" ) );

            DirectedAcyclicGraphVerifier.verify( root );
        }
        catch ( CyclicDependencyException cde )
        {
            fail( "Incorrectly found a Cycle" );
        }

        try
        {
            Vertex root = new Vertex( "Root" );
            root.addDependency( new Vertex( "Child1" ) );
            root.addDependency( new Vertex( "Child2" ) );

            Vertex child3 = new Vertex( "Child3" );
            child3.addDependency( root );

            root.addDependency( child3 );

            DirectedAcyclicGraphVerifier.verify( root );

            fail( "Incorrectly missed the Cycle" );
        }
        catch ( CyclicDependencyException cde )
        {
            // Success!
        }
    }

    /**
     * This test waas written to test the algorithm used to search for cycles.
     *  It makes sure that cycles that start a ways into the dependency tree
     *  are handled correctly.
     */
    public void testCycleTest() throws Exception
    {
        Vertex component1 = new Vertex( "Component1" );
        Vertex component2 = new Vertex( "Component2" );
        Vertex component3 = new Vertex( "Component3" );
        Vertex component4 = new Vertex( "Component4" );
        Vertex component5 = new Vertex( "Component5" );
        
        List vertices = new ArrayList( 5 );
        vertices.add( component1 );
        vertices.add( component2 );
        vertices.add( component3 );
        vertices.add( component4 );
        vertices.add( component5 );
        
        component1.addDependency( component2 );
        component2.addDependency( component3 );
            
        component3.addDependency( component4 );
        component4.addDependency( component5 );
        component5.addDependency( component3 ); // Cycle
        
        try
        {
            DirectedAcyclicGraphVerifier.topologicalSort( vertices );
            fail( "Did not detect the expected cyclic dependency" );
        }
        catch ( CyclicDependencyException cde )
        {
            //Success!
        }
    }
        
    
    public void testSortDAG() throws Exception
    {
        Vertex component1 = new Vertex( "Component1" );
        Vertex component2 = new Vertex( "Component2" );
        Vertex component3 = new Vertex( "Component3" );
        Vertex component4 = new Vertex( "Component4" );
        Vertex component5 = new Vertex( "Component5" );

        component1.addDependency( component2 );
        component1.addDependency( component3 );

        component3.addDependency( component4 );

        component5.addDependency( component2 );
        component5.addDependency( component4 );

        List vertices = new ArrayList( 5 );
        vertices.add( component1 );
        vertices.add( component2 );
        vertices.add( component3 );
        vertices.add( component4 );
        vertices.add( component5 );

        DirectedAcyclicGraphVerifier.topologicalSort( vertices );
        verifyGraphOrders( vertices );
        verifyListOrder( vertices );

        Collections.shuffle( vertices );
        DirectedAcyclicGraphVerifier.topologicalSort( vertices );
        verifyGraphOrders( vertices );
        verifyListOrder( vertices );

        component4.addDependency( component1 );
        Collections.shuffle( vertices );

        try
        {
            DirectedAcyclicGraphVerifier.topologicalSort( vertices );
            fail( "Did not detect the expected cyclic dependency" );
        }
        catch ( CyclicDependencyException cde )
        {
            //Success!
        }
    }
    
    private void verifyGraphOrders( List vertices )
    {
        for ( Iterator iter = vertices.iterator(); iter.hasNext(); )
        {
            Vertex v = (Vertex)iter.next();
            
            // Make sure that the orders of all dependencies are less than
            //  the order of v.
            for ( Iterator iter2 = v.getDependencies().iterator(); iter2.hasNext(); )
            {
                Vertex dv = (Vertex)iter2.next();
                assertTrue( "The order of " + dv.getName() + " (" + dv.getOrder() + ") should be "
                    + "less than the order of " + v.getName() + " (" + v.getOrder() + ")",
                    dv.getOrder() < v.getOrder() );
            }
        }
    }
    
    private void verifyListOrder( List vertices )
    {
        Vertex[] ary = new Vertex[vertices.size()];
        vertices.toArray( ary );
        for ( int i = 1; i < ary.length; i++ )
        {
            assertTrue( "The order of vertex #" + ( i - 1 ) + " (" + ary[i - 1].getOrder() + ") "
                + "should be <= the order of vertex #" + ( i ) + " (" + ary[i].getOrder() + ")",
                ary[i - 1].getOrder() <= ary[i].getOrder() );
        }
    }
}
