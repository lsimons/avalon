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

package org.apache.metro.transit;

import junit.framework.TestCase;


/**
 * Create of a new Artifact test case.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: ArtifactTest.java 30977 2004-07-30 08:57:54Z niclas $
 */
public class ArtifactTestCase extends TestCase
{
    /**
     * Constructor for ArtifactReferenceTest.
     * @param name the test name
     */
    public ArtifactTestCase( String name )
    {
        super( name );
    }

    public void testNullPathConstructor() throws Exception
    {
        try
        {
            Artifact artifact = Artifact.createArtifact( (String) null );
            fail( "No null pointer exception not thown for null uri spec." );
        }
        catch( NullPointerException e )
        {
            assertTrue( true );
        }
    }

    public void testMissingProtocol() throws Exception
    {
        try
        {
            Artifact artifact = Artifact.createArtifact( "jar:/group/sub-group/name#version" );
            fail( "Illegal argument exception not thrown for missing protocol." );
        }
        catch( IllegalArgumentException e )
        {
            assertTrue( true );
        }
    }

    public void testBadGroup() throws Exception
    {
        try
        {
            Artifact artifact = Artifact.createArtifact( "artifact:jar://sub-group/name#version" );
            fail( "Illegal argument exception not thrown for bad group." );
        }
        catch( IllegalArgumentException e )
        {
            assertTrue( true );
        }
    }

    public void testAnotherBadGroup() throws Exception
    {
        try
        {
            Artifact artifact = Artifact.createArtifact( "artifact:jar:group//name#version" );
            fail( "Illegal argument exception not thrown for bad group." );
        }
        catch( IllegalArgumentException e )
        {
            assertTrue( true );
        }
    }

    public void testBadVersion() throws Exception
    {
        try
        {
            Artifact artifact = Artifact.createArtifact( "artifact:jar:group/name#version/xxx" );
            list( artifact );
            fail( "Illegal argument exception not thrown for bad version." );
        }
        catch( IllegalArgumentException e )
        {
            assertTrue( true );
        }
    }

    public void testFullSpec() throws Exception
    {
        Artifact artifact = Artifact.createArtifact( "artifact:jar:group/sub-group/name#version");
        verify( artifact, "group/sub-group", "name", "jar", "version" );
    }

    public void testLeadingSlash() throws Exception
    {
        Artifact artifact = Artifact.createArtifact( "artifact:jar:/group/sub-group/name#version");
        verify( artifact, "group/sub-group", "name", "jar", "version" );
    }

    public void testNullType() throws Exception
    {
        Artifact artifact = Artifact.createArtifact( "artifact:group/sub-group/name#version");
        verify( artifact, "group/sub-group", "name", "jar", "version" );
    }

    public void testNullVersion() throws Exception
    {
        Artifact artifact = Artifact.createArtifact( "artifact:jar:group/sub-group/name");
        verify( artifact, "group/sub-group", "name", "jar", null );
    }

    public void testNullVersionAndNullType() throws Exception
    {
        Artifact artifact = Artifact.createArtifact( "artifact:group/sub-group/name");
        verify( artifact, "group/sub-group", "name", "jar", null );
    }

    public void testMissingGroup() throws Exception
    {
        try
        {
            Artifact artifact = Artifact.createArtifact( "artifact:jar:name#version" );
            fail( "Illegal argument exception not thrown." );
        }
        catch( Throwable e )
        {
            assertTrue( true );
        }
    }

    public void testMissingGroupWithoutType() throws Exception
    {
        try
        {
            Artifact artifact = Artifact.createArtifact( "artifact:name#version" );
            fail( "Illegal argument exception not thrown." );
        }
        catch( Throwable e )
        {
            assertTrue( true );
        }
    }

    public void testZeroLengthVersion() throws Exception
    {
        Artifact artifact = Artifact.createArtifact( "artifact:jar:group/sub-group/name#");
        verify( artifact, "group/sub-group", "name", "jar", null );
    }

    public void testExternalForm() throws Exception
    {
        final String spec = "artifact:jar:group/sub-group/name#version";
        Artifact artifact = Artifact.createArtifact( spec );
        assertEquals( artifact.toString(), spec );
    }

    public void testExternalFormWithNonDefaultType() throws Exception
    {
        final String spec = "artifact:block:group/sub-group/name#version";
        Artifact artifact = Artifact.createArtifact( spec );
        assertEquals( artifact.toString(), spec );
    }

    public void testEquality() throws Exception
    {
        final String spec = "artifact:jar:group/sub-group/name#version";
        Artifact artifact1 = Artifact.createArtifact( spec );
        Artifact artifact2 = Artifact.createArtifact( spec );
        assertTrue( artifact1.equals( artifact2 ) );
    }

    public void testInequality() throws Exception
    {
        final String spec1 = "artifact:jar:group/sub-group/name#version";
        final String spec2 = "artifact:jar:group/sub-group/name";
        Artifact artifact1 = Artifact.createArtifact( spec1 );
        Artifact artifact2 = Artifact.createArtifact( spec2 );
        assertFalse( artifact1.equals( artifact2 ) );
    }

    public void testInequalityOnType() throws Exception
    {
        final String spec1 = "artifact:group/sub-group/name#version";
        final String spec2 = "artifact:block:group/sub-group/name#version";
        Artifact artifact1 = Artifact.createArtifact( spec1 );
        Artifact artifact2 = Artifact.createArtifact( spec2 );
        assertFalse( artifact1.equals( artifact2 ) );
    }

    public void testCoparability() throws Exception
    {
        final String spec1 = "artifact:aaa/name";
        final String spec2 = "artifact:bbb/name";
        Artifact artifact1 = Artifact.createArtifact( spec1 );
        Artifact artifact2 = Artifact.createArtifact( spec2 );

        assertTrue( artifact1.compareTo( artifact2 ) < 0 );
        assertTrue( artifact2.compareTo( artifact1 ) > 0 );
        assertTrue( artifact1.compareTo( artifact1 ) == 0 );
        assertTrue( artifact2.compareTo( artifact2 ) == 0 );
    }

    public void verify( Artifact artifact, String group, String name, String type, String version )
    {
        assertEquals( "group", group, artifact.getGroup() );
        assertEquals( "name", name, artifact.getName() );
        assertEquals( "version", version, artifact.getVersion() );
        assertEquals( "type", type, artifact.getType() );

        String base = group + "/" + type + "s";
        assertEquals( "base", base, artifact.getBase() );

        if( null == version )
        {
            String path = base + "/" + name + "." + type;
            assertEquals( "path", path, artifact.getPath() );
        }
        else
        {
            String path = base + "/" + name + "-" + version + "." + type;
            assertEquals( "path", path, artifact.getPath() );
        }
    }

    public void list( Artifact artifact )
    {        
        System.out.println( "GROUP: " + artifact.getGroup() );
        System.out.println( "NAME: " + artifact.getName() );
        System.out.println( "TYPE: " + artifact.getType() );
        System.out.println( "VERSION: " + artifact.getVersion() );
    }
}
