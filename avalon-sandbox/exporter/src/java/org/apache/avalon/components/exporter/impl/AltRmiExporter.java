package org.apache.avalon.components.exporter.impl;

import org.apache.avalon.activation.lifecycle.LifecycleCreateExtension;
import org.apache.avalon.activation.lifecycle.LifecycleDestroyExtension;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.meta.info.Type;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.altrmi.server.impl.AbstractServer;
import org.apache.altrmi.server.PublicationDescription;
import org.apache.altrmi.server.PublicationException;
import org.apache.altrmi.server.impl.socket.CompleteSocketCustomStreamServer;
import org.apache.altrmi.server.impl.classretrievers.BcelDynamicGeneratorClassRetriever;
import org.apache.altrmi.server.impl.classretrievers.AbstractDynamicGeneratorClassRetriever;
import org.apache.altrmi.server.impl.AbstractServerStreamReadWriter;
import org.apache.altrmi.server.impl.DefaultServerSideClientContextFactory;
import org.apache.altrmi.server.impl.NullServerMonitor;
import org.apache.altrmi.server.impl.ServerCustomStreamReadWriter;
import org.apache.altrmi.server.impl.DefaultAuthenticator;
import org.apache.altrmi.server.impl.adapters.InvocationHandlerAdapter;
import org.apache.altrmi.server.impl.classretrievers.NoClassRetriever;
import org.apache.altrmi.common.DefaultThreadPool;
import java.util.StringTokenizer;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.components.exporter.ServiceExporter;
import org.apache.avalon.components.exporter.ExportException;


/**
 * @author Avalon Development Team
 * @version $Id: AltRmiExporter.java,v 1.1 2003/09/28 02:22:19 farra Exp $
 * @avalon.component name="altrmi-exporter" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.components.exporter.ServiceExporter"
 * @avalon.extension id="urn:exporter:altrmi"
 */
public class AltRmiExporter
    extends AbstractLogEnabled
    implements LifecycleCreateExtension, LifecycleDestroyExtension,
               ServiceExporter, Configurable, Initializable, Startable
{

  protected AbstractServer m_server = null;
  protected AbstractDynamicGeneratorClassRetriever m_generator = null;
  protected int m_port = 7124;

  public AltRmiExporter() {
  }


  public void export(String name, Object obj, Class[] services,
                     Class[] additionalFacades) throws ExportException {
    try {
      PublicationDescription pub = new PublicationDescription(services,
          additionalFacades);

      if(getLogger().isDebugEnabled()) getLogger().debug("Generating Proxy...");

      m_generator.generate(name, pub,
                           Thread.currentThread().getContextClassLoader());

      if (getLogger().isDebugEnabled()) getLogger().debug("Exported: " + name);

      m_server.publish(obj, name, pub);

    }
    catch (PublicationException ex) {
      throw new ExportException("Error exporting the service",ex);
    }
  }

  public void create(DeploymentModel deploymentModel,
                     StageDescriptor stageDescriptor, Object object) {
    try {

      Type type = deploymentModel.getType();
      String name = type.getInfo().getName();
      if(getLogger().isDebugEnabled())
        getLogger().debug("Preparing export: "+name);

      // get service list
      ServiceDescriptor[] services = type.getServices();
      Class[] serviceClasses = new Class[services.length];
      for (int i = 0; i < services.length; i++) {
        ServiceDescriptor desc = services[i];
        serviceClasses[i] = Class.forName(desc.getReference().getClassname());
      }

      String facades = stageDescriptor.getAttribute("urn:altrmi:add-facades",null);
      Class facadeClasses[] = new Class[0];
      if(facades != null){
        StringTokenizer st = new StringTokenizer(facades,",");
        facadeClasses = new Class[st.countTokens()];
        int i = 0;
        while(st.hasMoreTokens()){
          facadeClasses[i] = Class.forName(st.nextToken());
          i++;
        }
      }

      export(name,object,serviceClasses,facadeClasses);

    }
    catch (Exception ex) {
      getLogger().error("Error publishing component",ex);
    }
  }

  public void destroy(DeploymentModel deploymentModel,
                      StageDescriptor stageDescriptor, Object object) {
    /** @todo hmm... how to destroy with altrmi? */
  }

  public void configure(Configuration configuration) {
    m_port = configuration.getChild("port",true).getValueAsInteger(7124);
    /** @todo add more configuration for the server... */
  }

  public void initialize() {
    m_generator = new BcelDynamicGeneratorClassRetriever();
    m_server = new CompleteSocketCustomStreamServer(m_generator,
        new DefaultAuthenticator(), new NullServerMonitor(),
        new DefaultThreadPool(),new DefaultServerSideClientContextFactory(),m_port);
  }


  public void start() throws Exception {
    m_server.start();
  }

  public void stop() {
    m_server.stop();
  }

}