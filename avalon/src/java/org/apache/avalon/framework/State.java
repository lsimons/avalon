/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Avalon" and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.avalon.framework;

/**
 * Encodes the lifecycle contract. Typical usage is to check whether a
 * change from one state to another is allowed. This makes it easy for
 * a component to protect itself against non-compliant containers.
 *
 * <pre>
 * class MyComponent implements Initializeable
 * {
 *     private m_state = State.STATIC;
 *
 *     MyComponent()
 *     {
 *         m_state = State.change( m_state, State.CONSTRUCTED, this );
 *     }
 *     initialize()
 *     {
 *         m_state = State.change( m_state, State.INITIALIZED, this );
 *     }
 * }
 * </pre>
 *
 * Note that this class currently doesn't support the Re* interfaces, as
 * their contract is insufficiently specified.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: State.java,v 1.1 2003/02/09 14:08:52 leosimons Exp $
 * @since 4.1.4
 */
public class State
{
    // ----------------------------------------------------------------------
    //  Static Properties
    // ----------------------------------------------------------------------

    /**
     * Static state enumeration value indicating that the component has not
     * been constructed.
     */
    public static final int STATIC = 0;

    /**
     * Static state enumeration value indicating that the component has been
     * constructed
     */
    public static final int CONSTRUCTED = 1;

    /**
     * Static state enumeration value indicating that logging has been
     * enabled for this component
     */
    public static final int LOGENABLED = 2;

    /**
     * Static state enumeration value indicating that the component has been
     * contextualized
     */
    public static final int CONTEXTUALIZED = 3;

    /**
     * Static state enumeration value indicating that the component has been
     * serviced <i>or</i> it has been composed.
     */
    public static final int SERVICED = 4;

    /**
     * Static state enumeration value indicating that the component has been
     * serviced <i>or</i> it has been composed.
     */
    public static final int COMPOSED = 5;

    /**
     * Static state enumeration value indicating that the component has been
     * configured
     */
    public static final int CONFIGURED = 6;

    /**
     * Static state enumeration value indicating that the component has been
     * parameterized
     */
    public static final int PARAMETERIZED = 7;

    /**
     * Static state enumeration value indicating that the component has been
     * initialized
     */
    public static final int INITIALIZED = 8;

    /**
     * Static state enumeration value indicating that the component has been
     * started
     */
    public static final int STARTED = 9;

    /**
     * Static state enumeration value indicating that the component has been
     * suspended
     */
    public static final int SUSPENDED = 10;

    /* not supported because they're not supported anywhere:

        RECONTEXTUALIZED
        RECONFIGURED
        REPARAMETERIZED
    */

    /**
     * Static state enumeration value indicating that the component has been
     * resumed
     */
    public static final int RESUMED = 14;

    /**
     * Static state enumeration value indicating that the component has been
     * stopped
     */
    public static final int STOPPED = 15;

    /**
     * Static state enumeration value indicating that the component has been
     * disposed
     */
    public static final int DISPOSED = 16;

    /**
     * Static arraylist containing the string representations of the various states
     * Use a state enumeration value as the array index, ie:
     *
     * <pre>
     *     final String disposed = STATE_NAMES[DISPOSED];
     * </pre>
     */
    public static final String[] STATE_NAMES = new String[17];

    static
    {
        STATE_NAMES[STATIC] = "STATIC";
        STATE_NAMES[CONSTRUCTED] = "CONSTRUCTED";
        STATE_NAMES[LOGENABLED] = "LOGENABLED";
        STATE_NAMES[CONTEXTUALIZED] = "CONTEXTUALIZED";
        STATE_NAMES[SERVICED] = "SERVICED";
        STATE_NAMES[COMPOSED] = "COMPOSED";
        STATE_NAMES[CONFIGURED] = "CONFIGURED";
        STATE_NAMES[PARAMETERIZED] = "PARAMETERIZED";
        STATE_NAMES[INITIALIZED] = "INITIALIZED";
        STATE_NAMES[STARTED] = "STARTED";
        STATE_NAMES[SUSPENDED] = "SUSPENDED";
        STATE_NAMES[RESUMED] = "RESUMED";
        STATE_NAMES[STOPPED] = "STOPPED";
        STATE_NAMES[DISPOSED] = "DISPOSED";
    }

