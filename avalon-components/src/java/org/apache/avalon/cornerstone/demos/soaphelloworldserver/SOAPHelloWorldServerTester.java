/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.demos.soaphelloworldserver;

import org.apache.avalon.cornerstone.demos.helloworldserver.HelloWorldServer;
import electric.registry.Registry;
import electric.registry.RegistryException;

/**
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 * @version 1.0
 */
public class SOAPHelloWorldServerTester
    
{
    protected HelloWorldServer    mHelloWorldServer;

    public static void main(String[] args)
    {
        String url = "http://127.0.0.1:7998/soap/helloworld.wsdl";
        HelloWorldServer hws = null;
        try 
        {
            hws = (HelloWorldServer) Registry.bind( url, HelloWorldServer.class );
            
        } catch (RegistryException re)
        {
            System.err.println( "Opps some Glue problem " + re.getMessage());
            re.printStackTrace();
        }
        hws.setGreeting("Howdie Partner");
        System.out.println( "The greeting on the HelloWorldServer block has been changed.  Point your browser to http://localhost:7999 to see it." );        

    }

}
