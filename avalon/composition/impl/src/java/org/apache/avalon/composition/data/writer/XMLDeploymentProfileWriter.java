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
import java.io.Writer;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.composition.data.DeploymentProfile;
import org.apache.avalon.composition.data.ContextDirective;
import org.apache.avalon.composition.data.DependencyDirective;
import org.apache.avalon.composition.data.SelectionDirective;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.data.CategoryDirective;
import org.apache.avalon.composition.data.ImportDirective;
import org.apache.avalon.composition.data.EntryDirective;
import org.apache.avalon.composition.data.ConstructorDirective;
import org.apache.avalon.composition.data.StageDirective;
import org.apache.avalon.composition.data.Parameter;
import org.apache.avalon.meta.info.InfoDescriptor;
import org.apache.excalibur.configuration.ConfigurationUtil;

/**
 * Write a {@link DeploymentProfile} to a stream as xml documents.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2003/10/28 20:21:00 $
 */
public class XMLDeploymentProfileWriter
{

    public static final String INDENT = "  ";

   /**
    * Write out a containment deployment profile to XML.
    * @param writer the writer
    * @throws IOException if unable to write xml
    */
    protected void writeDeploymentProfile( 
      final Writer writer, DeploymentProfile profile, String pad )
      throws Exception
    {
        //
        // write the component declaration
        //

        writer.write( pad + "<component name=\"" + profile.getName() + "\"");
        writer.write( "\n" + pad + "  class=\"" + profile.getClassname() + "\"");

        if( !profile.getActivationPolicy() )
        {
            writer.write( 
              "\n" + pad 
              + "  activation=\"false\"" ); 
        }

        final int collection = profile.getCollectionPolicy();
        if( collection > InfoDescriptor.UNDEFINED )
        {
            writer.write( 
              "\n" + pad 
              + "  collection=\"" 
              + InfoDescriptor.getCollectionPolicyKey( collection ) 
              + "\"" ); 
        }

        if(( profile.getCategories() == null ) && ( profile.getContext() == null )
         && ( isEmptyConfiguration( profile.getConfiguration() ) ) && ( profile.getParameters() == null ))
        {
            writer.write( "/>");
        }
        else
        {
            writer.write( ">" );
            final String padding = pad + INDENT;
            writeDeploymentBody( writer, profile, padding );
            writer.write( "\n" + pad + "</component>" );
        }
    }

    private boolean isEmptyConfiguration( Configuration config )
    {
        if( config == null ) return true;
        if( config.getChildren().length > 0 ) return false;
        return config.getAttributeNames().length == 0;
    }

   /**
    * Write out a containment deployment content.
    * @param writer the writer
    * @throws IOException if unable to write xml
    */
    protected void writeDeploymentBody( 
      final Writer writer, DeploymentProfile profile, String pad )
      throws Exception
    {
        //
        // write out the lifecycle directives
        //
     
        writeCategories( writer, profile.getCategories(), pad );
        writeContext( writer, profile.getContext(), pad );
        writeDependencies( writer, profile.getDependencyDirectives(), pad );
        writeStages( writer, profile.getStageDirectives(), pad );
        writeConfiguration( writer, profile.getConfiguration(), pad );
        writeParameters( writer, profile.getParameters(), pad );
    }

    /**
     * Write out xml representation of the dependency directives.
     *
     * @param writer the writer
     * @param dependencies the dependency directives
     * @throws IOException if unable to write xml
     */
    private void writeDependencies( 
      final Writer writer, final DependencyDirective[] dependencies, String pad )
      throws IOException
    {
        if( dependencies.length == 0 ) return;
        writer.write( "\n" + pad + "<dependencies>" );
        final String padding = pad + INDENT;
        for( int i=0; i<dependencies.length; i++ )
        {
            writeDependency( writer, dependencies[i], padding );
        }
        writer.write( "\n" + pad + "</dependencies>" );
    }

    /**
     * Write out xml representation of a dependency directive.
     *
     * @param writer the writer
     * @param dependency the dependency directive
     * @throws IOException if unable to write xml
     */
    private void writeDependency( 
      final Writer writer, final DependencyDirective dependency, String pad )
      throws IOException
    {
        writer.write( "\n" + pad + "<dependency key=\"" + dependency.getKey() + "\"" );
        if( dependency.getSource() != null )
        {
            writer.write( " source=\"" + dependency.getSource() + "\"/>" );
        }
        else
        {
            writer.write( ">" );
            SelectionDirective[] features = dependency.getSelectionDirectives();
            final String padding = pad + INDENT;
            for( int i=0; i<features.length; i++ )
            {
                writeSelectionDirective( writer, features[i], padding );
            }
            writer.write( "\n" + pad + "</dependency>" );
        }
    }

