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

package tutorial;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.lifecycle.Accessor;
import org.apache.avalon.lifecycle.Creator;

/**
 * Definition of an extension type that logs messages related to
 * all lifestyle stages.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @avalon.component name="extension" version="1.0" lifestyle="singleton"
 * @avalon.extension id="urn:demo:demonstratable"
 */
public class DemonstratableProvider extends AbstractLogEnabled
        implements Creator, Accessor, Initializable, Disposable
{

    //=======================================================================
    // Initializable
    //=======================================================================

    /**
     * Initialization of the component. Simply demonstrates that this is
     * a real componet that can implement any lifcycle stage in the role of
     * classic component.
     */
    public void initialize()
    {
        getLogger().info( "initialize" );
    }

    //=======================================================================
    // Creator
    //=======================================================================

    /**
     * Invoked by a container to handle a create stage action.
     * @param target the object to handle
     * @param context the context supplied to the extension by container
     *   corresponding to its extension context criteria
     * @exception Exception if an error occurs
     */
    public void create( Object target, Context context ) throws Exception
    {
        getLogger().info( "invoking create on target" );
        if( target instanceof Demonstratable )
        {
            ((Demonstratable) target).demo( "create id: "
                    + System.identityHashCode( this )
                    + ", " + Thread.currentThread() );
        }
    }

    /**
     * Invoked by a container to handle a destroy stage action.
     * @param target the object to handle
     * @param context the context supplied to the extension by container
     *   corresponding to its extension context criteria
     */
    public void destroy( Object target, Context context )
    {
        getLogger().info( "invoking destroy on target" );
        if( target instanceof Demonstratable )
        {
            ((Demonstratable) target).demo( "destroy id: "
                    + System.identityHashCode( this )
                    + ", " + Thread.currentThread() );
        }
    }

    /**
     * Invoked by a container to handle a access stage action.
     * @param target the object to handle
     * @param context the context supplied to the extension by container
     *   corresponding to its extension context criteria
     * @exception Exception if an error occurs
     */
    public void access( Object target, Context context ) throws Exception
    {
        getLogger().info( "invoking access on target" );
        if( target instanceof Demonstratable )
        {
            ((Demonstratable) target).demo( "access id: "
                    + System.identityHashCode( this )
                    + ", " + Thread.currentThread() );
        }
    }

    /**
     * Invoked by a container to handle a release stage action.
     * @param target the object to handle
     * @param context the context supplied to the extension by container
     *   corresponding to its extension context criteria
     */
    public void release( Object target, Context context )
    {
        getLogger().info( "invoking release on target" );
        if( target instanceof Demonstratable )
        {
            ((Demonstratable) target).demo( "release id: "
                    + System.identityHashCode( this )
                    + ", " + Thread.currentThread() );
        }
    }

    //=======================================================================
    // Disposable
    //=======================================================================

    /**
     * Disposal of the component.
     */
    public void dispose()
    {
        if( getLogger().isInfoEnabled() )
        {
            getLogger().info( "dispose" );
        }
    }
}