    /**
     * Static Map containing the class that a component must implement in order
     * for a lifecycle state to apply to it. Use a state enumeration value as the
     * key, ie:
     *
     * <pre>
     *     final Class disposable = STATE_INTERFACES[DISPOSABLE];
     *     final boolean isTrue = Disposable.class.equals(disposable);
     * </pre>
     */
    public static final Class[] STATE_INTERFACES = new Class[17];

    static
    {
        STATE_INTERFACES[STATIC] = Object.class;
        STATE_INTERFACES[CONSTRUCTED] = Object.class;
        STATE_INTERFACES[LOGENABLED] =
                org.apache.avalon.framework.logger.LogEnabled.class;
        STATE_INTERFACES[CONTEXTUALIZED] =
                org.apache.avalon.framework.context.Contextualizable.class;
        STATE_INTERFACES[SERVICED] =
                org.apache.avalon.framework.service.Serviceable.class;
        STATE_INTERFACES[COMPOSED] =
                org.apache.avalon.framework.component.Composable.class;
        STATE_INTERFACES[CONFIGURED] =
                org.apache.avalon.framework.configuration.Configurable.class;
        STATE_INTERFACES[PARAMETERIZED] =
                org.apache.avalon.framework.parameters.Parameterizable.class;
        STATE_INTERFACES[INITIALIZED] =
                org.apache.avalon.framework.activity.Initializable.class;
        STATE_INTERFACES[STARTED] =
                org.apache.avalon.framework.activity.Startable.class;
        STATE_INTERFACES[SUSPENDED] =
                org.apache.avalon.framework.activity.Suspendable.class;
        STATE_INTERFACES[RESUMED] =
                org.apache.avalon.framework.activity.Suspendable.class;
        STATE_INTERFACES[STOPPED] =
                org.apache.avalon.framework.activity.Startable.class;
        STATE_INTERFACES[DISPOSED] =
                org.apache.avalon.framework.activity.Disposable.class;
    }

    // ----------------------------------------------------------------------
    //  Static Methods
    // ----------------------------------------------------------------------

    /**
     * Test whether a specific change is allowed according to the lifecycle
     * contract,  provided that a component indeed supports the specified
     * lifecycle stages.
     *
     * @param from the static state enumeration value indicating the current
     *             component state
     * @param to the static state enumeration value indicating the proposed
     *             component state
     * @return true if the change is allowed, false otherwise
     */
    public static boolean isValidChange( int from, int to )
    {
        if( from > DISPOSED || to >= DISPOSED )
            return false;

        if( from < STATIC || to < STATIC )
            return false;

        if( from == RESUMED && to == SUSPENDED ) // you can re-suspend a resumed component
            return true;

        if( from == SERVICED && to == COMPOSED ) // cannot compose a serviceable!
            return true;

        if(from == to) // this is normally not accepted, but...
        {
            if((to != STATIC )  // classes are free to exist
               && (to != CONSTRUCTED )  // as are objects
               && (to != STARTED ) // start() may be called more than once
               && (to != STOPPED ) // stop() may be called more than once
               && (to != RESUMED ) // resume() may be called more than once
              )
                return true;
            else
                return false;
        }

        if( to != from+1 ) // allowed for suspension or stages which can be set multiple
                           // times in a row, but that is covered above
            return false;

        return true;
    }