    /**
     * Write out xml representation of the dependency directives.
     *
     * @param writer the writer
     * @param dependencies the dependency directives
     * @throws IOException if unable to write xml
     */
    private void writeStages( 
      final Writer writer, final StageDirective[] stages, String pad )
      throws IOException
    {
        if( stages.length == 0 ) return;
        writer.write( "\n" + pad + "<stages>" );
        final String padding = pad + INDENT;
        for( int i=0; i<stages.length; i++ )
        {
            writeStage( writer, stages[i], padding );
        }
        writer.write( "\n" + pad + "</stages>" );
    }

    /**
     * Write out xml representation of a stage directive.
     *
     * @param writer the writer
     * @param stage the stage directive
     * @throws IOException if unable to write xml
     */
    private void writeStage( 
      final Writer writer, final StageDirective stage, String pad )
      throws IOException
    {
        writer.write( "\n" + pad + "<stage key=\"" + stage.getKey() + "\"" );
        if( stage.getSource() != null )
        {
            writer.write( " source=\"" + stage.getSource() + "\"/>" );
        }
        else
        {
            writer.write( ">" );
            SelectionDirective[] features = stage.getSelectionDirectives();
            final String padding = pad + INDENT;
            for( int i=0; i<features.length; i++ )
            {
                writeSelectionDirective( writer, features[i], padding );
            }
            writer.write( "\n" + pad + "</stage>" );
        }
    }

    /**
     * Write out xml representation of a selection directive.
     *
     * @param writer the writer
     * @param feature the dependency selection directive
     * @throws IOException if unable to write xml
     */
    private void writeSelectionDirective( 
      final Writer writer, final SelectionDirective feature, String pad )
      throws IOException
    {
        writer.write( "\n" + pad 
          + "<select feature=\"" + feature.getFeature() 
          + "\" value=\"" + feature.getValue() 
          + "\" match=\"" + feature.getCriteria() 
          + "\"" );
        if( feature.isOptional() )
        {
            writer.write( " optional=\"true\"/>" );
        }
        else
        {
            writer.write( "/>" );
        }
    }

    /**
     * Write out xml representation of the logging categories
     *
     * @param writer the writer
     * @param categories the logging categopries directive
     * @throws IOException if unable to write xml
     */
    protected void writeCategories( 
      final Writer writer, final CategoriesDirective categories, String pad )
      throws IOException
    {

        //
        // if the categoriy is null or empty then don't bother with it
        //

        if( categories == null ) return;
        if( ( categories.getPriority() == null ) 
          && ( categories.getTarget() == null )
          && ( categories.getCategories().length == 0 ) ) return;

        //
        // write out the categories directive
        //

        writer.write( "\n" + pad + "<categories" );
        if( categories.getPriority() != null )
        {
            writer.write( " priority=\"" + categories.getPriority() + "\"" );
        }
        if( categories.getTarget() != null )
        {
            writer.write( " target=\"" + categories.getTarget() + "\"" );
        }

        if( categories.getCategories().length > 0 )
        {
            writer.write( ">" );
            CategoryDirective[] cats = categories.getCategories();
            final String padding = pad + INDENT;
            for( int i=0; i<cats.length; i++ )
            {
                writeCategoryDirective( writer, cats[i], padding );
            }
            writer.write( "\n" + pad + "</categories>" );
        }
        else
        {
            writer.write( "/>" );
        }
    }

    /**
     * Write out xml representation of the logging categories
     *
     * @param writer the writer
     * @param category a logging category
     * @throws IOException if unable to write xml
     */
    private void writeCategoryDirective( 
      final Writer writer, final CategoryDirective category, String pad )
      throws IOException
    {
        writer.write( "\n" + pad + "<category name=\"" + category.getName() + "\"" );
        if( category.getPriority() != null )
        {
            writer.write( " priority=\"" + category.getPriority() + "\"" );
        }
        if( category.getTarget() != null )
        {
            writer.write( " target=\"" + category.getTarget() + "\"" );
        }
        writer.write( "/>" );
    }

    /**
     * Write out xml representation of the configuration
     *
     * @param writer the writer
     * @param config the type info descriptor
     * @throws IOException if unable to write xml
     */
    private void writeConfiguration( 
      final Writer writer, final Configuration config, String pad )
      throws IOException
    {
        if( !isEmptyConfiguration( config ) )
        {
            StringBuffer buffer = new StringBuffer();
            ConfigurationUtil.list( buffer, pad, config );
            writer.write( "\n" + buffer.toString() );
        }
    }

