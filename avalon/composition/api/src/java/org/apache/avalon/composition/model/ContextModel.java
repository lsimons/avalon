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

package org.apache.avalon.composition.model;

import org.apache.avalon.framework.context.Context;

/**
 * <p>Specification of a context model from which a 
 * a fully qualifed context can be established.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.5 $ $Date: 2004/02/22 17:28:29 $
 */
public interface ContextModel extends Dependent
{
    /**
     * The default context strategy interface class.
     */
    public static final String DEFAULT_STRATEGY_CLASSNAME = 
      "org.apache.avalon.framework.context.Contextualizable";


   /**
    * Return the class representing the contextualization 
    * stage interface.
    * 
    * @return the contextualization interface class
    */
    Class getStrategyClass();

   /**
    * Return the context object for the component.
    * 
    * @return the context object
    */
    Context getContext();

   /**
    * Return the set of entry models associated with this context model.
    * 
    * @return the entry models
    */
    EntryModel[] getEntryModels();

   /**
    * Return an entry model matching the supplied key.
    * 
    * @return the entry model or null if tyhe key is unknown
    */
    EntryModel getEntryModel( String key );

   /**
    * Set the entry model relative to a supplied key.
    * 
    * @param key the entry key
    * @param model the entry model
    */
    void setEntryModel( String key, EntryModel model );

   /**
    * Set the entry to a suplied value.
    * 
    * @param key the entry key
    * @param value the entry value
    */
    void setEntry( String key, Object value );

}