    /**
     * Test whether a specific change is allowed according to the lifecycle
     * contract,  provided that a component indeed supports the specified
     * lifecycle stages, and throw an IllegalStateException with appropriate
     * error message if it is not.
     *
     * @param fromState the static state enumeration value indicating the
     *        current component state
     * @param toState the static state enumeration value indicating the
     *        proposed component state
     * @param component the component on which the change is proposed. May
     *        not be null; in that case, use isValidChange() instead
     * @throws IllegalStateException if the proposed change is not acceptable
     *         for this component
     */
    public static void testChange( int fromState, int toState, Object component ) throws IllegalStateException
    {
        // 1) make sure arguments make sense
        int from = ( fromState >= 0 )? fromState : 0 ; // no negative state please
        int to =  ( toState >= 0 )? toState : 0 ; // no negative state please
        from = ( from > DISPOSED )? DISPOSED : from ; // no bigger than DISPOSED please
        to = ( to > DISPOSED )? DISPOSED : to ; // no bigger than DISPOSED please

        final Class clazz = component.getClass();

        // 2) check if the proposed state is implemented in the component
        final Class fromClass = STATE_INTERFACES[from];
        final Class toClass = STATE_INTERFACES[to];

        if( fromClass.isAssignableFrom( clazz ) )
        {
            // sorry, were in a bad state already
            throw new IllegalStateException( "Cannot change to state " + STATE_NAMES[to] +
                    " because the current state is " + STATE_NAMES[from] +
                    ", which is not acceptable for this component!" );
        }
        if( toClass.isAssignableFrom( clazz ) )
        {
            // sorry, not acceptable!
            throw new IllegalStateException( "Cannot change to state " + STATE_NAMES[to] +
                    " because the current component doesn't support that state!" );
        }

        // 3) check that we're not DISPOSED already
        if( from == DISPOSED ) // last state already!
        {
            // sorry, not acceptable!
            throw new IllegalStateException( "Cannot change to state " + STATE_NAMES[to] +
                " because the current state, " + STATE_NAMES[DISPOSED] + " is the final state!" );
        }

        // 4) check that we're not composing after servicing
        if( from == SERVICED && to == COMPOSED )
        {
            // sorry, not acceptable!
            throw new IllegalStateException( "Cannot change to state " + STATE_NAMES[to] +
                " because " + STATE_NAMES[from] + " cannot be used along with" +
                STATE_NAMES[to] + "!" );
        }

        // 5) check that the proposed new state isn't too small
        if( to < from ) // this is normally not okay, except for suspension,
                         // but that is covered inside
        {
             // you can re-suspend a resumed component
            if( from == RESUMED
                && to == SUSPENDED
              )
                return;

            // sorry, from always needs to be bigger than to
            throw new IllegalStateException( "Cannot change to state " + STATE_NAMES[to] +
                    " because the current state is " + STATE_NAMES[from] + "!" );
        }

        // 6) check that the proposed state isn't the same as the current state
        //    if that is not allowed
        if(from == to)        // this is normally not accepted, but...
        {
            if(
               (to != STATIC )  // classes are free to exist
            && (to != CONSTRUCTED )  // as are objects
            && (to != STARTED ) // start() may be called more than once
            && (to != STOPPED ) // stop() may be called more than once
            && (to != RESUMED ) // resume() may be called more than once
              )
                return;

            throw new IllegalStateException( "Cannot change to state " + STATE_NAMES[to] +
                    " because already in that state!" );
        }

        // 7) figure out what the next state now must be....

        // this indexing would fail for DISPOSED+1, but we've ruled out
        // from being DISPOSED already
        int nextState = from + 1;
        Class nextStateClass = STATE_INTERFACES[nextState];
        for( ;
             (
                !nextStateClass.isAssignableFrom( clazz )
                && nextState < DISPOSED
             );
             nextState = nextState + 1)
        {
            nextStateClass = STATE_INTERFACES[nextState];
        }

        // the proposed state is wrong!
        if( to != nextState )
            throw new IllegalStateException( "Cannot change to state " + STATE_NAMES[to] +
                    " because the current state is " + STATE_NAMES[from] + ", and the" +
                    " state " + STATE_NAMES[nextState] + " should come next!" );
    }

    /**
     * Test whether a specific change is allowed according to the lifecycle
     * contract,  provided that a component indeed supports the specified
     * lifecycle states, and throw an IllegalStateException with appropriate
     * error message if it is not. If it is, return the new state.
     *
     * @param fromState the static state enumeration value indicating the
     *        current component state
     * @param toState the static state enumeration value indicating the
     *        proposed component state
     * @param component the component on which the change is proposed. May
     *        not be null; in that case, use isValidChange() instead
     * @return the new state
     * @throws IllegalStateException if the proposed change is not acceptable
     *         for this component
     */
    public static int change( int from, int to, Object component ) throws IllegalStateException
    {
        testChange( from, to, component );
        return to;
    }

}
