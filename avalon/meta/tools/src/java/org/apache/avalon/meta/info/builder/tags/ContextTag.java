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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Properties;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import org.apache.avalon.meta.info.ContextDescriptor;
import org.apache.avalon.meta.info.EntryDescriptor;
import org.apache.avalon.meta.info.ReferenceDescriptor;

/**
 * A doclet tag handler for the 'extension' tag.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:16:14 $
 */
public class ContextTag extends AbstractTag
{
   /**
    * The default context class.
    */
    protected static final String CONTEXT_CLASS =
        "org.apache.avalon.framework.context.Context";

   /**
    * The javadoc key for the context tag.
    */
    public static final String KEY = "context";

   /**
    * The javadoc context context strategy parameter.
    */
    public static final String STRATEGY_PARAM = "strategy";

   /**
    * The javadoc context tag key parameter.
    */
    public static final String KEY_PARAM = "key";

   /**
    * The javadoc context tag volatile flag.
    */
    public static final String VOLATILE_PARAM = "volatile";

   /**
    * The javadoc context tag alias parameter.
    */
    public static final String ALIAS_PARAM = "alias";

   /**
    * The javadoc context tag optional parameter.
    */
    public static final String OPTIONAL_PARAM = "optional";

   /**
    * The javadoc context entry tag name.
    */
    public static final String ENTRY = "entry";

   /**
    * Context tag constructor.
    * @param clazz the javadoc class descriptor
    */
    public ContextTag( final JavaClass clazz )
    {
        super( clazz );
    }

   /**
    * Return an array of StageDescriptor instances based on declared 'stage' tags.
    * @return the context descriptors
    */
    public ContextDescriptor getContext()
    {
        JavaMethod[] methods = findTaggedMethods( 
          getJavaClass(), getNS() + Tags.DELIMITER + ENTRY );
        if( methods.length == 0 )
        {
            return new ContextDescriptor( new EntryDescriptor[0] );
        }
        else
        {
            
            //
            // collect the @avalon.entry tags from this class and 
            // all supertypes methods marked with @avalon.entry 
            // 

            final ArrayList list = new ArrayList();
            final Set marked = new HashSet( 10 );
            for( int j = 0; j < methods.length; j++ )
            {
                final DocletTag[] tags = 
                  methods[j].getTagsByName( getNS() + Tags.DELIMITER + ENTRY );
                for( int i = 0; i < tags.length; i++ )
                {
                    final String key = getNamedParameter( tags[i], KEY_PARAM );
                    if( !marked.contains( key ) )
                    {
                        list.add( getEntry( tags[i] ) );
                        marked.add( key );
                    }
                }
            }

            final EntryDescriptor[] entries =
              (EntryDescriptor[])list.toArray( new EntryDescriptor[ list.size() ] );

            String type = null;
            String strategy = null;
            for( int j = 0; j < methods.length; j++ )
            {
                JavaMethod method = methods[j];
                final DocletTag tag = method.getTagByName( getNS() + Tags.DELIMITER + KEY );
                if( tag != null )
                {
                    type = 
                      resolveType( getNamedParameter( tag, TYPE_PARAM, CONTEXT_CLASS ) );
                    strategy = getNamedParameter( tag, STRATEGY_PARAM, null );
                    break;
                }
            }

            Properties properties = null;
            if( strategy != null )
            {
                properties = new Properties();
                properties.setProperty( 
                   ContextDescriptor.STRATEGY_KEY, strategy );
            }

            return new ContextDescriptor( type, entries, properties );
        }
    }

    private EntryDescriptor getEntry( DocletTag tag )
    {
        final String key = getNamedParameter( tag, KEY_PARAM );
        final String alias = getNamedParameter( tag, ALIAS_PARAM, null );
        final String entryType = getNamedParameter( tag, TYPE_PARAM, "java.lang.String" );
        final String optional = getNamedParameter( tag, OPTIONAL_PARAM, "false" );
        final boolean isOptional = "true".equals( optional );
        final String volatileValue = getNamedParameter( tag, VOLATILE_PARAM, "false" );
        final boolean isVolatile = "true".equals( volatileValue );
        return new EntryDescriptor( key, entryType, isOptional, isVolatile, alias );
    }
}
