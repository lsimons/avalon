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

package org.apache.avalon.meta.info.builder.tags;

import java.util.Properties;

import org.apache.avalon.framework.Version;
import org.apache.avalon.meta.info.ContextDescriptor;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ExtensionDescriptor;
import org.apache.avalon.meta.info.InfoDescriptor;
import org.apache.avalon.meta.info.CategoryDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.meta.info.Type;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * <p>A doclet tag the declares a a {@link Type} descriptor using the
 * <code>&amp;avalon.type</code> or <code>&amp;avalon.component</code>
 * javadoc tag.  The &amp;avalon.component is senonomouse with &amp;avalon.type.
 * The &amp;avalon.type tag recognizes the following attributes:<p>
 *
 * <table border="1" cellpadding="3" cellspacing="0" width="100%">
 * <tr bgcolor="#CCCCFF" class="TableHeadingColor">
 *   <td colspan="3"><p><b>Tag Attributes</b></p></td>
 * </tr>
 * <tr bgcolor="white" class="TableRowColor">
 *   <td>name</td><td>required</td>
 *   <td>The name of the component type.</td></tr>
 * <tr bgcolor="white" class="TableRowColor">
 *   <td>version</td><td>optional</td>
 *   <td>A version identifier in the format [major].[minor].[micro].
 *       If not supplied the version 1.0.0 will be assumed.</td></tr>
 * <tr bgcolor="white" class="TableRowColor">
 *   <td>lifestyle</td><td>optional</td>
 *   <td>The component implementation lifestyle - one of "singleton",
 *       "thread", "pooled", or "transient".  If not supplied "transient"
 *       is assumed.</td></tr>
 * </table>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/10/04 11:51:25 $
 */
public class TypeTag extends AbstractTag
{
   /**
    * Javadoc tag key for the type tag.
    */
    public static final String KEY = "component";

   /**
    * The name parameter
    */
    public static final String NAME_PARAM = "name";

   /**
    * The version parameter
    */
    public static final String VERSION_PARAM = "version";

   /**
    * The version parameter
    */
    public static final String LIFESTYLE_PARAM = "lifestyle";

   /**
    * Type tag constructor.
    * @param clazz the javadoc class descriptor
    */
    public TypeTag( final JavaClass clazz )
    {
        super( clazz );
    }

   /**
    * Return the value of the Avalon 'component' tag.
    * @return the name of the component type
    * @exception IllegalArgumentException if the name tag does not contain a value
    */
    public Type getType()
    {
        DocletTag tag = getJavaClass().getTagByName( getNS() + Tags.DELIMITER + KEY );
        if( null == tag )
        {
            return null;
        }

        final String name = getName( tag );
        final Version version = getVersion( tag );
        final String lifestyle = getLifestyle( tag );
        final String type = getJavaClass().getFullyQualifiedName();
        final Properties properties = new AttributeTag( getJavaClass() ).getProperties();
        final String schema = new SchemaTag( getJavaClass() ).getConfigurationSchema();
        final InfoDescriptor info = new InfoDescriptor( name, type, version, lifestyle, schema, properties );
        final ServiceDescriptor[] services = new ServicesTag( getJavaClass() ).getServices();
        final CategoryDescriptor[] loggers = new LoggerTag( getJavaClass() ).getCategories();
        final DependencyDescriptor[] dependencies =
          new DependencyTag( getJavaClass() ).getDependencies();
        final StageDescriptor[] stages = new StageTag( getJavaClass() ).getStages();
        final ExtensionDescriptor[] extensions = new ExtensionTag( getJavaClass() ).getExtensions();
        final ContextDescriptor context = new ContextTag( getJavaClass() ).getContext();

        return new Type( info, loggers, context, services, dependencies, stages, extensions, null );
    }

    private String getName(DocletTag tag)
    {
        return getNamedParameter( tag, NAME_PARAM );
    }

    private String getLifestyle(DocletTag tag)
    {
        return getNamedParameter( tag, LIFESTYLE_PARAM, null );
    }

    private Version getVersion(DocletTag tag)
    {
        return Version.getVersion( getNamedParameter( tag, VERSION_PARAM, "1.0" ) );
    }
}
