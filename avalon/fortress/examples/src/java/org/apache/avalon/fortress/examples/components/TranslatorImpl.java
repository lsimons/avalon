/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.avalon.fortress.examples.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.excalibur.instrument.AbstractLogEnabledInstrumentable;
import org.apache.excalibur.instrument.CounterInstrument;

/**
 * Simple implementation of the <code>Translator</code> component, which
 * maintains a simple mapping of keys to translated values, created during
 * configuration.
 *
 * <p>
 * Configuration format:
 *
 * <pre>
 * &lt;translations&gt;
 *   &lt;entry key="hello-world"&gt;
 *    &lt;value language="Deutsch"&gt;Hallo Welt&lt;/value&gt;
 *    &lt;value language="English"&gt;Hello World&lt;/value&gt;
 *   &lt;/entry&gt;
 * &lt;/translations&gt;
 * </pre>
 * </p>
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/02/25 16:28:24 $
 */
public class TranslatorImpl extends AbstractLogEnabledInstrumentable
    implements Translator, org.apache.avalon.framework.configuration.Configurable
{
    // Instrument to count the number of translations performed
    private CounterInstrument m_translationsInstrument;

    // internal store of translation mappings
    private Map m_keys = new java.util.HashMap();

    /**
     * Create a new TranslatorImpl.
     */
    public TranslatorImpl()
    {
        addInstrument( m_translationsInstrument = new CounterInstrument( "translations" ) );
    }

    /**
     * Configures this component. Reads configuration information
     * from container and appropriately sets up the internal mapping
     * array. Configuration syntax is specified in the class header.
     *
     * @param config <code>Configuration</code> details
     * @exception org.apache.avalon.framework.configuration.ConfigurationException if an error occurs
     */
    public void configure( Configuration config )
        throws ConfigurationException
    {
        if( config != null )
        {
            Configuration[] entries =
                config.getChild( "dictionary" ).getChildren( "translation" );

            for( int i = 0; i < entries.length; ++i )
            {
                String key = entries[ i ].getAttribute( "key" );
                Configuration[] values = entries[ i ].getChildren( "value" );

                Map translations = new HashMap();

                for( int j = 0; j < values.length; ++j )
                {
                    translations.put(
                        values[ j ].getAttribute( "language" ),
                        values[ j ].getValue()
                    );
                }

                m_keys.put( key, translations );
            }

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug(
                    "Translator configured with " + m_keys.size() + " translations"
                );
            }
        }

        else
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "No configuration specified" );
            }
        }
    }

    /**
     * <code>getSupportedLanguages</code> returns an array of String
     * objects detailing which languages are supported for the given
     * key.
     *
     * @param key a <code>String</code> value identifying a translation
     * @return a <code>String[]</code> array containing available language
     * translations for the given key
     */
    public String[] getSupportedLanguages( String key )
    {
        Map translations = (Map)m_keys.get( key );
        Set keys = translations.keySet();
        return (String[])keys.toArray( new String[]{} );
    }

    /**
     * <code>getTranslation</code> obtains a translation for a given
     * key in a given language. The language parameter must be listed
     * in <code>getSupportedLanguages</code>.
     *
     * @param key a <code>String</code> value identifying a translation
     * @param language a <code>String</code> value identifying the language
     * @return translated text
     */
    public String getTranslation( String key, String language )
    {
        // Notify the Instrument Manager
        m_translationsInstrument.increment();

        Map translationMap = (Map)m_keys.get( key );
        return (String)translationMap.get( language );
    }
}

