/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

package org.apache.avalon.merlin.jndi.block;

import javax.naming.*;
import javax.naming.spi.*;
import java.util.*;

/**
 * Block URL context factory.
 */
public class blockURLContextFactory implements ObjectFactory 
{
    /**
     *  The "block" url is of the form block:/<name in StandardBlock namespace>.
     */
    public Object getObjectInstance(Object urlInfo, Name name, Context nameCtx,
      Hashtable env) throws Exception 
    {
        //
        // Case 1: urlInfo is null
        // This means to create a URL context that can accept 
        // arbitrary "block" URLs.
        //

        if (urlInfo == null) 
        {
            return createURLContext(env);
        }

        //
        // Case 2: urlInfo is a single string
        // This means to create/get the object named by urlInfo
        //

        if (urlInfo instanceof String) 
        {
            Context urlCtx = createURLContext(env);
            try 
            {
                return urlCtx.lookup((String)urlInfo);
            } 
            finally 
            {
                urlCtx.close();
            }
        } 

        //
        // Case 3: urlInfo is an array of strings
        // This means each entry in array is equal alternative; create/get
        // the object named by one of the URls
        //

        if (urlInfo instanceof String[]) 
        {

            //
            // Try each URL until lookup() succeeds for one of them.
            // If all URLs fail, throw one of the exceptions arbitrarily.
            //

            String[] urls = (String[])urlInfo;
            if (urls.length == 0) 
            {
                throw (new ConfigurationException(
                  "blockURLContextFactory: empty URL array"));
            }
            Context urlCtx = createURLContext(env);
            try 
            {
                NamingException ne = null;
                for (int i = 0; i < urls.length; i++) 
                {
                    try 
                    {
                        return urlCtx.lookup(urls[i]);
                    } 
                    catch (NamingException e) 
                    {
                        ne = e;
                    }
                }
                throw ne;
            } 
            finally 
            {
                urlCtx.close();
            }
        } 

        // Case 4: urlInfo is of an unknown type
        // Provider-specific action: reject input

        final String error = 
          "argument must be a block URL string or an array of them";
        throw new IllegalArgumentException( error );

    }

    protected Context createURLContext(Hashtable env) 
    {
        return new blockURLContext(env);
    }
}
