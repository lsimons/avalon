

package org.apache.avalon.composition.model.test;

import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.AbstractTestCase;

public class DependencyTestCase extends AbstractTestCase
{      
   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public DependencyTestCase()
    {
        super( "dependency.xml" );
    }

   //-------------------------------------------------------
   // tests
   //-------------------------------------------------------

   /**
    * Validate the composition model.
    */
    public void testStandardDependencyModel() throws Exception
    {
        DeploymentModel a = (DeploymentModel) m_model.getModel( "test-a" );

        ContainmentModel fred = (ContainmentModel) m_model.getModel( "fred" );
        DeploymentModel b = (DeploymentModel) fred.getModel( "test-b" );
        DeploymentModel c = (DeploymentModel) fred.getModel( "test-c" );

        if( c == null )
        {
            fail( "null deployment model 'test-c'" );
        }

        DependencyModel[] deps = c.getDependencyModels();
        assertTrue( "dependency count", deps.length == 3 );

        for( int i=0; i<deps.length; i++ )
        {
            DependencyModel dep = deps[i];
            String key = dep.getDependency().getKey();
            String path = dep.getPath();
            assertNotNull( "selection for key: " + key, path );
        }
    }
}
