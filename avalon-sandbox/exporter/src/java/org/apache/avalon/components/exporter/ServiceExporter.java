package org.apache.avalon.components.exporter;

/**
 * Marker service for exporters
 * @version $Id: ServiceExporter.java,v 1.1 2003/09/28 02:22:19 farra Exp $
 * @avalon.service version="1.0"
 */
public interface ServiceExporter {

  public static final String ROLE = ServiceExporter.class.getName();

  /**
   * export the service
   * @param name the service bind name
   * @param obj the service implementation
   * @param services the service classes implemented
   * @param additionalFacades additional facades to publish
   * @throws ExportException
   */
  public void export(String name, Object obj,
                     Class[] services, Class[] additionalFacades)
      throws ExportException;


}