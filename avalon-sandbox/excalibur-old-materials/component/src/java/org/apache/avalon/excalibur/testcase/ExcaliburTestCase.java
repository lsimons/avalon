/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
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
    protected void addContext( DefaultContext context )
    {
    }

    final private void setupManagers( final Configuration confCM,
                                      final Configuration confRM,
                                      final Configuration confLM,
                                      final Context context )
        throws Exception
    {
        // Setup the log manager.  Get the logger name and log level from attributes
        //  in the <logkit> node
        String lmLoggerName = confLM.getAttribute( "logger", "lm" );
        String lmLogLevel = confLM.getAttribute( "log-level", "INFO" );
        Priority lmPriority = Priority.getPriorityForName( lmLogLevel );
        DefaultLogKitManager logKitManager = new DefaultLogKitManager();
        Logger lmLogger = Hierarchy.getDefaultHierarchy().getLoggerFor( lmLoggerName );
        lmLogger.setPriority( lmPriority );
        logKitManager.enableLogging( new LogKitLogger( lmLogger ) );
        logKitManager.contextualize( context );
        logKitManager.configure( confLM );
        Hierarchy h = logKitManager.getHierarchy();
        h.setDefaultPriority( lmPriority );
        m_logKitManager = logKitManager;

        // Setup the RoleManager
        String rmLoggerName = confRM.getAttribute( "logger", "rm" );
        DefaultRoleManager roleManager = new DefaultRoleManager();
        roleManager.setLogger( logKitManager.getLogger( rmLoggerName ) );
        roleManager.configure( confRM );

        // Set up the ComponentLocator
        String cmLoggerName = confCM.getAttribute( "logger", "cm" );
        ExcaliburComponentManager manager = new ExcaliburComponentManager();
        manager.setLogger( logKitManager.getLogger( cmLoggerName ) );
        manager.setLogKitManager( logKitManager );
        manager.contextualize( context );
        manager.setRoleManager( roleManager );
        manager.configure( confCM );
        manager.initialize();
        m_manager = manager;
    }

    protected final Object lookup( final String key )
        throws ComponentException
    {
        return manager.lookup( key );
    }

    protected final void release( final Component object )
    {
        manager.release( object );
    }
}
