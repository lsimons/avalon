/*
 * Generated file - Do not edit!
 */
package my.avalon.jmx.tools;

/**
 * MBean interface.
 * @xdoclet-genereted @ 20030629 0907
 */
public interface SystemInfoMBean {

  long getFreeMemory() throws java.lang.Exception;

  long getTotalMemory() throws java.lang.Exception;

  int getRatioMemory() throws java.lang.Exception;

  void gc(int status) throws java.lang.Exception;

  void exit(int status) throws java.lang.Exception;

  void traceInstructions(boolean on) throws java.lang.Exception;

  void traceMethodCalls(boolean on) throws java.lang.Exception;

  java.util.Properties showProperties() throws java.lang.Exception;

  java.lang.String getProperty(java.lang.String key) throws java.lang.Exception;

  int getNumberOfActiveThreads() throws java.lang.Exception;

}
