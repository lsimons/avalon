/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.

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

package org.apache.avalon.composition.data.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.avalon.composition.data.BlockIncludeDirective;
import org.apache.avalon.composition.data.BlockCompositionDirective;
import org.apache.avalon.composition.data.DeploymentProfile;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.ComponentProfile;
import org.apache.avalon.composition.data.ClassLoaderDirective;
import org.apache.avalon.composition.data.ClasspathDirective;
import org.apache.avalon.composition.data.LibraryDirective;
import org.apache.avalon.composition.data.ResourceDirective;
import org.apache.avalon.composition.data.RepositoryDirective;
import org.apache.avalon.composition.data.ServiceDirective;
import org.apache.avalon.composition.data.FilesetDirective;
import org.apache.avalon.composition.data.IncludeDirective;

/**
 * Write {@link ContainmentProfile} objects to a stream as xml documents.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/13 11:41:25 $
 */
public class XMLContainmentProfileWriter extends XMLComponentProfileWriter
{
    /**
     * Write out a containment profile to xml.
     *
     * @param profile the type object
     * @param output the stream to write to
     * @throws Exception if unable to write xml
     */
    public void writeContainmentProfile( 
      final ContainmentProfile profile, final OutputStream output )
      throws Exception
    {
        final Writer writer = new OutputStreamWriter( output );
        writeHeader( writer, profile );
        writeContainmentProfile( writer, profile, true, "" );
        writer.flush();
    }

    /**
     * Write out a containment profile to xml.
     *
     * @param writer the stream to write to
     * @param profile the containment profile to write
     * @param pad character offset
     * @throws Exception if unable to write xml
     */
    protected void writeContainmentProfile( 
      final Writer writer, final ContainmentProfile profile, String pad )
      throws Exception
    {
        writeContainmentProfile( writer, profile, false, pad );
    }

    /**
     * Write out a containment profile to xml.
     *
     * @param writer the stream to write to
     * @param profile the containment profile to write
     * @param flag not used !
     * @param pad character offset
     * @throws Exception if unable to write xml
     */
    protected void writeContainmentProfile( 
      final Writer writer, final ContainmentProfile profile, boolean flag, String pad )
      throws Exception
    {
        writer.write( "\n" );
        writer.write( pad + "<container name=\"" + profile.getName() + "\">");
        final String padding = pad + INDENT;
        writeServiceDirectives( writer, profile.getExportDirectives(), padding );
        writeClassLoader( writer, profile.getClassLoaderDirective(), padding );
        writeCategories( writer, profile.getCategories(), padding );
        writeProfiles( writer, profile.getProfiles(), padding );
        writer.write( "\n" + pad + "</container>" );
        writer.write( "\n" );
    }

   /**
    * Write the XML header.
    * @param writer the writer
    * @param profile the containment profile
    * @throws IOException if unable to write xml
    */
    private void writeHeader( 
      final Writer writer, ContainmentProfile profile )
        throws IOException
    {
        writer.write( "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" );
    }

