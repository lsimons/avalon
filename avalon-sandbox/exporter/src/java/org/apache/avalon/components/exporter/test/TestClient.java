package org.apache.avalon.components.exporter.test;

import javax.naming.InitialContext;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.*;

/**
 * <p>Title:  Avalon Exporter</p>
 * <p>Description:  Exports avalon components via RMI/JNDI (using AltRMI)</p>
 * <p>Copyright:  Copyright (c) 2003 Apache Avalon Project - All Rights Reserved.</p>
 * <p>Company:  Apache Avalon Project</p>
 * @author cimadmin
 * @version $Id: TestClient.java,v 1.1 2003/09/28 02:22:19 farra Exp $
 */

public class TestClient {
  public TestClient() {
  }


  public static void main(String[] args) {

    try {
      Hashtable env = new Hashtable();
      env.put(
          Context.INITIAL_CONTEXT_FACTORY,
          "org.apache.altrmi.client.impl.naming.DefaultInitialContextFactory");
      env.put(Context.PROVIDER_URL, "altrmi://localhost:7124/SocketCustomStream");
      Context ctx = new InitialContext(env);

      TestService test = (TestService) ctx.lookup("test");
      System.out.println(test.getValue());
    }
    catch (NamingException ex) {
      ex.printStackTrace();
    }
    System.exit(0);
  }

}