    /**
     * Write out xml representation of a context directive
     *
     * @param writer the writer
     * @param config the type info descriptor
     * @throws IOException if unable to write xml
     */
    private void writeContext( 
      final Writer writer, final ContextDirective context, String pad )
      throws IOException
    {
        if( context == null )
        {
            return;
        }

        writer.write( "\n" + pad + "<context" );
        if( context.getClassname() != null )
        {
            writer.write( " class=\"" + context.getClassname() + "\"");
        }
        if( context.getSource() != null )
        {
            writer.write( " source=\"" + context.getSource() + "\"");
        }

        EntryDirective[] entries = context.getEntryDirectives();

        if( entries.length == 0 )
        {
            writer.write( "/>" );
            return;
        }
        else
        {
            writer.write( ">");
            final String padding = pad + INDENT;

            //
            // write out the entry directives
            //

            for( int i=0; i<entries.length; i++ )
            {
                EntryDirective entry = entries[i];
                writeEntryDirective( writer, entry, padding );
            }
            writer.write( "\n" + pad + "</context>" );
        }
    }

    /**
     * Write out xml representation of a entry directive
     *
     * @param writer the writer
     * @param entry the entry directive
     * @throws IOException if unable to write xml
     */
    private void writeEntryDirective( 
      final Writer writer, final EntryDirective entry, String pad )
      throws IOException
    {
        final String padding = pad + INDENT;
        writer.write( "\n" + pad + "<entry" );
        writer.write( " key=\"" + entry.getKey() + "\">" );
        if( entry instanceof ImportDirective )
        {
            ImportDirective imp = (ImportDirective) entry;
            writer.write( "\n" + padding + "<import" );
            writer.write( " key=\"" + imp.getImportKey() + "\"" );
            writer.write( "/>" );
        }
        else if( entry instanceof ConstructorDirective  )
        {
            final String fill = padding + INDENT;
            ConstructorDirective cd = (ConstructorDirective) entry;
            writer.write( "\n" + padding + "<constructor" );
            if( !cd.getClassname().equals( "java.lang.String" ) )
            {
                writer.write( " class=\"" + cd.getClassname() + "\"" );
            }
            writer.write( ">" );
            if( cd.getParameters().length > 0 )
            {
                writeParams( writer, cd.getParameters(), fill );
                writer.write( "\n" + padding + "</constructor>" );
            }
            else
            {
                writer.write( cd.getArgument() );
                writer.write( "</constructor>" );
            }
        }
        writer.write( "\n" + pad + "</entry>" );
    }

    /**
     * Write out xml representation of a set of parameters.
     *
     * @param writer the writer
     * @param params the set of parameters
     * @throws IOException if unable to write xml
     */
    private void writeParams( 
      final Writer writer, final Parameter[] params, String pad )
      throws IOException
    {
        for( int i=0; i<params.length; i++ )
        {
            writeParam( writer, params[i], pad );
        }
    }

    /**
     * Write out xml representation of a set of a single parameter.
     *
     * @param writer the writer
     * @param param the parameter
     * @throws IOException if unable to write xml
     */
    private void writeParam( 
      final Writer writer, final Parameter param, String pad )
      throws IOException
    {
        writer.write( "\n" + pad + "<param" );
        if( param.getClassname() != null )
        {
            if( !param.getClassname().equals( "java.lang.String" ) )
            {
                writer.write( " class=\"" + param.getClassname() + "\"" );
            }
        }
        writer.write( ">" );
        Parameter[] parameters = param.getParameters();
        if( parameters.length > 0 )
        {
            final String padding = pad + INDENT;
            writeParams( writer, parameters, padding );
            writer.write( "\n" + pad + "</param>" );
        }
        else
        {
            writer.write( param.getArgument() + "</param>" );
        }
    }

    /**
     * Write out xml representation of a set of parameters
     *
     * @param writer the writer
     * @param params the parameters
     * @throws IOException if unable to write xml
     */
    private void writeParameters( 
      final Writer writer, final Parameters params, String pad )
      throws IOException, ParameterException
    {
        if( params == null )
        {
            return;
        }
        String[] names = params.getNames();
        final String padding = pad + INDENT;
        writer.write( "\n" + pad + "<parameters>" );
        for( int i=0; i<names.length; i++ )
        {
            String name = names[i];
            String value = params.getParameter( name );
            writer.write( "\n" + padding + "<parameter" );
            writer.write( " name=\"" + name + "\"" );
            writer.write( " value=\"" + value + "\"/>" );
        }
        writer.write( "\n" + pad + "</parameters>" );
    }
}