    /**
     * Write out xml representation of the info descriptor from a Type.
     *
     * @param writer the writer
     * @param services the service directives
     * @param pad character offset
     * @throws IOException if unable to write xml
     */
    private void writeServiceDirectives( 
      final Writer writer, final ServiceDirective[] services, String pad )
      throws IOException
    {
        if( 0 == services.length )
        {
            return;
        }

        writer.write( "\n" + pad + "<services>" );
        final String padding = pad + INDENT;
        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDirective service = services[ i ];
            writer.write( "\n" + padding + "<service type=\"" );
            writer.write( service.getReference().getClassname() );
            if( service.getReference().getVersion().getMajor() > -1 )
            {
                writer.write( "\" version=\"" + service.getReference().getVersion() );
            }
            writer.write( "\">" );
            writer.write( "\n" + padding + INDENT 
              + "<source>" 
              + service.getPath()
              + "</source>" );
            writer.write( "\n" + padding + "</service>" );
        }
        writer.write( "\n" + pad + "</services>" );
    }

    /**
     * Write out xml representation of the classloader
     *
     * @param writer the writer
     * @param classloader the classloader directive
     * @param pad character offset
     * @throws IOException if unable to write xml
     */
    private void writeClassLoader( 
      final Writer writer, final ClassLoaderDirective classloader, String pad )
      throws IOException
    {
        if( classloader.isEmpty() ) return;

        writer.write( "\n" + pad + "<classloader>" );
        final String padding = pad + INDENT;

        LibraryDirective library = classloader.getLibrary();
        writeLibrary( writer, library, padding );

        ClasspathDirective classpath = classloader.getClasspathDirective();
        writeClasspathDirective( writer, classpath, padding );

        writer.write( "\n" + pad + "</classloader>" );
    }

    /**
     * Write out xml representation of a set of includes.
     *
     * @param writer the writer
     * @param library the library directives
     * @param pad character offset
     * @throws IOException if unable to write xml
     */
    private void writeLibrary( 
      final Writer writer, final LibraryDirective library, String pad )
      throws IOException
    {
        if( library.isEmpty() ) return;

        final String padding = pad + INDENT;
        writer.write( "\n" + pad + "<library>" );
        String[] includes = library.getIncludes();
        for( int i=0; i<includes.length; i++ )
        {
            writer.write( "\n" + padding
              + "<include>" + includes[i] + "</include>" );
        }

        String[] groups = library.getGroups();
        for( int i=0; i<groups.length; i++ )
        {
            writer.write( "\n" + padding
              + "<group>" + groups[i] + "</group>" );
        }

        writer.write( "\n" + pad + "</library>" );
    }

    /**
     * Write out xml representation of a classpath.
     *
     * @param writer the writer
     * @param classpath the classpath directives
     * @param pad character offset
     * @throws IOException if unable to write xml
     */
    private void writeClasspathDirective( 
      final Writer writer, 
      final ClasspathDirective classpath,
      String pad )
      throws IOException
    {
        if( classpath.isEmpty() ) return;

        final String padding = pad + INDENT;
        writer.write( "\n" + pad + "<classpath>" );

        FilesetDirective[] filesets = classpath.getFilesets();
        if( filesets.length > 0 )
        {
            for( int i=0; i<filesets.length; i++ )
            {
                writeFilesetDirective( writer, filesets[i], padding );
            }
        }

        RepositoryDirective[] repositories = classpath.getRepositoryDirectives();
        if( repositories.length > 0 )
        {
            for( int i=0; i<repositories.length; i++ )
            {
                writeRepositoryDirective( writer, repositories[i], padding );
            }
        }

        writer.write( "\n" + pad + "</classpath>" );
    }

    /**
     * Write out xml representation of a repository.
     *
     * @param writer the writer
     * @param repository the repository directives
     * @param pad character offset
     * @throws IOException if unable to write xml
     */
    private void writeRepositoryDirective( 
      final Writer writer, 
      final RepositoryDirective repository,
      String pad )
      throws IOException
    {
        ResourceDirective[] resources = repository.getResources();
        if( resources.length > 0 )
        {
            final String padding = pad + INDENT;
            writer.write( "\n" + pad + "<repository>" );
            for( int i=0; i<resources.length; i++ )
            {
                writeResourceDirective( writer, resources[i], padding  );
            }
            writer.write( "\n" + pad + "</repository>" );
        }
    }

    /**
     * Write out xml representation of a fileset.
     *
     * @param writer the writer
     * @param fileset the fileset directives
     * @param pad character offset
     * @throws IOException if unable to write xml
     */
    private void writeFilesetDirective( 
      final Writer writer, final FilesetDirective fileset, String pad )
      throws IOException
    {
        writer.write( "\n" + pad 
          + "<fileset dir=\"" 
          + fileset.getBaseDirectory() + "\">" );

        IncludeDirective[] includes = fileset.getIncludes();
        for( int i=0; i<includes.length; i++ )
        {
            writer.write( "\n" + pad + INDENT 
                  + "<include>" 
                  + includes[i].getPath() 
                  + "</include>" );
        }

        writer.write( "\n" + pad + "</fileset>" );
    }


    /**
     * Write out xml representation of the classloader
     *
     * @param writer the writer
     * @param resource a resource directive
     * @param pad character offset
     * @throws IOException if unable to write xml
     */
    private void writeResourceDirective( 
      final Writer writer, final ResourceDirective resource, String pad )
      throws IOException
    {
        writer.write( "\n" + pad + "<resource" );
        writer.write( " id=\"" + resource.getId() + "\"" );
        if( resource.getVersion() != null )
        {
            writer.write( " version=\"" + resource.getVersion() + "\"" );
            if( !resource.getType().equals( "jar" ) )
            {
                writer.write( " type=\"" + resource.getType() + "\"" );
            }
        }
        writer.write( "/>" );
    }

    /**
     * Write out xml representation of the embedded profiles
     *
     * @param writer the writer
     * @param profiles the nested profiles
     * @param pad character offset
     * @throws IOException if unable to write xml
     */
    private void writeProfiles( 
      final Writer writer, final DeploymentProfile[] profiles, String pad )
      throws Exception
    {
        for( int i=0; i<profiles.length; i++ )
        {
            DeploymentProfile profile = profiles[i];
            if( profile instanceof ContainmentProfile )
            {
                ContainmentProfile container = (ContainmentProfile) profile;
                writer.write( "\n" );
                writeContainmentProfile( writer, container, pad );
            }
            else if( profile instanceof ComponentProfile )
            {
                ComponentProfile component = (ComponentProfile) profile;
                writer.write( "\n" );
                writeComponentProfile( writer, component, pad );
            }
            else if( profile instanceof BlockIncludeDirective )
            {
                BlockIncludeDirective directive = (BlockIncludeDirective) profile;
                writer.write( "\n" );
                writeBlockIncludeDirective( writer, directive, pad );
            }
            else if( profile instanceof BlockCompositionDirective )
            {
                BlockCompositionDirective directive = (BlockCompositionDirective) profile;
                writer.write( "\n" );
                writeBlockCompositionDirective( writer, directive, pad );
            }
            else
            {
                throw new IllegalArgumentException( 
                  "Unrecognized profile class: " 
                  + profile.getClass().getName() );
            }
        }
    }

    private void writeBlockIncludeDirective( 
      final Writer writer, final BlockIncludeDirective directive, String pad )
      throws Exception
    {
        writer.write( "\n" + pad + "<include name=\"" );
        writer.write( directive.getName() + "\">" );
        writer.write( "\n" + pad + INDENT + "<source>" );
        writer.write( directive.getPath() + "</source>" );
        writer.write( "\n" + pad + "</include>" );
    }

    private void writeBlockCompositionDirective( 
      final Writer writer, final BlockCompositionDirective directive, String pad )
      throws Exception
    {
        writer.write( "\n" + pad + "<include name=\"" );
        writer.write( directive.getName() + "\" id=\"" );
        writer.write( directive.getResource().getId() + "\" version=\"" );
        writer.write( directive.getResource().getVersion() + "\"/>" );
    }
}
